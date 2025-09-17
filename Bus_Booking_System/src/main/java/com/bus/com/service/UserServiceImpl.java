package com.bus.com.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.bus.com.custom_exceptions.ApiException;
import com.bus.com.dto.ApiResponse;
import com.bus.com.dto.UserDTO;
import com.bus.com.dto.UserResponseDTO;
import com.bus.com.entities.User;
import com.bus.com.repositories.UserRepository;

import jakarta.transaction.Transactional;


@Service
@Transactional
public class UserServiceImpl implements UserService {

	// depcy - dao
		@Autowired
		private UserRepository userRepository;
		// model mapper
		@Autowired
		private ModelMapper modelMapper;
		//pwd encoder
		@Autowired
		private PasswordEncoder passwordEncoder;
		
//	@Override
//	public ApiResponse registerNewUser(UserDTO dto) {
//		// chk if user alrdy exists
//				if (userRepository.existsByEmail(dto.getEmail()))
//					throw new ApiException("User email already exists!!!!");
//				
//				 // check duplicate mobile
//			    if (userRepository.existsByMobileNo(dto.getMobileNo())) {
//			        throw new ApiException("Mobile number already exists: " + dto.getMobileNo());
//			    }
//
//			    // check confirm password
//			    if (!dto.getPassword().equals(dto.getConfirmPassword())) {
//			        throw new ApiException("Password and Confirm Password do not match");
//			    }
//			    
//				// map dto -> entity
//				User user = modelMapper.map(dto, User.class);
//				user.setPassword(passwordEncoder.encode(user.getPassword()));
//				User savedUser = userRepository.save(user);
//				return new ApiResponse("User registered with ID " + savedUser.getId(),"success");
//	}
		@Override
		public ApiResponse<UserResponseDTO> registerNewUser(UserDTO dto) {
		   
		    if (userRepository.existsByEmail(dto.getEmail()))
		        throw new ApiException("User email already exists");

		    if (userRepository.existsByMobileNo(dto.getMobileNo()))
		        throw new ApiException("Mobile number already exists: " + dto.getMobileNo());

		    if (!dto.getPassword().equals(dto.getConfirmPassword()))
		        throw new ApiException("Password and Confirm Password do not match");

		    // map DTO -> entity
		    User user = modelMapper.map(dto, User.class);
		    user.setPassword(passwordEncoder.encode(user.getPassword()));

		    User savedUser = userRepository.save(user);

		    // map entity -> response DTO
		    UserResponseDTO responseDTO = modelMapper.map(savedUser, UserResponseDTO.class);

		    // wrap response DTO inside ApiResponse
		    return new ApiResponse<>("User registered successfully", "success", responseDTO);
		}


}
