package com.respace.service;

import java.net.MalformedURLException;
import java.util.List;

import org.apache.commons.mail.EmailException;

import com.respace.domain.RS_User;



public interface UserService {
	public int readUser(RS_User user) throws Exception;
	public RS_User readUserData(RS_User user) throws Exception;
	public int createUser(RS_User user) throws Exception;
	//public void deleteUser(User user);
	public void updateUser(RS_User user);
	
	public List<RS_User> findAll();
	public int verifyUser(String id);
	public int deleteUser(String id);
	public int deleteUser(String id, boolean isDeleteRow);
	public int findPassword(String id) throws EmailException, MalformedURLException;
}
