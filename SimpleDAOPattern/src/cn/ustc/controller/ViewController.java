package cn.ustc.controller;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.ustc.service.UserService;

/**
 * Servlet implementation class DeleteController
 */
@WebServlet("/DeleteController")
public class ViewController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public ViewController() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String userID = request.getParameter("userID");
		String username = request.getParameter("userName");
		UserService service = new UserService();
		if(userID != null){
			service.findUserById(userID);
		}else if(username != null){
			service.findUserByCondition("userName", username);
		}
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
