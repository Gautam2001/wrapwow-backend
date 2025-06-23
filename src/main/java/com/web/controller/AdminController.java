package com.web.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.web.DTO.AddProductDTO;
import com.web.DTO.UpdateCategoryStatusDTO;
import com.web.DTO.UpdateProductDTO;
import com.web.DTO.UpdateStatusDTO;
import com.web.service.AdminService;
import com.web.utility.DataConstants;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/admin")
public class AdminController {

	@Autowired
	private AdminService adminService;

	@GetMapping("/getAllUsers")
	public ResponseEntity<Map<String, Object>> getAllUsers() {
		Map<String, Object> response = new HashMap<>();
		response.put("message", DataConstants.SUCCESS_Message);
		response.put("status", DataConstants.SUCCESS_STATUS);

		response.put("resultString", adminService.getAllUsers());

		return ResponseEntity.ok(response);

	}

	@GetMapping("/getAdmins")
	public ResponseEntity<Map<String, Object>> getAdmins() {
		Map<String, Object> response = new HashMap<>();
		response.put("message", DataConstants.SUCCESS_Message);
		response.put("status", DataConstants.SUCCESS_STATUS);

		response.put("resultString", adminService.getAdmins());

		return ResponseEntity.ok(response);
	}

	@PostMapping("/updateAdminsStatus")
	public ResponseEntity<Map<String, Object>> updateAdminsStatus(@RequestBody @Valid UpdateStatusDTO updateStatusDTO) {
		Map<String, Object> response = new HashMap<>();
		response.put("message", DataConstants.SUCCESS_Message);
		response.put("status", DataConstants.SUCCESS_STATUS);

		response.put("resultString", adminService.updateAdminsStatus(updateStatusDTO));

		return ResponseEntity.ok(response);
	}

	@GetMapping("/getUsers")
	public ResponseEntity<Map<String, Object>> getUsers() {
		Map<String, Object> response = new HashMap<>();
		response.put("message", DataConstants.SUCCESS_Message);
		response.put("status", DataConstants.SUCCESS_STATUS);

		response.put("resultString", adminService.getUsers());

		return ResponseEntity.ok(response);
	}

	@PostMapping("/updateUsersStatus")
	public ResponseEntity<Map<String, Object>> updateUsersStatus(@RequestBody @Valid UpdateStatusDTO updateStatusDTO) {
		Map<String, Object> response = new HashMap<>();
		response.put("message", DataConstants.SUCCESS_Message);
		response.put("status", DataConstants.SUCCESS_STATUS);

		response.put("resultString", adminService.updateUsersStatus(updateStatusDTO));

		return ResponseEntity.ok(response);
	}

