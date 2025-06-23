package com.web.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.web.entity.CartEntity;
import com.web.entity.ProductEntity;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@Repository
public interface CartDao extends JpaRepository<CartEntity, Long> {

	Optional<CartEntity> findByUserIdAndProductAndPriceId(Long userId, ProductEntity product, long priceId);

	List<CartEntity> findByUserId(@Valid Long userId);

	@Modifying
	@Transactional
	@Query("UPDATE CartEntity c SET c.quantity = :qty WHERE c.cartId = :cartId")
	int updateQuantityById(@Param("cartId") Long cartId, @Param("qty") Long quantity);

	int countByUserId(long userId);

	void deleteAllByUserId(Long userId);

}
