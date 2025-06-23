package com.web.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "order_item")
public class OrderItemEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "order_item_id")
	private Long orderItemId;

	@ManyToOne
	@JoinColumn(name = "order_id", nullable = false)
	private OrderEntity order;

	@ManyToOne
	@JoinColumn(name = "product_id", nullable = false)
	private ProductEntity product;

	@NotNull(message = "priceId cannot be empty.")
	@Column(name = "price_id", nullable = false)
	private Long priceId;

	@NotNull(message = "quantity cannot be empty.")
	@Column(name = "quantity", nullable = false)
	private Long quantity;

	@Column(name = "price_per_unit", nullable = false)
	private Double pricePerUnit;

	@Column(name = "total_price", nullable = false)
	private Double totalPrice;

	public OrderItemEntity(OrderEntity order, ProductEntity product,
			@NotNull(message = "priceId cannot be empty.") Long price, Long quantity, Double pricePerUnit,
			Double totalPrice) {
		this.order = order;
		this.product = product;
		this.priceId = price;
		this.quantity = quantity;
		this.pricePerUnit = pricePerUnit;
		this.totalPrice = totalPrice;
	}
}
