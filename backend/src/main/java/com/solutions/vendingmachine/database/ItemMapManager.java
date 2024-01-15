package com.solutions.vendingmachine.database;

import org.springframework.stereotype.Repository;

@Repository
public class ItemMapManager extends AbstractMapManager {
	private static final String MAP_NAME = "items";
	
	public ItemMapManager(DatabaseManager dbManager) {
		super(dbManager, MAP_NAME);
	}	
	
}
