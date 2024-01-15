package com.solutions.vendingmachine.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Item implements Serializable {

	private static final long serialVersionUID = -1188507791924300971L;
	
	private final Integer id;
	private final String type;
	private final double price;
	private final String insertDateTime;
	private final String purchaseDateTime;
	
	@JsonCreator
	public Item (@JsonProperty("id") Integer id, 
				 @JsonProperty("type") String type, 
				 @JsonProperty("price") double price, 
				 @JsonProperty("insertDateTime") String insertDateTime, 
				 @JsonProperty("purchaseDateTime") String purchaseDateTime) {
		this.id = id;
		this.type = type;
		this.price = price;
		this.insertDateTime = insertDateTime;
		this.purchaseDateTime = purchaseDateTime;
	}

	public String getType() {
		return type;
	}

	public double getPrice() {
		return price;
	}

	public Integer getId() {
		return id;
	}

	public String getInsertDateTime() {
		return insertDateTime;
	}

	public String getPurchaseDateTime() {
		return purchaseDateTime;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((insertDateTime == null) ? 0 : insertDateTime.hashCode());
		long temp;
		temp = Double.doubleToLongBits(price);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((purchaseDateTime == null) ? 0 : purchaseDateTime.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		Item other = (Item) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (insertDateTime == null) {
			if (other.insertDateTime != null)
				return false;
		} else if (!insertDateTime.equals(other.insertDateTime))
			return false;
		if (Double.doubleToLongBits(price) != Double.doubleToLongBits(other.price))
			return false;
		if (purchaseDateTime == null) {
			if (other.purchaseDateTime != null)
				return false;
		} else if (!purchaseDateTime.equals(other.purchaseDateTime))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Item [id=" + id + ", type=" + type + ", price=" + price + ", insertDateTime=" + insertDateTime
				+ ", purchaseDateTime=" + purchaseDateTime + "]";
	}


	
}
