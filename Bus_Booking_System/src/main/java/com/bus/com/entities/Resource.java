package com.bus.com.entities;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "resources")
@NoArgsConstructor
@Getter
@Setter
@ToString(callSuper = true, exclude = "reservations")
public class Resource extends BaseEntity {

    @Column(length = 100, nullable = false)
    private String name;

    @Column(length = 50)
    private String type;

    @Column(length = 255)
    private String description;

    @Column
    private int capacity;

    @Column(name = "is_active")
    private boolean isActive = true;

    @OneToMany(mappedBy = "resource", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Reservation> reservations = new ArrayList<>();

    public Resource(String name, String type, String description, int capacity, boolean isActive) {
        this.name = name;
        this.type = type;
        this.description = description;
        this.capacity = capacity;
        this.isActive = isActive;
    }
}
