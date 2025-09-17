package com.bus.com.dto;

import java.math.BigDecimal;
import java.time.Instant;
import com.bus.com.entities.ReservationStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@NoArgsConstructor
@Getter
@Setter
public class ReservationRequestDTO {

    @NotNull(message = "Resource ID is required")
    private Long resourceId;

    // Optional for admin; for user it will be derived from JWT
    private Long userId;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    private BigDecimal price;

    @NotNull(message = "Start time is required")
    private Instant startTime;

    @NotNull(message = "End time is required")
    private Instant endTime;

    private ReservationStatus status = ReservationStatus.PENDING;
    
    private int seatsBooked = 1;
}
