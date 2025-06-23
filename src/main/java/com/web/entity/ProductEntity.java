package com.web.entity;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString(exclude = "images")
@Entity
@Table(name = "Products")
public class ProductEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "product_id")
	private Long productId;

	@NotNull(message = "Name cannot be Empty")
	@Column(nullable = false)
	private String name;

	@NotNull(message = "Description cannot be Empty")
	@Column(nullable = false, columnDefinition = "TEXT")
	private String description;

	@NotNull(message = "Available Qty cannot be Empty")
	@Column(nullable = false, name = "available_qty")
	private Long availableQty;

	@NotNull(message = "Total Ordered Qty cannot be Empty")
	@Column(nullable = false, name = "total_ordered_qty")
	private Long totalOrderedQty;

	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@Column(nullable = false)
	private LocalDateTime updatedAt;

	@NotNull(message = "Updated By cannot be Empty")
	@Column(nullable = false)
	private String updatedBy;

	@NotNull(message = "Category cannot be Empty")
	@Column(nullable = false)
	private String category;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private ProductStatus productStatus;

	@OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ProductPriceEntity> prices;

	@OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ProductImagesEntity> images;

	public enum ProductStatus {
		ACTIVE, INACTIVE
	}

	@PrePersist
	protected void onCreate() {
		this.createdAt = LocalDateTime.now();
		this.updatedAt = LocalDateTime.now();
		if (this.productStatus == null) {
			this.productStatus = ProductStatus.INACTIVE;
		}
		this.totalOrderedQty = (long) 0;
	}

	@PreUpdate
	protected void onUpdate() {
		this.updatedAt = LocalDateTime.now();
	}

	public ProductEntity(@NotNull(message = "Name cannot be Empty") String name,
			@NotNull(message = "Description cannot be Empty") String description,
			@NotNull(message = "Available Qty cannot be Empty") Long availableQty,
			@NotNull(message = "Category cannot be Empty") String category, ProductStatus productStatus, String email) {
		super();
		this.name = name;
		this.description = description;
		this.availableQty = availableQty;
		this.category = category;
		this.productStatus = productStatus;
		this.updatedBy = email;
	}

	public ProductEntity(@NotNull(message = "ProductId cannot be empty") Long productId,
			@NotNull(message = "Name cannot be Empty") String name,
			@NotNull(message = "Description cannot be Empty") String description,
			@NotNull(message = "Available Qty cannot be Empty") Long availableQty,
			@NotNull(message = "Category cannot be Empty") String category, ProductStatus productStatus, String email) {
		super();
		this.productId = productId;
		this.name = name;
		this.description = description;
		this.availableQty = availableQty;
		this.category = category;
		this.productStatus = productStatus;
		this.updatedBy = email;
	}

}
