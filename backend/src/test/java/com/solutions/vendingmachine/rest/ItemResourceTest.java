package com.solutions.vendingmachine.rest;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.solutions.vendingmachine.constants.ResourceConstants;
import com.solutions.vendingmachine.constants.States;
import com.solutions.vendingmachine.model.Item;
import com.solutions.vendingmachine.model.builder.ItemBuilder;
import com.solutions.vendingmachine.service.MachineContextService;
import com.solutions.vendingmachine.service.MachineItemService;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(locations="classpath:backend-test.properties")
public class ItemResourceTest {

private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private MachineContextService contextService;
	
	@Autowired
	private MachineItemService itemService;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@After
	public void destroy() {
		itemService.deleteAll();
		contextService.deleteAll();
	}
	
	@Test
	public void insertItems() throws Exception {
		List<Item> items = new ArrayList<Item>();
		for(int i = 0; i < 10; i++) {
			
			Item item = new ItemBuilder().with(itm -> {
				itm.id = null;
				itm.type = "Soda";
				itm.price = 1.25;
			}).build();
			items.add(item);
		}
		String itemsStr = objectMapper.writeValueAsString(items);
		RequestBuilder requestBuilder = MockMvcRequestBuilders.post(ResourceConstants.MACHINE_ITEMS).contentType(MediaType.APPLICATION_JSON_UTF8_VALUE).content(itemsStr);
		
		MvcResult result = mockMvc.perform(requestBuilder)
				  .andExpect(status().isOk())
				  .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				  .andExpect(jsonPath("id", is(10)))
				  .andExpect(jsonPath("itemCount", is(10)))
				  .andExpect(jsonPath("balance", is(0.0)))
				  .andExpect(jsonPath("income", is(0.0)))
				  .andExpect(jsonPath("dateTime", notNullValue()))
				  .andReturn();
		String content = result.getResponse().getContentAsString();
		LOGGER.debug("insertItems => " + content);
	}
	
	@Test 
	public void getItems() throws Exception {
		insertItems();
		RequestBuilder requestBuilder = MockMvcRequestBuilders.get(ResourceConstants.MACHINE_ITEMS);
		MvcResult result = mockMvc.perform(requestBuilder)
				  .andExpect(status().isOk())
				  .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				  .andReturn();
		String content = result.getResponse().getContentAsString();
		LOGGER.debug("getItems => " + content);
		List<Item> items = objectMapper.readValue(content, new TypeReference<List<Item>>(){});
		assertThat(items, notNullValue());
		assertThat(items.size(), is(10));
	}
	
	@Test 
	public void getItem() throws Exception {
		insertItems();
		RequestBuilder requestBuilder = MockMvcRequestBuilders.get(ResourceConstants.MACHINE_ITEMS + "/6");
		MvcResult result = mockMvc.perform(requestBuilder)
				  .andExpect(status().isOk())
				  .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				  .andExpect(jsonPath("id", is(6)))
				  .andExpect(jsonPath("type", is("Soda")))
				  .andExpect(jsonPath("price", is(1.25)))
				  .andExpect(jsonPath("insertDateTime", notNullValue()))
				  .andReturn();
		String content = result.getResponse().getContentAsString();
		LOGGER.debug("getItem => " + content);
	}
	
	@Test 
	public void updateItem() throws Exception {
		insertItems();
		for (int i = 1; i < 6; i++) {
			RequestBuilder requestBuilder1 = MockMvcRequestBuilders.put(ResourceConstants.MACHINE_CONTEXTS + "/.25");
			MvcResult result1 = mockMvc.perform(requestBuilder1)
					  .andExpect(status().isOk())
					  .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
					  .andExpect(jsonPath("id", is(10 + i)))
					  .andExpect(jsonPath("itemCount", is(10)))
					  .andExpect(jsonPath("balance", is(.25 * i)))
					  .andExpect(jsonPath("income", is(0.0)))
					  .andExpect(jsonPath("dateTime", notNullValue()))
					  .andExpect(jsonPath("state.status", is(States.HAS_QUARTER.toString())))
					  .andReturn();
		}
		
		RequestBuilder requestBuilder2 = MockMvcRequestBuilders.put(ResourceConstants.MACHINE_ITEMS + "/5").contentType(MediaType.APPLICATION_JSON_UTF8_VALUE);	
		MvcResult result2 = mockMvc.perform(requestBuilder2)
				  .andExpect(status().isOk())
				  .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				  .andExpect(jsonPath("id", is(17)))
				  .andExpect(jsonPath("itemCount", is(9)))
				  .andExpect(jsonPath("balance", is(0.0)))
				  .andExpect(jsonPath("income", is(1.25)))
				  .andExpect(jsonPath("dateTime", notNullValue()))
				  .andExpect(jsonPath("state.status", is(States.NO_QUARTER.toString())))
				  .andReturn();
		String content = result2.getResponse().getContentAsString();
		LOGGER.debug("!!! updateItem => " + content);
		
		RequestBuilder requestBuilder3 = MockMvcRequestBuilders.get(ResourceConstants.MACHINE_ITEMS + "/5");
		MvcResult result3 = mockMvc.perform(requestBuilder3)
				  .andExpect(status().isOk())
				  .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				  .andExpect(jsonPath("id", is(5)))
				  .andExpect(jsonPath("type", is("Soda")))
				  .andExpect(jsonPath("price", is(1.25)))
				  .andExpect(jsonPath("insertDateTime", notNullValue()))
				  .andExpect(jsonPath("purchaseDateTime", notNullValue()))
				  .andReturn();
		String content3 = result3.getResponse().getContentAsString();
		LOGGER.debug("updateItem 3 => " + content3);
		
		RequestBuilder requestBuilder4 = MockMvcRequestBuilders.get(ResourceConstants.MACHINE_CONTEXTS + "/16");
		MvcResult result4 = mockMvc.perform(requestBuilder4)
				  .andExpect(status().isOk())
				  .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				  .andExpect(jsonPath("id", is(16)))
				  .andExpect(jsonPath("itemCount", is(9)))
				  .andExpect(jsonPath("balance", is(0.0)))
				  .andExpect(jsonPath("income", is(1.25)))
				  .andExpect(jsonPath("dateTime", notNullValue()))
				  .andExpect(jsonPath("state.status", is(States.SOLD.toString())))
				  .andReturn();
	}
	
