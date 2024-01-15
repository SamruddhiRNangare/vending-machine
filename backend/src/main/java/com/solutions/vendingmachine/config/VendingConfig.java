package com.solutions.vendingmachine.config;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.solutions.vendingmachine.constants.VendingConstants;
import com.solutions.vendingmachine.model.Item;
import com.solutions.vendingmachine.model.MachineContext;
import com.solutions.vendingmachine.model.builder.ItemBuilder;
import com.solutions.vendingmachine.model.builder.MachineContextBuilder;

@Configuration
public class VendingConfig {

	@Bean
	public ObjectMapper getObjectMapper() {
		ObjectMapper objectMapper = new ObjectMapper();
		JavaTimeModule module = new JavaTimeModule();
		LocalDateTimeDeserializer localDateTimeDeserializer =  new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(VendingConstants.DATE_FORMAT));
		module.addDeserializer(LocalDateTime.class, localDateTimeDeserializer);
		objectMapper.registerModule(module);
		objectMapper.setSerializationInclusion(Include.NON_NULL);
		return objectMapper;
	}
	
	@Bean
	public ObjectWriter getObjectWriter(ObjectMapper objectMapper){
		return objectMapper.writerWithDefaultPrettyPrinter();
	}
	
	@Bean
	@Scope("prototype")
	public MachineContext getMachineContext() {
		return new MachineContextBuilder().build();
	}
	
	@Bean
	@Scope("prototype")
	public Item getItem() {
		return new ItemBuilder().build();
	}
	
	@Bean 
	@Scope("prototype")
	public double getDouble() {
		return Double.valueOf(0);
	}
}
