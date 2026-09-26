package com.vlad.reservation.dto;

import java.time.LocalDateTime;

public record ReservationResponse(
        Long id,
        String customerName,
        String email,
        LocalDateTime reservationTime,
        int numberOfGuests
) {}