package ru.chnr.vn.tinkbotservice.exceptions;

/**
 * Throws when connection to exchange is unavailable
 */
public class ExchangeUnavailableException extends CommandException {
    public ExchangeUnavailableException(String message) {
        super(message);
    }
}