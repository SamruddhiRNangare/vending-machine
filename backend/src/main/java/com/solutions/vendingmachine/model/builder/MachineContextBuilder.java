package com.solutions.vendingmachine.model.builder;

import java.util.function.Consumer;

import com.solutions.vendingmachine.model.MachineContext;
import com.solutions.vendingmachine.model.state.State;

/**
 * Use example
 * Person person = new PersonBuilder()
    .with(personBuilder -> {
        personBuilder.salutation = "Mr.";
        personBuilder.firstName = "John";
        personBuilder.lastName = "Doe";
        personBuilder.isFemale = false;
    })
    .build();
 */

public class MachineContextBuilder {

	public Integer id;
	public int itemCount;
	public double balance;
	public double income;
	public String dateTime;
	public State state;

	public MachineContextBuilder with(
            Consumer<MachineContextBuilder> builderFunction) {
        builderFunction.accept(this);
        return this;
    }
	
	public MachineContext build() {
		return new MachineContext(this.id, this.itemCount, this.balance, 
								  this.income, this.dateTime, this.state);
	}
}
