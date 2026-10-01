package com.vlad.reservation.service;

import com.vlad.reservation.dto.ReservationRequest;
import com.vlad.reservation.dto.ReservationResponse;
import com.vlad.reservation.entity.Reservation;
import com.vlad.reservation.repository.ReservationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @InjectMocks
    private ReservationService reservationService;

    @Test
    void getAllReservations_ShouldReturnPageResponses() {

        Reservation dummyReservation = new Reservation();
        dummyReservation.setId(1L);
        dummyReservation.setCustomerName("Vlad Test");
        dummyReservation.setEmail("test@email.com");
        dummyReservation.setReservationTime(LocalDateTime.now());
        dummyReservation.setNumberOfGuests(2);

        Page<Reservation> mockPage = new PageImpl<>(List.of(dummyReservation));
        when(reservationRepository.findAll(any(Pageable.class))).thenReturn(mockPage);

        Page<ReservationResponse> result = reservationService.getAllReservations(0, 10, "id", "ASC");

        assertNotNull(result);
        assertEquals("Vlad Test", result.getContent().get(0).customerName());

        verify(reservationRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    void createReservation_ShouldReturnSavedReservation() {

        ReservationRequest inputRequest = new ReservationRequest(
                "Mihai Eminescu",
                "mihai@gmail.com",
                LocalDateTime.now().plusDays(2),
                4
        );

        Reservation savedEntity = new Reservation();
        savedEntity.setId(99L);
        savedEntity.setCustomerName(inputRequest.customerName());
        savedEntity.setEmail(inputRequest.email());
        savedEntity.setReservationTime(inputRequest.reservationTime());
        savedEntity.setNumberOfGuests(inputRequest.numberOfGuests());

        when(reservationRepository.save(any(Reservation.class))).thenReturn(savedEntity);

        ReservationResponse result = reservationService.createReservation(inputRequest);

        assertNotNull(result);
        assertEquals(99L, result.id());
        assertEquals("Mihai Eminescu", result.customerName());

        verify(reservationRepository, times(1)).save(any(Reservation.class));
    }

    @Test
    void getAllReservations_unknownSortField_throwsException() {
        assertThrows(IllegalArgumentException.class,
                () -> reservationService.getAllReservations(0, 10, "password", "ASC"));
    }

    @Test
    void getAllReservations_invalidSortDirection_throwsException() {
        assertThrows(IllegalArgumentException.class,
                () -> reservationService.getAllReservations(0, 10, "id", "sideways"));
    }
}
