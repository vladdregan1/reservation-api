package com.vlad.reservation.service;

import com.vlad.reservation.dto.ReservationRequest;
import com.vlad.reservation.dto.ReservationResponse;
import com.vlad.reservation.entity.Reservation;
import com.vlad.reservation.exception.ResourceNotFoundException;
import com.vlad.reservation.repository.ReservationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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

    public Page<ReservationResponse> getAllReservations(int pageNo, int pageSize, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

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
        Reservation entity = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found a reservation with id: " + id));

        entity.setCustomerName(request.customerName());
        entity.setEmail(request.email());
        entity.setReservationTime(request.reservationTime());
        entity.setNumberOfGuests(request.numberOfGuests());

        Reservation updatedEntity = reservationRepository.save(entity);

        return mapToResponse(updatedEntity);
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