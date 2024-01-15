package com.solutions.vendingmachine.database;

import org.springframework.stereotype.Repository;

@Repository
public class ContextMapManager extends AbstractMapManager {

	private static final String MAP_NAME = "context";
	
	public ContextMapManager(DatabaseManager dbManager) {
		super(dbManager, MAP_NAME);
	}

}
