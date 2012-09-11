package biz;

import java.sql.SQLException;

import org.junit.Test;

import dao.UsersDao;

import vo.Users;

public class UsersBiz {
	
	@Test
	public void addUser(){
		Users user = new Users();
		user.setGender("M");
		user.setTelphone("110");
		user.setUserName("police");
		try {
			UsersDao.addUser(user);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
