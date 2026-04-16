package blinov_first.command.impl;

import blinov_first.command.Command;
import blinov_first.entity.User;
import blinov_first.exception.CommandException;
import blinov_first.exception.DaoException;
import blinov_first.exception.ServiceException;
import blinov_first.service.impl.UserServiceImpl;
import blinov_first.util.AttributeName;
import blinov_first.util.PagePath;
import blinov_first.util.SessionUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EditProfileCommand implements Command {

    private static final Logger LOGGER = LogManager.getLogger(EditProfileCommand.class);

    @Override
    public String execute(HttpServletRequest request) throws CommandException {
        if (!SessionUtil.isLoggedIn(request)) {
            LOGGER.warn("Unauthorized access to profile");
            return PagePath.INDEX;
        }

        Long userId = SessionUtil.getUserId(request);
        if (userId == null) {
            return PagePath.INDEX;
        }

        // Handle GET (view profile) vs POST (update profile)
        if ("GET".equalsIgnoreCase(request.getMethod())) {
            try {
                User user = blinov_first.dao.impl.UserDaoImpl.getInstance()
                        .findByLogin(SessionUtil.getLogin(request))
                        .orElseThrow(() -> new CommandException("User not found"));
                request.setAttribute(AttributeName.USER, user);
                return PagePath.PROFILE;
            } catch (DaoException e) {
                LOGGER.error("Failed to load user profile", e);
                throw new CommandException("Profile loading failed", e);
            }
        }

        // POST: update profile
        String lastname = request.getParameter("lastname");
        String phone = request.getParameter("phone");
        String email = request.getParameter(AttributeName.EMAIL);

        // Validate required fields
        if (lastname == null || lastname.trim().isEmpty()) {
            request.setAttribute(AttributeName.ERROR_MSG, "Lastname is required");
            return PagePath.PROFILE;
        }

        try {
            boolean success = UserServiceImpl.getInstance().updateUserProfile(
                    userId.intValue(), lastname, phone, email);
            if (success) {
                request.setAttribute(AttributeName.SUCCESS_MSG, "Profile updated successfully");
            } else {
                request.setAttribute(AttributeName.ERROR_MSG, "Profile update failed");
            }
        } catch (ServiceException e) {
            LOGGER.error("Profile update error for user id: {}", userId, e);
            throw new CommandException("Profile update failed", e);
        }
        return PagePath.PROFILE;
    }
}