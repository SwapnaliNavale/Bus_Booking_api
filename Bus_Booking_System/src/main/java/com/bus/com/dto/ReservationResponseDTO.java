package com.bus.com.dto;

import java.math.BigDecimal;
import java.time.Instant;
import com.bus.com.entities.ReservationStatus;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class ReservationResponseDTO {
    private Long id;
    private UserResponseDTO user;
    private ResourceResponseDTO resource;
    private ReservationStatus status;
    private BigDecimal price;
    private Instant startTime;
    private Instant endTime;

    public ReservationResponseDTO(Long id, UserResponseDTO user, ResourceResponseDTO resource,
                                  ReservationStatus status, BigDecimal price,
                                  Instant startTime, Instant endTime) {
        this.id = id;
        this.user = user;
        this.resource = resource;
        this.status = status;
        this.price = price;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Long getId() { return id; }
    public UserResponseDTO getUser() { return user; }
    public ResourceResponseDTO getResource() { return resource; }
    public ReservationStatus getStatus() { return status; }
    public BigDecimal getPrice() { return price; }
    public Instant getStartTime() { return startTime; }
    public Instant getEndTime() { return endTime; }
}

