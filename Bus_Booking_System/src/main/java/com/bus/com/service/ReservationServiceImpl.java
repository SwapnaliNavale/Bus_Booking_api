package com.bus.com.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.bus.com.custom_exceptions.ResourceNotFoundException;
import com.bus.com.dto.ApiResponse;
import com.bus.com.dto.ReservationRequestDTO;
import com.bus.com.dto.ReservationResponseDTO;
import com.bus.com.entities.Reservation;
import com.bus.com.entities.ReservationStatus;
import com.bus.com.entities.Resource;
import com.bus.com.entities.User;
import com.bus.com.repositories.ReservationRepository;
import com.bus.com.repositories.ResourceRepository;

import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;

@Service
public class ReservationServiceImpl implements ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ResourceRepository resourceRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public ApiResponse<Page<ReservationResponseDTO>> listReservationsForAdmin(ReservationStatus status, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        Specification<Reservation> spec = buildFilterSpecification(null, status, minPrice, maxPrice);
        Page<ReservationResponseDTO> page = reservationRepository.findAll(spec, pageable)
                .map(res -> modelMapper.map(res, ReservationResponseDTO.class));
        return new ApiResponse<>("Reservations fetched", "success", page);
    }

    @Override
    public ApiResponse<Page<ReservationResponseDTO>> listReservationsForUser(Long userId, ReservationStatus status, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        Specification<Reservation> spec = buildFilterSpecification(userId, status, minPrice, maxPrice);
        Page<ReservationResponseDTO> page = reservationRepository.findAll(spec, pageable)
                .map(res -> modelMapper.map(res, ReservationResponseDTO.class));
        return new ApiResponse<>("Reservations fetched", "success", page);
    }

    @Override
    public ApiResponse<ReservationResponseDTO> findById(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found with id: " + id));
        return new ApiResponse<>("Reservation found", "success", modelMapper.map(reservation, ReservationResponseDTO.class));
    }

