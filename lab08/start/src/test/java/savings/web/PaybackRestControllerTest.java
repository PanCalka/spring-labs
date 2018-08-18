package savings.web;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;
import static savings.PaybackFixture.confirmation;
import static savings.PaybackFixture.purchaseJson;
import static savings.web.impl.WebConfiguration.buildJsonMessageConverter;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.web.servlet.MockMvc;

import savings.model.Purchase;
import savings.service.PaybackBookKeeper;
import savings.web.impl.PaybackRestController;

/**
 * Notice that you do _not_ need a Spring ApplicationContext to test MVC controllers using MockMvc.
 */
@RunWith(MockitoJUnitRunner.class)
public class PaybackRestControllerTest {

    @Mock
    PaybackBookKeeper bookKeeper;

    @InjectMocks
    PaybackRestController controller;

    MockMvc mockMvc;

    @Before
    public void setUp() throws Exception {
        mockMvc = standaloneSetup(controller)
                .setMessageConverters(buildJsonMessageConverter())
                .build();
    }

    @Test
    public void shouldCreatePayback() throws Exception {
        when(bookKeeper.registerPaybackFor(any(Purchase.class)))
                .thenReturn(confirmation("123"));

        mockMvc.perform(post("/api/payback")
            .content(purchaseJson).contentType(APPLICATION_JSON).accept(APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.number").value("123"))
            .andExpect(jsonPath("$.income.amount").value("6.00 EUR"))
            .andExpect(jsonPath("$.income.distributions").isArray())
            .andExpect(jsonPath("$.income.distributions[0].amount").value("3.00 EUR"))
            .andExpect(jsonPath("$.income.distributions[1].amount").value("3.00 EUR"));
    }
}
