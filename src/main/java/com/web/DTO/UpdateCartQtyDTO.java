package com.web.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCartQtyDTO {

	@NotBlank(message = "Email notnot be blank.")
	@Email
	private String email;

	@NotNull(message = "cartId cannot be empty.")
	private Long cartId;

	@NotNull(message = "Quantity cannot be empty.")
	private Long quantity;

}
