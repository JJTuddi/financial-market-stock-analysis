package tuddi.stock.processor.stock.sink;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import tuddi.stock.processor.stock.data.Stock;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Map;

public class MySqlStockSink extends RichSinkFunction<Stock> {

    private static final String INSERT_STOCK = """
            INSERT INTO stocks (`ts`, `stock_name`, `open`, `close`, `volume`, `low`, `high`) VALUES (?, ?, ?, ?, ?, ?, ?);
            """;

    private transient DataSource dataSource;

    @Override
    public void invoke(Stock stock, Context context) {
        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(INSERT_STOCK);
        ) {
            preparedStatement.setLong(1, stock.timestamp);
            preparedStatement.setString(2, stock.stockName);
            preparedStatement.setDouble(3, stock.open);
            preparedStatement.setDouble(4, stock.close);
            preparedStatement.setDouble(5, stock.volume);
            preparedStatement.setDouble(6, stock.low);
            preparedStatement.setDouble(7, stock.high);

            preparedStatement.execute();
        } catch (SQLException sqlException) {
            throw new RuntimeException("Couldn't persist the stock: " + stock, sqlException);
        }
    }

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        Class.forName("com.mysql.cj.jdbc.Driver");
        Map<String, String> params = getRuntimeContext().getExecutionConfig().getGlobalJobParameters().toMap();

        String username = params.get("mysql.stockDb.username");
        if (StringUtils.isBlank(username)) throw new RuntimeException("Mysql StockDb username is missing!");
        String password = params.get("mysql.stockDb.password");
        if (StringUtils.isBlank(password)) throw new RuntimeException("Mysql StockDb password is missing!");
        String url = params.get("mysql.stockDb.url");
        if (StringUtils.isBlank(password)) throw new RuntimeException("Mysql StockDb url is missing!");

        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setUsername(username);
        hikariConfig.setPassword(password);
        hikariConfig.setJdbcUrl(url);

        dataSource = new HikariDataSource(hikariConfig);
    }

}
