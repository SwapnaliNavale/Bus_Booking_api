package com.bus.com.service;

import java.time.Instant;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.bus.com.dto.ApiResponse;
import com.bus.com.dto.ResourceRequestDTO;
import com.bus.com.dto.ResourceResponseDTO;
import com.bus.com.entities.Resource;

public interface ResourceService {

    ApiResponse<Page<ResourceResponseDTO>> listAll(Pageable pageable);

    ApiResponse<ResourceResponseDTO> findById(Long id);

    ApiResponse<ResourceResponseDTO> create(ResourceRequestDTO resourceDTO);

    ApiResponse<ResourceResponseDTO> update(Long id, ResourceRequestDTO resourceDTO);

    ApiResponse<Void> delete(Long id); 
    
//    int getAvailableSeats(Long resourceId, Instant start, Instant end);
    
}