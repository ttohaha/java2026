package blinov_first.command.impl;

import blinov_first.command.Command;
import blinov_first.exception.CommandException;
import blinov_first.exception.ServiceException;
import blinov_first.service.impl.UserServiceImpl;
import jakarta.servlet.http.HttpServletRequest;

public class AddUserCommand implements Command {

    @Override
    public String execute(HttpServletRequest request) throws CommandException {
        String login = request.getParameter("login");
        String password = request.getParameter("password");
        String email = request.getParameter("email");

        try {
            if (UserServiceImpl.getInstance().registerNewUser(login, password, email)) {

                request.setAttribute("successMsg", "Регистрация прошла успешно! Теперь вы можете войти.");
                return "/index.jsp";

            } else {

                request.setAttribute("errorMsg", "Ошибка регистрации. Логин занят или данные некорректны.");
                return "/pages/registration.jsp";

            }
        } catch (ServiceException e) {
            throw new CommandException("Ошибка в команде регистрации пользователя", e);
        }
    }
}