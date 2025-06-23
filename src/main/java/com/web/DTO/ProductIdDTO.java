package com.web.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProductIdDTO {

	@NotBlank(message = "Email cannot be blank")
	@Email(message = "Invalid Email format")
	private String email;

	@NotNull(message = "Product Id cannot be empty")
	private long productId;

}
