package com.solutions.vendingmachine.service;

import java.util.List;

public interface MachineService<T> {

	public String marshall(T obj);
	public T unmarshall(String objStr);
	public T retrieveById(String id);
	public List<T> retrieveAll();
	public Integer retrieveNextKey();
	public Boolean deleteById(Integer key);
	public Boolean deleteAll();
	public Boolean save(Integer key, T obj);
	public int retrieveSize();
}
