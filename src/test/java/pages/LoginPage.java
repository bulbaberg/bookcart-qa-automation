package pages;



import io.qameta.allure.Step;

import java.time.Duration;

import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;


public class LoginPage {

    private static final String USERNAME_INPUT = "input[formcontrolname='username']";
    private static final String PASSWORD_INPUT = "input[formcontrolname='password']";
    private static final String REGISTER = "app-login mat-card-header button";
    private static final String SUBMIT_BUTTON = "app-login form button";
    private static final String LOGIN_ERROR = "app-login mat-error";

    //Находим и вводим логин
    private void setUsername(String username) {

        $(USERNAME_INPUT)
                .shouldBe(visible)
                .setValue(username);
    }

    //Находим и вводим пароль
    private void setPassword(String password) {

        $(PASSWORD_INPUT)
                .shouldBe(visible)
                .setValue(password);
    }

    @Step("Нажимаем на кнопку авторизации")
    public void submitLogin() {
        $(SUBMIT_BUTTON)
                .shouldBe(visible)
                .click();
    }

    @Step("Проверяем ошибку при авторизации")
    public void shouldHaveLoginError(String expectedText) {

                $(LOGIN_ERROR)
                .shouldBe(visible)
                .shouldHave(exactText(expectedText));
    }

    @Step("Заполняем поля логин и пароль")
    public void fillLoginForm(String username, String password) {

        setUsername(username);
        setPassword(password);
    }

    @Step("Нажать на кнопку регистрация")
    public void clickRegisterUser() {
        $(REGISTER)
                .shouldBe(visible)
                .click();
    }


}
