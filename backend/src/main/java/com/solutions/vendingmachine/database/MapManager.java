package com.solutions.vendingmachine.database;

import java.util.List;

public interface MapManager {

	public String get(Integer key);
	public List<String> getAllValues();
	public List<Integer> getAllKeys();
	public void insert(Integer key, String value);
	public void update(Integer key, String value);
	public boolean delete(Integer key);
	public boolean deleteAll();
}
