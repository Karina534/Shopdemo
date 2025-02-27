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
public class LoginValidatorForAdmins implements Validator<AdminsDto>{
    private final static LoginValidatorForAdmins INSTANCE = new LoginValidatorForAdmins();

    // Только для тестов!
    private AdminsIndNumsDao adminsIndNumsDao = AdminsIndNumsDao.getInstance();

    @Override
    public ValidationResult isValid(AdminsDto adminsDto) {
        LogService.logInfo("Starting admin login validation.", "Admin email: %s", adminsDto.getEmail());
        ValidationResult validationResult = new ValidationResult();
        if (!adminsDto.getEmail().contains("@") || !adminsDto.getEmail().contains(".")){
            validationResult.add(ValidationErrors.INVALID_EMAIL);
        }
        if (adminsDto.getPassword().isBlank()){
            validationResult.add(ValidationErrors.INVALID_PASSWORD);
        }
        if (adminsDto.getIndividualNum() == null){
            validationResult.add(ValidationErrors.INVALID_IND_NUM_NULL);
        } else {
            if (!checkIndNum(adminsDto.getIndividualNum())) {
                validationResult.add(ValidationErrors.INVALID_IND_NUM_NOT_FOUND);
            }
        }
        LogService.logDebug("Admin login validation ended.", "Admin email: %s, validationResult: %s",
                adminsDto.getEmail(), validationResult.getErrors());
        return validationResult;
    }

    public boolean checkIndNum(Long indNum){
        return adminsIndNumsDao.findByIndNum(indNum).isPresent();
    }

    public static LoginValidatorForAdmins getInstance(){
        return INSTANCE;
    }
}
