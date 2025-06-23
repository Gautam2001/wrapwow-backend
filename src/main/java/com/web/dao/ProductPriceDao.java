package com.web.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.web.entity.ProductPriceEntity;

import jakarta.transaction.Transactional;

@Repository
public interface ProductPriceDao extends JpaRepository<ProductPriceEntity, Long> {

	@Transactional
	@Modifying
	@Query("DELETE FROM ProductPriceEntity p where p.product.productId = :productId")
	int deleteByProductId(@Param("productId") Long productId);

}
