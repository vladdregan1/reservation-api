package com.vlad.reservation.service;

import com.vlad.reservation.dto.ReservationRequest;
import com.vlad.reservation.dto.ReservationResponse;
import com.vlad.reservation.entity.Reservation;
import com.vlad.reservation.entity.ReservationStatus;
import com.vlad.reservation.exception.InvalidStatusChangeException;
import com.vlad.reservation.exception.ResourceNotFoundException;
import com.vlad.reservation.repository.ReservationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
public class ReservationService {

    private static final Set<String> ALLOWED_SORT_FIELDS =
            Set.of("id", "customerName", "reservationTime", "numberOfGuests");

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

    public Page<ReservationResponse> getAllReservations(int pageNo, int pageSize, String sortBy, String sortDir) {
        if (!ALLOWED_SORT_FIELDS.contains(sortBy)) {
            throw new IllegalArgumentException("sortBy must be one of: " + ALLOWED_SORT_FIELDS);
        }

        Sort.Direction direction = Sort.Direction.fromString(sortDir);
        Sort sort = Sort.by(direction, sortBy);

        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        Page<Reservation> reservations = reservationRepository.findAll(pageable);

        return reservations.map(this::mapToResponse);
    }

    public void deleteReservation(Long id) {
        if (!reservationRepository.existsById(id)) {
            throw new ResourceNotFoundException("Not found a reservation with id: " + id);
        }
        reservationRepository.deleteById(id);
    }

    public ReservationResponse updateReservation(Long id, ReservationRequest request) {
        Reservation entity = findReservationOrThrow(id);

        entity.setCustomerName(request.customerName());
        entity.setEmail(request.email());
        entity.setReservationTime(request.reservationTime());
        entity.setNumberOfGuests(request.numberOfGuests());

        Reservation updatedEntity = reservationRepository.save(entity);

        return mapToResponse(updatedEntity);
    }

    @Transactional
    public ReservationResponse confirmReservation(Long id) {
        Reservation entity = findReservationOrThrow(id);

        if (entity.getStatus() != ReservationStatus.PENDING) {
            throw new InvalidStatusChangeException(
                    "Only PENDING reservations can be confirmed. Current status: " + entity.getStatus());
        }

        entity.setStatus(ReservationStatus.CONFIRMED);
        return mapToResponse(entity);
    }

    @Transactional
    public ReservationResponse cancelReservation(Long id) {
        Reservation entity = findReservationOrThrow(id);

        if (entity.getStatus() == ReservationStatus.CANCELLED) {
            throw new InvalidStatusChangeException("Reservation is already cancelled");
        }

        entity.setStatus(ReservationStatus.CANCELLED);
        return mapToResponse(entity);
    }

    private Reservation findReservationOrThrow(Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found a reservation with id: " + id));
    }

    private ReservationResponse mapToResponse(Reservation entity) {
        return new ReservationResponse(
                entity.getId(),
                entity.getCustomerName(),
                entity.getEmail(),
                entity.getReservationTime(),
                entity.getNumberOfGuests(),
                entity.getStatus()
        );
    }
}