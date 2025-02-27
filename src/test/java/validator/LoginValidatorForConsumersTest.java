package validator;

import org.example.shopdemo.dto.ConsumerDto;
import org.example.shopdemo.utils.ValidationErrors;
import org.example.shopdemo.validator.Error;
import org.example.shopdemo.validator.LoginValidatorForConsumers;
import org.example.shopdemo.validator.ValidationResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class LoginValidatorForConsumersTest {
    private final LoginValidatorForConsumers loginValidatorForConsumers = LoginValidatorForConsumers.getInstance();
    private final ValidationResult validationResult = new ValidationResult();

    @Test
    void SuccessLoginValidationConsumer(){
        ConsumerDto consumers = ConsumerDto.builder()
                .email("email@mail.ru")
                .password("17263543")
                .build();
        Assertions.assertEquals(validationResult.isValid(), loginValidatorForConsumers.isValid(consumers).isValid());
    }

    @Test
    void EmailErrorLoginValidationConsumer(){
        ConsumerDto consumers = ConsumerDto.builder()
                .email("email_mail.ru")
                .password("17263543")
                .build();
        ConsumerDto consumers2 = ConsumerDto.builder()
                .email("email@mailru")
                .password("17263543")
                .build();
        Error error = ValidationErrors.INVALID_EMAIL;
        Assertions.assertEquals(List.of(error), loginValidatorForConsumers.isValid(consumers).getErrors());
        Assertions.assertEquals(List.of(error), loginValidatorForConsumers.isValid(consumers2).getErrors());
    }

    @Test
    void PasswordEmptyErrorLoginValidationConsumer(){
        ConsumerDto consumers = ConsumerDto.builder()
                .email("email@mail.ru")
                .password("")
                .build();
        Error error = ValidationErrors.INVALID_PASSWORD;
        Assertions.assertEquals(List.of(error), loginValidatorForConsumers.isValid(consumers).getErrors());
    }
}
