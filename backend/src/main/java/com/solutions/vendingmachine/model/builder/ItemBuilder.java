package com.solutions.vendingmachine.model.builder;

import java.util.function.Consumer;

import com.solutions.vendingmachine.model.Item;

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

public class ItemBuilder {
	public Integer id;
	public String type;
	public double price;
	public String insertDateTime;
	public String purchaseDateTime;
	
	public ItemBuilder with(
            Consumer<ItemBuilder> builderFunction) {
        builderFunction.accept(this);
        return this;
    }
	
	public Item build() {
		return new Item(this.id, this.type, this.price, 
				this.insertDateTime, this.purchaseDateTime);
	}
}
