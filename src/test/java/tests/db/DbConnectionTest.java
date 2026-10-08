package tests.db;

import java.sql.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import config.TestConfig;

public class DbConnectionTest {

    @Test
    @DisplayName("DB : Проверка соединения в базой данных")
    void shouldConnectToDatabase() throws SQLException {
        String dbPassword = System.getenv("BOOKCART_DB_PASSWORD");

        String sql = """
                SELECT * FROM dbo.Book
                WHERE title = ?
                """;

    try (
                Connection connection = DriverManager.getConnection(
                TestConfig.DB_URL,
                TestConfig.DB_USER,
                dbPassword);

            PreparedStatement statement = connection.prepareStatement(sql)
        )

        {
            statement.setString(1, "Убить пересмешника");
            ResultSet resultSet = statement.executeQuery();

            assertTrue(resultSet.next());
            String title = resultSet.getString("Title");
            String author = resultSet.getString("Author");
            int bookId = resultSet.getInt("bookID");

            assertEquals("Убить пересмешника", title);
            assertEquals("Харпер Ли", author);
            assertEquals(2, bookId);


        }
    }
}

