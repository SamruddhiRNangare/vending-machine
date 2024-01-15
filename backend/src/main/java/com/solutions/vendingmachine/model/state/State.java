package com.solutions.vendingmachine.model.state;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
	    use = JsonTypeInfo.Id.NAME,
	    include = JsonTypeInfo.As.PROPERTY,
	    property = "type")
@JsonSubTypes({
    @Type(value = AbstractState.class, name = "AbstractState"),
    @Type(value = ItemContextState.class, name = "ItemContextState"),
    @Type(value = NoQuarterState.class, name = "NoQuarterState"),
    @Type(value = HasQuarterState.class, name = "HasQuarterState"),
    @Type(value = SoldOutState.class, name = "SoldOutState"),
    @Type(value = SoldState.class, name = "SoldState")})
public interface State {
	public void doAction();
	public String getStatus();
}
