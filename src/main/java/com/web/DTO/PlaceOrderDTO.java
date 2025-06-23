package com.web.DTO;

import java.util.List;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PlaceOrderDTO {

	@NotBlank(message = "Email cannot be blank.")
	@Email(message = "Invalid email format.")
	private String userEmail;

	@NotNull(message = "Total items cannot be null.")
	@Min(value = 1, message = "At least one item must be purchased.")
	private Integer totalItems;

	@NotNull(message = "Total amount cannot be null.")
	@Min(value = 1, message = "Total amount must be greater than zero.")
	private Double totalAmount;

	@NotEmpty(message = "Item list cannot be empty.")
	private List<OrderItemDTO> items;

	@Data
	public static class OrderItemDTO {

		@NotNull(message = "Product ID cannot be null.")
		private Long productId;

		@NotBlank(message = "Product name cannot be blank.")
		private String productName;

		@NotNull(message = "Quantity cannot be null.")
		@Min(value = 1, message = "Quantity must be at least 1.")
		private Long quantity;

		@NotNull(message = "Price per unit cannot be null.")
		@Min(value = 1, message = "Price per unit must be positive.")
		private Double pricePerUnit;

		@NotNull(message = "Total price cannot be null.")
		@Min(value = 1, message = "Total price must be positive.")
		private Double totalPrice;

		@NotNull(message = "Price ID cannot be null.")
		private Long priceId;
	}
}
