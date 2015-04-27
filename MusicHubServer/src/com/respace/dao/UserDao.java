package com.respace.dao;

import java.util.List;

import com.respace.domain.RS_User;


public interface UserDao {
	public RS_User readUser(RS_User user);
	public void createUser(RS_User user);
	public void deleteUser(RS_User user);
	public void updateUser(RS_User user);
	public List<RS_User> findAll();
}
