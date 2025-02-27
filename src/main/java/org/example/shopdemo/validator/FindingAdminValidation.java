package org.example.shopdemo.validator;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.shopdemo.dao.AdminsDao;
import org.example.shopdemo.dao.AdminsIndNumsDao;
import org.example.shopdemo.dto.AdminsDto;
import org.example.shopdemo.entity.Admins;
import org.example.shopdemo.entity.AdminsIndNums;
import org.example.shopdemo.service.LogService;
import org.example.shopdemo.utils.ValidationErrors;

import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FindingAdminValidation implements Validator<AdminsDto>{
    private final static FindingAdminValidation INSTANCE = new FindingAdminValidation();

    @Setter // Только для тестов
    private AdminsDao adminsDao = AdminsDao.getInstance();

    @Setter // Только для тестов
    private AdminsIndNumsDao adminsIndNumsDao = AdminsIndNumsDao.getInstance();

    @Override
    public ValidationResult isValid(AdminsDto admins) {
        LogService.logInfo("Finding admin by email and password in bd starts.",
                "Admin email: %s", admins.getEmail());
        ValidationResult validationResult = new ValidationResult();
        Optional<Admins> adminCheck = adminsDao.findByEmailAndPassword(admins.getEmail(), admins.getPassword());
        if (adminCheck.isEmpty()){
            validationResult.add(ValidationErrors.INVALID_EMAIL);
        }else {
            LogService.logDebug("Starts checking individual admin number in bd",
                    "IndNum: %s, admin email: %s", admins.getIndividualNum(), admins.getEmail());
            Optional<AdminsIndNums> adminsIndNums =  adminsIndNumsDao.findById(adminCheck.get().getIndividualNumId());
            if (adminsIndNums.isEmpty() || !admins.getIndividualNum().equals(adminsIndNums.get().getIndNum())){
                validationResult.add(ValidationErrors.INVALID_IND_NUM_NOT_FOUND);
            }
        }
        LogService.logDebug("Finding admin by email and password in bd ended.",
                "Admin email: %s", admins.getEmail());
        return validationResult;
    }
    public static FindingAdminValidation getInstance(){
        return INSTANCE;
    }
}
