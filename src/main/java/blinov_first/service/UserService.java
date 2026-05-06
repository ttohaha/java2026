package blinov_first.service;

import blinov_first.entity.User;
import blinov_first.exception.ServiceException;
import java.util.List;

public interface UserService {
    List<User> findAllUsers() throws ServiceException;
    boolean authenticate(String login, String password) throws ServiceException;
    boolean registerNewUser(String login, String password, String email) throws ServiceException;
    boolean updateUserProfile(int userId, String lastname, String phone, String email) throws ServiceException;
    // === New methods for email confirmation ===
    boolean registerWithConfirmation(String login, String password, String email) throws ServiceException;
    String  registerAndGetToken(String login, String password, String email) throws ServiceException;
    boolean confirmRegistration(String token) throws ServiceException;
}