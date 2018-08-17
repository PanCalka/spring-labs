package savings.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;

@EnableTransactionManagement
@Configuration
@ComponentScan(basePackageClasses = ServiceConfiguration.class, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ANNOTATION, value = Configuration.class)
})
public class ServiceConfiguration {

    @Autowired
    DataSource dataSource;

    public PlatformTransactionManager transactionManager() {
        return new DataSourceTransactionManager(dataSource);
    }
}
