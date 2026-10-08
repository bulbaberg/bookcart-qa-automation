package pages;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

public class AdminBooksPage {
    private static final String ADD_BOOK_BUTTON = "app-manage-books mat-card-header button";
    private static final String SEARCH_INPUT = "input[placeholder='Search']";
    private static final String TITLE = "input[placeholder='Title']";
    private static final String AUTHOR = "input[placeholder='Author']";
    private static final String CATEGORY = "mat-select[placeholder='Category']";
    private static final String CATEGORY_SELECT = "mat-option";
    private static final String PRICE = "input[placeholder='Price']";
    private static final String SAVE = "button[type='submit']";
    private static final String SUBMIT_DELETE = "app-delete-book button[type='button']";
    private static final String CANCEL_DELETE = "";


    //Открыть шаблон добавления новой книги
    public void openAddBookForm() {
        $(ADD_BOOK_BUTTON)
                .shouldBe(visible)
                .click();
    }

    //Поиск книги в строке Search
    public void searchBook(String title) {
        $(SEARCH_INPUT)
                .shouldBe(visible)
                .setValue(title);
    }

    //Проверка созданной книги
    public void checkAddedNewBook(
            String title,
            String author,
            String category,
            String price) {

        SelenideElement bookRow = $$("tbody tr")
                .findBy(text(title))
                .shouldBe(visible);

        bookRow.shouldHave(text(title));
        bookRow.shouldHave(text(author));
        bookRow.shouldHave(text(category));
        bookRow.shouldHave(text(price));
    }

    @Step("Нажимаем на кноку Изменить книгу")
    public void clickEditButton() {
        $("mat-icon[aria-label='Edit']")
                .closest("button")
                .click();
    }

    @Step("Нажимаем на кноку удалить книгу")
    public void clickDeleteButton() {
        $$("mat-icon")
                .findBy(exactText("delete"))
                .closest("button")
                .click();
    }

    @Step("Нажимаем на кнопку сохранить изменённую книгу")
    public void clickSubmitEditBook() {
        $(SAVE)
                .shouldBe(visible)
                .click();
    }

    @Step("Заполнение формы по редактированию книги")
    public void fillFormEditBook(String title, String author, String category, String price) {
        $(TITLE)
                .shouldBe(visible)
                .setValue(title);
        $(AUTHOR)
                .shouldBe(visible)
                .setValue(author);
        $(PRICE)
                .shouldBe(visible)
                .setValue(price);
        $(CATEGORY)
                .shouldBe(visible)
                .click();
        $$(CATEGORY_SELECT)
                .findBy(exactText(category))
                .click();
    }

    @Step("Подтверждаем удаление книги")
    public void submitDeleteBook() {
        $(SUBMIT_DELETE)
                .shouldBe(visible)
                .click();
    }

    @Step("Проверка отсутсвия книги")
    public void checkDeleteBook(String title) {
        $$("tbody tr")
                .findBy(text(title))
                .shouldNot(exist);
    }
}
