package com.bus.com.service;

import java.time.Instant;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.bus.com.custom_exceptions.ResourceNotFoundException;
import com.bus.com.dto.ApiResponse;
import com.bus.com.dto.ResourceRequestDTO;
import com.bus.com.dto.ResourceResponseDTO;
import com.bus.com.entities.ReservationStatus;
import com.bus.com.entities.Resource;
import com.bus.com.repositories.ReservationRepository;
import com.bus.com.repositories.ResourceRepository;
import com.bus.com.service.ResourceService;

@Service
public class ResourceServiceImpl implements ResourceService {

    @Autowired
    private ResourceRepository resourceRepository;
    
    @Autowired
	private ModelMapper modelMapper;
    
    @Autowired
    private ReservationRepository reservationRepository;

    @Override
    public ApiResponse<Page<ResourceResponseDTO>> listAll(Pageable pageable) {
        Page<Resource> entities = resourceRepository.findAll(pageable);
        Page<ResourceResponseDTO> dtoPage = entities.map(entity -> modelMapper.map(entity, ResourceResponseDTO.class));
        return new ApiResponse<>("Resources fetched successfully", "success", dtoPage);
    }


    @Override
    public ApiResponse<ResourceResponseDTO> findById(Long id) {
        Resource entity = resourceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found with id: " + id));
        ResourceResponseDTO dto = modelMapper.map(entity, ResourceResponseDTO.class);
        return new ApiResponse<>("Resource fetched successfully", "success", dto);
    }



    @Override
    public ApiResponse<ResourceResponseDTO> create(ResourceRequestDTO dto) {
        Resource entity = modelMapper.map(dto, Resource.class);
        entity.setId(null);
        Resource saved = resourceRepository.save(entity);

        ResourceResponseDTO responseDTO = modelMapper.map(saved, ResourceResponseDTO.class);
        return new ApiResponse<>("Resource created successfully", "success", responseDTO);
    }
    
    // helper
    private Resource getResourceEntityById(Long id) {
        return resourceRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Resource not found with id: " + id));
    }


    @Override
    public ApiResponse<ResourceResponseDTO> update(Long id, ResourceRequestDTO dto) {
        Resource existing = getResourceEntityById(id); 
        modelMapper.map(dto, existing); 
        Resource updated = resourceRepository.save(existing);

        ResourceResponseDTO responseDTO = modelMapper.map(updated, ResourceResponseDTO.class);
        return new ApiResponse<>("Resource updated successfully", "success", responseDTO);
    }



    @Override
    public ApiResponse<Void> delete(Long id) {
        Resource existing = resourceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found with id: " + id));
        resourceRepository.delete(existing);
        return new ApiResponse<>("Resource deleted successfully", "success");
    }
    
//    @Override
//    public int getAvailableSeats(Long resourceId, Instant start, Instant end) {
//        // Fetch the resource or throw an exception if not found
//        Resource resource = resourceRepository.findById(resourceId)
//                .orElseThrow(() -> new ResourceNotFoundException("Resource not found with id: " + resourceId));
//
//        // Get total confirmed seats for overlapping reservations
//        Long booked = reservationRepository.sumSeatsBookedByStatusAndOverlap(
//                resourceId,
//                ReservationStatus.CONFIRMED,
//                start,
//                end
//        );
//
//        // booked might be null if no reservations exist
//        int bookedSeats = booked != null ? booked.intValue() : 0;
//
//        // Return available seats
//        return resource.getCapacity() - bookedSeats;
//    }

}
