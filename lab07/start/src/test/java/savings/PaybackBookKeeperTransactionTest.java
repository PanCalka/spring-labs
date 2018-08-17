package savings;

import static com.googlecode.catchexception.CatchException.catchException;
import static com.googlecode.catchexception.CatchException.caughtException;
import static org.fest.assertions.Assertions.assertThat;
import static org.joda.money.CurrencyUnit.EUR;
import static org.joda.time.DateTime.now;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

import org.joda.money.Money;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import savings.model.AccountIncome;
import savings.model.Objective;
import savings.model.Purchase;
import savings.repository.AccountRepository;
import savings.repository.PaybackRepository;
import savings.service.PaybackBookKeeper;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class PaybackBookKeeperTransactionTest {

    @Configuration
    static class Config extends PaybackBookKeeperModuleTest.Config {

        @Bean
        public PaybackRepository paybackRepository() {
            return mock(PaybackRepository.class);
        }
    }

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    PaybackRepository paybackRepository;

    @Autowired
    PaybackBookKeeper bookKeeper;

    String accountNumber = "123456789";

    String creditCardNumber = "1234123412341234";

    String merchantNumber = "1234567890";

    Purchase purchase = new Purchase(Money.of(EUR, 100L), creditCardNumber, merchantNumber, now());

    @Test
    public void shouldRegisterPaybackInTransaction() throws Exception {
        doThrow(new RuntimeException("DB error!"))
                .when(paybackRepository).save(any(AccountIncome.class), any(Purchase.class));

        catchException(bookKeeper, RuntimeException.class).registerPaybackFor(purchase);

        assertThat(caughtException()).isNotNull();
        assertThat(paybackRepository.findAllByAccountNumber(accountNumber)).isEmpty();
        for (Objective objective : accountRepository.findByCreditCard(creditCardNumber).getObjectives()) {
            assertThat(objective.getSavings()).isEqualTo(Money.zero(EUR));
        }
    }
}
