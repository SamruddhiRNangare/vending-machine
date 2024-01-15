package com.solutions.vendingmachine.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.solutions.vendingmachine.constants.States;
import com.solutions.vendingmachine.errors.VendingException;
import com.solutions.vendingmachine.model.Item;
import com.solutions.vendingmachine.model.MachineContext;
import com.solutions.vendingmachine.model.state.HasQuarterState;

@Service
public class MachineStateService implements StateService {

	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private MachineContextService contextService;
	
	@Autowired
	private MachineItemService itemService;
	
	@Autowired
	private HasQuarterState hasQuarterState;
	
	@Override
	public MachineContext insertCoin(double value) throws VendingException {
		MachineContext result = null;
		MachineContext latestContext = this.contextService.retrieveLatestContext();
		if (value > 0 && value != .25) {
			throw new VendingException("This machine only accepts quarters.");
		}
		if (latestContext != null) {
			hasQuarterState.setMachineContext(latestContext);
			hasQuarterState.setValue(value);
			hasQuarterState.setItem(null);
			hasQuarterState.doAction();
			
			result = this.contextService.retrieveLatestContext();
		}
		return result;
	}

	@Override
	public MachineContext removeCoin(double value) throws VendingException {
		MachineContext result = null;
		MachineContext latestContext = this.contextService.retrieveLatestContext();
		if (latestContext != null) {
			if (value > latestContext.getBalance()) {
				throw new VendingException("Invalid refund.");
			}
			hasQuarterState.setMachineContext(latestContext);
			hasQuarterState.setValue(value);
			hasQuarterState.setItem(null);
			hasQuarterState.doAction();
			
			result = this.contextService.retrieveLatestContext();
		}
		return result;
	}

	@Override
	public MachineContext selectItem(String itemId) throws VendingException {
		MachineContext result = null;
		MachineContext latestContext = this.contextService.retrieveLatestContext();
		if (latestContext != null && !latestContext.getState().equals(States.SOLD_OUT.toString())) {
			Item item = this.itemService.retrieveById(itemId);
			if (item != null && latestContext.getBalance() > 0) {
				hasQuarterState.setMachineContext(latestContext);
				hasQuarterState.setItem(item);
				hasQuarterState.setValue(0);
				hasQuarterState.doAction();
			} else {
				if (item == null) {
					throw new VendingException("Invalid ItemId: " + itemId);
				} else if (latestContext.getBalance() <= 0) {
					throw new VendingException("Insert money to purchase item. ItemId: " + itemId);
				}
				
			}
		}
		result = this.contextService.retrieveLatestContext();
		return result;
	}

	@Override
	public MachineContext dispenseItem(String itemId) throws VendingException {
		// TODO Can't dispense Item without selecting it unless there is a giveaway or contest
		return null;
	}

}
