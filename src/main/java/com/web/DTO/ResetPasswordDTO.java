package com.web.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ResetPasswordDTO {

	@NotBlank(message = "Email cannot be blank")
	@Email(message = "Invalid Email format")
	private String email;

	@NotBlank(message = "Password cannot be blank")
	private String oldPassword;

	@NotBlank(message = "New Password cannot be blank")
	@Size(min = 8, max = 20, message = "Password must be between 8 and 20 characters")
	private String password;

}
