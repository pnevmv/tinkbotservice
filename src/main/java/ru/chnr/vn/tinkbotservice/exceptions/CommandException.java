package ru.chnr.vn.tinkbotservice.exceptions;

/**
 * Throws when there is some problems in commands
 */
public class CommandException extends Exception{
    public CommandException(){
        super("Exception in command");
    }
    public CommandException(String message){
        super(message);
    }
}
