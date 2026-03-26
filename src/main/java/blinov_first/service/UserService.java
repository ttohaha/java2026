package blinov_first.service;

import blinov_first.exception.ServiceException;

public interface UserService {
    // Твой старый метод логина...
    boolean authenticate(String login, String password) throws ServiceException;

    // Добавь этот метод:
    boolean registerNewUser(String login, String password, String email) throws ServiceException;
}