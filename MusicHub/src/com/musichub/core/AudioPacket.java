package com.musichub.core;


public class AudioPacket {
	int length;
	byte[] packet = null;
	
	public AudioPacket(int length, byte[] packet){
		this.length = length;
		this.packet = packet;
	}
	
	public String toString(){
		return "Packet Length : "+Integer.toString(length);
	}
}
