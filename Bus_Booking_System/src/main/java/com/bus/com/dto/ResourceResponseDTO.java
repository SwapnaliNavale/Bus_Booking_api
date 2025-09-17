package com.bus.com.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class ResourceResponseDTO {
    private Long id;
    private String name;
    private String type;
    private int capacity;
    private boolean isActive;

    public ResourceResponseDTO(Long id, String name, String type, int capacity, boolean isActive) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.capacity = capacity;
        this.isActive = isActive;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getType() { return type; }
    public int getCapacity() { return capacity; }
    public boolean isActive() { return isActive; }
}

