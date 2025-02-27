package validator;

import org.example.shopdemo.dao.AdminsIndNumsDao;
import org.example.shopdemo.dto.AdminsDto;
import org.example.shopdemo.dto.ConsumerDto;
import org.example.shopdemo.entity.AdminsIndNums;
import org.example.shopdemo.utils.ValidationErrors;
import org.example.shopdemo.validator.Error;
import org.example.shopdemo.validator.RegistrationValidatorForAdmins;
import org.example.shopdemo.validator.ValidationResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

public class RegistrationValidatorForAdminsTest {
    RegistrationValidatorForAdmins validatorForAdmins;
    private final ValidationResult validationResult = new ValidationResult();
    @Mock
    private AdminsIndNumsDao adminsIndNumsDao;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
        validatorForAdmins = RegistrationValidatorForAdmins.getInstance();
        validatorForAdmins.setAdminsIndNumsDao(adminsIndNumsDao);
    }


    @Test
    void SuccessRegistrationValidationForAdmin(){
        Long IndNum = 123L;
        Mockito.when(adminsIndNumsDao.findByIndNum(IndNum)).thenReturn(Optional.of(new AdminsIndNums()));
        AdminsDto adminsDto = AdminsDto.builder()
                .email("email@mail.ru")
                .password("91827364")
                .telephone("+79134567845")
                .individualNum(IndNum).build();
        Assertions.assertEquals(validationResult.isValid(), validatorForAdmins.isValid(adminsDto).isValid());
        Mockito.verify(adminsIndNumsDao, Mockito.times(1)).findByIndNum(IndNum);
    }

    @Test
    void EmailErrorRegistrationValidationForAdmin(){
        Long IndNum = 123L;
        Mockito.when(adminsIndNumsDao.findByIndNum(IndNum)).thenReturn(Optional.of(new AdminsIndNums()));
        AdminsDto adminsDto = AdminsDto.builder()
                .email("email_mail.ru")
                .password("17263543")
                .telephone("+79139081402")
                .individualNum(IndNum).build();
        AdminsDto adminsDto2 = AdminsDto.builder()
                .email("email@mailru")
                .password("17263543")
                .telephone("+79139081402")
                .individualNum(IndNum).build();

        Error error = ValidationErrors.INVALID_EMAIL;
        Assertions.assertEquals(List.of(error), validatorForAdmins.isValid(adminsDto).getErrors());
        Assertions.assertEquals(List.of(error), validatorForAdmins.isValid(adminsDto2).getErrors());
        Mockito.verify(adminsIndNumsDao, Mockito.times(2)).findByIndNum(IndNum);
    }

    @Test
    void PasswordLengthErrorRegistrationValidationForAdmin(){
        Long IndNum = 123L;
        Mockito.when(adminsIndNumsDao.findByIndNum(IndNum)).thenReturn(Optional.of(new AdminsIndNums()));
        AdminsDto adminsDto = AdminsDto.builder()
                .email("email@mail.ru")
                .password("172633")
                .telephone("+79139081402")
                .individualNum(IndNum).build();
        Error error = ValidationErrors.INVALID_PASSWORD_LENGTH;
        Assertions.assertEquals(List.of(error), validatorForAdmins.isValid(adminsDto).getErrors());
        Mockito.verify(adminsIndNumsDao).findByIndNum(IndNum);
    }

    @Test
    void TelephoneLengthErrorRegistrationValidationAdmin(){
        Long IndNum = 123L;
        Mockito.when(adminsIndNumsDao.findByIndNum(IndNum)).thenReturn(Optional.of(new AdminsIndNums()));
        AdminsDto adminsDto = AdminsDto.builder()
                .email("email@mail.ru")
                .password("17267633")
                .telephone("+791381402")
                .individualNum(IndNum).build();
        Error error = ValidationErrors.INVALID_TELEPHONE_LENGTH;
        Assertions.assertEquals(List.of(error), validatorForAdmins.isValid(adminsDto).getErrors());
        Mockito.verify(adminsIndNumsDao).findByIndNum(IndNum);
    }

    @Test
    void TelephoneStartsErrorRegistrationValidationAdmin(){
        Long IndNum = 123L;
        Mockito.when(adminsIndNumsDao.findByIndNum(IndNum)).thenReturn(Optional.of(new AdminsIndNums()));
        AdminsDto adminsDto = AdminsDto.builder()
                .email("email@mail.ru")
                .password("17263093")
                .telephone("91381402947")
                .individualNum(IndNum).build();
        Error error = ValidationErrors.INVALID_TELEPHONE_STARTS;
        Assertions.assertEquals(List.of(error), validatorForAdmins.isValid(adminsDto).getErrors());
        Mockito.verify(adminsIndNumsDao).findByIndNum(IndNum);
    }

    @Test
    void IndNumNullErrorRegistrationValidationAdmin(){
        AdminsDto adminsDto = AdminsDto.builder()
                .email("email@mail.ru")
                .password("17209633")
                .telephone("+79139081402")
                .individualNum(null).build();
        Error error = ValidationErrors.INVALID_IND_NUM_NULL;
        Assertions.assertEquals(List.of(error), validatorForAdmins.isValid(adminsDto).getErrors());
    }

    @Test
    void IndNumNotFoundErrorRegistrationValidationAdmin(){
        Long IndNum = 564L;
        Mockito.when(adminsIndNumsDao.findByIndNum(IndNum)).thenReturn(Optional.empty());
        AdminsDto adminsDto = AdminsDto.builder()
                .email("email@mail.ru")
                .password("17260933")
                .telephone("+79139081402")
                .individualNum(IndNum).build();
        Error error = ValidationErrors.INVALID_IND_NUM_NOT_FOUND;
        Assertions.assertEquals(List.of(error), validatorForAdmins.isValid(adminsDto).getErrors());
        Mockito.verify(adminsIndNumsDao).findByIndNum(IndNum);
    }
}
