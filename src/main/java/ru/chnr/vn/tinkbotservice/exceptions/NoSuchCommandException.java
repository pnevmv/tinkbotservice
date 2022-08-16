package ru.chnr.vn.tinkbotservice.exceptions;

/**
 * Throws when inputted command isn't exist
 */
public class NoSuchCommandException extends CommandException {

    public NoSuchCommandException(){
        super("Exception in command");
    }
    public NoSuchCommandException(String message){
        super(message);
    }
}
