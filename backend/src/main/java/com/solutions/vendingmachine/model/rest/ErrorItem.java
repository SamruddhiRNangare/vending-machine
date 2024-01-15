package com.solutions.vendingmachine.model.rest;

import com.solutions.vendingmachine.model.Item;

public class ErrorItem extends Item {

	private static final long serialVersionUID = 6414716921959388873L;
	private String error;
	
	public ErrorItem(Integer id, String type, double price, 
			String insertDateTime, String purchaseDateTime, 
			String error) {
		super(id, type, price, insertDateTime, purchaseDateTime);
		this.error = error;
	}
	
	public ErrorItem(Item item, String error) {
		super(item.getId(), item.getType(), item.getPrice(), 
				item.getInsertDateTime(), item.getPurchaseDateTime());
		this.error = error;
	}

	@Override
	public String toString() {
		return "ErrorItem [error=" + error + "]";
	}

	
}
