package com.solutions.vendingmachine.model.state;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.solutions.vendingmachine.Utils.VendingUtils;
import com.solutions.vendingmachine.constants.States;
import com.solutions.vendingmachine.model.MachineContext;
import com.solutions.vendingmachine.model.builder.MachineContextBuilder;

@Component
@Scope("prototype")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SoldOutState extends AbstractState {

	private static final long serialVersionUID = 3604773493394633264L;

	@JsonCreator
	public SoldOutState(@JsonProperty("machineContext") MachineContext machineContext) {
		super(machineContext, States.SOLD_OUT.toString());
	}

	@Override
	public void doAction() {
		Integer key = this.contextService.retrieveNextKey();
		MachineContext nextContext = new MachineContextBuilder().with(c -> {
			c.id = key;
			c.itemCount = 0;
			c.balance = machineContext.getBalance();
			c.income = machineContext.getIncome();
			c.dateTime = VendingUtils.retrieveNow();
			c.state = this;
		}).build();
		
		this.contextService.save(key, nextContext);
		
	}

	@Override
	public String toString() {
		return "SoldOutState [machineContext=" + machineContext + ", status=" + status + "]";
	}
	
}
