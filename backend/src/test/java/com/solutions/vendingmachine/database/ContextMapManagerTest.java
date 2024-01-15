package com.solutions.vendingmachine.database;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.solutions.vendingmachine.Utils.VendingUtils;
import com.solutions.vendingmachine.constants.States;
import com.solutions.vendingmachine.model.MachineContext;
import com.solutions.vendingmachine.model.builder.MachineContextBuilder;
import com.solutions.vendingmachine.model.state.NoQuarterState;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(locations="classpath:backend-test.properties")
public class ContextMapManagerTest {

	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private DatabaseManager dbManager;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	private ContextMapManager mapManager;
	
	@Before
	public void setup() {
		this.mapManager = new ContextMapManager(dbManager);
	}
	
	@Test
	public void getMapName() {
		assertThat(this.mapManager, notNullValue());
		assertThat(this.mapManager.mapName, is("context"));
	}
	
	@Test
	public void insertValue() {
		LOGGER.debug("insertValue");
		this.insertValue2();
		this.mapManager.deleteAll();
		assertThat(this.mapManager.getSize(), is(0));
	}
	
	public void insertValue2() {
		LOGGER.debug("insertValue2");
		assertThat(this.mapManager, notNullValue());
		int key  = this.mapManager.getSize() + 1;
		MachineContext context = new MachineContextBuilder().with(c -> {
			c.id = 1;
			c.balance = 0;
			c.income = 0;
			c.itemCount = 0;
			c.state = new NoQuarterState(null, 0);
			c.dateTime = VendingUtils.retrieveNow();
		}).build();
		
		String value = "";
		try {
			value = objectMapper.writeValueAsString(context);
			LOGGER.debug(value);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		this.mapManager.insert(key, value);
		assertThat(this.mapManager.getSize(), is(1));
	}
	
	@Test
	public void getValue() {
		LOGGER.debug("getValue");
		this.insertValue2();
		LOGGER.debug("getKeys => " + this.mapManager.getAllKeys());
		LOGGER.debug("getValues => " + this.mapManager.getAllValues());
		String getValue = this.mapManager.get(1);
		LOGGER.debug("getValue => " + getValue);
		assertThat(getValue, notNullValue());
		try {
			MachineContext getContext = objectMapper.readValue(getValue, MachineContext.class);
			assertThat(getContext, notNullValue());
			assertThat(getContext.getId(), is(1));
			assertThat(getContext.getBalance(), is(0.0));
			assertThat(getContext.getIncome(), is(0.0));
			assertThat(getContext.getItemCount(), is(0));
			assertThat(getContext.getDateTime(), notNullValue());
			assertThat(getContext.getState().getStatus(), is(States.NO_QUARTER.toString()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.mapManager.deleteAll();
		assertThat(this.mapManager.getSize(), is(0));
	
	}
	
	@Test
	public void deleteValue() {
		LOGGER.debug("deleteValue");
		this.insertValue2();
		this.mapManager.delete(1);
		assertThat(this.mapManager.getSize(), is(0));
	}
}
