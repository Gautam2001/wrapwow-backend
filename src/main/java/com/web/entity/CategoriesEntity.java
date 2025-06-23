package com.web.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "Categories")
public class CategoriesEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "category_id")
	private Long categoryId;

	@NotNull(message = "Category cannot be Empty")
	@Column(nullable = false)
	private String category;

	@Column(nullable = false)
	private String path;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private CategoryStatus categoryStatus;

	public enum CategoryStatus {
		ACTIVE, INACTIVE
	}

	@PrePersist
	protected void onCreate() {
		this.categoryStatus = CategoryStatus.INACTIVE;
	}

	public CategoriesEntity(Long categoryId, @NotNull(message = "Category cannot be Empty") String category,
			String path) {
		super();
		this.categoryId = categoryId;
		this.category = category;
		this.path = path;
	}

	public CategoriesEntity(@NotNull(message = "Category cannot be Empty") String category, String path) {
		super();
		this.category = category;
		this.path = path;
	}

}
