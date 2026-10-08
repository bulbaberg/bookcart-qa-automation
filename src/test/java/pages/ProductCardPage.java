package pages;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Condition.*;

public class ProductCardPage {

    private static final String NOTIFICATION_ADDED_BOOK = "mat-snack-bar-container";

    public void checkNotificationBookAdded () {
        $(NOTIFICATION_ADDED_BOOK)
                .shouldBe(visible)
                .shouldHave(text("One Item added to cart"));

    }

    @Step("Добавить книгу в корзину")
    public void addBookToCart() {
        $("app-book-details > mat-card app-addtocart button")
                .shouldBe(visible)
                .click();
    }

    private SelenideElement getBookParameter(String parameterName) {
        return $$("tbody tr")
                .findBy(text(parameterName))
                .$$("td")
                .get(1);
    }

    @Step("Проверка характеристик книги")
    public void checkBookDetails(
            String title,
            String author,
            String category,
            String price) {

        getBookParameter("Title")
                .shouldHave(exactText(title));

        getBookParameter("Author")
                .shouldHave(exactText(author));

        getBookParameter("Category")
                .shouldHave(exactText(category));

        getBookParameter("Price")
                .shouldHave(text(price));


    }
}
