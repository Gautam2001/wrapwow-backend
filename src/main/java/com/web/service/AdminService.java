package com.web.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.web.DTO.AddProductDTO;
import com.web.DTO.UpdateCategoryStatusDTO;
import com.web.DTO.UpdateProductDTO;
import com.web.DTO.UpdateStatusDTO;

import jakarta.validation.Valid;

@Component
public interface AdminService {

	Map<String, Object> getAllUsers();

	Map<String, Object> getAdmins();

	Map<String, Object> updateAdminsStatus(@Valid UpdateStatusDTO updateStatusDTO);

	Map<String, Object> getUsers();

	Map<String, Object> updateUsersStatus(@Valid UpdateStatusDTO updateStatusDTO);

	Map<String, Object> addCategory(String emailId, String category, MultipartFile image) throws IOException;

	Map<String, Object> updateCategory(String emailId, Long categoryId, String category, MultipartFile image)
			throws IOException;

	Map<String, Object> updateCategoryStatus(@Valid UpdateCategoryStatusDTO updateCategoryStatusDTO);

	Map<String, Object> addProduct(@Valid AddProductDTO addProductDTO);

	Map<String, Object> uploadImage(Long productId, List<MultipartFile> images) throws IOException;

	Map<String, Object> updateProductsStatus(@Valid UpdateStatusDTO updateStatusDTO);

	Map<String, Object> getProductList();

	Map<String, Object> getProductById(@Valid long productId);

	Map<String, Object> updateProduct(@Valid UpdateProductDTO updateProductDTO);

	Map<String, Object> updateProductImage(Long productId, List<Long> imageIds, List<MultipartFile> images)
			throws IOException;

	Map<String, Object> getAlertData();

	Map<String, Object> getAnalyticsSummary();

	Map<String, Object> getAnalyticsGraphs();

}
