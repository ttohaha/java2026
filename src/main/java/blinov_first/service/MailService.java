package blinov_first.service;

import blinov_first.entity.User;
import blinov_first.exception.ServiceException;

public interface MailService {
    void sendConfirmationEmail(User user, String token) throws ServiceException;
}