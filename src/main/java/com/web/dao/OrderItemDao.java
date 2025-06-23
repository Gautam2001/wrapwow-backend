package com.web.dao;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.web.entity.OrderItemEntity;

public interface OrderItemDao extends JpaRepository<OrderItemEntity, Long> {

	@Query("SELECT SUM(o.quantity) FROM OrderItemEntity o")
	Long sumAllQuantity();

	@Query("SELECT MONTH(o.order.orderTime) as month, SUM(o.quantity) as productsSold "
			+ "FROM OrderItemEntity o WHERE YEAR(o.order.orderTime) = :year GROUP BY MONTH(o.order.orderTime)")
	List<Map<String, Object>> getMonthlyProductsSold(@Param("year") int year);

}
