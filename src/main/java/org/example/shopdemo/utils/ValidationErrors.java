package org.example.shopdemo.utils;

import lombok.experimental.UtilityClass;
import org.example.shopdemo.validator.Error;

@UtilityClass
public class ValidationErrors {
    public static final Error INVALID_EMAIL = Error.of("invalid.email", "Your email address is wrong");
    public static final Error INVALID_PASSWORD_LENGTH = Error.of("invalid.password", "Your password less then 8 symbols");
    public static final Error INVALID_TELEPHONE_LENGTH = Error.of("invalid.telephone", "Your telephone is less then 11 numbers");
    public static final Error INVALID_TELEPHONE_STARTS = Error.of("invalid.telephone", "Telephone number should starts with +7 or 8");
    public static final Error INVALID_IND_NUM_NULL = Error.of("invalid.individualNum", "If you are admin your individual number can't be null");
    public static final Error INVALID_IND_NUM_NOT_FOUND = Error.of("invalid.IndividualNum", "Such individual num doesn't belong to someone");
    public static final Error INVALID_PASSWORD = Error.of("invalid.password", "Your password is wrong");
}
