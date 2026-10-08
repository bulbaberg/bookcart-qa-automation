package tests.db;

import config.TestConfig;
import data.CreatedBook;
import data.LoginData;
import helpers.ApiBookHelper;
import helpers.ApiLoginHelper;
import helpers.RequestSpecFactory;
import io.qameta.allure.Allure;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.sql.*;

public class BookDbTest {

    @Test
    @DisplayName("DB : Проверка созданной книги в базе данных")
    void shouldSaveCreatedBookInDatabase() throws SQLException {

        RequestSpecification requestSpecification = RequestSpecFactory.create();
        ApiLoginHelper loginHelper = new ApiLoginHelper(requestSpecification);

        Response response = loginHelper.login(
                                LoginData.ADMIN_USERNAME,
                                LoginData.ADMIN_PASSWORD
                        );

        String token = response.path("token");

        ApiBookHelper bookHelper = new ApiBookHelper(
                requestSpecification,
                token
        );

        CreatedBook book = bookHelper.createBook();

        //Запрос в БД
        String sql = """
                SELECT Title, Author, Category, Price
                FROM Book
                WHERE Title = ?
                """;

        Allure.step("Проверяем созданную книгу в базе данных", () -> {
        try (
                Connection connection = DriverManager.getConnection(
                        TestConfig.DB_URL,
                        TestConfig.DB_USER,
                        System.getenv("BOOKCART_DB_PASSWORD")
                );

                PreparedStatement statement = connection.prepareStatement(sql)
        )

        {
            statement.setString(1, book.title());

            try (ResultSet resultSet = statement.executeQuery()) {

            assertTrue(resultSet.next(), "Созданная книга " + book.title() + " не была найдена в таблице Book.");

            String resultTitle = resultSet.getString("Title");
            String resultAuthor = resultSet.getString("Author");
            String resultCategory = resultSet.getString("Category");
            int resultPrice = resultSet.getInt("Price");

            //Проверка
            assertEquals(book.title(), resultTitle);
            assertEquals(book.author(), resultAuthor);
            assertEquals(book.category(), resultCategory);
            assertEquals(book.price(), resultPrice);

                }
            }
        });
    }
}
