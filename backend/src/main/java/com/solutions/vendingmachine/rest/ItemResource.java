package com.solutions.vendingmachine.rest;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.solutions.vendingmachine.Utils.VendingUtils;
import com.solutions.vendingmachine.constants.ResourceConstants;
import com.solutions.vendingmachine.errors.VendingException;
import com.solutions.vendingmachine.model.Item;
import com.solutions.vendingmachine.model.MachineContext;
import com.solutions.vendingmachine.model.builder.ItemBuilder;
import com.solutions.vendingmachine.model.rest.ErrorContext;
import com.solutions.vendingmachine.service.MachineContextService;
import com.solutions.vendingmachine.service.MachineItemService;
import com.solutions.vendingmachine.service.MachineStateService;

@RestController
@RequestMapping(ResourceConstants.MACHINE_ITEMS)
public class ItemResource {

	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private MachineItemService itemService;
	
	@Autowired
	private MachineContextService contextService;
	
	@Autowired
	private MachineStateService machineStateService;
	
	
	@RequestMapping(path = "", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<List<Item>> getItems() {
		List<Item> items = this.itemService.retrieveAll();
		return new ResponseEntity<List<Item>>(items, HttpStatus.OK);
	}
	
	@RequestMapping(path = "/{itemId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Item> getItem(
			@PathVariable(value = "itemId", required = true)
			String itemId) {
		Item item = this.itemService.retrieveById(itemId);
		LOGGER.debug("*** getItem => " + item);
		return new ResponseEntity<Item>(item, HttpStatus.OK);
	}
	
	@RequestMapping(path = "", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<MachineContext> insertItems(@RequestBody List<Item> items) {
		
		if (items == null || items.isEmpty()) {
			MachineContext context = this.contextService.retrieveLatestContext();
			MachineContext errorContext = new ErrorContext(context, "Invalid request: item list is empty.");
			return new ResponseEntity<MachineContext>(errorContext, HttpStatus.BAD_REQUEST);
		}
		
		for(Item item: items) {
			int key = this.itemService.retrieveNextKey();
			Item itm = new ItemBuilder().with(i -> {
				i.id = key;
				i.price = item.getPrice();
				i.type = item.getType();
				i.insertDateTime = VendingUtils.retrieveNow();
			}).build();
			this.itemService.save(key, itm);
			this.contextService.incrementItemCountAndSave(this.contextService.retrieveLatestContext());
		}
		MachineContext context = this.contextService.retrieveLatestContext();
		return new ResponseEntity<MachineContext>(context, HttpStatus.OK);
	}
	
	@RequestMapping(path = "/{itemId}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<MachineContext> updateItem(
			@PathVariable(value = "itemId", required = true) String itemId) {
		// You can only update the item by purchasing it
		MachineContext context;
		try {
			context = machineStateService.selectItem(itemId);
			LOGGER.info("*** updateItem -> context: " + context);
		} catch (VendingException e) {
			LOGGER.error(e.getMessage(), e);
			context = new ErrorContext(this.contextService.retrieveLatestContext(), e.getMessage());
			LOGGER.info("*** ERROR updateItem -> context: " + context);
			return new ResponseEntity<MachineContext>(context, HttpStatus.BAD_REQUEST);
		}
		
		return new ResponseEntity<MachineContext>(context, HttpStatus.OK);
	}
	
	@RequestMapping(path = "/{itemId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<MachineContext> deleteItem(
			@PathVariable(value = "itemId", required = true) String itemId) {
		
		MachineContext latestContext = this.contextService.retrieveLatestContext();
		this.itemService.deleteById(Integer.valueOf(itemId));
		this.contextService.decrementItemCountAndSave(latestContext);
		MachineContext context = this.contextService.retrieveLatestContext();
		
		return new ResponseEntity<MachineContext>(context, HttpStatus.OK);
	}
}
