package ru.chnr.vn.tinkbotservice.exceptions;

/**
 * Throws when command arguments inputted in wrong format, or wrong number of args was inputted
 */
public class IllegalCommandArgsException extends CommandException {

    public IllegalCommandArgsException(){
        super("Exception in command");
    }
    public IllegalCommandArgsException(String message){
        super(message);
    }
}
