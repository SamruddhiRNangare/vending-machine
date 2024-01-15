package com.solutions.vendingmachine.model.rest;

import com.solutions.vendingmachine.model.MachineContext;
import com.solutions.vendingmachine.model.state.State;

public class ErrorContext extends MachineContext {

	private static final long serialVersionUID = -4325046440692711868L;
	
	private String error;
	
	public ErrorContext(Integer id, int itemCount, double balance, 
						double income, String dateTime, 
						State state, String error) {
		super(id, itemCount, balance, income, dateTime, state);
		this.error = error;
	}

	public ErrorContext(MachineContext latestContext, String error) {
		super(latestContext.getId(), latestContext.getItemCount(), 
				latestContext.getBalance(), latestContext.getIncome(), 
				latestContext.getDateTime(), latestContext.getState());
		this.error = error;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	@Override
	public String toString() {
		return "ErrorContext [error=" + error + ", getState()=" + getState() + ", getId()=" + getId()
				+ ", getItemCount()=" + getItemCount() + ", getBalance()=" + getBalance() + ", getIncome()="
				+ getIncome() + ", getDateTime()=" + getDateTime() + "]";
	}


	
	
	
}
