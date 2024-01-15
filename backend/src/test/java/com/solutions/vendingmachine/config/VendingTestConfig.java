package com.solutions.vendingmachine.config;

import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import com.solutions.vendingmachine.database.DatabaseManager;

@Configuration
@Profile("test")
public class VendingTestConfig {

	@Bean
	@Primary
	public DatabaseManager getDatabaseManager() {
		DatabaseManager dbManager = new DatabaseManager();
		DB db = DBMaker.memoryDB().make();
		dbManager.setDabtabase(db);
		return dbManager;
	}
	
}
