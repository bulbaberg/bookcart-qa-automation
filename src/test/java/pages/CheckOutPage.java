package pages;

import io.qameta.allure.Step;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Condition.*;

public class CheckOutPage {

    private static final String NAME = "input[placeholder='Name']";
    private static final String ADDRESS_LINE_ONE = "input[placeholder='Address Line 1']";
    private static final String ADDRESS_LINE_TWO = "input[placeholder='Address Line 2']";
    private static final String PINCODE = "input[placeholder='Pincode']";
    private static final String STATE = "input[placeholder='State']";
    private static final String PLACE_ORDER = "button[type='submit']";

    public void cancelOrder() {
        $$("mat-card-actions")
                .findBy(text("Cancel"))
                .shouldBe(visible)
                .click();
    }

    public void submitOrder() {
        $(PLACE_ORDER)
                .shouldBe(visible)
                .click();
    }

    @Step("Заполнение карточки адреса")
    public void fillShoppingAddress(String name,
                                    String addressOne,
                                    String addressTwo,
                                    String pincode,
                                    String state
    ) {
        $(NAME)
                .shouldBe(visible)
                .setValue(name);
        $(ADDRESS_LINE_ONE)
                .shouldBe(visible)
                .setValue(addressOne);
        $(ADDRESS_LINE_TWO)
                .shouldBe(visible)
                .setValue(addressTwo);
        $(PINCODE)
                .shouldBe(visible)
                .setValue(pincode);
        $(STATE)
                .shouldBe(visible)
                .setValue(state);
    }

}
