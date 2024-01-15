package com.solutions.vendingmachine.service;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import com.solutions.vendingmachine.Utils.VendingUtils;
import com.solutions.vendingmachine.constants.States;
import com.solutions.vendingmachine.model.Item;
import com.solutions.vendingmachine.model.MachineContext;
import com.solutions.vendingmachine.model.builder.ItemBuilder;
import com.solutions.vendingmachine.model.builder.MachineContextBuilder;
import com.solutions.vendingmachine.model.state.NoQuarterState;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(locations="classpath:backend-test.properties")
public class MachineContextServiceTest {

	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private MachineContextService contextService;
	
	@Autowired
	private MachineItemService itemService;
	
	private NoQuarterState state = new NoQuarterState(null, 0);
	private final String contextStr = "{\"id\":1,\"itemCount\":0,\"balance\":0.0,\"income\":0.0,\"dateTime\":\"2018-12-29 23:21:11:308\",\"state\":{\"type\":\"NoQuarterState\",\"value\":0.0,\"status\":\"NO_QUARTER\"}}";
	
	private MachineContext retrieveContext(Integer key) {
		return  new MachineContextBuilder().with(c -> {
			c.id = key != null ? key : 1;
			c.balance = 0;
			c.income = 0;
			c.itemCount = 0;
			c.state = state;
			c.dateTime = "2018-12-29 23:21:11:308";
		}).build();
	}
	
	@Test
	public void marshallTest() {
		MachineContext context = retrieveContext(null);
		String cStr = this.contextService.marshall(context);
		
		assertThat(cStr, is(contextStr));
	}
	
	@Test
	public void unmarshallTest() {
		MachineContext expectedContext = retrieveContext(null);
		MachineContext context = this.contextService.unmarshall(contextStr);
		LOGGER.info("unmarshallTest :: context -> " + context);
		assertThat(context.getId(), is(expectedContext.getId()));
		assertThat(context.getItemCount(), is(expectedContext.getItemCount()));
		assertThat(context.getBalance(), is(expectedContext.getBalance()));
		assertThat(context.getIncome(), is(expectedContext.getIncome()));
		assertThat(context.getDateTime(), is(expectedContext.getDateTime()));
		assertThat(((NoQuarterState) context.getState()).getValue(), is(((NoQuarterState) expectedContext.getState()).getValue()));
		assertThat(((NoQuarterState) context.getState()).getStatus(), is(((NoQuarterState) expectedContext.getState()).getStatus()));
		assertThat(((NoQuarterState) context.getState()).getMachineContext(), is(((NoQuarterState) expectedContext.getState()).getMachineContext()));
	}
	
	@Test
	public void saveTest() {
		save(null);
		this.contextService.deleteAll();
		assertThat(this.contextService.retrieveSize(), is(0));
	}
	
	private void save(Integer key) {
		MachineContext context = retrieveContext(key);
		if (key == null) {
			key = this.contextService.retrieveNextKey();
		}
		Boolean result = this.contextService.save(key, context);
		
		assertThat(result, is(true));
	}
	
	@Test
	public void deleteByIdTest() {
		save(this.contextService.retrieveNextKey());
		save(this.contextService.retrieveNextKey());
		assertThat(this.contextService.retrieveSize(), is(2));
		this.contextService.deleteById(2);
		assertThat(this.contextService.retrieveSize(), is(1));
		this.contextService.deleteAll();
		assertThat(this.contextService.retrieveSize(), is(0));
	}
	
	@Test
	public void retrieveAllTest() {
		for(int i = 0; i < 60; i++) {
			save(this.contextService.retrieveNextKey());
		}
		assertThat(this.contextService.retrieveSize(), is(60));
		List<MachineContext> contexts = this.contextService.retrieveAll();
		assertThat(contexts.size(), is(60));
		this.contextService.deleteAll();
		assertThat(this.contextService.retrieveSize(), is(0));
	}
	
	@Test
	public void retrieveByIdTest() {
		for(int i = 0; i < 60; i++) {
			save(this.contextService.retrieveNextKey());
		}
		assertThat(this.contextService.retrieveSize(), is(60));
		MachineContext context = this.contextService.retrieveById("20");
		assertThat(context.getId(), is(20));
		assertThat(context.getBalance(), is(0.0));
		assertThat(context.getIncome(), is(0.0));
		assertThat(context.getItemCount(), is(0));
		assertThat(context.getDateTime(), is("2018-12-29 23:21:11:308"));
		assertThat(context.getState().getStatus(), is(States.NO_QUARTER.toString()));
		this.contextService.deleteAll();
		assertThat(this.contextService.retrieveSize(), is(0));
	}
	
