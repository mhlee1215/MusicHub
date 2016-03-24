package com.musichub.core.domain;

import java.util.List;

import com.musichub.core.CapitalizeServer;
import com.musichub.core.Client;
import com.musichub.core.Host;

public class MusicChannel {
	public static final int MAX_UNLIMITED = -10;
	
	String id;
	String name;
	int maxPeople;
	
	CapitalizeServer server;
	List<MusicSource> musicSources;
	
	Host host;
	List<Client> clients;
	
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
	public List<MusicSource> getMusicSources() {
		return musicSources;
	}
	public void setMusicSources(List<MusicSource> musicSources) {
		this.musicSources = musicSources;
	}
}
