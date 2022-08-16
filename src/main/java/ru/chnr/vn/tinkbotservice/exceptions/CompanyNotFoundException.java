package ru.chnr.vn.tinkbotservice.exceptions;

/**
 * Throws when there is not such company in companies storage
 */
public class CompanyNotFoundException extends CommandException {
    public CompanyNotFoundException(String message) {
        super(message);
    }
}
