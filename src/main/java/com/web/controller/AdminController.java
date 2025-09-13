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
import com.web.utility.CommonUtils;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/admin")
public class AdminController {

	@Autowired
	private AdminService adminService;

	@GetMapping("/getAllUsers")
	public ResponseEntity<Map<String, Object>> getAllUsers() {
		CommonUtils.logMethodEntry(this);
		Map<String, Object> response = adminService.getAllUsers();

		return ResponseEntity.ok(response);

	}

	@GetMapping("/getAdmins")
	public ResponseEntity<Map<String, Object>> getAdmins() {
		CommonUtils.logMethodEntry(this);
		Map<String, Object> response = adminService.getAdmins();

		return ResponseEntity.ok(response);
	}

	@PostMapping("/updateAdminsStatus")
	public ResponseEntity<Map<String, Object>> updateAdminsStatus(@RequestBody @Valid UpdateStatusDTO updateStatusDTO) {
		CommonUtils.logMethodEntry(this);
		Map<String, Object> response = adminService.updateAdminsStatus(updateStatusDTO);

		return ResponseEntity.ok(response);
	}

	@GetMapping("/getUsers")
	public ResponseEntity<Map<String, Object>> getUsers() {
		CommonUtils.logMethodEntry(this);
		Map<String, Object> response = adminService.getUsers();

		return ResponseEntity.ok(response);
	}

	@PostMapping("/updateUsersStatus")
	public ResponseEntity<Map<String, Object>> updateUsersStatus(@RequestBody @Valid UpdateStatusDTO updateStatusDTO) {
		CommonUtils.logMethodEntry(this);
		Map<String, Object> response = adminService.updateUsersStatus(updateStatusDTO);

		return ResponseEntity.ok(response);
	}

	@PostMapping("/addCategory")
	public ResponseEntity<Map<String, Object>> addCategory(@RequestParam String emailId, @RequestParam String category,
			@RequestParam MultipartFile image) {
		CommonUtils.logMethodEntry(this);
		Map<String, Object> response = new HashMap<>();
		try {
			response = adminService.addCategory(emailId, category, image);
			return ResponseEntity.ok(response);
		} catch (IOException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(CommonUtils.prepareResponse(response, "API Call Failed", false));
		}
	}

	@PostMapping("/updateCategory")
	public ResponseEntity<Map<String, Object>> updateCategory(@RequestParam String emailId,
			@RequestParam Long categoryId, @RequestParam String category,
			@RequestParam(value = "Image", required = false) MultipartFile image) {
		CommonUtils.logMethodEntry(this);
		Map<String, Object> response = new HashMap<>();

		try {
			response = adminService.updateCategory(emailId, categoryId, category, image);
			return ResponseEntity.ok(response);
		} catch (IOException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(CommonUtils.prepareResponse(response, "API Call Failed", false));
		}
	}

	@PostMapping("/updateCategoryStatus")
	public ResponseEntity<Map<String, Object>> updateCategoryStatus(
			@RequestBody @Valid UpdateCategoryStatusDTO updateCategoryStatusDTO) {
		CommonUtils.logMethodEntry(this);
		Map<String, Object> response = adminService.updateCategoryStatus(updateCategoryStatusDTO);

		return ResponseEntity.ok(response);
	}

	@PostMapping("/addProduct")
	public ResponseEntity<Map<String, Object>> addProduct(@RequestBody @Valid AddProductDTO addProductDTO) {
		CommonUtils.logMethodEntry(this);
		Map<String, Object> response = adminService.addProduct(addProductDTO);

		return ResponseEntity.ok(response);
	}

	@PostMapping("/uploadImage")
	public ResponseEntity<Map<String, Object>> uploadImage(@RequestParam Long productId,
			@RequestParam("Images") List<MultipartFile> images) {
		CommonUtils.logMethodEntry(this);
		Map<String, Object> response = new HashMap<>();

		try {
			response = adminService.uploadImage(productId, images);
			return ResponseEntity.ok(response);
		} catch (IOException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(CommonUtils.prepareResponse(response, "API Call Failed", false));
		}
	}

	@PostMapping("/updateProductsStatus")
	public ResponseEntity<Map<String, Object>> updateProductsStatus(
			@RequestBody @Valid UpdateStatusDTO updateStatusDTO) {
		CommonUtils.logMethodEntry(this);
		Map<String, Object> response = adminService.updateProductsStatus(updateStatusDTO);

		return ResponseEntity.ok(response);
	}

	@GetMapping("/getProductList")
	public ResponseEntity<Map<String, Object>> getProductList() {
		CommonUtils.logMethodEntry(this);
		Map<String, Object> response = adminService.getProductList();

		return ResponseEntity.ok(response);
	}

	@GetMapping("/getProductById")
	public ResponseEntity<Map<String, Object>> getProductById(@RequestParam @Valid long productId) {
		CommonUtils.logMethodEntry(this);
		Map<String, Object> response = adminService.getProductById(productId);

		return ResponseEntity.ok(response);
	}

	@PostMapping("/updateProduct")
	public ResponseEntity<Map<String, Object>> updateProduct(@RequestBody @Valid UpdateProductDTO updateProductDTO) {
		CommonUtils.logMethodEntry(this);
		Map<String, Object> response = adminService.updateProduct(updateProductDTO);

		return ResponseEntity.ok(response);
	}

	@PostMapping("/updateProductImage")
	public ResponseEntity<Map<String, Object>> updateProductImage(@RequestParam Long productId,
			@RequestParam(required = false) List<Long> imageIds,
			@RequestParam(value = "Images", required = false) List<MultipartFile> images) {
		CommonUtils.logMethodEntry(this);
		Map<String, Object> response = new HashMap<>();

		try {
			response = adminService.updateProductImage(productId, imageIds, images);
			return ResponseEntity.ok(response);
		} catch (IOException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(CommonUtils.prepareResponse(response, "API Call Failed", false));
		}
	}

	@GetMapping("/getAlertData")
	public ResponseEntity<Map<String, Object>> getAlertData() {
		CommonUtils.logMethodEntry(this);
		Map<String, Object> response = adminService.getAlertData();

		return ResponseEntity.ok(response);
	}

	@GetMapping("/analytics/summary")
	public ResponseEntity<Map<String, Object>> getAnalyticsSummary() {
		CommonUtils.logMethodEntry(this);
		Map<String, Object> response = adminService.getAnalyticsSummary();

		return ResponseEntity.ok(response);
	}

	@GetMapping("/analytics/graphs")
	public ResponseEntity<Map<String, Object>> getAnalyticsGraphs() {
		CommonUtils.logMethodEntry(this);
		Map<String, Object> response = adminService.getAnalyticsGraphs();

		return ResponseEntity.ok(response);
	}

}
