package com.bus.com.dto;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class ApiResponse<T> {  
    private LocalDateTime timeStamp;
    private String status;
    private String message;
    private T data; // add data field

    public ApiResponse(String message, String status) {
        super();
        this.status = status;
        this.message = message;
        this.timeStamp = LocalDateTime.now();
    }

    public ApiResponse(String message, String status, T data) {  
        super();
        this.status = status;
        this.message = message;
        this.data = data;
        this.timeStamp = LocalDateTime.now();
    }
}
