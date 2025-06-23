package com.web.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "cart", uniqueConstraints = @UniqueConstraint(columnNames = { "user_id", "product_id", "price_id" }))
public class CartEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "cart_id")
	private Long cartId;

	@NotNull(message = "userId cannot be empty.")
	@Column(name = "user_id")
	private Long userId;

	@ManyToOne
	@JoinColumn(name = "product_id", nullable = false)
	@JsonManagedReference
	private ProductEntity product;

	@NotNull(message = "priceId cannot be empty.")
	@Column(name = "price_id", nullable = false)
	private Long priceId;

	@NotNull(message = "Quantity cannot be empty.")
	@Column(name = "quantity")
	private Long quantity;

	public CartEntity(@NotNull(message = "userId cannot be empty.") Long userId, ProductEntity product,
			@NotNull(message = "priceId cannot be empty.") Long price,
			@NotNull(message = "Quantity cannot be empty.") Long quantity) {
		super();
		this.userId = userId;
		this.product = product;
		this.priceId = price;
		this.quantity = quantity;
	}

	public CartEntity(Long cartId, @NotNull(message = "Quantity cannot be empty.") Long quantity) {
		super();
		this.cartId = cartId;
		this.quantity = quantity;
	}

}
