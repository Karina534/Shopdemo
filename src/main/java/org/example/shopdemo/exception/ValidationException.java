package org.example.shopdemo.exception;

import lombok.Getter;
import org.example.shopdemo.validator.Error;

import java.util.List;

@Getter
public class ValidationException extends RuntimeException{

    private final List<Error> errors;

    public ValidationException(List<Error> errors) {
        this.errors = errors;
    }
}