	@PostMapping("/addCategory")
	public ResponseEntity<Map<String, Object>> addCategory(@RequestParam String emailId, @RequestParam String category,
			@RequestParam MultipartFile image) {
		Map<String, Object> response = new HashMap<>();

		try {
			response.put("message", DataConstants.SUCCESS_Message);
			response.put("status", DataConstants.SUCCESS_STATUS);
			response.put("resultString", adminService.addCategory(emailId, category, image));
			return ResponseEntity.ok(response);
		} catch (IOException e) {
			response.put("message", DataConstants.FAIL_Message);
			response.put("status", DataConstants.FAIL_STATUS);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}

	@PostMapping("/updateCategory")
	public ResponseEntity<Map<String, Object>> updateCategory(@RequestParam String emailId,
			@RequestParam Long categoryId, @RequestParam String category,
			@RequestParam(value = "Image", required = false) MultipartFile image) {
		Map<String, Object> response = new HashMap<>();

		try {
			response.put("message", DataConstants.SUCCESS_Message);
			response.put("status", DataConstants.SUCCESS_STATUS);
			response.put("resultString", adminService.updateCategory(emailId, categoryId, category, image));
			return ResponseEntity.ok(response);
		} catch (IOException e) {
			response.put("message", DataConstants.FAIL_Message);
			response.put("status", DataConstants.FAIL_STATUS);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}

	@PostMapping("/updateCategoryStatus")
	public ResponseEntity<Map<String, Object>> updateCategoryStatus(
			@RequestBody @Valid UpdateCategoryStatusDTO updateCategoryStatusDTO) {
		Map<String, Object> response = new HashMap<>();
		response.put("message", DataConstants.SUCCESS_Message);
		response.put("status", DataConstants.SUCCESS_STATUS);

		response.put("resultString", adminService.updateCategoryStatus(updateCategoryStatusDTO));

		return ResponseEntity.ok(response);
	}

	@PostMapping("/addProduct")
	public ResponseEntity<Map<String, Object>> addProduct(@RequestBody @Valid AddProductDTO addProductDTO) {
		Map<String, Object> response = new HashMap<>();
		response.put("message", DataConstants.SUCCESS_Message);
		response.put("status", DataConstants.SUCCESS_STATUS);

		response.put("resultString", adminService.addProduct(addProductDTO));

		return ResponseEntity.ok(response);
	}

	@PostMapping("/uploadImage")
	public ResponseEntity<Map<String, Object>> uploadImage(@RequestParam Long productId,
			@RequestParam("Images") List<MultipartFile> images) {
		Map<String, Object> response = new HashMap<>();

		try {
			response.put("message", DataConstants.SUCCESS_Message);
			response.put("status", DataConstants.SUCCESS_STATUS);
			response.put("resultString", adminService.uploadImage(productId, images));
			return ResponseEntity.ok(response);
		} catch (IOException e) {
			response.put("message", DataConstants.FAIL_Message);
			response.put("status", DataConstants.FAIL_STATUS);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}

	@PostMapping("/updateProductsStatus")
	public ResponseEntity<Map<String, Object>> updateProductsStatus(
			@RequestBody @Valid UpdateStatusDTO updateStatusDTO) {
		Map<String, Object> response = new HashMap<>();
		response.put("message", DataConstants.SUCCESS_Message);
		response.put("status", DataConstants.SUCCESS_STATUS);

		response.put("resultString", adminService.updateProductsStatus(updateStatusDTO));

		return ResponseEntity.ok(response);
	}

	@GetMapping("/getProductList")
	public ResponseEntity<Map<String, Object>> getProductList() {
		Map<String, Object> response = new HashMap<>();
		response.put("message", DataConstants.SUCCESS_Message);
		response.put("status", DataConstants.SUCCESS_STATUS);

		response.put("resultString", adminService.getProductList());

		return ResponseEntity.ok(response);
	}

	@GetMapping("/getProductById")
	public ResponseEntity<Map<String, Object>> getProductById(@RequestParam @Valid long productId) {
		Map<String, Object> response = new HashMap<>();
		response.put("message", DataConstants.SUCCESS_Message);
		response.put("status", DataConstants.SUCCESS_STATUS);

		response.put("resultString", adminService.getProductById(productId));

		return ResponseEntity.ok(response);
	}

	@PostMapping("/updateProduct")
	public ResponseEntity<Map<String, Object>> updateProduct(@RequestBody @Valid UpdateProductDTO updateProductDTO) {
		Map<String, Object> response = new HashMap<>();
		response.put("message", DataConstants.SUCCESS_Message);
		response.put("status", DataConstants.SUCCESS_STATUS);

		response.put("resultString", adminService.updateProduct(updateProductDTO));

		return ResponseEntity.ok(response);
	}

	@PostMapping("/updateProductImage")
	public ResponseEntity<Map<String, Object>> updateProductImage(@RequestParam Long productId,
			@RequestParam(required = false) List<Long> imageIds,
			@RequestParam(value = "Images", required = false) List<MultipartFile> images) {
		Map<String, Object> response = new HashMap<>();

		try {
			response.put("message", DataConstants.SUCCESS_Message);
			response.put("status", DataConstants.SUCCESS_STATUS);
			response.put("resultString", adminService.updateProductImage(productId, imageIds, images));
			return ResponseEntity.ok(response);
		} catch (IOException e) {
			response.put("message", DataConstants.FAIL_Message);
			response.put("status", DataConstants.FAIL_STATUS);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}

	@GetMapping("/getAlertData")
	public ResponseEntity<Map<String, Object>> getAlertData() {
		Map<String, Object> response = new HashMap<>();
		response.put("message", DataConstants.SUCCESS_Message);
		response.put("status", DataConstants.SUCCESS_STATUS);

		response.put("resultString", adminService.getAlertData());

		return ResponseEntity.ok(response);
	}

	@GetMapping("/analytics/summary")
	public ResponseEntity<Map<String, Object>> getAnalyticsSummary() {
		Map<String, Object> response = new HashMap<>();
		response.put("message", DataConstants.SUCCESS_Message);
		response.put("status", DataConstants.SUCCESS_STATUS);

		response.put("resultString", adminService.getAnalyticsSummary());

		return ResponseEntity.ok(response);
	}

	@GetMapping("/analytics/graphs")
	public ResponseEntity<Map<String, Object>> getAnalyticsGraphs() {
		Map<String, Object> response = new HashMap<>();
		response.put("message", DataConstants.SUCCESS_Message);
		response.put("status", DataConstants.SUCCESS_STATUS);

		response.put("resultString", adminService.getAnalyticsGraphs());

		return ResponseEntity.ok(response);
	}

}
