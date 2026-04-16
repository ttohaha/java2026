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
        fileSizeThreshold = 1024 * 1024 * 2, // 2 MB
        maxFileSize = 10L * 1024 * 1024 * 1024, // 10 GB
        maxRequestSize = 15L * 1024 * 1024 * 1024
)
public class Controller extends HttpServlet {

    private static final Logger LOGGER = LogManager.getLogger(Controller.class);

    @Override
    public void init() throws ServletException {
        LOGGER.info("Controller servlet initialized");
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

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        // Inject response for download commands
        request.setAttribute("__HTTP_RESPONSE__", response);

        String commandStr = request.getParameter(AttributeName.COMMAND);
        LOGGER.debug("Processing command: {}", commandStr);

        Command command = CommandType.define(commandStr);
        LOGGER.debug("Resolved command class: {}", command.getClass().getSimpleName());

        try {
            String result = command.execute(request);
            LOGGER.debug("Command returned: {}", result);

            if (result == null) return; // Response already handled (e.g., download)

            if (result.startsWith("redirect:")) {
                String redirectUrl = request.getContextPath() + result.substring(9);
                LOGGER.debug("Redirecting to: {}", redirectUrl);
                response.sendRedirect(redirectUrl);
                return;
            }

            if (result != null && !result.isEmpty()) {
                request.getRequestDispatcher(result).forward(request, response);
            } else {
                response.sendRedirect(request.getContextPath() + PagePath.INDEX);
            }
        } catch (CommandException e) {
            LOGGER.error("Command execution failed for command: {}", commandStr, e);
            request.setAttribute(AttributeName.ERROR_MSG, "Internal error: " + e.getMessage());
            request.getRequestDispatcher(PagePath.ERROR_500).forward(request, response);
        }
    }

    @Override
    public void destroy() {
        LOGGER.info("Controller servlet destroyed");
        super.destroy();
    }
}