package blinov_first.service;

import blinov_first.exception.ServiceException;

public interface MailService {

    /**
     * Sends an email-confirmation link to the newly registered user.
     *
     * @param toAddress recipient email address
     * @param token     confirmation token that will be appended to the link
     */
    void sendConfirmationEmail(String toAddress, String token) throws ServiceException;
}
