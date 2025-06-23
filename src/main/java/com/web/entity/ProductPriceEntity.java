package com.web.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString(exclude = "product")
@Entity
@Table(name = "ProductPrice")
public class ProductPriceEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "price_id")
	private long priceId;

	@NotNull(message = "Price cannot be Empty")
	@Column(nullable = false)
	private double price;

	@NotNull(message = "Discount cannot be Empty")
	@Column(nullable = false)
	private float discount;

	@NotNull(message = "Final Price cannot be Empty")
	@Column(nullable = false)
	private double finalPrice;

	@ManyToOne
	@JoinColumn(name = "product_id", nullable = false)
	@JsonIgnore
	private ProductEntity product;

}
