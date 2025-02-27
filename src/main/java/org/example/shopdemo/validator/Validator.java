package org.example.shopdemo.validator;

public interface Validator<T> {
    ValidationResult isValid(T t);
}
