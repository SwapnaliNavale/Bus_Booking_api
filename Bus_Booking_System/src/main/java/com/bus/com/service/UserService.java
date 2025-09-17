package com.bus.com.service;

import com.bus.com.dto.ApiResponse;
import com.bus.com.dto.UserDTO;

public interface UserService {
	ApiResponse registerNewUser(UserDTO dto);
}
