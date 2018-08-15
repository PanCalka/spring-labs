package savings.repository.impl;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import savings.repository.AccountRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class RepositoryPerformanceMonitorTest {

    @Configuration
    @EnableAspectJAutoProxy
    static class Config {

        @Bean
        public RepositoryPerformanceMonitor performanceMonitor() {
            return new RepositoryPerformanceMonitor();
        }

        @Bean
        public AccountRepository accountRepository() {
            return mock(AccountRepository.class);
        }
    }

    @Autowired
    AccountRepository repository;

    TestAppender appender = new TestAppender();

    @Before
    public void setUp() throws Exception {
        org.apache.log4j.Logger.getRootLogger().addAppender(appender);
    }

    @After
    public void tearDown() throws Exception {
        org.apache.log4j.Logger.getRootLogger().removeAppender(appender);
    }

    @Test
    public void shouldLogPerformance() throws Exception {
        repository.findByCreditCard("123456789");

        assertThat(appender.events).hasSize(1);
        assertThat(appender.events.get(0).getLevel()).isEqualTo(Level.INFO);
        assertThat((String) appender.events.get(0).getMessage())
                .startsWith("StopWatch 'AccountRepository.findByCreditCard': running time (millis) = ");
    }

    static class TestAppender extends AppenderSkeleton {

        final List<LoggingEvent> events = new LinkedList<>();

        @Override
        protected void append(LoggingEvent event) {
            events.add(event);
        }

        @Override
        public void close() {
        }

        @Override
        public boolean requiresLayout() {
            return false;
        }
    }
}
