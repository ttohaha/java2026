package blinov_first.controller;

import java.io.*;

import blinov_first.command.Command;
import blinov_first.command.CommandType;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

// Используем именно эти импорты для SLF4J
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(name = "helloServlet", urlPatterns = {"/controller", "*.do"})
public class Controller extends HttpServlet {

    // 1. Инициализируем логгер через SLF4J
    private static final Logger logger = LoggerFactory.getLogger(Controller.class);

    public void init() {
        logger.info("Сервлет blinov_first.controller.Controller инициализирован");
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");
        String strNum = request.getParameter("num");

        String commandStr = request.getParameter("command");
        Command command = CommandType.define(commandStr);
        String page = null;
        try {
            page = command.execute(request);
            request.getRequestDispatcher(page).forward(request, response);
        } catch (blinov_first.exception.CommandException e) {
            response.sendError(500);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }

    public void destroy() {
        logger.info("Сервлет blinov_first.controller.Controller уничтожен");
    }
}