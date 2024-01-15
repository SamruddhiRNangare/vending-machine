package com.solutions.vendingmachine.rest;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.After;
import org.junit.Before;
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
import com.solutions.vendingmachine.Utils.VendingUtils;
import com.solutions.vendingmachine.constants.ResourceConstants;
import com.solutions.vendingmachine.model.Item;
import com.solutions.vendingmachine.model.MachineContext;
import com.solutions.vendingmachine.model.builder.ItemBuilder;
import com.solutions.vendingmachine.service.MachineContextService;
import com.solutions.vendingmachine.service.MachineItemService;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(locations="classpath:backend-test.properties")
public class MachineContextResourceTest {

	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private MachineContextService contextService;
	
	@Autowired
	private MachineItemService itemService;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Before
	public void setup() {
		for(int i = 0; i < 50; i++) {
			Integer key = itemService.retrieveNextKey();
			Item item = new ItemBuilder().with(itm -> {
				itm.id = key;
				itm.type = "Soda";
				itm.price = 1.25;
				itm.insertDateTime = VendingUtils.retrieveNow();
			}).build();
			itemService.save(key, item);
			contextService.incrementItemCountAndSave(contextService.retrieveLatestContext());
		}
		
	}
	
	@After
	public void destroy() {
		itemService.deleteAll();
		contextService.deleteAll();
	}
	
	@Test
	public void getContexts() throws Exception {
		// Setup mock request
		RequestBuilder requestBuilder = MockMvcRequestBuilders.get(ResourceConstants.MACHINE_CONTEXTS);
		// Perform request, test against expected results, and return result
		MvcResult result = mockMvc.perform(requestBuilder)
						  .andExpect(status().isOk())
						  .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
						  .andReturn();
		String content = result.getResponse().getContentAsString();
		LOGGER.debug("getContexts => " + content);
		List<MachineContext> contexts = objectMapper.readValue(content, new TypeReference<List<MachineContext>>(){});
		assertThat(contexts, notNullValue());
		assertThat(contexts.size(), is(50));
		assertThat(contexts.get(49).getItemCount(), is(50));
	}
	
	@Test
	public void getContext() throws Exception {
		// Setup mock request
		RequestBuilder requestBuilder = MockMvcRequestBuilders.get(ResourceConstants.MACHINE_CONTEXTS + "/49");
		MvcResult result = mockMvc.perform(requestBuilder)
				  .andExpect(status().isOk())
				  .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				  .andExpect(jsonPath("id", is(49)))
				  .andExpect(jsonPath("itemCount", is(49)))
				  .andExpect(jsonPath("balance", is(0.0)))
				  .andExpect(jsonPath("income", is(0.0)))
				  .andExpect(jsonPath("dateTime", notNullValue()))
				  .andReturn();
		String content = result.getResponse().getContentAsString();
		LOGGER.debug("getContext => " + content);
	}
	
	@Test
	public void updatContext() throws Exception {
		RequestBuilder requestBuilder = MockMvcRequestBuilders.put(ResourceConstants.MACHINE_CONTEXTS + "/.25");
		MvcResult result = mockMvc.perform(requestBuilder)
				  .andExpect(status().isOk())
				  .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				  .andExpect(jsonPath("id", is(51)))
				  .andExpect(jsonPath("itemCount", is(50)))
				  .andExpect(jsonPath("balance", is(0.25)))
				  .andExpect(jsonPath("income", is(0.0)))
				  .andExpect(jsonPath("dateTime", notNullValue()))
				  .andReturn();
		
		String content = result.getResponse().getContentAsString();
		LOGGER.debug("updatContext => " + content);
	}
	
	@Test
	public void deleteContext() throws Exception {
		RequestBuilder requestBuilder = MockMvcRequestBuilders.delete(ResourceConstants.MACHINE_CONTEXTS + "/12");
		MvcResult result = mockMvc.perform(requestBuilder)
				  .andExpect(status().isOk())
				  .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				  .andReturn();
		
		String content = result.getResponse().getContentAsString();
		
		LOGGER.debug("deleteContext => " + content);
		assertThat(content, is("true"));
	}
}
