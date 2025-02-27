package org.example.shopdemo.validator;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.shopdemo.dao.AdminsIndNumsDao;
import org.example.shopdemo.dto.AdminsDto;
import org.example.shopdemo.service.LogService;
import org.example.shopdemo.utils.ValidationErrors;

@Setter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RegistrationValidatorForAdmins implements Validator<AdminsDto>{
    private final static RegistrationValidatorForAdmins INSTANCE = new RegistrationValidatorForAdmins();

    // Только для тестов
    private AdminsIndNumsDao adminsIndNumsDao = AdminsIndNumsDao.getInstance();

    public static RegistrationValidatorForAdmins getInstance(){
        return INSTANCE;
    }

    @Override
    public ValidationResult isValid(AdminsDto adminsDto) {
        LogService.logInfo("Starting admin registration validation.", "Admin email: %s",
                adminsDto.getEmail());
        ValidationResult validationResult = new ValidationResult();
        if (!adminsDto.getEmail().contains("@") || !adminsDto.getEmail().contains(".")){
            validationResult.add(ValidationErrors.INVALID_EMAIL);
        }
        if (adminsDto.getPassword().length() < 8){
            validationResult.add(ValidationErrors.INVALID_PASSWORD_LENGTH);
        }
        if (adminsDto.getTelephone().length() < 11){
            validationResult.add(ValidationErrors.INVALID_TELEPHONE_LENGTH);
        }
        if (!adminsDto.getTelephone().startsWith("+7") && !adminsDto.getTelephone().startsWith("8")){
            validationResult.add(ValidationErrors.INVALID_TELEPHONE_STARTS);
        }
        if (adminsDto.getIndividualNum() == null){
            validationResult.add(ValidationErrors.INVALID_IND_NUM_NULL);
        } else {
            if (!checkIndNum(adminsDto.getIndividualNum())) {
                validationResult.add(ValidationErrors.INVALID_IND_NUM_NOT_FOUND);
            }
        }
        LogService.logDebug("Admin registration validation ended.", "Admin email: %s," +
                " validationResult: %s", adminsDto.getEmail(), validationResult.getErrors());

        return validationResult;
    }

    private boolean checkIndNum(Long indNum){
        return adminsIndNumsDao.findByIndNum(indNum).isPresent();
    }
}
