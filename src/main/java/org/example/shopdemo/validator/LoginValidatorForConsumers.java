package org.example.shopdemo.validator;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.example.shopdemo.dto.ConsumerDto;
import org.example.shopdemo.service.LogService;
import org.example.shopdemo.utils.ValidationErrors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LoginValidatorForConsumers implements Validator<ConsumerDto>{
    private final static LoginValidatorForConsumers INSTANCE = new LoginValidatorForConsumers();
    @Override
    public ValidationResult isValid(ConsumerDto consumers) {
        LogService.logInfo("Starting consumer login validation.", "Consumer email: %s", consumers.getEmail());

        ValidationResult validationResult = new ValidationResult();
        if (!consumers.getEmail().contains("@") || !consumers.getEmail().contains(".")){
            validationResult.add(ValidationErrors.INVALID_EMAIL);
        }
        if (consumers.getPassword().isBlank()){
            validationResult.add(ValidationErrors.INVALID_PASSWORD);
        }
        LogService.logDebug("Consumer login validation ended.", "Consumer email: %s," +
                " validationResult: %s", consumers.getEmail(), validationResult.getErrors());
        return validationResult;
    }

    public static LoginValidatorForConsumers getInstance(){
        return INSTANCE;
    }
}
