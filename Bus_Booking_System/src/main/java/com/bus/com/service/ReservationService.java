package com.bus.com.service;

import java.math.BigDecimal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.bus.com.dto.ApiResponse;
import com.bus.com.dto.ReservationRequestDTO;
import com.bus.com.dto.ReservationResponseDTO;
import com.bus.com.entities.ReservationStatus;
import com.bus.com.entities.User;

public interface ReservationService {

    ApiResponse<Page<ReservationResponseDTO>> listReservationsForAdmin(
            ReservationStatus status,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Pageable pageable
    );

    ApiResponse<Page<ReservationResponseDTO>> listReservationsForUser(
            Long userId,
            ReservationStatus status,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Pageable pageable
    );

    ApiResponse<ReservationResponseDTO> findById(Long id);

    ApiResponse<ReservationResponseDTO> createReservation(ReservationRequestDTO reservationDTO, User user,  boolean enforceOverlapCheck);

    ApiResponse<ReservationResponseDTO> updateReservation(Long id, ReservationRequestDTO updatedDTO, Long currentUserId, boolean isAdmin, boolean enforceOverlapCheck);

    ApiResponse<Void> deleteReservation(Long id, Long currentUserId, boolean isAdmin);
}
