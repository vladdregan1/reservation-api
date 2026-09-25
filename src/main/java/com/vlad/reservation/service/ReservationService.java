package com.vlad.reservation.service;

import com.vlad.reservation.entity.Reservation;
import com.vlad.reservation.repository.ReservationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;

    public ReservationService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    public Reservation createReservation(Reservation reservation) {
        if (reservation.getReservationTime() != null && reservation.getReservationTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Data rezervării nu poate fi în trecut!");
        }

        return reservationRepository.save(reservation);
    }

    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }
}