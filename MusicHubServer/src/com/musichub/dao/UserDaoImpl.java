package com.musichub.dao;

import java.util.List;

import javax.annotation.Resource;




import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;
import org.springframework.stereotype.Repository;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.musichub.domain.MH_User;

@Repository
public class UserDaoImpl extends SqlMapClientDaoSupport implements UserDao {
	
	 @Resource(name="sqlMapClient")
	 protected void initDAO(SqlMapClient sqlMapClient) {        
		 this.setSqlMapClient(sqlMapClient);
	 } 
	
	
	@SuppressWarnings("unchecked")
	public List<MH_User> findAll() {	
		List<MH_User> array = getSqlMapClientTemplate().queryForList("UserSql.readUserList");
		return array;
	}


	public MH_User readUser(MH_User user) {
		MH_User result = (MH_User)getSqlMapClientTemplate().queryForObject("UserSql.readUser", user);
		return result;
	}


	public void createUser(MH_User user) {
		getSqlMapClientTemplate().insert("UserSql.createUser", user);
	}


	public void deleteUser(MH_User user) {
		getSqlMapClientTemplate().delete("UserSql.deleteUser", user);
		
	}


	public void updateUser(MH_User user) {
		getSqlMapClientTemplate().update("UserSql.updateUser", user);
	}

}
