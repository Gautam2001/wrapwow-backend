package com.web.DTO;

import java.util.List;

import com.web.entity.ProductEntity.ProductStatus;
import com.web.entity.ProductImagesEntity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductListDTOUser {

	@NotNull(message = "Id cannot be blank")
	private Long productId;

	@NotBlank(message = "Name cannot be Blank.")
	private String name;

	@NotBlank(message = "Category cannot be Blank")
	private String category;

	@NotNull(message = "Images cannot be Blank.")
	private List<ProductImagesEntity> images;

	@NotNull(message = "Prices cannot be Blank.")
	private double minPrice;

	@NotBlank(message = "Product Status cannot be Blank.")
	private ProductStatus productStatus;

	@NotNull(message = "Quantity cannot be Blank.")
	private Long availableQty;

}
