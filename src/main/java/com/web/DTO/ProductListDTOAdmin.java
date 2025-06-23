package com.web.DTO;

import com.web.entity.ProductEntity.ProductStatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductListDTOAdmin {

	@NotNull(message = "Id cannot be blank")
	private Long productId;

	@NotBlank(message = "Name cannot be Blank.")
	private String name;

	@NotBlank(message = "Category cannot be Blank")
	private String category;

	@NotNull(message = "Quantity cannot be Blank.")
	private Long availableQty;

	@NotNull(message = "Quantity cannot be Blank.")
	private Long totalOrderedQty;

	@NotBlank(message = "Product Status cannot be Blank.")
	private ProductStatus productStatus;

}
