package com.solutions.vendingmachine.service;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import com.solutions.vendingmachine.model.Item;
import com.solutions.vendingmachine.model.builder.ItemBuilder;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(locations="classpath:backend-test.properties")
public class MachineItemServiceTest {

	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private MachineItemService itemService;
	
	private final String itemStr = "{\"id\":1,\"type\":\"Soda\",\"price\":1.25,\"insertDateTime\":\"2018-12-30 14:41:35:273\"}";
	
	private Item retrieveItem(Integer key) {
		return new ItemBuilder().with(i -> {
			i.id = key != null ? key: 1;
			i.type = "Soda";
			i.price = 1.25;
			i.insertDateTime = "2018-12-30 14:41:35:273";
			i.purchaseDateTime = null;
		}).build();
	}
	
	private void save(Integer key) {
		Item item = retrieveItem(key);
		if (key == null) {
			key = this.itemService.retrieveNextKey();
		}
		Boolean result = this.itemService.save(key, item);
		
		assertThat(result, is(true));
	}
	
	@Test
	public void marshallTest() {
		Item item = retrieveItem(null);
		String iStr = this.itemService.marshall(item);
		LOGGER.debug(itemStr);
		assertThat(itemStr, is(iStr));
	}
	
	@Test
	public void unmarshallTest() {
		Item expectedItem = retrieveItem(null);
		Item item = this.itemService.unmarshall(itemStr);
		
		assertThat(item.equals(expectedItem), is(true));
	}
	
	@Test
	public void saveTest() {
		save(null);
		this.itemService.deleteAll();
		assertThat(this.itemService.retrieveSize(), is(0));
	}
	
	@Test
	public void retrieveByIdTest() {
		for(int i = 0; i < 60; i++) {
			save(this.itemService.retrieveNextKey());
		}
		assertThat(this.itemService.retrieveSize(), is(60));
		Item item = this.itemService.retrieveById("20");
		assertThat(item.getId(), is(20));
		assertThat(item.getType(), is("Soda"));
		assertThat(item.getInsertDateTime(), is ("2018-12-30 14:41:35:273"));
		assertThat(item.getPrice(), is(1.25));
		assertThat(item.getPurchaseDateTime(), nullValue());
		
		
		this.itemService.deleteAll();
		assertThat(this.itemService.retrieveSize(), is(0));
	}
	
	
	@Test
	public void retrieveAllTest() {
		for(int i = 0; i < 60; i++) {
			save(this.itemService.retrieveNextKey());
		}
		assertThat(this.itemService.retrieveSize(), is(60));
		List<Item> contexts = this.itemService.retrieveAll();
		assertThat(contexts.size(), is(60));
		
		this.itemService.deleteAll();
		assertThat(this.itemService.retrieveSize(), is(0));
	}
	
	@Test
	public void deleteByIdTest() {
		save(this.itemService.retrieveNextKey());
		save(this.itemService.retrieveNextKey());
		assertThat(this.itemService.retrieveSize(), is(2));
		this.itemService.deleteById(2);
		assertThat(this.itemService.retrieveSize(), is(1));
		
		this.itemService.deleteAll();
		assertThat(this.itemService.retrieveSize(), is(0));
	}
	
	@Test
	public void sellItemTest() {
		for(int i = 0; i < 60; i++) {
			save(this.itemService.retrieveNextKey());
		}
		assertThat(this.itemService.retrieveSize(), is(60));
		Item item = this.itemService.retrieveById("25");
		LOGGER.debug("1 item => " + item);
		Boolean hasSold = this.itemService.sellItem(item);
		Item soldItem = this.itemService.retrieveById("25");
		LOGGER.debug("2 item => " + soldItem);
		assertThat(hasSold, is(true));
		assertThat(this.itemService.retrieveSize(), is(59));
		assertThat(this.itemService.retrieveMapSize(), is(60));
		
		assertThat(soldItem.getId(), is(25));
		assertThat(soldItem.getType(), is("Soda"));
		assertThat(soldItem.getInsertDateTime(), is ("2018-12-30 14:41:35:273"));
		assertThat(soldItem.getPrice(), is(1.25));
		assertThat(soldItem.getPurchaseDateTime(), notNullValue());
		
		this.itemService.deleteAll();
		assertThat(this.itemService.retrieveSize(), is(0));
	}
	
}
