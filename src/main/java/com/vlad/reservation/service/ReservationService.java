package com.vlad.reservation.service;

import com.vlad.reservation.dto.ReservationRequest;
import com.vlad.reservation.dto.ReservationResponse;
import com.vlad.reservation.entity.Reservation;
import com.vlad.reservation.repository.ReservationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;

    public ReservationService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    public ReservationResponse createReservation(ReservationRequest request) {
        Reservation entity = new Reservation();
        entity.setCustomerName(request.customerName());
        entity.setEmail(request.email());
        entity.setReservationTime(request.reservationTime());
        entity.setNumberOfGuests(request.numberOfGuests());

        Reservation savedEntity = reservationRepository.save(entity);

        return mapToResponse(savedEntity);
    }

    public List<ReservationResponse> getAllReservations() {
        List<Reservation> reservations = reservationRepository.findAll();

        return reservations.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private ReservationResponse mapToResponse(Reservation entity) {
        return new ReservationResponse(
                entity.getId(),
                entity.getCustomerName(),
                entity.getEmail(),
                entity.getReservationTime(),
                entity.getNumberOfGuests()
        );
    }
}