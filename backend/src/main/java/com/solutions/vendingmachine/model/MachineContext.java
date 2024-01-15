package com.solutions.vendingmachine.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.solutions.vendingmachine.model.state.State;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MachineContext implements Serializable {

	private static final long serialVersionUID = -5176899727199788133L;
	
	private final Integer id;
	private final int itemCount;
	private final double balance; // current balance of money entered by user. may be returned
	private final double income; // balance of money saved from dispensed item.
	private final String dateTime;
	private final State state;
	
	@JsonCreator
	public MachineContext(@JsonProperty("id") Integer id, @JsonProperty("itemCount") int itemCount, 
						  @JsonProperty("balance") double balance, 
						  @JsonProperty("income") double income, 
						  @JsonProperty("dateTime")  
						  String dateTime, 
						  @JsonProperty("state") State state) {
		this.id = id;
		this.itemCount = itemCount;
		this.balance = balance;
		this.income = income;
		this.dateTime = dateTime;
		this.state = state;
	}
	
	public State getState() {
		return state;
	}
	
	public Integer getId() {
		return id;
	}

	public int getItemCount() {
		return itemCount;
	}

	public double getBalance() {
		return balance;
	}

	public double getIncome() {
		return income;
	}

	public String getDateTime() {
		return dateTime;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(balance);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((dateTime == null) ? 0 : dateTime.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		temp = Double.doubleToLongBits(income);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + itemCount;
		result = prime * result + ((state == null) ? 0 : state.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MachineContext other = (MachineContext) obj;
		if (Double.doubleToLongBits(balance) != Double.doubleToLongBits(other.balance))
			return false;
		if (dateTime == null) {
			if (other.dateTime != null)
				return false;
		} else if (!dateTime.equals(other.dateTime))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (Double.doubleToLongBits(income) != Double.doubleToLongBits(other.income))
			return false;
		if (itemCount != other.itemCount)
			return false;
		if (state == null) {
			if (other.state != null)
				return false;
		} else if (!state.equals(other.state))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "MachineContext [id=" + id + ", itemCount=" + itemCount + ", balance=" + balance + ", income=" + income
				+ ", dateTime=" + dateTime + ", state=" + state + "]";
	}

	
}
