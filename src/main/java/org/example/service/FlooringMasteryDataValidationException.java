package org.example.service;

import org.springframework.stereotype.Component;

//Will be used to verify:  state, Product type, date, area and calculations error.
public class FlooringMasteryDataValidationException extends Exception{
    public FlooringMasteryDataValidationException(String message) {
        super(message);
    }

    public FlooringMasteryDataValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
