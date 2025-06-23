package com.web.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.web.entity.CategoriesEntity;
import com.web.entity.CategoriesEntity.CategoryStatus;

import jakarta.transaction.Transactional;

@Repository
public interface CategoriesDao extends JpaRepository<CategoriesEntity, Long> {

	@Modifying
	@Transactional
	@Query("UPDATE CategoriesEntity c SET c.categoryStatus = CASE WHEN c.categoryStatus = :active THEN :inactive ELSE :active END WHERE c.categoryId = :id")
	int updateStatusForUserId(@Param("id") Long id, @Param("active") CategoryStatus active,
			@Param("inactive") CategoryStatus inactive);

	@Modifying
	@Transactional
	@Query("UPDATE CategoriesEntity c SET c.category = :category WHERE c.categoryId = :id")
	int updateCategoryById(@Param("category") String categoryName, @Param("id") Long categoryId);

	@Modifying
	@Transactional
	@Query("UPDATE CategoriesEntity c SET c.category = :category, c.path = :path WHERE c.categoryId = :id")
	int updateCategoryImageById(@Param("id") Long categoryId, @Param("category") String category,
			@Param("path") String path);

	@Query("SELECT c.category FROM CategoriesEntity c")
	List<String> getAllCategoryNames();

}
