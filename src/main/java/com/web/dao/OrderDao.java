package com.web.dao;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.web.entity.OrderEntity;

public interface OrderDao extends JpaRepository<OrderEntity, Long> {

	List<OrderEntity> findByUserIdOrderByOrderIdDesc(long userId);

	@Query("SELECT SUM(o.totalAmount) FROM OrderEntity o WHERE o.orderStatus = 'PLACED'")
	Double sumTotalAmount();

	@Query("SELECT MONTH(o.orderTime) as month, SUM(o.totalAmount) as revenue "
			+ "FROM OrderEntity o WHERE YEAR(o.orderTime) = :year GROUP BY MONTH(o.orderTime)")
	List<Map<String, Object>> getMonthlyRevenue(@Param("year") int year);

}
