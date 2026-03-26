package blinov_first.command.impl;

import blinov_first.command.Command;
import blinov_first.dao.impl.UserDaoImpl;
import blinov_first.entity.User;
import blinov_first.exception.CommandException;
import blinov_first.exception.ServiceException;
import blinov_first.service.impl.UserServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.util.List;

public class LoginCommand implements Command {
    private static final String PARAM_LOGIN = "login";
    private static final String PARAM_PASSWORD = "password";

    @Override
    public String execute(HttpServletRequest request) throws CommandException {
        String login = request.getParameter("login");
        String password = request.getParameter("password");

        try {
            if (UserServiceImpl.getInstance().authenticate(login, password)) {
                HttpSession session = request.getSession();
                session.setAttribute("user", login);

                List<User> users = UserDaoImpl.getInstance().findAll();
                request.setAttribute("userList", users);

                return "/pages/main.jsp";
            } else {
                request.setAttribute("errorMsg", "Неверный логин или пароль");
                return "/index.jsp";
            }
        } catch (ServiceException e) {
            throw new CommandException(e);
        }
    }
}