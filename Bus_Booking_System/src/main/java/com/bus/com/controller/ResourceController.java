package com.bus.com.controller;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.bus.com.dto.ApiResponse;
import com.bus.com.dto.ResourceRequestDTO;
import com.bus.com.dto.ResourceResponseDTO;
import com.bus.com.service.ResourceService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/resources")
public class ResourceController {

    @Autowired
    private ResourceService resourceService;

    // USER + ADMIN → list resources
    @GetMapping
    public ResponseEntity<ApiResponse<Page<ResourceResponseDTO>>> listResources(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdOn,desc") String sort // e.g. createdOn,desc
    ) {
        String[] sortParts = sort.split(",");
        Sort.Direction dir = sortParts.length > 1 && sortParts[1].equalsIgnoreCase("asc")
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(dir, sortParts[0]));
        ApiResponse<Page<ResourceResponseDTO>> resp = resourceService.listAll(pageable);
        return ResponseEntity.ok(resp);
    }

    // USER + ADMIN → get single resource
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ResourceResponseDTO>> getResource(@PathVariable Long id) {
        ApiResponse<ResourceResponseDTO> resp = resourceService.findById(id);
        return ResponseEntity.ok(resp);
    }

    // ADMIN only
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse<ResourceResponseDTO>> createResource(
            @Valid @RequestBody ResourceRequestDTO dto) {
        ApiResponse<ResourceResponseDTO> resp = resourceService.create(dto);
        return ResponseEntity.status(201).body(resp);
    }

    // ADMIN only
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ResourceResponseDTO>> updateResource(
            @PathVariable Long id,
            @Valid @RequestBody ResourceRequestDTO dto) {
        ApiResponse<ResourceResponseDTO> resp = resourceService.update(id, dto);
        return ResponseEntity.ok(resp);
    }

    // ADMIN only
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteResource(@PathVariable Long id) {
        ApiResponse<Void> resp = resourceService.delete(id);
        return ResponseEntity.ok(resp);
    }
    
//    @GetMapping("/{id}/availability")
//    public ResponseEntity<ApiResponse<Integer>> getAvailableSeats(
//            @PathVariable Long id,
//            @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant start,
//            @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant end) {
//
//        int availableSeats = resourceService.getAvailableSeats(id, start, end);
//
//        ApiResponse<Integer> response = new ApiResponse<>(
//                "Available seats fetched successfully",
//                "success",
//                availableSeats
//        );
//
//        return ResponseEntity.ok(response);
//    }
}
