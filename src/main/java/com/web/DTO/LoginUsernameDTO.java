package com.web.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginUsernameDTO {

	@NotBlank(message = "Username is required")
	@Email(message = "Invalid Email format")
	private String username;

}
