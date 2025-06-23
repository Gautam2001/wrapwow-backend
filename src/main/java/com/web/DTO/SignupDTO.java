package com.web.DTO;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SignupDTO {

	@NotBlank(message = "Email cannot be blank")
	@Email(message = "Invalid Email format")
	private String email;

	@NotBlank(message = "Name cannot be blank")
	@Size(min = 2, max = 50, message = "Name between 2 and 50 Characters")
	private String name;

	@NotBlank(message = "Password cannot be blank")
	@Size(min = 8, max = 20, message = "Password must be between 8 and 20 characters")
	private String password;

	@NotNull(message = "DOB cannot be blank")
	@JsonFormat(pattern = "dd/MM/yyyy")
	private LocalDate dob;

	@NotBlank(message = "Role cannot be blank")
	@Size(min = 4, max = 5, message = "Specify the role of the Member")
	private String role;

}
