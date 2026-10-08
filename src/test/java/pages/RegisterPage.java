package pages;

import io.qameta.allure.Step;

import java.time.Duration;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Condition.cssClass;
import static com.codeborne.selenide.Selenide.$$;

public class RegisterPage {

    private static final String FIRST_NAME = "input[placeholder='First name']";
    private static final String LAST_NAME = "input[placeholder='Last Name']";
    private static final String USER_NAME = "input[placeholder='User name']";
    private static final String PASSWORD = "input[placeholder='Password']";
    private static final String CONF_PASSWORD = "input[placeholder='Confirm Password']";
    private static final String FEMALE = "mat-radio-button[value='Female']";
    private static final String MALE = "mat-radio-button[value='Male']";
    private static final String SUBMIT_REGISTER = "app-user-registration mat-card-actions button";
    private static final String REGISTRATION_FORM = "app-user-registration form";
    private static final String VALIDATION_ERRORS = "app-user-registration mat-error";


    private void setFirstName(String fName) {
        $(FIRST_NAME)
                .shouldBe(visible)
                .setValue(fName);
    }

    private void setLastName(String lName) {
        $(LAST_NAME)
                .shouldBe(visible)
                .setValue(lName);
    }

    private void setUserName(String userName) {
        $(USER_NAME)
                .shouldBe(visible)
                .setValue(userName);
    }

    private void setPassword(String password) {
        $(PASSWORD)
                .shouldBe(visible)
                .setValue(password);
    }

    private void setConfPassword(String confPassword) {
        $(CONF_PASSWORD)
                .shouldBe(visible)
                .setValue(confPassword);
    }

    private void setGender() {
        $(FEMALE)
                .shouldBe(visible)
                .click();
    }

    private void waitUntilFormValid() {
        $(REGISTRATION_FORM)
                .shouldHave(cssClass("ng-valid"), Duration.ofSeconds(10));
    }

    @Step("Проверка текста ошибки")
    public void shouldHavePasswordError(String messageError) {
        $$(VALIDATION_ERRORS)
                .findBy(exactText(messageError))
                .shouldBe(visible);
    }

    public void shouldBeRegistrationFormInvalid() {
        $(REGISTRATION_FORM)
                .shouldHave(cssClass("ng-invalid"));
    }

    @Step("Заполнение формы Регистрация нового пользователя")
    public void registerNewUser(
            String fName,
            String lName,
            String userName,
            String password,
            String confPassword
            ) {

        setFirstName(fName);
        setLastName(lName);
        setUserName(userName);
        setPassword(password);
        setConfPassword(confPassword);
        setGender();
    }

    @Step("Нажать на кнопку Подтвердить регистрацию")
    public void submitRegister() {
        waitUntilFormValid();
        $(SUBMIT_REGISTER)
                .shouldBe(visible, enabled)
                .shouldHave(exactText("Register"))
                .click();
    }

}
