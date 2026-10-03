package com.vlad.reservation.controller;

import com.vlad.reservation.config.SecurityConfig;
import com.vlad.reservation.dto.ReservationResponse;
import com.vlad.reservation.entity.ReservationStatus;
import com.vlad.reservation.service.ReservationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReservationController.class)
@Import(SecurityConfig.class)
@TestPropertySource(properties = {
        "app.admin.username=admin",
        "app.admin.password=test-password",
        "app.cors.allowed-origins=http://localhost:63342"
})
class ReservationControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReservationService reservationService;

    @Test
    void listWithoutLogin_returns401() throws Exception {
        mockMvc.perform(get("/api/reservations"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void listWithWrongPassword_returns401() throws Exception {
        mockMvc.perform(get("/api/reservations").with(httpBasic("admin", "wrong")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void listAsAdmin_returns200() throws Exception {
        when(reservationService.getAllReservations(anyInt(), anyInt(), anyString(), anyString()))
                .thenReturn(Page.empty());

        mockMvc.perform(get("/api/reservations").with(httpBasic("admin", "test-password")))
                .andExpect(status().isOk());
    }

    @Test
    void deleteWithoutLogin_returns401() throws Exception {
        mockMvc.perform(delete("/api/reservations/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createWithoutLogin_isAllowed() throws Exception {
        LocalDateTime time = LocalDateTime.now().plusDays(2).withNano(0);
        when(reservationService.createReservation(any()))
                .thenReturn(new ReservationResponse(1L, "Mihai Eminescu", "mihai@gmail.com", time, 4, ReservationStatus.PENDING));

        String body = """
                {
                  "customerName": "Mihai Eminescu",
                  "email": "mihai@gmail.com",
                  "reservationTime": "%s",
                  "numberOfGuests": 4
                }
                """.formatted(time);

        mockMvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated());
    }

    @Test
    void pageSizeTooBig_returns400() throws Exception {
        mockMvc.perform(get("/api/reservations?pageSize=500").with(httpBasic("admin", "test-password")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteAsAdmin_returns204() throws Exception {
        mockMvc.perform(delete("/api/reservations/1")
                .with(httpBasic("admin", "test-password")))
                .andExpect(status().isNoContent());
    }

    @Test
    void confirmWithoutLogin_returns401() throws Exception {
        mockMvc.perform(patch("/api/reservations/1/confirm"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void confirmAsAdmin_returns200() throws Exception {
        when(reservationService.confirmReservation(1L))
                .thenReturn(new ReservationResponse(1L, "Mihai Eminescu", "mihai@gmail.com",
                        LocalDateTime.now().plusDays(2), 4, ReservationStatus.CONFIRMED));

        mockMvc.perform(patch("/api/reservations/1/confirm")
                        .with(httpBasic("admin", "test-password")))
                .andExpect(status().isOk());
    }
}
