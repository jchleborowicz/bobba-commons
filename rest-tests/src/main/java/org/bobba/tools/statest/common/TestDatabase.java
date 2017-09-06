package org.bobba.tools.statest.common;

import lombok.Data;
import org.apache.commons.configuration.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.jdbc.SimpleJdbcTestUtils;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.Map;

import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static org.apache.commons.lang3.Validate.notEmpty;

public class TestDatabase {

    private final DriverManagerDataSource dataSource;

    public TestDatabase(String url, String username, String password) {
        notEmpty(url, "Database url cannot be empty");
        notEmpty(username, "Database username cannot be empty");
        notEmpty(password, "Database password cannot be empty");
        this.dataSource = new DriverManagerDataSource(url, username, password);
    }

    public TestDatabase(DatabaseConnectionDetails databaseConnectionDetails) {
        this(databaseConnectionDetails.getUrl(), databaseConnectionDetails.getUsername(),
                databaseConnectionDetails.getPassword());
    }

    private static TestDatabase createTestDatabase(Configuration crmAdapterConfiguration, String databaseName) {
        final DatabaseConnectionDetails result = getDatabaseConnectionDetails(crmAdapterConfiguration, databaseName);
        return new TestDatabase(result);
    }

    private static DatabaseConnectionDetails getDatabaseConnectionDetails(Configuration configuration,
                                                                          final String databaseName) {
        final DatabaseConnectionDetails result = new DatabaseConnectionDetails();
        result.setUrl(configuration.getString("dataSource." + databaseName + ".url"));
        result.setUsername(configuration.getString("dataSource." + databaseName + ".user"));
        result.setPassword(configuration.getString("dataSource." + databaseName + ".password"));
        return result;
    }

    public List<Map<String, Object>> queryForList(final String sql, final Object... args) {
        final TransactionTemplate transactionTemplate =
                new TransactionTemplate(new DataSourceTransactionManager(dataSource));
        return transactionTemplate.execute(new TransactionCallback<List<Map<String, Object>>>() {
            @Override
            public List<Map<String, Object>> doInTransaction(TransactionStatus status) {
                return new JdbcTemplate(dataSource).queryForList(sql, args);
            }
        });
    }

    public int queryForInt(final String sql, final Object... args) {
        final DataSourceTransactionManager transactionManager = new DataSourceTransactionManager(dataSource);
        final TransactionTemplate transactionTemplate =
                new TransactionTemplate(transactionManager);
        return transactionTemplate.execute(new TransactionCallback<Integer>() {
            @Override
            public Integer doInTransaction(TransactionStatus status) {
                return new JdbcTemplate(dataSource).queryForInt(sql, args);
            }
        });
    }

    public int executeUpdate(final String sql, final Object... args) {
        final DataSourceTransactionManager transactionManager = new DataSourceTransactionManager(dataSource);
        final TransactionTemplate transactionTemplate =
                new TransactionTemplate(transactionManager);
        return transactionTemplate.execute(new TransactionCallback<Integer>() {
            @Override
            public Integer doInTransaction(TransactionStatus status) {
                return new JdbcTemplate(dataSource).update(sql, args);
            }
        });
    }

    public void executeScript(String resourceName) {
        final DataSourceTransactionManager transactionManager = new DataSourceTransactionManager(dataSource);
        final TransactionTemplate transactionTemplate =
                new TransactionTemplate(transactionManager);
        final Resource resource = new ClassPathResource(resourceName);
        transactionTemplate.execute(new TransactionCallback<Boolean>() {
            @Override
            public Boolean doInTransaction(TransactionStatus status) {
                SimpleJdbcTestUtils.executeSqlScript(new SimpleJdbcTemplate(dataSource), resource, true);
                return true;
            }
        });
    }

    public Map<String, Object> queryForFirstRow(final String sql, final Object... args) {
        final TransactionTemplate transactionTemplate =
                new TransactionTemplate(new DataSourceTransactionManager(dataSource));
        return transactionTemplate.execute(new TransactionCallback<Map<String, Object>>() {
            @Override
            public Map<String, Object> doInTransaction(TransactionStatus status) {
                JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
                jdbcTemplate.setMaxRows(1);

                List<Map<String, Object>> result = jdbcTemplate.queryForList(sql, args);
                isTrue(result.size() == 1);
                return result.get(0);
            }
        });
    }

    public <T> List<T> queryForList(final String sql, final RowMapper<T> rowMapper, final Object... args) {
        final TransactionTemplate transactionTemplate =
                new TransactionTemplate(new DataSourceTransactionManager(dataSource));
        return transactionTemplate.execute(new TransactionCallback<List<T>>() {
            @Override
            public List<T> doInTransaction(TransactionStatus status) {
                JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
                return jdbcTemplate.query(sql, rowMapper, args);
            }
        });
    }

    @Data
    public static final class DatabaseConnectionDetails {

        private String url;
        private String username;
        private String password;

    }
}
