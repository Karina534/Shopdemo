package org.example.shopdemo.service;

import org.example.shopdemo.dao.ConsumersDao;
import org.example.shopdemo.dto.ConsumerDto;
import org.example.shopdemo.entity.Consumers;
import org.example.shopdemo.exception.EntityNotFoundException;
import org.example.shopdemo.exception.ValidationException;
import org.example.shopdemo.validator.FindingConsumerValidation;
import org.example.shopdemo.validator.LoginValidatorForConsumers;
import org.example.shopdemo.validator.RegistrationValidatorForConsumers;
import org.slf4j.MDC;

import java.util.Optional;

public class ConsumersService {
    private final static ConsumersService INSTANCE = new ConsumersService();
    private final ConsumersDao consumersDao = ConsumersDao.getInstance();
    private final RegistrationValidatorForConsumers registrationValidator = RegistrationValidatorForConsumers.getInstance();
    private final LoginValidatorForConsumers loginValidatorForConsumers = LoginValidatorForConsumers.getInstance();
    private final FindingConsumerValidation validation = FindingConsumerValidation.getInstance();

    public Consumers saveRegistrationConsumer(ConsumerDto consumer){
        // Проверка формы на валидацию
        LogService.logInfo("Validation starts", "for consumer email: %s", consumer.getEmail());
        var validatorRes = registrationValidator.isValid(consumer);
        if (!validatorRes.isValid()){
            LogService.logWarn("Validation failed", "for consumer email: %s with such validationErrors: %s",
                    consumer.getEmail(), validatorRes.getErrors());
            throw new ValidationException(validatorRes.getErrors());
        }

        Consumers consumers = new Consumers(
                consumer.getConsumerId(),
                consumer.getConsumerName(),
                consumer.getSurname(),
                consumer.getEmail(),
                consumer.getPassword(),
                consumer.getTelephone(),
                consumer.getRole());

        LogService.logInfo("Validation was successful. Consumer saved.", "for consumer email: %s",
                consumer.getEmail());
        return consumersDao.save(consumers);
    }

    public boolean checkLogin(ConsumerDto consumer){
        LogService.logInfo("Validation starts", "for consumer email: %s", consumer.getEmail());
        var validatorRes = loginValidatorForConsumers.isValid(consumer);
        if (!validatorRes.isValid()){
            LogService.logWarn("Validation failed", "for consumer email: %s with such validationErrors: %s",
                    consumer.getEmail(), validatorRes.getErrors());
            throw new ValidationException(validatorRes.getErrors());
        }

        LogService.logInfo("Finding in bd starts", "for consumer email: %s", consumer.getConsumerId());
        var validationRes2 = validation.isValid(consumer);
        if (!validationRes2.isValid()){
            LogService.logWarn("Finding in bd failed", "for consumer email: %s, with such validationErrors: %s",
            consumer.getEmail(), validatorRes.getErrors());
            throw new ValidationException(validationRes2.getErrors());
        }
        LogService.logInfo("CheckLogin was successful", "for consumer email: %s.", consumer.getEmail());
        return true;
    }

    public Consumers getConsumerFromDto(ConsumerDto consumerDto){
        LogService.logInfo("Start find getConsumerFromDto", "for consumer email: %s",
                MDC.get("requestId"), consumerDto.getEmail());
        Optional<Consumers> consumer = consumersDao.findByEmailAndPassword(
                consumerDto.getEmail(), consumerDto.getPassword());
        if (consumer.isPresent()){
            return consumer.get();
        }
        LogService.logError("Consumer wasn't found", "with email: %s", consumerDto.getEmail());
        throw new EntityNotFoundException("I haven't found such consumer in bd");
    }

    public Optional<ConsumerDto> getConsumerById(Long id){
        LogService.logInfo("Start find Consumer in Dao by id.", "ConsumerId: ", id);
        var consumer = consumersDao.findById(id);
        if (consumer.isPresent()) {
            LogService.logInfo("Consumer in Dao by id successfully found.", "ConsumerId: ", id);
            return consumer.map(consumers -> ConsumerDto
                    .builder()
                    .consumerId(consumers.getConsumerId())
                    .consumerName(consumers.getName())
                    .surname(consumers.getSurname())
                    .email(consumers.getEmail())
                    .telephone(consumers.getTelephone())
                    .role(consumers.getRole())
                    .build());
        } else {
            LogService.logDebug("Consumer in Dao by id not found.", "ConsumerId: ", id);
            return Optional.empty();
        }
    }

    public void deleteConsumer(Long id){
        consumersDao.delete(id);
    }

    public static ConsumersService getInstance(){
        return INSTANCE;
    }
    private ConsumersService() {
    }
}
