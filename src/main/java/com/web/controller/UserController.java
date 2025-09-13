package com.web.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.web.DTO.AddToCartDTO;
import com.web.DTO.PlaceOrderDTO;
import com.web.DTO.UpdateCartQtyDTO;
import com.web.service.UserService;
import com.web.utility.CommonUtils;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserService userService;

	@GetMapping("/getProductList")
	public ResponseEntity<Map<String, Object>> getProductList() {
		CommonUtils.logMethodEntry(this);
		Map<String, Object> response = userService.getProductList();

		return ResponseEntity.ok(response);
	}

	@GetMapping("/getBestSellingProductList")
	public ResponseEntity<Map<String, Object>> getBestSellingProductList() {
		CommonUtils.logMethodEntry(this);
		Map<String, Object> response = userService.getBestSellingProductList();

		return ResponseEntity.ok(response);
	}

	@GetMapping("/getMostExpensiveProductList")
	public ResponseEntity<Map<String, Object>> getMostExpensiveProductList() {
		CommonUtils.logMethodEntry(this);
		Map<String, Object> response = userService.getMostExpensiveProductList();

		return ResponseEntity.ok(response);
	}

	@GetMapping("/getProductById")
	public ResponseEntity<Map<String, Object>> getProductById(@RequestParam @Valid long productId) {
		CommonUtils.logMethodEntry(this);
		Map<String, Object> response = userService.getProductById(productId);

		return ResponseEntity.ok(response);
	}

	@PostMapping("/addUpdateToCart")
	public ResponseEntity<Map<String, Object>> addUpdateToCart(@RequestBody @Valid AddToCartDTO addToCartDTO) {
		CommonUtils.logMethodEntry(this);
		Map<String, Object> response = userService.addUpdateToCart(addToCartDTO);

		return ResponseEntity.ok(response);
	}

	@GetMapping("/getCart")
	public ResponseEntity<Map<String, Object>> getCart(@RequestParam @Valid String email) {
		CommonUtils.logMethodEntry(this);
		Map<String, Object> response = userService.getCart(email);

		return ResponseEntity.ok(response);
	}

	@GetMapping("/getCartQty")
	public ResponseEntity<Map<String, Object>> getCartQty(@RequestParam @Valid String email) {
		CommonUtils.logMethodEntry(this);
		Map<String, Object> response = userService.getCartQty(email);

		return ResponseEntity.ok(response);
	}

	@PostMapping("/updateCartQty")
	public ResponseEntity<Map<String, Object>> updateCartQty(@RequestBody @Valid UpdateCartQtyDTO updateCartQtyDTO) {
		CommonUtils.logMethodEntry(this);
		Map<String, Object> response = userService.updateCartQty(updateCartQtyDTO);

		return ResponseEntity.ok(response);
	}

	@PostMapping("/checkout")
	public ResponseEntity<Map<String, Object>> placeOrder(@RequestBody @Valid PlaceOrderDTO placeOrderDTO) {
		CommonUtils.logMethodEntry(this);
		Map<String, Object> response = userService.placeOrder(placeOrderDTO);

		return ResponseEntity.ok(response);
	}

	@GetMapping("/getOrders")
	public ResponseEntity<Map<String, Object>> getOrders(@RequestParam @Valid String email) {
		CommonUtils.logMethodEntry(this);
		Map<String, Object> response = userService.getOrders(email);

		return ResponseEntity.ok(response);
	}

}
