package pages;


import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class MyOrderPage {

    private final ElementsCollection orderRow = $$("tr.example-element-row");

    //Ищем в детялях заказа конкретную книгу
    private SelenideElement findBookRow (String orderId, String title) {
        return findDetailRow(orderId).$$("tbody tr").findBy(text(title));
    }

    //Находим наш заказ
    private SelenideElement findOrderRow(String orderId) {
        return orderRow.findBy(text(orderId));
    }

    //поиск строки с деталями заказа
    private SelenideElement findDetailRow(String orderId) {
        return findOrderRow(orderId).sibling(0);
    }

    //кликаем на строку с заказом чтобы получить его детали
    public void clickOrder(String orderId) {
        findOrderRow(orderId).
                shouldBe(visible).
                click();
    }

    @Step("Проверка состава заказа")
    public void checkDetailRow(String orderId, String title, int amount) {
        ElementsCollection detailsCollection = findBookRow(orderId, title).$$("td");
        detailsCollection.get(0).shouldHave(text(title));
        detailsCollection.get(1).shouldHave(exactText("1"));
        detailsCollection.get(2).shouldHave(text(String.valueOf(amount)));
    }

    @Step("Проверка характеристик созданного заказа")
    public void checkOrderRow(String orderId, int orderTotal) {
        SelenideElement checkOrder = findOrderRow(orderId);
                checkOrder.$("td.cdk-column-orderId")
                        .shouldHave(exactText(orderId));
                checkOrder.$("td.cdk-column-cartTotal")
                        .shouldHave(text(String.valueOf(orderTotal)));
                checkOrder.$("td.cdk-column-orderDate")
                        .shouldNotBe(empty);

    }
}
