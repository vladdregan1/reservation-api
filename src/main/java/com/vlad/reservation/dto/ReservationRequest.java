package com.vlad.reservation.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record ReservationRequest(
        @NotBlank(message = "Customer name is mandatory")
        String customerName,

        @NotBlank(message = "Email is mandatory")
        @Email(message = "Invalid email format")
        String email,

        @NotNull(message = "Reservation time is mandatory")
        @Future(message = "Reservation time must be in the future")
        LocalDateTime reservationTime,

        @Min(value = 1, message = "Minimum 1 guest is required")
        int numberOfGuests
) {}