package com.solutions.vendingmachine.service;

import com.solutions.vendingmachine.model.MachineContext;

public interface StateService {

	public MachineContext insertCoin(double value) throws Exception;
	public MachineContext removeCoin(double value) throws Exception;
	public MachineContext selectItem(String itemId) throws Exception;
	public MachineContext dispenseItem(String itemId) throws Exception;
}
