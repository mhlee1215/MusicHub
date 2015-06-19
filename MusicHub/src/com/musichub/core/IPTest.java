package com.musichub.core;
import java.net.Inet4Address;
import java.net.UnknownHostException;


public class IPTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			System.out.println(Inet4Address.getLocalHost().getHostAddress());
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
