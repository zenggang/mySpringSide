package biz;

import java.sql.SQLException;

import org.junit.Test;
import org.myspringside.dao.imp.jdbc.tools.LoggerTool;

import dao.UsersDao;
import dao.UsersMainDao;

import vo.Users;
import vo.UsersMain;

public class UsersBiz {
	
	@Test
	public void addUser(){
		Users user = new Users();
		user.setGender("M");
		user.setTelphone("110");
		user.setUserName("police1");
		UsersMain user2 = new UsersMain();
		user2.setGender("M");
		user2.setTelphone("110");
		user2.setUserName("police1");
		try {
			UsersDao.addUser(user);
			UsersMainDao.addUser(user2);
		} catch (SQLException e) {
			e.printStackTrace(); LoggerTool.error(this.getClass(), e);
		}
	}
}
