package com.respace.service;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.apache.commons.mail.EmailException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import com.respace.dao.UserDao;
import com.respace.domain.RS_User;
import com.respace.util.Crypto;



@Service
public class UserServiceImpl implements UserService {

	private Logger logger = Logger.getLogger(getClass());

	@Autowired
	private UserDao		userDao;
	
	boolean isEncrypt = true;

	public List<RS_User> findAll() {
		return userDao.findAll();
	}

	public int createUser(RS_User user) throws Exception {
		
		
		//Encrypt
        if(isEncrypt)
        	 user.setPassword(Crypto.encrypt(user.getPassword()));       
		RS_User paramUser = new RS_User();
		paramUser.setEmail(user.getEmail());
		RS_User foundUser = readUserData(paramUser);
		logger.debug("create User");
		logger.debug("==[S]============================");
		
		if(foundUser != null){
			logger.debug("User is already registered. Cancel Register.");
			logger.debug("==[E]============================");
			return RS_User.STATUS_ALREADY_REGISTEREDED;
		}
		else{
			logger.debug("User doesn't find. Go Register.");
			userDao.createUser(user);
			
			//Make User Folser
			String cmd = "mkdir";
			String resultStr = "";
			
			//Make ID folder
			//resultStr+=Env.exeCmd(cmd+" "+Env.ENV_DATA_PATH+user.getInternalid());
			if(!resultStr.equals(""))resultStr+="<br>";
		
			//Make Log folder
			//Env.exeCmd(cmd+" "+Env.ENV_LOG_PATH+user.getInternalid());
			System.out.println(resultStr);
			logger.debug("==[E]============================");
			return RS_User.STATUS_SUCCESS_REGISTER;
		}
		
		
	}

	

	public int readUser(RS_User user) throws Exception {
		
		if(isEncrypt)
		    user.setPassword(Crypto.encrypt(user.getPassword()));
        //
		RS_User readed = userDao.readUser(user);
		
		if(readed == null){
			return RS_User.STATUS_NOT_FOUNDED;
		}else{
			return RS_User.STATUS_FOUNDED;
		}
	}
	
	public RS_User readUserData(RS_User user) throws Exception {
		return userDao.readUser(user);
	}

	public void updateUser(RS_User user) {
		userDao.updateUser(user);
	}

	public int verifyUser(String email) {

		RS_User paramUser = new RS_User();
		paramUser.setEmail(email);
		RS_User user = userDao.readUser(paramUser);
		if (user != null) {
			if (user.getIsverified().equals("Y")) {
				//alread verified.
				return RS_User.STATUS_ALREADY_VERIFIED;
			} else {
				paramUser.setIsverified("Y");
				paramUser.setStatus(RS_User.STATUS_VERIFIED);
				userDao.updateUser(paramUser);
				// Activate successfully.
				return RS_User.STATUS_SUCCESS_VERIFIED;
			}
		}
		// Activate fail.
		return RS_User.STATUS_NOT_FOUNDED;
	}

	public int deleteUser(String id) {
		return deleteUser(id, false);
	}
	
	public int deleteUser(String email, boolean isDeleteRow) {
		RS_User paramUser = new RS_User();
		paramUser.setEmail(email);
		RS_User user = userDao.readUser(paramUser);
		if (user != null) {
			if (user.getIsdeleted().equals("Y")) {
				//alread deleted.
				return RS_User.STATUS_ALREADY_DELETED;
			} else {
				userDao.deleteUser(paramUser);
				return RS_User.STATUS_SUCCESS_DELETED;
			}
		}
		// Delete fail.
		return RS_User.STATUS_DELTE_FAIL;
		
	}

	@Override
	public int findPassword(String id) throws EmailException,
			MalformedURLException {
		// TODO Auto-generated method stub
		return 0;
	}



	

}
