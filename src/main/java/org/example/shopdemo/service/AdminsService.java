package org.example.shopdemo.service;

import lombok.Setter;
import org.example.shopdemo.dao.AdminsDao;
import org.example.shopdemo.dao.AdminsIndNumsDao;
import org.example.shopdemo.dto.AdminsDto;
import org.example.shopdemo.entity.Admins;
import org.example.shopdemo.entity.AdminsIndNums;
import org.example.shopdemo.exception.EntityNotFoundException;
import org.example.shopdemo.exception.ValidationException;
import org.example.shopdemo.validator.FindingAdminValidation;
import org.example.shopdemo.validator.LoginValidatorForAdmins;
import org.example.shopdemo.validator.RegistrationValidatorForAdmins;
import org.slf4j.MDC;

import java.util.Optional;

public class AdminsService {
    private final static AdminsService INSTANCE = new AdminsService();

    @Setter // Только для тестов
    private RegistrationValidatorForAdmins registrationValidator = RegistrationValidatorForAdmins.getInstance();

    @Setter // Только для тестов
    private LoginValidatorForAdmins loginValidatorForAdmins = LoginValidatorForAdmins.getInstance();
    private final AdminsDao adminsDao = AdminsDao.getInstance();

    @Setter // Только для тестов
    private AdminsIndNumsDao adminsIndNumsDao = AdminsIndNumsDao.getInstance();

    @Setter // Только для тестов
    private FindingAdminValidation findingAdminValidation = FindingAdminValidation.getInstance();

    public Admins saveRegistrationAdmin(AdminsDto adminsDto){
        // Проверка формы на валидацию
        LogService.logInfo("Validation starts", "for admin email: %s", adminsDto.getEmail());
        var registrationVal = registrationValidator.isValid(adminsDto);
        if (!registrationVal.isValid()){
            LogService.logWarn("Validation failed", "for admin email: %s with such validationErrors: %s",
                    adminsDto.getEmail(), registrationVal.getErrors());
            throw new ValidationException(registrationVal.getErrors());
        }

        LogService.logInfo("Send to IndNumsDao to find individual number.", "Admin email: %s",
                adminsDto.getEmail());
        Optional<AdminsIndNums> indNumObj = adminsIndNumsDao.findByIndNum(adminsDto.getIndividualNum());

        Admins admins = new Admins(
                adminsDto.getAdminId(),
                adminsDto.getName(),
                adminsDto.getSurname(),
                adminsDto.getEmail(),
                adminsDto.getPassword(),
                adminsDto.getTelephone(),
                adminsDto.getRole(),
                indNumObj.get().getAdminsIndNumsId()
        );
        LogService.logInfo("Validation was successful. Admin saved.", "for admin email: %s",
                adminsDto.getEmail());
        return adminsDao.save(admins);
    }

    public boolean checkLogin(AdminsDto adminsDto){
        LogService.logInfo("Validation starts", "for admin email: %s", adminsDto.getEmail());
        var validatorRes = loginValidatorForAdmins.isValid(adminsDto);
        if (!validatorRes.isValid()){
            LogService.logWarn("Validation failed", "for admin email: %s with such validationErrors: %s",
                    adminsDto.getEmail(), validatorRes.getErrors());
            throw new ValidationException(validatorRes.getErrors());
        }

        LogService.logInfo("Finding in bd starts", "for admin email: %s", adminsDto.getEmail());
        var validatorRes2 = findingAdminValidation.isValid(adminsDto);
        if (!validatorRes2.isValid()){
            LogService.logWarn("Finding in bd failed", "for admin email: %s, with such validationErrors: %s",
                    adminsDto.getEmail(), validatorRes.getErrors());
            throw new ValidationException(validatorRes2.getErrors());
        }
        LogService.logInfo("CheckLogin was successful", "for admin email: %s.", adminsDto.getEmail());
        return true;
    }

    public Admins getAdminFromDto(AdminsDto adminsDto){
        LogService.logInfo("Start find getAdminFromDto", "for admin email: %s",
                MDC.get("requestId"), adminsDto.getEmail());
        Optional<Admins> admin = adminsDao.findByEmailAndPassword(adminsDto.getEmail(), adminsDto.getPassword());
        if (admin.isPresent()){
            return admin.get();
        }
        LogService.logError("Admin wasn't found", "with email: %s", adminsDto.getEmail());
        throw new EntityNotFoundException("I haven't found such admin in bd");
    }

    public Optional<AdminsDto> getAdminById(Long id){
        LogService.logInfo("Start find Admin in Dao by id.", "AdminId: ", id);
        var admin = adminsDao.findById(id);
        if (admin.isPresent()) {
            LogService.logInfo("Admin in Dao by id successfully found.", "AdminId: ", id);
            return admin.map(admins -> AdminsDto
                    .builder()
                    .adminId(admins.getAdminId())
                    .name(admins.getName())
                    .surname(admins.getSurname())
                    .email(admins.getEmail())
                    .password(admins.getPassword())
                    .telephone(admins.getTelephone())
                    .role(admins.getRole())
                    .individualNum(admins.getIndividualNumId())
                    .build());
        } else {
            LogService.logDebug("Admin in Dao by id not found.", "AdminId: ", id);
            return Optional.empty();
        }
    }

    public static AdminsService getInstance(){
        return INSTANCE;
    }
    private AdminsService() {
    }
}
