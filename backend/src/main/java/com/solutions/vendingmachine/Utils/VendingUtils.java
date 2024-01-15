package com.solutions.vendingmachine.Utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.solutions.vendingmachine.constants.VendingConstants;

public class VendingUtils {

	private VendingUtils() {}
	
	public static String retrieveNow() {
		return LocalDateTime.now().format(DateTimeFormatter.ofPattern(VendingConstants.DATE_FORMAT));
	}
}
