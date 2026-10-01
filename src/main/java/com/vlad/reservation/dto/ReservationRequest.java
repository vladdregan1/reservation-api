package com.vlad.reservation.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public record ReservationRequest(
        @NotBlank(message = "Customer name is mandatory")
        @Size(max = 100, message = "Customer name can have at most 100 characters")
        String customerName,

        @NotBlank(message = "Email is mandatory")
        @Email(message = "Invalid email format")
        @Size(max = 254, message = "Email can have at most 254 characters")
        String email,

        @NotNull(message = "Reservation time is mandatory")
        @Future(message = "Reservation time must be in the future")
        LocalDateTime reservationTime,

        @Min(value = 1, message = "Minimum 1 guest is required")
        @Max(value = 20, message = "Maximum 20 guests per reservation")
        int numberOfGuests
) {}