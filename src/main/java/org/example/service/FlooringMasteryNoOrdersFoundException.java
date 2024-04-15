package org.example.service;

import org.springframework.stereotype.Component;


public class FlooringMasteryNoOrdersFoundException extends Exception {
    //This class inherits all of the capabilities of Exception and then can add in any special features
    //that need to be added.

    public FlooringMasteryNoOrdersFoundException(String message) {
        super(message);
    }

    public FlooringMasteryNoOrdersFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
