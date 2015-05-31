package com.musichub.core;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class Client {
	private String name;
	private Socket sockets;
	private DataInputStream in;
	private DataOutputStream out;
	private boolean init;
	private boolean alive;
	private boolean play;
	
	public Client(Socket socket, String clientName, DataInputStream in, DataOutputStream out, boolean init, boolean alive){
		this.sockets = socket;
		this.name = clientName;
		this.in = in;
		this.out = out;
		this.init = init;
		this.alive = alive;
	}
	
	

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Socket getSockets() {
		return sockets;
	}

	public void setSockets(Socket sockets) {
		this.sockets = sockets;
	}

	public DataInputStream getIn() {
		return in;
	}

	public void setIn(DataInputStream in) {
		this.in = in;
	}

	public DataOutputStream getOut() {
		return out;
	}

	public void setOut(DataOutputStream out) {
		this.out = out;
	}

	public boolean isInit() {
		return init;
	}

	public void setInit(boolean init) {
		this.init = init;
	}

	public boolean isAlive() {
		return alive;
	}

	public void setAlive(boolean alive) {
		this.alive = alive;
	}

	public boolean isPlay() {
		return play;
	}

	public void setPlay(boolean play) {
		this.play = play;
	}



	@Override
	public String toString() {
		return "{\"name\":\"" + name + "\", sockets\":\"" + sockets
				+ "\", in\":\"" + in + "\", out\":\"" + out + "\", init\":\""
				+ init + "\", alive\":\"" + alive + "\", play\":\"" + play
				+ "}";
	}
}