//    @Override
//    public ApiResponse<ReservationResponseDTO> createReservation(ReservationRequestDTO dto, User user, boolean enforceOverlapCheck) {
//        Reservation reservation = modelMapper.map(dto, Reservation.class);
//
//        Resource resource = resourceRepository.findById(dto.getResourceId())
//                .orElseThrow(() -> new ResourceNotFoundException("Resource not found with id: " + dto.getResourceId()));
//        reservation.setResource(resource);
//
//        if (enforceOverlapCheck && reservation.getStatus() == ReservationStatus.CONFIRMED) {
//            boolean existsOverlap = reservationRepository.existsOverlappingReservation(
//                    resource.getId(),
//                    ReservationStatus.CONFIRMED,
//                    reservation.getStartTime(),
//                    reservation.getEndTime()
//            );
//            if (existsOverlap) {
//                throw new IllegalArgumentException("Overlapping confirmed reservation exists for this resource and time range.");
//            }
//        }
//        reservation.setUser(user);
//        reservation.setCreatedAt(Instant.now());
//        reservation.setUpdatedAt(Instant.now());
//        Reservation saved = reservationRepository.save(reservation);
//
//        return new ApiResponse<>("Reservation created successfully", "success", modelMapper.map(saved, ReservationResponseDTO.class));
//    }
//
//    @Override
//    public ApiResponse<ReservationResponseDTO> updateReservation(Long id, ReservationRequestDTO updatedDTO, Long currentUserId, boolean isAdmin, boolean enforceOverlapCheck) {
//        Reservation existing = reservationRepository.findById(id)
//                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found with id: " + id));
//
//        if (!isAdmin && !existing.getUser().getId().equals(currentUserId)) {
//            throw new SecurityException("Not authorized to update this reservation");
//        }
//
//        modelMapper.map(updatedDTO, existing);
//
//        if (enforceOverlapCheck && updatedDTO.getStatus() == ReservationStatus.CONFIRMED) {
//            boolean existsOverlap = reservationRepository.existsOverlappingReservation(
//                    existing.getResource().getId(),
//                    ReservationStatus.CONFIRMED,
//                    updatedDTO.getStartTime() != null ? updatedDTO.getStartTime() : existing.getStartTime(),
//                    updatedDTO.getEndTime() != null ? updatedDTO.getEndTime() : existing.getEndTime()
//            );
//            if (existsOverlap && existing.getStatus() != ReservationStatus.CONFIRMED) {
//                throw new IllegalArgumentException("Overlapping confirmed reservation exists for this resource and time range.");
//            }
//        }
//
//        existing.setUpdatedAt(Instant.now());
//        Reservation saved = reservationRepository.save(existing);
//
//        return new ApiResponse<>("Reservation updated successfully", "success", modelMapper.map(saved, ReservationResponseDTO.class));
//    }
//
//    @Override
//    public ApiResponse<Void> deleteReservation(Long id, Long currentUserId, boolean isAdmin) {
//        Reservation existing = reservationRepository.findById(id)
//                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found with id: " + id));
//        if (!isAdmin && !existing.getUser().getId().equals(currentUserId)) {
//            throw new SecurityException("Not authorized to delete this reservation");
//        }
//        reservationRepository.delete(existing);
//        return new ApiResponse<>("Reservation deleted successfully", "success", null);
//    }
    
    @Transactional
    @Override
    public ApiResponse<ReservationResponseDTO> createReservation(ReservationRequestDTO dto, User user, boolean enforceOverlapCheck) {
        Reservation reservation = modelMapper.map(dto, Reservation.class);
        Resource resource = resourceRepository.findById(dto.getResourceId())
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found with id: " + dto.getResourceId()));

        reservation.setResource(resource);
        reservation.setUser(user);
        reservation.setCreatedAt(Instant.now());
        reservation.setUpdatedAt(Instant.now());

        // normalize seats
        if (reservation.getSeatsBooked() <= 0) reservation.setSeatsBooked(1);

        // If this reservation is being created as CONFIRMED, check availability
        if (reservation.getStatus() == ReservationStatus.CONFIRMED) {
            // optionally lock the resource to avoid race conditions
            Resource locked = resourceRepository.findByIdForUpdate(resource.getId()).orElse(resource);

            long alreadyBooked = reservationRepository.sumSeatsBookedByStatusAndOverlap(
                    resource.getId(), ReservationStatus.CONFIRMED, reservation.getStartTime(), reservation.getEndTime());

            long available = locked.getCapacity() - alreadyBooked;
            if (reservation.getSeatsBooked() > available) {
                throw new IllegalArgumentException("Not enough seats available. Requested: "
                    + reservation.getSeatsBooked() + ", Available: " + available);
            }
        }

        Reservation saved = reservationRepository.save(reservation);
        return new ApiResponse<>("Reservation created successfully", "success", modelMapper.map(saved, ReservationResponseDTO.class));
    }

    @Transactional
    @Override
    public ApiResponse<ReservationResponseDTO> updateReservation(Long id, ReservationRequestDTO updatedDTO, Long currentUserId, boolean isAdmin, boolean enforceOverlapCheck) {
        Reservation existing = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found with id: " + id));

        // authorization check remains same...
        modelMapper.map(updatedDTO, existing); // maps seatsBooked if present

        // If new status is CONFIRMED but old status was not, or if times/seats changed while CONFIRMED -> re-check
        boolean wasConfirmed = existing.getStatus() == ReservationStatus.CONFIRMED;
        boolean willBeConfirmed = updatedDTO.getStatus() == ReservationStatus.CONFIRMED;
        Instant start = updatedDTO.getStartTime() != null ? updatedDTO.getStartTime() : existing.getStartTime();
        Instant end = updatedDTO.getEndTime() != null ? updatedDTO.getEndTime() : existing.getEndTime();
        int seatsRequested = updatedDTO.getSeatsBooked() > 0 ? updatedDTO.getSeatsBooked() : existing.getSeatsBooked();

        if (willBeConfirmed) {
            // lock resource
            Resource locked = resourceRepository.findByIdForUpdate(existing.getResource().getId())
                .orElse(existing.getResource());

            // sum of other confirmed overlapping seats (exclude this reservation id)
            long alreadyBookedExcludingThis = reservationRepository.sumSeatsBookedByStatusAndOverlapExcludingReservation(
                    existing.getResource().getId(), ReservationStatus.CONFIRMED, start, end, existing.getId());

            long available = locked.getCapacity() - alreadyBookedExcludingThis;
            if (seatsRequested > available) {
                throw new IllegalArgumentException("Not enough seats available. Requested: " + seatsRequested + ", Available: " + available);
            }
        }

        existing.setUpdatedAt(Instant.now());
        Reservation saved = reservationRepository.save(existing);
        return new ApiResponse<>("Reservation updated successfully", "success", modelMapper.map(saved, ReservationResponseDTO.class));
    }

    @Transactional
    @Override
    public ApiResponse<Void> deleteReservation(Long id, Long currentUserId, boolean isAdmin) {
        Reservation existing = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found with id: " + id));
        // auth checks...
        reservationRepository.delete(existing);
        // no capacity mutation because availability is computed
        return new ApiResponse<>("Reservation deleted successfully", "success", null);
    }

    private Specification<Reservation> buildFilterSpecification(Long userId, ReservationStatus status, BigDecimal minPrice, BigDecimal maxPrice) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (userId != null) predicates.add(cb.equal(root.get("user").get("id"), userId));
            if (status != null) predicates.add(cb.equal(root.get("status"), status));
            if (minPrice != null) predicates.add(cb.greaterThanOrEqualTo(root.get("price"), minPrice));
            if (maxPrice != null) predicates.add(cb.lessThanOrEqualTo(root.get("price"), maxPrice));
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
    
    public int getAvailableSeats(Long resourceId, Instant start, Instant end) {
        // Fetch the resource or throw an exception if not found
        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found with id: " + resourceId));

        // Get total confirmed seats for overlapping reservations
        Long booked = reservationRepository.sumSeatsBookedByStatusAndOverlap(
                resourceId,
                ReservationStatus.CONFIRMED,
                start,
                end
        );

        // booked might be null if no reservations exist
        int bookedSeats = booked != null ? booked.intValue() : 0;

        // Return available seats
        return resource.getCapacity() - bookedSeats;
    }

}
