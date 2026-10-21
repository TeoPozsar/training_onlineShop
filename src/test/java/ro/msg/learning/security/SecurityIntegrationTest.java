
package ro.msg.learning.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ro.msg.learning.service.OrderService;

import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderService orderService;

    @Test
    void unauthenticatedRequestReturns401() throws Exception {
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void customerCannotDeleteOrder() throws Exception {
        mockMvc.perform(delete("/api/orders/5f232669-dfcf-420e-96e8-9c7ec0c184ca")
                        .with(user("testcustomer").roles("CUSTOMER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminCanDeleteOrder() throws Exception {
        mockMvc.perform(
                delete("/api/orders/5f232669-dfcf-420e-96e8-9c7ec0c184ca")
                        .with(user("testadmin").roles("ADMIN"))
        ).andExpect(status().isNoContent());

        verify(orderService).deleteOrder(
                UUID.fromString("5f232669-dfcf-420e-96e8-9c7ec0c184ca")
        );
    }
}
