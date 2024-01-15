package com.solutions.vendingmachine.model.state;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.solutions.vendingmachine.model.MachineContext;
import com.solutions.vendingmachine.service.MachineContextService;

public abstract class AbstractState implements State, Serializable {

	private static final long serialVersionUID = -6963725310939332357L;
	@Autowired
	protected MachineContextService contextService;
	
	protected MachineContext machineContext;
	protected String status;
	
	@JsonCreator
	public AbstractState(@JsonProperty("machineContext") MachineContext machineContext,
						 @JsonProperty("status") String status) {
		this.machineContext = machineContext;
		this.status = status;
	}

	public MachineContext getMachineContext() {
		return machineContext;
	}

	public void setMachineContext(MachineContext machineContext) {
		this.machineContext = machineContext;
	}
	
	@Override
	public String getStatus() {
		return status;
	}
	
}
