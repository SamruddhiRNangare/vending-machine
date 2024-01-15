package com.solutions.vendingmachine.database;

import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("!test")
public class DatabaseManager implements CommandLineRunner {

	private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseManager.class);
	
	private DB database;
	
	@Value("${backend.testing}")
	private boolean testing;
	
	@Override
	public void run(String... args) throws Exception {
		if (!testing) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(("Bootstraping DatabaseManager... "));
			}
			this.database = DBMaker.fileDB("vending.db").closeOnJvmShutdown().transactionEnable().make();

		}
	}

	public DB getDatabase() {
		return database;
	}
	
	public void setDabtabase(DB database) {
		this.database = database;
	}
}
