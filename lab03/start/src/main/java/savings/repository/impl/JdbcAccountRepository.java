package savings.repository.impl;

import static org.joda.money.CurrencyUnit.EUR;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.joda.money.Money;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.dao.EmptyResultDataAccessException;

import common.math.Percentage;
import savings.model.Account;
import savings.model.Objective;
import savings.repository.AccountRepository;

// FIXME 0: follow instructions in `application-context.xml` first.
public class JdbcAccountRepository implements AccountRepository {

    private DataSource dataSource;

    // FIXME 1: after you've got the red bar, add @Required annotation to this setter to get a more descriptive error message.
    // Think why and how @Required worked?
    // FIXME 2: now restore the `application-context.xml` to previous state and ensure all tests are green again.
    @Required
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Account findByCreditCard(String creditCardNumber) {
        try (Connection connection = dataSource.getConnection()) {
            Account account = findAccountByCreditCard(creditCardNumber, connection);
            loadAccountObjectives(account, connection);
            return account;
        } catch (SQLException e) {
            throw new RuntimeException("Error in findByCreditCard!", e);
        }
    }

    private Account findAccountByCreditCard(String creditCardNumber, Connection connection) throws SQLException {
        String sql =
            "select * " +
            "from ACCOUNT a " +
                "join ACCOUNT_CREDIT_CARD c on (a.ID = c.ACCOUNT_ID)" +
            "where c.NUMBER = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, creditCardNumber);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return readAccountFrom(resultSet);
                }
            }
        }
        throw new EmptyResultDataAccessException(1);
    }

    private Account readAccountFrom(ResultSet resultSet) throws SQLException {
        Account account = new Account(
                resultSet.getString("NUMBER"),
                resultSet.getString("NAME")
        );
        account.setId(resultSet.getLong("ID"));
        return account;
    }

    private void loadAccountObjectives(Account account, Connection connection) throws SQLException {
        String sql =
                "select * " +
                "from ACCOUNT_OBJECTIVE o " +
                "where o.ACCOUNT_ID = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, account.getId());
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    account.withObjective(readObjectvieFrom(resultSet));
                }
            }
        }
    }

    private Objective readObjectvieFrom(ResultSet resultSet) throws SQLException {
        Objective objective = new Objective(
                resultSet.getString("NAME"),
                new Percentage(resultSet.getBigDecimal("ALLOCATION")),
                Money.of(EUR, resultSet.getBigDecimal("SAVINGS"))
        );
        objective.setId(resultSet.getLong("ID"));
        return objective;
    }

    @Override
    public void update(Account account) {
    }
}
