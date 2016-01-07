package cn.ustc.controller;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.ustc.domain.User;
import cn.ustc.service.UserService;

/**
 * Servlet implementation class LoginController
 */
@WebServlet("/LoginController")
public class UpdateController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	public UserService service = new UserService();

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String userID =request.getParameter("userID");
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		UserService service = new UserService();
		service.update(userID, username, password);
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

	@Override
	public void init() throws ServletException {
	}
}
