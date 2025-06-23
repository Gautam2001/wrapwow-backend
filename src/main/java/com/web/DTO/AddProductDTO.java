package com.web.DTO;

import java.util.List;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddProductDTO {

	@NotBlank(message = "Email cannot Be Blank.")
	@Email(message = "Invalid email format.")
	private String email;

	@NotBlank(message = "Name cannot be Blank.")
	private String name;

	@NotBlank(message = "Description cannot be Blank.")
	private String description;

	@NotNull(message = "Price cannot be Blank.")
	private List<Double> price;

	@NotNull(message = "discount cannot be Blank.")
	private List<Float> discount;

	@NotNull(message = "finalPrice cannot be Blank.")
	private List<Double> finalPrice;

	@NotNull(message = "Quantity cannot be Blank.")
	private Long quantity;

	@NotNull(message = "Category cannot be Blank")
	private String category;

	@NotNull(message = "Product Status cannot be Blank.")
	private String productStatus;

}
