package pages;

import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class AddBookPage {
    private static final String TITLE_INPUT = "input[formcontrolname='title']";
    private static final String AUTHOR_INPUT = "input[formcontrolname='author']";
    private static final String PRICE_INPUT = "input[formcontrolname='price']";
    private static final String CATEGORY_SELECT = "mat-select[formcontrolname='category']";
    private static final String SAVE_BUTTON = "app-book-form button[type='submit']";

    //Вводим название книги
    private void setTitle(String title) {

        $(TITLE_INPUT).shouldBe(visible).setValue(title);
    }

    //Вводим автора
    private void setAuthor(String author) {

        $(AUTHOR_INPUT).shouldBe(visible).setValue(author);
    }

    //Вводим цену книги
    private void setPrice(String price) {

        $(PRICE_INPUT).shouldBe(visible).setValue(price);
    }

    //Выбираем категорию
    private void setCategory(String category) {
        $(CATEGORY_SELECT)
                .shouldBe(visible)
                .click();

        $$("mat-option")
                .findBy(exactText(category))
                .shouldBe(visible)
                .click();
    }

    @Step("Сохраняем добавленную книгу")
    public void saveBook() {

        $(SAVE_BUTTON).shouldBe(visible).click();
    }

    //Доступные категории : Biography, Fiction, Mystery, Fantasy, Romance
    @Step("Заполняем форму новой книги")
    public void fillBookForm(String title, String author, String category, String price) {
        setTitle(title);
        setAuthor(author);
        setCategory(category);
        setPrice(price);

    }



}
