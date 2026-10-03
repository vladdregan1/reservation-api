package com.vlad.reservation.dto;

import com.vlad.reservation.entity.ReservationStatus;

import java.time.LocalDateTime;

public record ReservationResponse(
        Long id,
        String customerName,
        String email,
        LocalDateTime reservationTime,
        int numberOfGuests,
        ReservationStatus status
) {}