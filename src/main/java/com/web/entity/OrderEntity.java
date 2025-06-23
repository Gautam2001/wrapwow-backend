package com.web.entity;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "orders")
public class OrderEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "order_id")
	private Long orderId;

	@Column(name = "user_id", nullable = false)
	private Long userId;

	@Column(name = "total_items", nullable = false)
	private Integer totalItems;

	@Column(name = "total_amount", nullable = false)
	private Double totalAmount;

	@Column(name = "order_time", nullable = false)
	private LocalDateTime orderTime;

	@Column(name = "order_status", nullable = false)
	private String orderStatus;

	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
	private List<OrderItemEntity> items;

	public OrderEntity(Long userId, Integer totalItems, Double totalAmount, LocalDateTime orderTime) {
		this.userId = userId;
		this.totalItems = totalItems;
		this.totalAmount = totalAmount;
		this.orderTime = orderTime;
	}
}
