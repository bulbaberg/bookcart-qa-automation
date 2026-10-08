package helpers;
import config.TestConfig;
import io.qameta.allure.Step;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class OrderDbHelper {

    @Step("Найти созданный заказ в базе данных")
    public String findOrderId(int userId, int bookId) throws SQLException {

        String sql = """
                SELECT co.OrderId
                FROM CustomerOrders co
                INNER JOIN CustomerOrderDetails cod
                    ON co.OrderId = cod.OrderId
                WHERE co.UserID = ?
                  AND cod.ProductId = ?
                """;

        try (
                Connection connection = DriverManager.getConnection(
                        TestConfig.DB_URL,
                        TestConfig.DB_USER,
                        System.getenv("BOOKCART_DB_PASSWORD")
                );

                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, userId);
            statement.setInt(2, bookId);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return resultSet.getString("OrderId");
                }

                return null;
            }
        }
    }

}