	@Test
	public void retrieveLatestTest() {
		MachineContext context = this.contextService.retrieveLatestContext();
		assertThat(context, notNullValue());
		assertThat(context.getId(), is(1));
		assertThat(context.getBalance(), is(0.0));
		assertThat(context.getIncome(), is(0.0));
		assertThat(context.getItemCount(), is(0));
		assertThat(context.getDateTime(), notNullValue());
		assertThat(context.getState().getStatus(), is(States.NO_QUARTER.toString()));
		
		for(int i = 0; i < 60; i++) {
			save(this.contextService.retrieveNextKey());
		}
		assertThat(this.contextService.retrieveSize(), is(60));
		MachineContext latest = this.contextService.retrieveLatestContext();
		assertThat(latest.getId(), is(60));
		assertThat(latest.getBalance(), is(0.0));
		assertThat(latest.getIncome(), is(0.0));
		assertThat(latest.getItemCount(), is(0));
		assertThat(latest.getDateTime(), is("2018-12-29 23:21:11:308"));
		assertThat(latest.getState().getStatus(), is(States.NO_QUARTER.toString()));
		
		this.contextService.deleteAll();
		assertThat(this.contextService.retrieveSize(), is(0));
	}
	
	@Test 
	public void incrementItemCountAndSaveTest() {
		MachineContext latestContext = this.contextService.retrieveLatestContext();
		Integer key = this.itemService.retrieveNextKey();
		Item item = new ItemBuilder().with( i -> {
			i.id = key;
		}).build();
		this.itemService.save(key, item);
		this.contextService.incrementItemCountAndSave(latestContext);
		MachineContext latestContext2 = this.contextService.retrieveLatestContext();
		assertThat(latestContext.getItemCount(), is(0));
		assertThat(latestContext2.getItemCount(), is(1));
		
		this.itemService.deleteAll();
		this.contextService.deleteAll();
		assertThat(this.contextService.retrieveSize(), is(0));
	}
	
	@Test 
	public void decrementItemCountAndSaveTest() {
		for(int i = 0; i < 10; i++) {
			MachineContext latestContext = this.contextService.retrieveLatestContext();
			Integer key = this.itemService.retrieveNextKey();
			Item item = new ItemBuilder().with(itm -> {
				itm.id = key;
				itm.price = 1.25;
				itm.type = "Soda";
			}).build();
			this.itemService.save(key, item);
			this.contextService.incrementItemCountAndSave(latestContext);
			LOGGER.info("decrementItemCountAndSaveTest ::: latestContext => " + latestContext);
		}
		assertThat(this.contextService.retrieveSize(), is(10));
		MachineContext latestContext = this.contextService.retrieveLatestContext();
		this.itemService.deleteById(10);
		this.contextService.decrementItemCountAndSave(latestContext);
		MachineContext latestContext2 = this.contextService.retrieveLatestContext();
		assertThat(latestContext.getItemCount(), is(10));
		assertThat(latestContext2.getItemCount(), is(9));
		
		this.itemService.deleteAll();
		this.contextService.deleteAll();
		assertThat(this.contextService.retrieveSize(), is(0));
	}
	
	@Test 
	public void decrementItemCountIncreaseIncomeResetBalanceAndSaveTest() {
		
		Integer key = this.contextService.retrieveNextKey();
		MachineContext lContext = this.contextService.retrieveLatestContext();
		MachineContext nextContext = new MachineContextBuilder()
				.with(c -> {
					c.id = key;
					c.itemCount = lContext.getItemCount() + 1;
					c.income = lContext.getIncome();
					c.state = state;
					c.balance = lContext.getBalance() + 1.25;
					c.dateTime = VendingUtils.retrieveNow();
				}).build();
		
		this.contextService.save(key, nextContext);

		assertThat(this.contextService.retrieveSize(), is(1));
		
		MachineContext latestContext = this.contextService.retrieveLatestContext();
		this.contextService.decrementItemCountIncreaseIncomeResetBalanceAndSave(latestContext, state, 1.25);
		MachineContext latestContext2 = this.contextService.retrieveLatestContext();
		assertThat(latestContext.getItemCount(), is(1));
		assertThat(latestContext.getIncome(), is(0.0));
		assertThat(latestContext.getBalance(), is(1.25));
		
		assertThat(latestContext2.getItemCount(), is(0));
		assertThat(latestContext2.getIncome(), is(1.25));
		assertThat(latestContext2.getBalance(), is(0.0));
		
		this.contextService.deleteAll();
		assertThat(this.contextService.retrieveSize(), is(0));
	}
	
	@Test
	public void updateBalanceSaveContextTest() {
		MachineContext latestContext = this.contextService.retrieveLatestContext();
		this.contextService.updateBalanceSaveContext(latestContext, null, 1.25);
		MachineContext latestContext2 = this.contextService.retrieveLatestContext();
		
		assertThat(latestContext.getBalance(), is(0.0));
		assertThat(latestContext2.getBalance(), is(1.25));
		
		this.contextService.deleteAll();
		assertThat(this.contextService.retrieveSize(), is(0));
	}
}

