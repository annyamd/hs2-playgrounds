package com.example.hs2playgrounds.controller.exceptions.invalid;


import com.example.hs2playgrounds.controller.exceptions.ControllerException;

public class InvalidRequestDataException extends ControllerException {
    public InvalidRequestDataException(String message) {
        super("Invalid request", message);
    }
}