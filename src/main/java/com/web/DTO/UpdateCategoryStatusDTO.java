package com.web.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateCategoryStatusDTO {

	@NotBlank(message = "Email cannot be Blank.")
	@Email(message = "Invalid email format.")
	private String email;

	@NotNull(message = "Id cannot be Blank.")
	private Long id;

}
