package validator;

import org.example.shopdemo.dao.ConsumersDao;
import org.example.shopdemo.dto.ConsumerDto;
import org.example.shopdemo.entity.Consumers;
import org.example.shopdemo.utils.ValidationErrors;
import org.example.shopdemo.validator.Error;
import org.example.shopdemo.validator.FindingConsumerValidation;
import org.example.shopdemo.validator.ValidationResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

public class FindingConsumerValidationTest {
    private FindingConsumerValidation consumerValidation;
    @Mock
    ConsumersDao consumersDao;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
        consumerValidation = FindingConsumerValidation.getInstance();
        consumerValidation.setConsumersDao(consumersDao);
    }

    @Test
    void SuccessFindingConsumer(){
        String email = "email@mail.ru";
        String password = "234566789";
        ConsumerDto consumerDto = ConsumerDto.builder().email(email).password(password).build();
        Consumers consumers = new Consumers(1L, "name", "surname", email, password,
                "+79129345674", "u");

        Mockito.when(consumersDao.findByEmailAndPassword(email, password)).thenReturn(Optional.of(consumers));
        ValidationResult validationResult = new ValidationResult();
        Assertions.assertEquals(validationResult.isValid(), consumerValidation.isValid(consumerDto).isValid());
    }

    @Test
    void NotFoundFindingConsumer(){
        String email = "email@mail.ru";
        String password = "234566789";
        ConsumerDto consumerDto = ConsumerDto.builder().email(email).password(password).build();
        Mockito.when(consumersDao.findByEmailAndPassword(email, password)).thenReturn(Optional.empty());
        Error error = ValidationErrors.INVALID_EMAIL;
        Assertions.assertEquals(List.of(error), consumerValidation.isValid(consumerDto).getErrors());
    }
}
