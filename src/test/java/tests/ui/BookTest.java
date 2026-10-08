package tests.ui;

import data.BookData;
import helpers.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import pages.*;
import data.LoginData;
import org.junit.jupiter.api.Test;
import data.CreatedBook;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import pages.components.Header;
import io.qameta.allure.Allure;

import java.sql.SQLException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class BookTest extends BaseUiTest {

    LoginPage loginPage = new LoginPage();
    AdminBooksPage adminBooksPage = new AdminBooksPage();
    AddBookPage addBookPage = new AddBookPage();
    MainPage mainPage = new MainPage();
    Header header = new Header();
    ProductCardPage productCardPage = new ProductCardPage();
    ShoppingCartPage shoppingCartPage = new ShoppingCartPage();
    CheckOutPage checkOutPage = new CheckOutPage();
    OrderDbHelper orderDbHelper = new OrderDbHelper();
    MyOrderPage orderPage = new MyOrderPage();
    RequestSpecification requestSpecification = RequestSpecFactory.create();
    ApiLoginHelper loginHelper = new ApiLoginHelper(requestSpecification);

    @BeforeEach
    void setUp() {
        mainPage.openMainPage();
        mainPage.maxWindow();
        header.openLoginPage();
    }

    @Test
    @DisplayName("Создание, изменение и удаление книги")
    void shouldCreateUpdateAndDeleteBook() {

        String uniqueId = UUID.randomUUID().toString();
        String title = "Selenide Test Book " + uniqueId;
        String editTitle = "Edit Selenide Test Book " + uniqueId;

        loginPage.fillLoginForm(
                LoginData.ADMIN_USERNAME,
                LoginData.ADMIN_PASSWORD
                );
        loginPage.submitLogin();
        header.openAdminPanel();
                adminBooksPage.openAddBookForm();
        addBookPage.fillBookForm(
                title,
                BookData.AUTHOR,
                BookData.CATEGORY,
                BookData.PRICE
        );
        addBookPage.saveBook();
        adminBooksPage.searchBook(title);
        adminBooksPage.checkAddedNewBook(
                title,
                BookData.AUTHOR,
                BookData.CATEGORY,
                BookData.PRICE
        );

        adminBooksPage.clickEditButton();
        adminBooksPage.fillFormEditBook(
                editTitle,
                BookData.EDIT_AUTHOR,
                BookData.EDIT_CATEGORY,
                BookData.EDIT_PRICE
        );

        adminBooksPage.clickSubmitEditBook();
        adminBooksPage.searchBook(editTitle);
        adminBooksPage.checkAddedNewBook(
                editTitle,
                BookData.EDIT_AUTHOR,
                BookData.EDIT_CATEGORY,
                BookData.EDIT_PRICE
        );

        adminBooksPage.clickDeleteButton();
        adminBooksPage.submitDeleteBook();
        adminBooksPage.checkDeleteBook(editTitle);
    }

    @Test
    @DisplayName("Покупка книги и проверка созданного заказа")
    void shouldBuyBookAndVerifyCreatedOrder() throws SQLException {

        Response adminResponse = Allure.step(
                "Авторизовать администратора через API для создания книги", () ->
                        loginHelper.login(
                        LoginData.ADMIN_USERNAME,
                        LoginData.ADMIN_PASSWORD
                ));

        Response buyerResponse = Allure.step(
                "Авторизовать покупателя через API для очистки корзины", () ->
                        loginHelper.login(
                        LoginData.BUYER_USERNAME,
                        LoginData.BUYER_PASSWORD
                ));

        String adminToken = adminResponse.path("token");
        String buyerToken = buyerResponse.path("token");
        int buyerId = buyerResponse.path("userDetails.userId");

        ApiBookHelper bookHelper = new ApiBookHelper(
                requestSpecification,
                adminToken
        );

        ApiShoppingCartHelper cartHelper = new ApiShoppingCartHelper(
                requestSpecification,
                buyerToken,
                buyerId
        );

        CreatedBook book = bookHelper.createBook();
        cartHelper.clearCart();

        Allure.step("Авторизовать покупателя через UI", () -> {
            loginPage.fillLoginForm(
                    LoginData.BUYER_USERNAME,
                    LoginData.BUYER_PASSWORD
            );
            loginPage.submitLogin();
        });

        Allure.step("Найти и проверить созданную книгу", () -> {
            header.searchBook(book.title());
            mainPage.openBookDetails(book.title());

            productCardPage.checkBookDetails(
                    book.title(),
                    book.author(),
                    book.category(),
                    String.valueOf(book.price())
            );
        });


        Allure.step("Добавить книгу в корзину и проверить её", () -> {
            productCardPage.addBookToCart();
            productCardPage.checkNotificationBookAdded();
            header.openShopCart();
            shoppingCartPage.checkBookInCart(
                    book.title(),
                    String.valueOf(book.price()),
                    "1"
            );
        });

        Allure.step("Оформить заказ", () -> {
            shoppingCartPage.clickCheckOut();
            checkOutPage.fillShoppingAddress(
                    "Test User",
                    "Test Street 1",
                    "Apartment 10",
                    "123456",
                    "Test State"
            );
            checkOutPage.submitOrder();
        });

        String orderId = Allure.step(
                "Найти и проверить созданный заказ в базе данных",
                () -> {
                    String id = orderDbHelper.findOrderId(
                            buyerId,
                            book.bookId()
                    );

                    assertNotNull(id);

                    return id;
                }
        );

        Allure.step("Проверить созданный заказ в My Orders", () -> {
            orderPage.checkOrderRow(orderId, book.price());
            orderPage.clickOrder(orderId);
            orderPage.checkDetailRow(orderId, book.title(), book.price());
        });
    }
}
