package com.web.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MemberLoginDTO {

	@NotBlank(message = "Email cannot be Blank.")
	@Email(message = "Invalid email format.")
	private String email;

	@NotBlank(message = "Password cannot be blank.")
	private String password;

}
