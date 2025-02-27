package validator;

import org.example.shopdemo.dao.AdminsIndNumsDao;
import org.example.shopdemo.dto.AdminsDto;
import org.example.shopdemo.entity.AdminsIndNums;
import org.example.shopdemo.utils.ValidationErrors;
import org.example.shopdemo.validator.Error;
import org.example.shopdemo.validator.LoginValidatorForAdmins;
import org.example.shopdemo.validator.ValidationResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

public class LoginValidatorForAdminsTest {
    private final ValidationResult validationResult = new ValidationResult();
    @Mock
    private AdminsIndNumsDao adminsIndNumsDao;
    private LoginValidatorForAdmins loginValidatorForAdmins;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
        loginValidatorForAdmins = LoginValidatorForAdmins.getInstance();
        loginValidatorForAdmins.setAdminsIndNumsDao(adminsIndNumsDao); // Подменяем мок-объектом
    }

    @Test
    void SuccessLoginValidationCAdmin(){
        Long indNum = 123L;
        Mockito.when(adminsIndNumsDao.findByIndNum(indNum)).thenReturn(Optional.of(new AdminsIndNums()));
        AdminsDto adminsDto = AdminsDto.builder()
                .email("email@mail.ru")
                .password("1928374657")
                .individualNum(indNum)
                .build();
        Assertions.assertEquals(validationResult.isValid(), loginValidatorForAdmins.isValid(adminsDto).isValid());
        Mockito.verify(adminsIndNumsDao, Mockito.times(1)).findByIndNum(indNum);
    }

    @Test
    void EmailErrorLoginValidationAdmin(){
        Long indNum = 123L;
        Mockito.when(adminsIndNumsDao.findByIndNum(indNum)).thenReturn(Optional.of(new AdminsIndNums()));
        AdminsDto adminsDto = AdminsDto.builder()
                .email("email_mail.ru")
                .password("1928374657")
                .individualNum(indNum)
                .build();
        AdminsDto adminsDto2 = AdminsDto.builder()
                .email("email@mailru")
                .password("1928374657")
                .individualNum(indNum)
                .build();
        Error yourEmailAddressIsWrong = ValidationErrors.INVALID_EMAIL;
        Assertions.assertEquals(List.of(yourEmailAddressIsWrong), loginValidatorForAdmins.isValid(adminsDto).getErrors());
        Assertions.assertEquals(List.of(yourEmailAddressIsWrong), loginValidatorForAdmins.isValid(adminsDto2).getErrors());
        Mockito.verify(adminsIndNumsDao, Mockito.times(2)).findByIndNum(indNum);
    }

    @Test
    void PasswordEmptyErrorLoginValidationAdmin(){
        Long indNum = 123L;
        Mockito.when(adminsIndNumsDao.findByIndNum(indNum)).thenReturn(Optional.of(new AdminsIndNums()));
        AdminsDto adminsDto = AdminsDto.builder()
                .email("email@mail.ru")
                .password("")
                .individualNum(indNum)
                .build();
        Error yourEmailAddressIsWrong = ValidationErrors.INVALID_PASSWORD;
        Assertions.assertEquals(List.of(yourEmailAddressIsWrong), loginValidatorForAdmins.isValid(adminsDto).getErrors());
        Mockito.verify(adminsIndNumsDao, Mockito.times(1)).findByIndNum(indNum);
    }

    @Test
    void IndividualNumNullErrorLoginValidationAdmin(){
        AdminsDto adminsDto = AdminsDto.builder()
                .email("email@mail.ru")
                .password("1928374657")
                .individualNum(null)
                .build();
        Error yourEmailAddressIsWrong = ValidationErrors.INVALID_IND_NUM_NULL;
        Assertions.assertEquals(List.of(yourEmailAddressIsWrong), loginValidatorForAdmins.isValid(adminsDto).getErrors());
    }

    @Test
    void IndividualNumNotInBdLoginValidationAdmin(){
        Long indNum = 687L;
        Mockito.when(adminsIndNumsDao.findByIndNum(indNum)).thenReturn(Optional.empty());
        Assertions.assertFalse(loginValidatorForAdmins.checkIndNum(indNum));
        Mockito.verify(adminsIndNumsDao, Mockito.times(1)).findByIndNum(indNum);
    }

    @Test
    void IndividualNumIsInBdLoginValidationAdmin(){
        Long indNum = 687L;
        Mockito.when(adminsIndNumsDao.findByIndNum(indNum)).thenReturn(Optional.of(new AdminsIndNums()));
        Assertions.assertTrue(loginValidatorForAdmins.checkIndNum(indNum));
        Mockito.verify(adminsIndNumsDao, Mockito.times(1)).findByIndNum(indNum);
    }
}
