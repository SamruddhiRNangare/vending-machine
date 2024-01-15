package com.solutions.vendingmachine.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.solutions.vendingmachine.Utils.VendingUtils;
import com.solutions.vendingmachine.database.ItemMapManager;
import com.solutions.vendingmachine.model.Item;
import com.solutions.vendingmachine.model.builder.ItemBuilder;

@Service
public class MachineItemService implements MachineService<Item> {

	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
	
	private ItemMapManager itemManager;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	public MachineItemService(ItemMapManager itemManager){
		this.itemManager = itemManager;
	}


	@Override
	public String marshall(Item obj) {
		String result = null;
		if (obj != null) {
			try {
				result = this.objectMapper.writeValueAsString(obj);
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				LOGGER.error(e.getMessage());
			}
		}
		return result;
	}

	@Override
	public Item unmarshall(String objStr) {
		Item result = null;
		if (objStr != null) {
			try {
				result = this.objectMapper.readValue(objStr, Item.class);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				LOGGER.error(e.getMessage());
			}
		}
		return result;
	}

	@Override
	public Item retrieveById(String id) {
		String itemStr = this.itemManager.get(Integer.valueOf(id));
		return this.unmarshall(itemStr);
	}

	@Override
	public List<Item> retrieveAll() {
		List<Item> result = null;
		List<String> list = this.itemManager.getAllValues();
//		LOGGER.info("retrieveAll ::: list => " + list);
		if (list != null && !list.isEmpty()) {
			result = new ArrayList<Item>();
			for(String itemStr: list) {
				Item item = this.unmarshall(itemStr);
				result.add(item);
			}
		}
		return result;
	}

	@Override
	public Integer retrieveNextKey() {
//		int size = this.itemManager.getSize();
		int key = 1;
		String itemStr = this.itemManager.getLatestValue();
//		LOGGER.info("retrieveNextKey :: itemStr => " + itemStr);
			if (itemStr != null) {
			Item item = this.unmarshall(itemStr);
			if (item != null && item.getId() != null) {
				key = item.getId() + 1;
			}
		}

		return key;
	}

	@Override
	public Boolean deleteById(Integer key) {
		return new Boolean(this.itemManager.delete(key));
	}

	@Override
	public Boolean deleteAll() {
		this.itemManager.deleteAll();
		return this.retrieveSize() == 0;
	}


	@Override
	public Boolean save(Integer key, Item obj) {
		int size = this.retrieveSize();
		Item item = null;
		if (obj != null && obj.getId() == null) {
			item = new ItemBuilder().with( i -> {
				i.id = key;
				i.type = obj.getType();
				i.price = obj.getPrice();
				i.insertDateTime = VendingUtils.retrieveNow();
				i.purchaseDateTime = obj.getPurchaseDateTime();
			}).build();
		}
		if (item == null) {
			this.itemManager.insert(key, this.marshall(obj));
		} else {
			this.itemManager.insert(key, this.marshall(item));
		}
		
		int size2 = this.retrieveSize();

		return new Boolean(size < size2);
	}
	
	public Boolean sellItem(Item obj) {
		int size = this.retrieveSize();
		
		Item purchasedItem = new ItemBuilder().with(i -> {
			i.id = obj.getId();
			i.type = obj.getType();
			i.price = obj.getPrice();
			i.insertDateTime = obj.getInsertDateTime();
			i.purchaseDateTime = VendingUtils.retrieveNow();
		}).build();
		
		this.itemManager.update(purchasedItem.getId(), this.marshall(purchasedItem));
		int size2 = this.retrieveSize();
		return new Boolean(size2 < size);
	}
	// Only returns none purchased items
	@Override
	public int retrieveSize() {
		int result = 0;
		List<Item> items = this.retrieveAll();
		
		if (items != null) {
//			LOGGER.info("retrieveSize => " + items.size());
			List<Item> filteredItems = items.stream()
					.filter(item -> item.getPurchaseDateTime() == null).collect(Collectors.toList());
			result = filteredItems.size();
		}
		return result;
	}
	
	public int retrieveMapSize() {
		return this.itemManager.getSize();
	}
}
