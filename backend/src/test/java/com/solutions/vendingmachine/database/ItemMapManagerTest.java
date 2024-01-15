package com.solutions.vendingmachine.database;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
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
import com.solutions.vendingmachine.model.Item;
import com.solutions.vendingmachine.model.builder.ItemBuilder;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(locations="classpath:backend-test.properties")
public class ItemMapManagerTest {

private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private DatabaseManager dbManager;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	private ItemMapManager mapManager;
	
	@Before
	public void setup() {
		this.mapManager = new ItemMapManager(dbManager);
	}
	
	@Test
	public void getMapName() {
		assertThat(this.mapManager, notNullValue());
		assertThat(this.mapManager.mapName, is("items"));
	}
	
	public void insertValue() {
		this.insertValue2();
		this.mapManager.deleteAll();
		assertThat(this.mapManager.getSize(), is(0));
	}
	
	public void insertValue2() {
		assertThat(this.mapManager, notNullValue());
		int key = 1;
		Item item = new ItemBuilder().with(i -> {
			i.id = 1;
			i.price = 1.25;
			i.type = "Soda";
			i.insertDateTime = VendingUtils.retrieveNow();
			i.purchaseDateTime = null;
		}).build();
		
		String value = "";
		try {
			value = objectMapper.writeValueAsString(item);
			LOGGER.debug(value);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		this.mapManager.insert(key, value);
		assertThat(this.mapManager.getSize(), is(1));
	}
	
	@Test
	public void getValue() {
		this.insertValue2();
		String getValue = this.mapManager.get(1);
		assertThat(getValue, notNullValue());
		try {
			Item item = objectMapper.readValue(getValue, Item.class);
			assertThat(item, notNullValue());
			assertThat(item.getId(), is(1));
			assertThat(item.getPrice(), is(1.25));
			assertThat(item.getType(), is("Soda"));
			assertThat(item.getInsertDateTime(), notNullValue());
			assertThat(item.getPurchaseDateTime(), nullValue());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.mapManager.deleteAll();
		assertThat(this.mapManager.getSize(), is(0));
	}
	
	@Test
	public void deleteValue() {
		this.insertValue2();
		this.mapManager.delete(1);
		assertThat(this.mapManager.getSize(), is(0));
	}
	
}
