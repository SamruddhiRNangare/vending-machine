package com.solutions.vendingmachine.model.state;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.solutions.vendingmachine.constants.States;
import com.solutions.vendingmachine.model.Item;
import com.solutions.vendingmachine.model.MachineContext;

@Component
@Scope("prototype")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class HasQuarterState extends ItemContextState {
	
	@Autowired
	private SoldState soldState;
	
	@Autowired
	private NoQuarterState noQuarterState;
	
	private static final long serialVersionUID = -7197850239735267987L;
	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
	private double value;
	
	@JsonCreator
	public HasQuarterState(@JsonProperty("machineContext") MachineContext machineContext, 
						   @JsonProperty("item") Item item, 
						   @JsonProperty("value") double value) {
		super(machineContext, States.HAS_QUARTER.toString(), item);
		this.value = value;
	}

	@Override
	public void doAction() {
		if (machineContext != null) {
			if (item != null && item.getPrice() > 0) {
				if (machineContext.getBalance() >= item.getPrice()) {
					soldState.setMachineContext(machineContext);
					soldState.setItem(item);
					soldState.doAction();
				} else {
					contextService.updateBalanceSaveContext(machineContext, this, value);
				}
			} else {
				if (value > 0) {
					contextService.updateBalanceSaveContext(machineContext, this, value);
				} else {
					noQuarterState.setMachineContext(machineContext);
					noQuarterState.setValue(value);
					noQuarterState.doAction();
				}
				
			}
		}
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "HasQuarterState [value=" + value + ", item=" + item + ", machineContext=" + machineContext + ", status="
				+ status + "]";
	}

}
