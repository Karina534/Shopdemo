package org.example.shopdemo.validator;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.java.Log;
import org.example.shopdemo.dto.ConsumerDto;
import org.example.shopdemo.service.LogService;
import org.example.shopdemo.utils.ValidationErrors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RegistrationValidatorForConsumers implements Validator<ConsumerDto>{
    private final static RegistrationValidatorForConsumers INSTANCE = new RegistrationValidatorForConsumers();

    public static RegistrationValidatorForConsumers getInstance(){
        return INSTANCE;
    }

    @Override
    public ValidationResult isValid(ConsumerDto consumers) {
        LogService.logInfo("Starting consumer registration validation.", "Consumer email: %s",
                consumers.getEmail());
        ValidationResult validationResult = new ValidationResult();
        if (!consumers.getEmail().contains("@") || !consumers.getEmail().contains(".")){
            validationResult.add(ValidationErrors.INVALID_EMAIL);
        }
        if (consumers.getPassword().length() < 8){
            validationResult.add(ValidationErrors.INVALID_PASSWORD_LENGTH);
        }
        if (consumers.getTelephone().length() < 11){
            validationResult.add(ValidationErrors.INVALID_TELEPHONE_LENGTH);
        }
        if (!consumers.getTelephone().startsWith("+7") && !consumers.getTelephone().startsWith("8")){
            validationResult.add(ValidationErrors.INVALID_TELEPHONE_STARTS);
        }
        LogService.logDebug("Consumer registration validation ended.", "Consumer email: %s," +
                " validationResult: %s", consumers.getEmail(), validationResult.getErrors());

        return validationResult;
    }
}
