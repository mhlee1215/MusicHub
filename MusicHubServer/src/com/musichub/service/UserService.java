package com.musichub.service;

import java.net.MalformedURLException;
import java.util.List;

import org.apache.commons.mail.EmailException;

import com.musichub.domain.MH_User;



public interface UserService {
	public int readUser(MH_User user) throws Exception;
	public MH_User readUserData(MH_User user) throws Exception;
	public int createUser(MH_User user) throws Exception;
	//public void deleteUser(User user);
	public void updateUser(MH_User user);
	
	public List<MH_User> findAll();
	public int verifyUser(String id);
	public int deleteUser(String id);
	public int deleteUser(String id, boolean isDeleteRow);
	public int findPassword(String id) throws EmailException, MalformedURLException;
}
