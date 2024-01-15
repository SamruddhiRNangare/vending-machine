package com.solutions.vendingmachine.model.state;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.solutions.vendingmachine.constants.States;
import com.solutions.vendingmachine.model.MachineContext;

@Component
@Scope("prototype")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class NoQuarterState extends AbstractState {
	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
	
	private static final long serialVersionUID = -1601004309718352838L;
	private double value;
	
	@JsonCreator
	public NoQuarterState(@JsonProperty("machineContext") MachineContext machineContext, @JsonProperty("value") double value) {
		super(machineContext, States.NO_QUARTER.toString());
		this.value = value;
	}

	@Override
	public void doAction() {
//		LOGGER.info("NoQuarterState doAction");
		if (machineContext != null) {
			this.contextService.updateBalanceSaveContext(machineContext, this, value);
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
		return "NoQuarterState [value=" + value + ", machineContext=" + machineContext + ", status=" + status + "]";
	}
	
	

}
