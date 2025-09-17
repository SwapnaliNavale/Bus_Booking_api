package com.bus.com.repositories;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.bus.com.entities.Reservation;
import com.bus.com.entities.ReservationStatus;
import com.bus.com.entities.Resource;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long>, JpaSpecificationExecutor<Reservation> {

    // Check for overlapping CONFIRMED reservations for the same resource
    @Query("""
        select case when count(r) > 0 then true else false end
        from Reservation r
        where r.resource.id = :resourceId
          and r.status = :status
          and not (r.endTime <= :startTime or r.startTime >= :endTime)
    """)
    boolean existsOverlappingReservation(@Param("resourceId") Long resourceId,
                                         @Param("status") ReservationStatus status,
                                         @Param("startTime") Instant startTime,
                                         @Param("endTime") Instant endTime);

    List<Reservation> findByUserId(Long userId);
    
    @Query("SELECT COALESCE(SUM(r.seatsBooked), 0) " +
    	       "FROM Reservation r " +
    	       "WHERE r.resource.id = :resourceId " +
    	       "AND r.status = :status " +
    	       "AND r.startTime < :endTime " +
    	       "AND r.endTime > :startTime")
    	Long sumSeatsBookedByStatusAndOverlap(
    	        @Param("resourceId") Long resourceId,
    	        @Param("status") ReservationStatus status,
    	        @Param("startTime") Instant startTime,
    	        @Param("endTime") Instant endTime
    	);

     // helper that excludes a given reservation id (useful on update)
     @Query("SELECT COALESCE(SUM(r.seatsBooked), 0) " +
            "FROM Reservation r " +
            "WHERE r.resource.id = :resourceId " +
            "  AND r.status = :status " +
            "  AND r.startTime < :endTime " +
            "  AND r.endTime > :startTime " +
            "  AND r.id <> :excludeId")
     Long sumSeatsBookedByStatusAndOverlapExcludingReservation(@Param("resourceId") Long resourceId,
                                                              @Param("status") ReservationStatus status,
                                                              @Param("startTime") Instant startTime,
                                                              @Param("endTime") Instant endTime,
                                                              @Param("excludeId") Long excludeId);
     
     
}

