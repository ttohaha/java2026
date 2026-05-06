package blinov_first.controller;

import blinov_first.command.Command;
import blinov_first.command.CommandType;
import blinov_first.exception.CommandException;
import blinov_first.util.AttributeName;
import blinov_first.util.PagePath;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

@WebServlet(name = "Controller", urlPatterns = {"/controller"})
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 2,
        maxFileSize       = 10L * 1024 * 1024,
        maxRequestSize    = 15L * 1024 * 1024
)
public class Controller extends HttpServlet {

    private static final Logger LOGGER = LogManager.getLogger(Controller.class);

    @Override
    public void init() throws ServletException {
        LOGGER.info("Controller initialized");
        super.init();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    private void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setAttribute(AttributeName.HTTP_RESPONSE, response);

        String commandStr = request.getParameter(AttributeName.COMMAND);
        LOGGER.debug("Processing command: {}", commandStr);

        Command command = CommandType.define(commandStr);

        try {
            String result = command.execute(request);

            if (result == null || PagePath.AJAX_HANDLED.equals(result)) {
                return;
            }

            if (result.startsWith("redirect:")) {
                response.sendRedirect(request.getContextPath() + result.substring(9));
                return;
            }

            request.getRequestDispatcher(result).forward(request, response);

        } catch (CommandException e) {
            LOGGER.error("Command '{}' failed: {}", commandStr, e.getMessage(), e);
            request.setAttribute(AttributeName.ERROR_MSG, e.getMessage());
            request.getRequestDispatcher(PagePath.ERROR_500).forward(request, response);
        }
    }

    @Override
    public void destroy() {
        LOGGER.info("Controller destroyed");
        super.destroy();
    }
}
