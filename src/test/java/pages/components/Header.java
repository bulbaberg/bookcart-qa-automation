package pages.components;

import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class Header {

    private static final String ADMIN_PANEL = "button[mattooltip='Admin panel']";
    private static final String LOGIN_BUTTON = "button[mattooltip='Login']";
    private static final String SEARCH = "input[placeholder='Search books or authors']";


    //Проверяем успешную авторизацию
    public void checkAuth() {
        // Проверяем успешную авторизацию
        $(ADMIN_PANEL)
                .shouldBe(visible);
    }

    @Step("Переходим на страницу авторизации")
    public void openLoginPage() {
        $(LOGIN_BUTTON)
                .shouldBe(visible)
                .click();
    }

    @Step("Открыть Админ панель")
    public void openAdminPanel() {
        $(ADMIN_PANEL)
                .shouldBe(visible)
                .click();
    }

    //Проверяем что пользователь авторизовался
    @Step("Проверка авторизации пользователя")
    public void checkUserLoggedIn(String userName) {
        $$("app-nav-bar button")
                .findBy(text(userName))
                .shouldBe(visible);
    }

    @Step("Переходим в корзину покупателя")
    public void openShopCart() {
        $$("app-nav-bar button")
                .findBy(text("shopping_cart"))
                .shouldBe(visible)
                .click();
    }

    @Step("Находим товар через строку поиска")
    public void searchBook(String title) {
        $(SEARCH)
                .shouldBe(visible)
                .setValue(title);

        $$("mat-option")
                .findBy(exactText(title))
                .shouldBe(visible)
                .click();
    }
}
