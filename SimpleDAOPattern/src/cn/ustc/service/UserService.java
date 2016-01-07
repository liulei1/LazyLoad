package cn.ustc.service;

import java.sql.SQLException;

import cn.ustc.DAO.Conversation;
import cn.ustc.domain.User;
import cn.ustc.domain.UserInterface;

public class UserService {
	private Conversation<User> service = new Conversation<User>(new User());

	public boolean register(String userID, String username, String password) {
		User user = new User();
		user.setUserID(userID);
		user.setUserName(username);
		user.setPassword(password);
		try {
			service.persist(user);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return true;
	}

	public boolean deleteByUserID(String userID) {
		try {
			service.remove(userID);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return true;
	}

	@SuppressWarnings("unused")
	public boolean findUserById(String userID) {
		try {
			UserInterface user = service.get(userID);
			System.out.println(user);
			user.getPassword();
			System.out.println(user);
			user.getPassword();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public <T> boolean findUserByCondition(String fieldname, T value) {
		try {
			User user = service.get(fieldname, value);
			System.out.println(user);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public void update(String userID, String userName, String password) {
		User user = new User();
		user.setUserID(userID);
		user.setUserName(userName);
		user.setPassword(password);
		try {
			service.update(user);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
