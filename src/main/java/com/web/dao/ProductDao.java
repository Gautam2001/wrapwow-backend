package com.web.dao;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.web.DTO.ProductListDTOAdmin;
import com.web.entity.ProductEntity;
import com.web.entity.ProductEntity.ProductStatus;

import jakarta.transaction.Transactional;

@Repository
public interface ProductDao extends JpaRepository<ProductEntity, Long> {

	List<ProductEntity> findAllByProductIdIn(List<Long> ids);

	@Query("SELECT new com.web.DTO.ProductListDTOAdmin(p.productId, p.name, p.category, p.availableQty, p.totalOrderedQty, p.productStatus) FROM ProductEntity p")
	List<ProductListDTOAdmin> getAdminProductListDTO();

	@Query("SELECT p FROM ProductEntity p where p.productStatus = 'ACTIVE' AND p.availableQty > 0 ORDER BY p.totalOrderedQty DESC")
	List<ProductEntity> getTopSellingProducts(Pageable pageable);

	@Query(value = "SELECT p.* FROM products p JOIN product_price pp ON p.product_id = pp.product_id WHERE p.product_status = 'ACTIVE' AND p.available_qty > 0 GROUP BY p.product_id ORDER BY MAX(pp.final_price) DESC", nativeQuery = true)
	List<ProductEntity> getMostExpensiveProducts(Pageable pageable);

	@Modifying
	@Transactional
	@Query("UPDATE ProductEntity m SET m.productStatus = CASE WHEN m.productStatus = :active THEN :inactive ELSE :active END WHERE m.productId IN :ids")
	int updateStatusForUserIds(@Param("ids") List<Long> ids, @Param("active") ProductStatus active,
			@Param("inactive") ProductStatus inactive);

	@Transactional
	@Modifying
	@Query("UPDATE ProductEntity p SET p.name = :name, p.description = :description, p.availableQty = :availableQty, p.category = :category, p.productStatus = :productStatus, p.updatedBy = :updatedBy, p.updatedAt = :now WHERE p.productId = :productId")
	int updateProductById(@Param("productId") Long productId, @Param("name") String name,
			@Param("description") String description, @Param("availableQty") Long availableQty,
			@Param("category") String category, @Param("productStatus") ProductStatus productStatus,
			@Param("updatedBy") String updatedBy, @Param("now") LocalDateTime now);

	@Query("SELECT p.availableQty FROM ProductEntity p where p.availableQty < 6")
	List<Long> getAvailableQty();

	@Query("SELECT p.category as category, COUNT(p) as count " + "FROM ProductEntity p GROUP BY p.category")
	List<Map<String, Object>> getProductsPerCategory();

}
