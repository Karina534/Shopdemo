package service;

import org.example.shopdemo.dao.AdminsIndNumsDao;
import org.example.shopdemo.dto.AdminsDto;
import org.example.shopdemo.entity.Admins;
import org.example.shopdemo.entity.AdminsIndNums;
import org.example.shopdemo.service.AdminsService;
import org.example.shopdemo.validator.FindingAdminValidation;
import org.example.shopdemo.validator.LoginValidatorForAdmins;
import org.example.shopdemo.validator.RegistrationValidatorForAdmins;
import org.example.shopdemo.validator.ValidationResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

public class AdminsServiceTest {
    private AdminsService adminsService;
    @Mock
    private RegistrationValidatorForAdmins registrationValidator;

    @Mock
    private AdminsIndNumsDao adminsIndNumsDao;

    @Mock
    LoginValidatorForAdmins loginValidatorForAdmins;

    @Mock
    private FindingAdminValidation findingAdminValidation;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
        adminsService = AdminsService.getInstance();
        adminsService.setRegistrationValidator(registrationValidator);
        adminsService.setAdminsIndNumsDao(adminsIndNumsDao);
        adminsService.setLoginValidatorForAdmins(loginValidatorForAdmins);
        adminsService.setFindingAdminValidation(findingAdminValidation);
    }

    @Test
    void SuccessSaveRegistrationAdmin(){
        Long indNum = 123L;
        AdminsDto adminsDto = AdminsDto.builder().adminId(1L).name("name").surname("surname").email("email@mai.ru")
                .telephone("+79129049586").role("a").individualNum(indNum).build();

        Admins admins = new Admins(
                1L,
                "name",
                "surname",
                "email@mai.ru",
                "123456789",
                "+79129049586",
                "a",
                1L
        );
        Mockito.when(registrationValidator.isValid(adminsDto)).thenReturn(new ValidationResult());
        Mockito.when(adminsIndNumsDao.findByIndNum(indNum)).thenReturn(Optional.of(new AdminsIndNums(1L, 123L)));

        // Как проверить сохранение если не надо реально сохранять
    }

    @Test
    void SuccessCheckLogin(){
        Long indNum = 123L;
        AdminsDto adminsDto = AdminsDto.builder().adminId(1L).name("name").surname("surname").email("email@mai.ru")
                .telephone("+79129049586").role("a").individualNum(indNum).build();
        Mockito.when(loginValidatorForAdmins.isValid(adminsDto)).thenReturn(new ValidationResult());
        Mockito.when(findingAdminValidation.isValid(adminsDto)).thenReturn(new ValidationResult());

        Assertions.assertTrue(adminsService.checkLogin(adminsDto));
    }
}
