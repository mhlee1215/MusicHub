package com.musichub.dao;

import java.util.List;

import com.musichub.domain.MH_User;


public interface UserDao {
	public MH_User readUser(MH_User user);
	public void createUser(MH_User user);
	public void deleteUser(MH_User user);
	public void updateUser(MH_User user);
	public List<MH_User> findAll();
}
