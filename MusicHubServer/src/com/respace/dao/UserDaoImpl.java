package com.respace.dao;

import java.util.List;

import javax.annotation.Resource;



import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;
import org.springframework.stereotype.Repository;
import com.ibatis.sqlmap.client.SqlMapClient;
import com.respace.domain.RS_User;

@Repository
public class UserDaoImpl extends SqlMapClientDaoSupport implements UserDao {
	
	 @Resource(name="sqlMapClient")
	 protected void initDAO(SqlMapClient sqlMapClient) {        
		 this.setSqlMapClient(sqlMapClient);
	 } 
	
	
	@SuppressWarnings("unchecked")
	public List<RS_User> findAll() {	
		List<RS_User> array = getSqlMapClientTemplate().queryForList("UserSql.readUserList");
		return array;
	}


	public RS_User readUser(RS_User user) {
		RS_User result = (RS_User)getSqlMapClientTemplate().queryForObject("UserSql.readUser", user);
		return result;
	}


	public void createUser(RS_User user) {
		getSqlMapClientTemplate().insert("UserSql.createUser", user);
	}


	public void deleteUser(RS_User user) {
		getSqlMapClientTemplate().delete("UserSql.deleteUser", user);
		
	}


	public void updateUser(RS_User user) {
		getSqlMapClientTemplate().update("UserSql.updateUser", user);
	}

}
