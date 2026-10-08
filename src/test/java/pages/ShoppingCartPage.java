package pages;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Condition.*;

public class ShoppingCartPage {

    private static final String BODY = "tbody tr";
    private static final String TITLE = "td.mat-column-title";
    private static final String PRICE = "td.mat-column-price";
    private static final String QUANTITY = "td.mat-column-quantity";

    //Находим проверяемый блок по книге
    private SelenideElement getBookRow(String title) {
        return $$(BODY)
                .findBy(text(title));
    }

    //Проверяем книгу
    public void checkBookInCart(String title, String price, String quantity) {
        SelenideElement bookRow = getBookRow(title);
        bookRow.$(TITLE)
                .shouldBe(visible)
                .shouldHave(exactText(title));
        bookRow.$(PRICE)
                .shouldBe(visible)
                .shouldHave(text(price));
        bookRow.$(QUANTITY)
                .shouldBe(visible)
                .shouldHave(text(quantity));

    }

    @Step("Подтвердить оформиление заказа")
    public void clickCheckOut() {
        $$("app-shoppingcart button")
                .findBy(exactText("CheckOut"))
                .shouldBe(visible)
                .click();
    }

}
