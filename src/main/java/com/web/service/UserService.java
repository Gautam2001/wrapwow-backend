package com.web.service;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.web.DTO.AddToCartDTO;
import com.web.DTO.PlaceOrderDTO;
import com.web.DTO.UpdateCartQtyDTO;

import jakarta.validation.Valid;

@Component
public interface UserService {

	Map<String, Object> getProductList();

	Map<String, Object> getBestSellingProductList();

	Map<String, Object> getMostExpensiveProductList();

	Map<String, Object> getProductById(@Valid long productId);

	Map<String, Object> addUpdateToCart(@Valid AddToCartDTO addToCartDTO);

	Map<String, Object> getCart(@Valid String userId);

	Map<String, Object> getCartQty(@Valid String email);

	Map<String, Object> updateCartQty(@Valid UpdateCartQtyDTO updateCartQtyDTO);

	Map<String, Object> placeOrder(@Valid PlaceOrderDTO checkoutDTO);

	Map<String, Object> getOrders(@Valid String email);

	// AI Bot related Service Start
//	Map<String, Object> sendMessage(@Valid SendMessageDTO sendMessageDTO, String token);
//
//	Map<String, Object> getChatHistory(@Valid ChatHistoryDTO chatHistoryDTO);

}
