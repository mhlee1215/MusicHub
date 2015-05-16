package com.musichub.core;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.net.Socket;

public class Client {
	private String name;
	private Socket sockets;
	private BufferedReader in;
	private DataOutputStream out;
	private boolean init;
	private boolean alive;
	
	public Client(Socket socket, BufferedReader in, DataOutputStream out, boolean init, boolean alive){
		this.sockets = socket;
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

	public BufferedReader getIn() {
		return in;
	}

	public void setIn(BufferedReader in) {
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

	
}
