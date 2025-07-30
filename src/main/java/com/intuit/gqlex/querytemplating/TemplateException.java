package com.intuit.gqlex.querytemplating;

/**
 * Exception thrown when template processing fails
 * 
 * @author gqlex-team
 * @version 1.0.0
 */
public class TemplateException extends Exception {
    
    /**
     * Constructs a new TemplateException with the specified detail message
     * 
     * @param message the detail message
     */
    public TemplateException(String message) {
        super(message);
    }
    
    /**
     * Constructs a new TemplateException with the specified detail message and cause
     * 
     * @param message the detail message
     * @param cause the cause
     */
    public TemplateException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * Constructs a new TemplateException with the specified cause
     * 
     * @param cause the cause
     */
    public TemplateException(Throwable cause) {
        super(cause);
    }
} 