package com.web.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddToCartDTO {

	@NotBlank(message = "Email cannot be Blank.")
	@Email(message = "Invalid Email format.")
	private String email;

	@NotNull(message = "ProductId cannot by empty.")
	private long productId;

	@NotNull(message = "PriceId cannot be empty.")
	private long priceId;

	@NotNull(message = "Quatity cannot be empty.")
	private long quantity;

}
