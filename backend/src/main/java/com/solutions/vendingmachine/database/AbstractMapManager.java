package com.solutions.vendingmachine.database;

import java.util.List;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.stream.Collectors;

import org.mapdb.Serializer;
import org.springframework.beans.factory.annotation.Autowired;

public  abstract class AbstractMapManager implements MapManager {

	protected final String mapName;
	private final DatabaseManager dbManager;
	
	@Autowired
	public AbstractMapManager(DatabaseManager dbManager, String mapName) {
		this.dbManager = dbManager;
		this.mapName = mapName;
	}
	
	protected ConcurrentNavigableMap<Integer,String> retrieveMap() {
		return this.dbManager.getDatabase()
		        .treeMap(mapName, Serializer.INTEGER, Serializer.STRING)
		        .createOrOpen();
	}
	
	public String get(Integer key) {
		return this.retrieveMap().get(key);
	}
	
	public List<String> getAllValues() {
		return this.retrieveMap().values().stream().collect(Collectors.toList());
	}
	
	public List<Integer> getAllKeys() {
		return this.retrieveMap().keySet().stream().collect(Collectors.toList());
	}
	
	public List<Integer> getAllKeysReversed() {
		return this.retrieveMap().descendingKeySet().stream().collect(Collectors.toList());
	}
	
	public String getLatestValue() {
		List<Integer> list = this.getAllKeysReversed();
		if (list != null && !list.isEmpty()) {
			return this.get(list.get(0));
		}
		return null;
	}
	
	public void insert(Integer key, String value) {
		ConcurrentNavigableMap<Integer,String> map = this.retrieveMap();
		
		map.put(key, value);
		
		this.dbManager.getDatabase().commit();
	}
	
	public void update(Integer key, String value) {
		this.retrieveMap().put(key, value);
		this.dbManager.getDatabase().commit();
	}
	
	public boolean delete(Integer key) {
		String obj = this.retrieveMap().remove(key);
		this.dbManager.getDatabase().commit();
		return obj != null;
	}
	
	public int getSize() {
		return this.retrieveMap().size();
	}

	public boolean deleteAll() {
		this.retrieveMap().clear();
		this.dbManager.getDatabase().commit();
		return this.getSize() == 0;
	}
}
