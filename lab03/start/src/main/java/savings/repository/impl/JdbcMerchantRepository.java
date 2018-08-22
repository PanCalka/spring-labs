package savings.repository.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.sql.DataSource;

import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import common.math.Percentage;
import org.springframework.dao.EmptyResultDataAccessException;
import savings.model.Merchant;
import savings.repository.MerchantRepository;

public class JdbcMerchantRepository implements MerchantRepository {

    private static final Logger LOG = LoggerFactory.getLogger(JdbcMerchantRepository.class);

    private DataSource dataSource;

    private final Map<String, Merchant> cache = new HashMap<>();

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    // FIXME 1: register this method as initialization lifecycle callback
    @PostConstruct
    void populateCache() {
        LOG.info("Populating merchants cache...");
        String sql =
                "select * " +
                "from MERCHANT";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Merchant merchant = readMerchantFrom(resultSet);
                    cache.put(merchant.getNumber(), merchant);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error in findByNumber!", e);
        }
    }

    // FIXME 2: register this method as destruction lifecycle callback
    @PreDestroy
    void clearCache() {
        LOG.info("Clearing merchants cache...");
        cache.clear();
    }

    @Override
    public Merchant findByNumber(String number) {
        Merchant merchant = cache.get(number);
        if(merchant ==null){
            throw new EmptyResultDataAccessException(1);
        }
        return merchant;
    }

    private Merchant readMerchantFrom(ResultSet resultSet) throws SQLException {
        Merchant account = new Merchant(
                resultSet.getString("NUMBER"),
                resultSet.getString("NAME"),
                new Percentage(resultSet.getBigDecimal("PAYBACK"))
        );
        account.setId(resultSet.getLong("ID"));
        return account;
    }
}
