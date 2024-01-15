package com.solutions.vendingmachine.model.state;

import org.springframework.beans.factory.annotation.Autowired;

import com.solutions.vendingmachine.model.Item;
import com.solutions.vendingmachine.model.MachineContext;
import com.solutions.vendingmachine.service.MachineItemService;

public abstract class ItemContextState extends AbstractState {

	private static final long serialVersionUID = 7582503812267265840L;
	
	@Autowired
	protected MachineItemService itemService;
	
	protected Item item;
	
	public ItemContextState(MachineContext machineContext, String status, Item item) {
		super(machineContext, status);
		this.item = item;
	}

	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}
	
	
}
