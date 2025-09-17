package com.bus.com.dto;

import java.time.LocalDate;
import com.bus.com.entities.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class UserRequestDTO {

    @NotBlank(message = "First name is required")
    private String firstName;

    private String lastName;

    @Email(message = "Invalid Email Format")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank
    @Pattern(
        regexp = "^(?=.*[A-Z])(?=.*\\d)(?=.*[#@$*.])[A-Za-z\\d#@$*.]{8,16}$",
        message = "Password must be 8-16 characters long, contain at least one uppercase, one digit, and one special character (#@$*.)"
    )
    private String password;

    @NotBlank(message = "Confirm password is required")
    private String confirmPassword;

    @NotNull(message = "DOB is required")
    @Past(message = "DOB must be in the past")
    private LocalDate dob;

    private Long mobileNo;

    @NotNull
    private UserRole role = UserRole.ROLE_USER;

    private int age; // optional, can calculate from DOB
}
