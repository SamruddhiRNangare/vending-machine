package com.solutions.vendingmachine.rest;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.solutions.vendingmachine.constants.ResourceConstants;
import com.solutions.vendingmachine.errors.VendingException;
import com.solutions.vendingmachine.model.MachineContext;
import com.solutions.vendingmachine.model.rest.ErrorContext;
import com.solutions.vendingmachine.service.MachineContextService;
import com.solutions.vendingmachine.service.MachineStateService;

@RestController
@RequestMapping(ResourceConstants.MACHINE_CONTEXTS)
public class MachineContextResource {

	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private MachineContextService contextService;
	
	@Autowired
	private MachineStateService stateService;
	
	@RequestMapping(path = "", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<List<MachineContext>> getContexts() {
		List<MachineContext> contexts = this.contextService.retrieveAll();
		return new ResponseEntity<List<MachineContext>>(contexts, HttpStatus.OK);
	}
	
	@RequestMapping(path = "/{contextId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<MachineContext> getContext(
			@PathVariable(value = "contextId", required = true)
			String contextId) {
		MachineContext context = this.contextService.retrieveById(contextId);
		return new ResponseEntity<MachineContext>(context, HttpStatus.OK);
	}
	
	@RequestMapping(path = "/{value}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<MachineContext> updateContext(
			@PathVariable(value = "value", required = true) String value) {
		
		Double valueDouble = Double.valueOf(value);
		MachineContext latestContext;
		try {
			if (valueDouble > 0) {
				latestContext = stateService.insertCoin(valueDouble);
			} else {
				latestContext = stateService.removeCoin(valueDouble);
			}
		} catch (VendingException e) {
			LOGGER.error(e.getMessage());
			latestContext = new ErrorContext(this.contextService.retrieveLatestContext(), e.getMessage());
			return new ResponseEntity<MachineContext>(latestContext, HttpStatus.BAD_REQUEST);
		}
		
		return new ResponseEntity<MachineContext>(latestContext, HttpStatus.OK);
	}
	
	@RequestMapping(path = "/{contextId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Boolean> deleteContext(
			@PathVariable(value = "contextId", required = true) String contextId) {
		Boolean result = this.contextService.deleteById(Integer.valueOf(contextId));
		return new ResponseEntity<Boolean>(result, HttpStatus.OK);
	}
}
