package com.bus.com.controller;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.bus.com.dto.ApiResponse;
import com.bus.com.dto.ReservationRequestDTO;
import com.bus.com.dto.ReservationResponseDTO;
import com.bus.com.entities.Reservation;
import com.bus.com.entities.ReservationStatus;
import com.bus.com.entities.Resource;
import com.bus.com.entities.User;
import com.bus.com.service.ReservationService;
import com.bus.com.repositories.ResourceRepository;
import com.bus.com.repositories.UserRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/reservations")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private ResourceRepository resourceRepository;

    @Autowired
    private UserRepository userRepository;

    // List: ADMIN => all; USER => own only. Filtering + pagination + sort
   @GetMapping
public ResponseEntity<ApiResponse<Page<ReservationResponseDTO>>> listReservations(
        @RequestParam(required = false) ReservationStatus status,
        @RequestParam(required = false) BigDecimal minPrice,
        @RequestParam(required = false) BigDecimal maxPrice,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "createdAt,desc") String sort,
        Authentication authentication
) {
    String[] sortParts = sort.split(",");
    Sort.Direction dir = sortParts.length > 1 && sortParts[1].equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
    Pageable pageable = PageRequest.of(page, size, Sort.by(dir, sortParts[0]));

    boolean isAdmin = authentication.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

    if (isAdmin) {
        ApiResponse<Page<ReservationResponseDTO>> resp = reservationService.listReservationsForAdmin(status, minPrice, maxPrice, pageable);
        return ResponseEntity.ok(resp);
    } else {
        Long userId = (Long) authentication.getCredentials();
        ApiResponse<Page<ReservationResponseDTO>> resp = reservationService.listReservationsForUser(userId, status, minPrice, maxPrice, pageable);
        return ResponseEntity.ok(resp);
    }
}


    // Get by id: ADMIN any; USER only own
   @GetMapping("/{id}")
   public ResponseEntity<ApiResponse<ReservationResponseDTO>> getReservation(
           @PathVariable Long id,
           Authentication authentication) {
       ApiResponse<ReservationResponseDTO> resp = reservationService.findById(id);

       boolean isAdmin = authentication.getAuthorities().stream()
               .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
       Long currentUserId = (Long) authentication.getCredentials();

       // check ownership from DTO
       if (!isAdmin && !resp.getData().getUser().getId().equals(currentUserId)) {
    	    return ResponseEntity.status(403).build();
    	}

       return ResponseEntity.ok(resp);
   }

    // Create: USER/ADMIN can create. For USER, userId is taken from JWT (ignore body userId)
    public static record CreateReservationRequest(Long resourceId, Long userId, ReservationStatus status,
                                                  BigDecimal price, Instant startTime, Instant endTime) {}

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<ApiResponse<ReservationResponseDTO>> createReservation(
            @Valid @RequestBody ReservationRequestDTO req,
            Authentication authentication) {

    	 boolean isAdmin = authentication.getAuthorities().stream()
    	            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

    	    // current logged in user
    	    Long currentUserId = (Long) authentication.getCredentials();

    	    // if admin, allow overriding via request body
    	    Long effectiveUserId = isAdmin && req.getUserId() != null ? req.getUserId() : currentUserId;

    	    User u = userRepository.findById(effectiveUserId)
    	            .orElseThrow(() -> new IllegalArgumentException("User not found: " + effectiveUserId));

    	    // delegate to service
    	    ApiResponse<ReservationResponseDTO> created = reservationService.createReservation(req, u, isAdmin);
        return ResponseEntity.status(201).body(created);
    }


    // Update reservation
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<ApiResponse<ReservationResponseDTO>> updateReservation(
            @PathVariable Long id,
            @Valid @RequestBody ReservationRequestDTO req,
            Authentication authentication) {

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        Long currentUserId = (Long) authentication.getCredentials();

        ApiResponse<ReservationResponseDTO> resp =
                reservationService.updateReservation(id, req, currentUserId, isAdmin, true);
        return ResponseEntity.ok(resp);
    }


    // Delete
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<ApiResponse<Void>> deleteReservation(
            @PathVariable Long id,
            Authentication authentication) {

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        Long currentUserId = (Long) authentication.getCredentials();

        ApiResponse<Void> resp = reservationService.deleteReservation(id, currentUserId, isAdmin);
        return ResponseEntity.ok(resp);
    }
    

}

