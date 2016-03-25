package com.musichub.domain;

import java.util.List;

public class MusicChannel {
	public static final int MAX_UNLIMITED = -10;
	
	String id;
	String name;
	int curPeople;
	int maxPeople;	
	
	
	public MusicChannel(String id, String name){
		this(id, name, MAX_UNLIMITED);	
	}
	
	public MusicChannel(String id, String name, int maxPeople){
		this.id = id;
		this.name = name;	
		this.maxPeople = maxPeople;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getMaxPeople() {
		return maxPeople;
	}
	public void setMaxPeople(int maxPeople) {
		this.maxPeople = maxPeople;
	}
}
