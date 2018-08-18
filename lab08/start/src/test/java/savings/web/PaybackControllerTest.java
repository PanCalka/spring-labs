package savings.web;

import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;
import static savings.PaybackFixture.creditCardNumber;
import static savings.PaybackFixture.merchantNumber;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import savings.service.PaybackBookKeeper;
import savings.web.impl.WebConfiguration;

/**
 * This test presents how to test MVC controllers logic utilizing minimized Spring ApplicationContext.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@WebAppConfiguration
public class PaybackControllerTest {

    @Configuration
    @Import(WebConfiguration.class)
    static class Config {

        @Bean
        public PaybackBookKeeper bookKeeper() {
            return mock(PaybackBookKeeper.class);
        }
    }

    @Autowired
    WebApplicationContext wac;

    @Autowired
    PaybackBookKeeper bookKeeper;

    MockMvc mockMvc;

    @Before
    public void setUp() throws Exception {
        mockMvc = webAppContextSetup(this.wac).build();
    }

    @Test
    public void shouldGetForm() throws Exception {
        mockMvc.perform(get("/payback/new"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(view().name("payback/new"));
    }

    @Test
    public void shouldPostForm() throws Exception {
        mockMvc.perform(post("/payback/confirm")
                .param("purchaseForm.creditCardNumber", creditCardNumber)
                .param("purchaseForm.merchantNumber", merchantNumber)
                .param("purchaseForm.transactionValue", "100.00 EUR"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(view().name("payback/confirm"));
    }
}
