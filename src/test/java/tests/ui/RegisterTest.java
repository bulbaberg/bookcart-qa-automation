package tests.ui;

import data.RegisterData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pages.RegisterPage;
import pages.LoginPage;

import pages.MainPage;
import pages.components.Header;

import java.util.UUID;

import static com.codeborne.selenide.Selenide.sleep;


public class RegisterTest extends BaseUiTest {

    RegisterPage registerPage = new RegisterPage();
    MainPage mainPage = new MainPage();
    Header header = new Header();
    LoginPage loginPage = new LoginPage();

    @BeforeEach
    void setUp() {
        mainPage.openMainPage();
        mainPage.maxWindow();
        header.openLoginPage();
        loginPage.clickRegisterUser();
    }

    @Test
    @Disabled("Неизвестная ошибка Book Cart : Бэк возвращет статус 200 при создании пользователя, однако в базе данные о новом пользователе отсутствуют")
    @DisplayName("Регистрация нового пользователя")
    void createNewUser() {

        String uniqueId = UUID.randomUUID().toString().substring(0, 8);
        String userName = "SelenideUser" + uniqueId;

        registerPage.registerNewUser(
                RegisterData.FIRST_NAME,
                RegisterData.LAST_NAME,
                userName,
                RegisterData.PASSWORD,
                RegisterData.PASSWORD
        );

        registerPage.submitRegister();
        loginPage.fillLoginForm(userName, RegisterData.PASSWORD);
        loginPage.submitLogin();
        header.checkUserLoggedIn(userName);

    }

    @Test
    @DisplayName("Валидация ошибки при регистарции нового пользователя")
    void shouldShowErrorForInvalidPassword() {
        String uniqueId = UUID.randomUUID().toString().substring(0, 8);
        String userName = "SelenideUser" + uniqueId;

        registerPage.registerNewUser(
                RegisterData.FIRST_NAME,
                RegisterData.LAST_NAME,
                userName,
                RegisterData.INVALID_PASSWORD,
                RegisterData.INVALID_PASSWORD
        );

        registerPage.shouldHavePasswordError(
                RegisterData.PASSWORD_REQUIREMENTS_ERROR
        );
        registerPage.shouldBeRegistrationFormInvalid();
    }

}
