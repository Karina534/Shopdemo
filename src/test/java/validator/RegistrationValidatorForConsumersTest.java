package validator;

import org.example.shopdemo.dto.ConsumerDto;
import org.example.shopdemo.utils.ValidationErrors;
import org.example.shopdemo.validator.Error;
import org.example.shopdemo.validator.RegistrationValidatorForConsumers;
import org.example.shopdemo.validator.ValidationResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class RegistrationValidatorForConsumersTest {
    private final RegistrationValidatorForConsumers validatorForConsumers = RegistrationValidatorForConsumers.getInstance();
    private final ValidationResult validationResult = new ValidationResult();
    @Test
    void SuccessRegistrationValidationForConsumer(){
        ConsumerDto consumerDto = ConsumerDto
                .builder()
                .email("email@mail.ru")
                .password("348563820")
                .telephone("+79139081402")
                .build();
        Assertions.assertEquals(validationResult.isValid(), validatorForConsumers.isValid(consumerDto).isValid());
    }

    @Test
    void EmailErrorRegistrationValidationForConsumer(){
        ConsumerDto consumers = ConsumerDto.builder()
                .email("email_mail.ru")
                .password("17263543")
                .telephone("+79139081402")
                .build();
        ConsumerDto consumers2 = ConsumerDto.builder()
                .email("email@mailru")
                .password("17263543")
                .telephone("+79139081402")
                .build();
        Error error = ValidationErrors.INVALID_EMAIL;
        Assertions.assertEquals(List.of(error), validatorForConsumers.isValid(consumers).getErrors());
        Assertions.assertEquals(List.of(error), validatorForConsumers.isValid(consumers2).getErrors());
    }

    @Test
    void PasswordLengthErrorLoginValidationConsumer(){
        ConsumerDto consumers = ConsumerDto.builder()
                .email("email@mail.ru")
                .password("123")
                .telephone("+79139081402")
                .build();
        Error error = ValidationErrors.INVALID_PASSWORD_LENGTH;
        Assertions.assertEquals(List.of(error), validatorForConsumers.isValid(consumers).getErrors());
    }

    @Test
    void TelephoneErrorLoginValidationConsumer(){
        ConsumerDto consumers = ConsumerDto.builder()
                .email("email@mail.ru")
                .password("123234566")
                .telephone("9139081402")
                .build();
        Error error = ValidationErrors.INVALID_TELEPHONE_LENGTH;
        Error error2 = ValidationErrors.INVALID_TELEPHONE_STARTS;
        Assertions.assertEquals(List.of(error, error2), validatorForConsumers.isValid(consumers).getErrors());
    }

    @Test
    void TelephoneLengthErrorLoginValidationConsumer(){
        ConsumerDto consumers = ConsumerDto.builder()
                .email("email@mail.ru")
                .password("123234566")
                .telephone("+739081402")
                .build();
        Error error = ValidationErrors.INVALID_TELEPHONE_LENGTH;
        Assertions.assertEquals(List.of(error), validatorForConsumers.isValid(consumers).getErrors());
    }

    @Test
    void TelephoneStartWithErrorLoginValidationConsumer(){
        ConsumerDto consumers = ConsumerDto.builder()
                .email("email@mail.ru")
                .password("123234566")
                .telephone("98739081402")
                .build();
        Error error = ValidationErrors.INVALID_TELEPHONE_STARTS;
        Assertions.assertEquals(List.of(error), validatorForConsumers.isValid(consumers).getErrors());
    }
}
