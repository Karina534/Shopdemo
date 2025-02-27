package validator;

import org.example.shopdemo.dao.AdminsDao;
import org.example.shopdemo.dao.AdminsIndNumsDao;
import org.example.shopdemo.dto.AdminsDto;
import org.example.shopdemo.entity.Admins;
import org.example.shopdemo.entity.AdminsIndNums;
import org.example.shopdemo.utils.ValidationErrors;
import org.example.shopdemo.validator.Error;
import org.example.shopdemo.validator.FindingAdminValidation;
import org.example.shopdemo.validator.ValidationResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

public class FindingAdminValidationTest {
    private FindingAdminValidation adminValidation;

    @Mock
    private AdminsDao adminsDao;

    @Mock
    private AdminsIndNumsDao adminsIndNumsDao;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
        adminValidation = FindingAdminValidation.getInstance();
        adminValidation.setAdminsDao(adminsDao);
        adminValidation.setAdminsIndNumsDao(adminsIndNumsDao);
    }

    @Test
    void SuccessFindingAdmin(){
        Long indNum = 123L;
        String email = "email@mail.ru";
        String password = "1928374657";
        Admins admin = new Admins(1L, "Name", "Surname", email, password,
                "+79129345463", "a", 1L);
        AdminsDto adminsDto = AdminsDto.builder()
                .adminId(1L)
                .name("Name")
                .surname("Surname")
                .email(email)
                .password(password)
                .telephone("+79129345463")
                .role("a")
                .individualNum(indNum).build();

        Mockito.when(adminsDao.findByEmailAndPassword(email, password)).thenReturn(Optional.of(admin));
        Mockito.when(adminsIndNumsDao.findById(1L)).thenReturn(Optional.of(new AdminsIndNums(1L, indNum)));
        ValidationResult validationResult = new ValidationResult();
        Assertions.assertEquals(validationResult.isValid(), adminValidation.isValid(adminsDto).isValid());
        Mockito.verify(adminsDao).findByEmailAndPassword(email, password);
        Mockito.verify(adminsIndNumsDao).findById(1L);
    }

    @Test
    void AdminNotFoundErrorFindingAdmin(){
        Long indNum = 123L;
        String email = "email@mail.ru";
        String password = "1928374657";
        AdminsDto adminsDto = AdminsDto.builder()
                .adminId(1L)
                .name("Name")
                .surname("Surname")
                .email(email)
                .password(password)
                .telephone("+79129345463")
                .role("a")
                .individualNum(indNum).build();

        Error error = ValidationErrors.INVALID_EMAIL;
        Mockito.when(adminsDao.findByEmailAndPassword(email, password)).thenReturn(Optional.empty());
        Assertions.assertEquals(List.of(error), adminValidation.isValid(adminsDto).getErrors());
        Mockito.verify(adminsDao).findByEmailAndPassword(email, password);
    }

    @Test
    void NullIndNumErrorFindingAdmin(){
        Long indNum = 123L;
        String email = "email@mail.ru";
        String password = "1928374657";
        Admins admin = new Admins(1L, "Name", "Surname", email, password,
                "+79129345463", "a", 1L);
        AdminsDto adminsDto = AdminsDto.builder()
                .adminId(1L)
                .name("Name")
                .surname("Surname")
                .email(email)
                .password(password)
                .telephone("+79129345463")
                .role("a")
                .individualNum(indNum).build();

        Error error = ValidationErrors.INVALID_IND_NUM_NOT_FOUND;
        Mockito.when(adminsDao.findByEmailAndPassword(email, password)).thenReturn(Optional.of(admin));
        Mockito.when(adminsIndNumsDao.findById(1L)).thenReturn(Optional.empty());

        Assertions.assertEquals(List.of(error), adminValidation.isValid(adminsDto).getErrors());
        Mockito.verify(adminsDao).findByEmailAndPassword(email, password);
        Mockito.verify(adminsIndNumsDao).findById(1L);
    }

    @Test
    void WrongIndNumErrorFindingAdmin(){
        Long indNum = 123L;
        String email = "email@mail.ru";
        String password = "1928374657";
        Admins admin = new Admins(1L, "Name", "Surname", email, password,
                "+79129345463", "a", 1L);
        AdminsDto adminsDto = AdminsDto.builder()
                .adminId(1L)
                .name("Name")
                .surname("Surname")
                .email(email)
                .password(password)
                .telephone("+79129345463")
                .role("a")
                .individualNum(indNum).build();

        Mockito.when(adminsDao.findByEmailAndPassword(email, password)).thenReturn(Optional.of(admin));
        Mockito.when(adminsIndNumsDao.findById(1L)).thenReturn(Optional.of(new AdminsIndNums(1L, 345L)));
        Error error = ValidationErrors.INVALID_IND_NUM_NOT_FOUND;
        Assertions.assertEquals(List.of(error), adminValidation.isValid(adminsDto).getErrors());
        Mockito.verify(adminsDao).findByEmailAndPassword(email, password);
        Mockito.verify(adminsIndNumsDao).findById(1L);
    }
}
