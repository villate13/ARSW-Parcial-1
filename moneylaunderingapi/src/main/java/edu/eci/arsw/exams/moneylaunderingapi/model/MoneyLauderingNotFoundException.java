package edu.eci.arsw.exams.moneylaunderingapi.model;

public class MoneyLauderingNotFoundException extends Exception{
    
    public MoneyLauderingNotFoundException(String message) {
        super(message);
    }

    public MoneyLauderingNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
