package com.web.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.web.entity.ProductImagesEntity;

@Repository
public interface ProductImageDao extends JpaRepository<ProductImagesEntity, Long> {

	@Query("SELECT i FROM ProductImagesEntity i WHERE i.product.productId = :productId")
	Optional<ProductImagesEntity> findByProductId(@Param("productId") Long productId);

	int countByProduct_ProductId(Long productId);

	@Modifying
	@Query("DELETE FROM ProductImagesEntity p WHERE p.product.id = :productId")
	void deleteByProductId(@Param("productId") Long productId);

}
