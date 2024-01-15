package com.solutions.vendingmachine.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.solutions.vendingmachine.Utils.VendingUtils;
import com.solutions.vendingmachine.constants.States;
import com.solutions.vendingmachine.database.ContextMapManager;
import com.solutions.vendingmachine.model.MachineContext;
import com.solutions.vendingmachine.model.builder.MachineContextBuilder;
import com.solutions.vendingmachine.model.state.AbstractState;
import com.solutions.vendingmachine.model.state.NoQuarterState;
import com.solutions.vendingmachine.model.state.State;

@Service
public class MachineContextService implements MachineService<MachineContext> {

	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private ContextMapManager contextManager;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private NoQuarterState noQuarterState;
	
	@Autowired
	private MachineItemService itemService;
	
	public MachineContextService(){}
	
	public MachineContext retrieveLatestContext() {
		String latest = this.contextManager.getLatestValue();
		if (latest != null && !latest.isEmpty()) {
			return this.unmarshall(latest);
		}
		return this.initializeContext();
	}
	
	private MachineContext initializeContext() {
		return  new MachineContextBuilder().with(c -> {
			c.id = this.retrieveNextKey();
			c.balance = 0;
			c.income = 0;
			c.itemCount = 0;
			c.dateTime = VendingUtils.retrieveNow();
			c.state = noQuarterState;
		}).build();
	}
	
	public Integer retrieveNextKey() {
//		int size = this.contextManager.getSize();
		String contentStr = this.contextManager.getLatestValue();
		MachineContext context = this.unmarshall(contentStr);
		int key = context != null ? context.getId() + 1: 1;
		return key;
	}
	
	@Override
	public int retrieveSize() {
		return this.contextManager.getSize();
	}

	@Override
	public String marshall(MachineContext obj) {
		String result = null;
		if (obj != null) {
			try {
				result = this.objectMapper.writeValueAsString(obj);
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				LOGGER.error(e.getMessage());
			}
		}
		return result;
	}

	@Override
	public MachineContext unmarshall(String objStr) {
		MachineContext result = null;
		if (objStr != null) {
			try {
				result = this.objectMapper.readValue(objStr, MachineContext.class);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				LOGGER.error(e.getMessage());
			}
		}
		return result;
	}

	@Override
	public MachineContext retrieveById(String id) {
		String contextStr = this.contextManager.get(Integer.valueOf(id));
		return this.unmarshall(contextStr);
	}

	@Override
	public List<MachineContext> retrieveAll() {
		List<MachineContext> result = null;
		List<String> strList = this.contextManager.getAllValues();
		if (strList != null && !strList.isEmpty()) {
			result = new ArrayList<MachineContext>();
			for(String contextStr: strList) {
				MachineContext context = this.unmarshall(contextStr);
				if (context != null) {
					result.add(context);
				}
			}
		}
		return result;
	}

	@Override
	public Boolean deleteById(Integer key) {
		return new Boolean(this.contextManager.delete(key));
	}

	@Override
	public Boolean deleteAll() {
		return new Boolean(this.contextManager.deleteAll());
	}

	@Override
	public Boolean save(Integer key, MachineContext obj) {
		int size = this.retrieveSize();
		this.contextManager.insert(key, this.marshall(obj));
		int size2 = this.retrieveSize();

		return new Boolean(size < size2);
	}
	
	public void incrementItemCountAndSave(MachineContext latestContext) {
		Integer key = this.retrieveNextKey();
		boolean wasSoldOut = latestContext.getState().getStatus().equals(States.SOLD_OUT.toString());
		int count = this.itemService.retrieveSize();
		MachineContext nextContext = new MachineContextBuilder().with(c -> {
			c.id = key;
			c.itemCount = count;
			c.income = latestContext.getIncome();
			c.state = wasSoldOut ? noQuarterState : latestContext.getState();
			c.balance = latestContext.getBalance();
			c.dateTime = VendingUtils.retrieveNow();
		}).build();
		this.save(key, nextContext);
	}
	
	public void decrementItemCountAndSave(MachineContext latestContext) {
		Integer key = this.retrieveNextKey();
		int count = this.itemService.retrieveSize();
		MachineContext nextContext = new MachineContextBuilder().with(c -> {
			c.id = key;
			c.itemCount = count;
			c.income = latestContext.getIncome();
			c.state = latestContext.getState();
			c.balance = latestContext.getBalance();
			c.dateTime = VendingUtils.retrieveNow();
		}).build();
		this.save(key, nextContext);
	}
	
//	public void increaseBalanceSaveContext(MachineContext latestContext, State state) {
//		Integer key = this.retrieveNextKey();
//		MachineContext nextContext = new MachineContextBuilder()
//				.with(c -> {
//					c.id = key;
//					c.itemCount = latestContext.getItemCount();
//					c.income = latestContext.getIncome();
//					c.state = state;
//					c.balance = latestContext.getBalance() + .25;
//					c.dateTime = LocalDateTime.now(ZoneOffset.UTC);
//				}).build();
//		
//		this.save(key, nextContext);
//	}

	public void decrementItemCountIncreaseIncomeResetBalanceAndSave(MachineContext machineContext,
			AbstractState state, double price) {
		Integer key = this.retrieveNextKey();
		int count = this.itemService.retrieveSize();
		if (state != null) {
			state.setMachineContext(null);
		}
		
		MachineContext nextContext = new MachineContextBuilder().with(c -> {
			c.id = key;
			c.itemCount = count;
			c.income = machineContext.getIncome() + price;
			c.state = state;
			c.balance = machineContext.getBalance() - price;
			c.dateTime = VendingUtils.retrieveNow();
		}).build();
		this.save(key, nextContext);
	}

	public void updateBalanceSaveContext(MachineContext latestContext, AbstractState state, double value) {
		Integer key = this.retrieveNextKey();
		int count = this.itemService.retrieveSize();
		if (state != null) {
			state.setMachineContext(null);
		}
		
		MachineContext nextContext = new MachineContextBuilder()
				.with(c -> {
					c.id = key;
					c.itemCount = count;
					c.income = latestContext.getIncome();
					c.state = state;
					c.balance = latestContext.getBalance() + value;
					c.dateTime = VendingUtils.retrieveNow();
				}).build();
		
		this.save(key, nextContext);
	}
}
