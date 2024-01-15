package com.solutions.vendingmachine.errors;

public class VendingException extends Exception {

	private static final long serialVersionUID = -272160890920002615L;

	public VendingException(String errorMsg) {
		super(errorMsg);
	}
	
	public VendingException(String errorMsg, Throwable err) {
		super(errorMsg, err);
	}
}
