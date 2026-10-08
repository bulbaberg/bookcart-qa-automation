package tests.ui;

import data.LoginData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pages.LoginPage;
import pages.MainPage;
import pages.components.Header;


public class LoginTest extends BaseUiTest{

    LoginPage loginPage = new LoginPage();
    MainPage mainPage = new MainPage();
    Header header = new Header();

    @BeforeEach
    void setUp() {
        mainPage.openMainPage();
        mainPage.maxWindow();
        header.openLoginPage();
    }

    @Test
    @DisplayName("Авторизация пользователя")
    void shouldLoginAsAdmin() {

        loginPage.fillLoginForm(LoginData.ADMIN_USERNAME, LoginData.ADMIN_PASSWORD);
        loginPage.submitLogin();
        header.checkAuth();
    }

    @Test
    @DisplayName("Проверка ошибки авторизации пользователя с невалидным паролем")
    void shouldShowErrorForInvalidPassword() {

        loginPage.fillLoginForm(LoginData.ADMIN_USERNAME, LoginData.INVALID_PASSWORD);
        loginPage.submitLogin();
        loginPage.shouldHaveLoginError(LoginData.INVALID_LOGIN_ERROR);
    }
}
