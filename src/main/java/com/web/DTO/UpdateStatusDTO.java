package com.web.DTO;

import java.util.List;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateStatusDTO {

	@NotBlank(message = "Email cannot be Blank.")
	@Email(message = "Invalid email format.")
	private String email;

	@NotEmpty(message = "List cannot be Blank.")
	private List<@NotNull Long> ids;

}
