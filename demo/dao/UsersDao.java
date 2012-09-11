package dao;

import java.sql.SQLException;
import java.util.List;

import org.myspringside.dao.api.database.IjdbcEntityDao;
import org.myspringside.dao.imp.jdbc.JdbcEntityDao;

import vo.Users;

public class UsersDao {
	
	private static IjdbcEntityDao<Users> userDao= new JdbcEntityDao<Users>(Users.class);
	
	public  static Users addUser(Users user) throws SQLException{
		return userDao.insert(user);
	}
	
	public static List<Users>  getUserList(){
		return userDao.getAll();
	}
	
	public static Users  getUser(int userId){
		return userDao.get(userId);
	}
	
	public static void updateUser(Users user){
		userDao.update(user);
	}
	
	public static void deleteUser(int userId){
		userDao.remove(userId);
	}
	
	
	
}
