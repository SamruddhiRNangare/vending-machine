package com.solutions.vendingmachine.model.state;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

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
public class SoldState extends ItemContextState {

	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
	
	private static final long serialVersionUID = 5926865510114355800L;
	@Autowired
	private NoQuarterState noQuarterState;
	@Autowired
	private SoldOutState soldOutState;
	
	public SoldState(@JsonProperty("machineContext") MachineContext machineContext, 
					 @JsonProperty("item") Item item) {
		super(machineContext, States.SOLD.toString(), item);
	}

	@Override
	public void doAction() {
		if (machineContext != null && item != null) {
//			LOGGER.info("SoldState doAction");
			this.itemService.sellItem(item);
			this.contextService.decrementItemCountIncreaseIncomeResetBalanceAndSave(machineContext, this, item.getPrice());
			MachineContext latestContext = this.contextService.retrieveLatestContext();
//			LOGGER.info("SoldState latestContext -> " + latestContext);
			if (latestContext.getBalance() == 0.0) {
				noQuarterState.setMachineContext(latestContext);
				noQuarterState.setValue(0.0);
				noQuarterState.doAction();
			}
			
			if (this.itemService.retrieveSize() == 0) {
				soldOutState.setMachineContext(this.contextService.retrieveLatestContext());
				soldOutState.doAction();
			}
		}
		
	}

	@Override
	public String toString() {
		return "SoldState [item=" + item + ", machineContext=" + machineContext + ", status=" + status + "]";
	}
}
