package org.example.shopdemo.validator;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.shopdemo.dao.ConsumersDao;
import org.example.shopdemo.dto.ConsumerDto;
import org.example.shopdemo.entity.Consumers;
import org.example.shopdemo.service.LogService;
import org.example.shopdemo.utils.ValidationErrors;

import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FindingConsumerValidation implements Validator<ConsumerDto>{
    private final static FindingConsumerValidation INSTANCE = new FindingConsumerValidation();

    @Setter // Только для тестов
    private ConsumersDao consumersDao = ConsumersDao.getInstance();

    @Override
    public ValidationResult isValid(ConsumerDto consumer) {
        LogService.logInfo("Finding consumer by email and password in bd starts.",
                "Consumer email: %s", consumer.getEmail());
        ValidationResult validationResult = new ValidationResult();
        Optional<Consumers> consumerCheck = consumersDao.findByEmailAndPassword(consumer.getEmail(), consumer.getPassword());
        if (consumerCheck.isEmpty()){
            validationResult.add(ValidationErrors.INVALID_EMAIL);
        }
        LogService.logDebug("Finding consumer by email and password in bd ended.",
                "Consumer email: %s", consumer.getEmail());
        return validationResult;
    }

    public static FindingConsumerValidation getInstance(){
        return INSTANCE;
    }
}
