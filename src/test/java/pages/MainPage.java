package pages;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import config.TestConfig;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.*;

public class MainPage {

    private static final String BOOK = "a[aria-label='Book title']";

    // Открываем приложение
    @Step("Открыть приложение Book Cart")
    public void openMainPage() {
        open(TestConfig.FRONTEND_URL);
    }

    //равзвернуть окно
    public void maxWindow() {
        WebDriverRunner.getWebDriver()
                .manage()
                .window()
                .maximize();
    }

    public void openBookDetails(String title) {
        SelenideElement bookCard = $$("app-book-card")
                .findBy(text(title));

        bookCard.$(BOOK)
                .click();
    }



}