	@Test
	public void deleteItem() throws Exception {
		insertItems();
		RequestBuilder requestBuilder = MockMvcRequestBuilders.delete(ResourceConstants.MACHINE_ITEMS + "/4");
		MvcResult result = mockMvc.perform(requestBuilder)
				  .andExpect(status().isOk())
				  .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				  .andExpect(jsonPath("id", is(11)))
				  .andExpect(jsonPath("itemCount", is(9)))
				  .andExpect(jsonPath("balance", is(0.0)))
				  .andExpect(jsonPath("income", is(0.0)))
				  .andExpect(jsonPath("dateTime", notNullValue()))
				  .andExpect(jsonPath("state.status", is(States.NO_QUARTER.toString())))
				  .andReturn();
		String content = result.getResponse().getContentAsString();
		LOGGER.debug("deleteItem => " + content);
	}
	
	@Test 
	public void soldOut() throws Exception {
		insertItems();
		for (int j = 1; j < 11; j ++) {
			for (int i = 1; i < 6; i++) {
				RequestBuilder requestBuilder1 = MockMvcRequestBuilders.put(ResourceConstants.MACHINE_CONTEXTS + "/.25");
				MvcResult result1 = mockMvc.perform(requestBuilder1)
						  .andExpect(status().isOk())
						  .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
						  .andExpect(jsonPath("id", notNullValue()))
						  .andExpect(jsonPath("itemCount", is(10 - (j - 1))))
						  .andExpect(jsonPath("balance", is(.25 * i)))
						  .andExpect(jsonPath("income", is(1.25 * (j - 1))))
						  .andExpect(jsonPath("dateTime", notNullValue()))
						  .andExpect(jsonPath("state.status", is(States.HAS_QUARTER.toString())))
						  .andReturn();
			}
			if (j < 10) {
				RequestBuilder requestBuilder2 = MockMvcRequestBuilders.put(ResourceConstants.MACHINE_ITEMS + "/" + j).contentType(MediaType.APPLICATION_JSON_UTF8_VALUE);	
				MvcResult result2 = mockMvc.perform(requestBuilder2)
						  .andExpect(status().isOk())
						  .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
						  .andExpect(jsonPath("id", notNullValue()))
						  .andExpect(jsonPath("itemCount", is(10 - j)))
						  .andExpect(jsonPath("balance", is(0.0)))
						  .andExpect(jsonPath("income", is(1.25 * j)))
						  .andExpect(jsonPath("dateTime", notNullValue()))
						  .andExpect(jsonPath("state.status", is(States.NO_QUARTER.toString())))
						  .andReturn();
				String content = result2.getResponse().getContentAsString();
				LOGGER.debug("!!! updateItem => " + content);
			} else {
				RequestBuilder requestBuilder2 = MockMvcRequestBuilders.put(ResourceConstants.MACHINE_ITEMS + "/" + j).contentType(MediaType.APPLICATION_JSON_UTF8_VALUE);	
				MvcResult result2 = mockMvc.perform(requestBuilder2)
						  .andExpect(status().isOk())
						  .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
						  .andExpect(jsonPath("id", notNullValue()))
						  .andExpect(jsonPath("itemCount", is(0)))
						  .andExpect(jsonPath("balance", is(0.0)))
						  .andExpect(jsonPath("income", is(12.50)))
						  .andExpect(jsonPath("dateTime", notNullValue()))
						  .andExpect(jsonPath("state.status", is(States.SOLD_OUT.toString())))
						  .andReturn();
				String content = result2.getResponse().getContentAsString();
				LOGGER.debug("!!! updateItem => " + content);
			}
		}
	}
}
