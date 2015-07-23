package dao;

import java.sql.SQLException;
import java.util.List;

import org.myspringside.dao.api.database.IjdbcEntityDao;
import org.myspringside.dao.imp.jdbc.JdbcEntityDao;
import vo.UsersMain;

public class UsersMainDao {
	
	private static IjdbcEntityDao<UsersMain> userDao= new JdbcEntityDao<UsersMain>(UsersMain.class);
	
	public  static UsersMain addUser(UsersMain user) throws SQLException{
		return userDao.insert(user);
	}
	
	public static List<UsersMain>  getUserList(){
		return userDao.getAll();
	}
	
	public static UsersMain  getUser(int userId){
		return userDao.get(userId);
	}
	
	public static void updateUser(UsersMain user) throws SQLException{
		userDao.update(user);
	}
	
	public static void deleteUser(int userId){
		userDao.remove(userId);
	}
	
	
	
}
