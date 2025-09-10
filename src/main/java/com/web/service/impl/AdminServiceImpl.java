package com.web.service.impl;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.web.DTO.AddProductDTO;
import com.web.DTO.GetUsersDTO;
import com.web.DTO.ProductListDTOAdmin;
import com.web.DTO.UpdateCategoryStatusDTO;
import com.web.DTO.UpdateProductDTO;
import com.web.DTO.UpdateStatusDTO;
import com.web.dao.CategoriesDao;
import com.web.dao.MemberDao;
import com.web.dao.OrderDao;
import com.web.dao.OrderItemDao;
import com.web.dao.ProductDao;
import com.web.dao.ProductImageDao;
import com.web.dao.ProductPriceDao;
import com.web.entity.CategoriesEntity;
import com.web.entity.CategoriesEntity.CategoryStatus;
import com.web.entity.MemberEntity;
import com.web.entity.MemberEntity.AccountStatus;
import com.web.entity.MemberEntity.Role;
import com.web.entity.ProductEntity;
import com.web.entity.ProductEntity.ProductStatus;
import com.web.entity.ProductImagesEntity;
import com.web.entity.ProductPriceEntity;
import com.web.service.AdminService;
import com.web.utility.CommonUtils;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.extern.java.Log;

@Service
@Log
public class AdminServiceImpl implements AdminService {

	@Autowired
	private Cloudinary cloudinary;

	@Autowired
	private MemberDao memberDao;

	@Autowired
	private CategoriesDao categoriesDao;

	@Autowired
	private ProductDao productDao;

	@Autowired
	private ProductPriceDao priceDao;

	@Autowired
	private ProductImageDao imageDao;

	@Autowired
	private OrderDao orderDao;

	@Autowired
	private OrderItemDao orderItemDao;

	private boolean userNotExist(String email, Map<String, Object> response) {
		CommonUtils.logMethodEntry(this);
		try {
			Optional<MemberEntity> userOptional = memberDao.getUserByEmail(email);

			if (userOptional.isEmpty()) {
				CommonUtils.prepareResponse(response, "Member does not exist.", false);
				return true;
			}

			String role = userOptional.get().getRole().toString();

			if ("USER".equals(role)) {
				CommonUtils.prepareResponse(response, "Login with Admin Credentials to access further.", false);
				return true;
			} else {
				CommonUtils.prepareResponse(response, "Admin already exists, try logging in.", true);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to validate admin. Please try again.");
		}
		return false;
	}

	@Override
	public Map<String, Object> getAllUsers() {
		CommonUtils.logMethodEntry(this);
		Map<String, Object> response = new HashMap<>();
		List<MemberEntity> users = new ArrayList<>();
		try {
			users = memberDao.findAll();
			if (users.isEmpty()) {
				return CommonUtils.prepareResponse(response, "No Members Present", false);
			} else {
				return CommonUtils.prepareResponse(response, users.toString(), true);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to get all Members. Please try again.");
		}
	}

	@Override
	public Map<String, Object> getAdmins() {
		CommonUtils.logMethodEntry(this);
		Map<String, Object> response = new HashMap<>();
		Role role = Role.ADMIN;
		try {
			List<GetUsersDTO> admins = memberDao.findAllByRole(role);
			if (admins.isEmpty()) {
				return CommonUtils.prepareResponse(response, "No Admins Present", false);
			} else {
				response.put("Admins", admins);
				return CommonUtils.prepareResponse(response, "Admins fetched successfully.", true);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to get the Admins at the moment. Please try again.");
		}
	}

	@Override
	public Map<String, Object> updateAdminsStatus(@Valid UpdateStatusDTO updateStatusDTO) {
		CommonUtils.logMethodEntry(this);
		String email = CommonUtils.normalizeUsername(updateStatusDTO.getEmail());
		CommonUtils.ValidateUserWithToken(email);
		Map<String, Object> response = new HashMap<>();

		if (userNotExist(email, response)) {
			return response;
		}

		try {
			List<Long> ids = updateStatusDTO.getIds();
			List<MemberEntity> admins = memberDao.findAllByUserIdInAndRole(ids, Role.ADMIN);
			if (admins.size() != ids.size()) {
				throw new RuntimeException("Data mismatch. refresh the page and try again.");
			}
			int updateResult = memberDao.updateStatusForUserIds(ids, AccountStatus.ACTIVE, AccountStatus.INACTIVE);
			if (updateResult > 0) {
				return CommonUtils.prepareResponse(response, "Status updated Successfully", true);
			} else {
				return CommonUtils.prepareResponse(response, "Status cannot be updated, please try again", false);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to update admin's account status. Please try again.");
		}
	}

	@Override
	public Map<String, Object> getUsers() {
		CommonUtils.logMethodEntry(this);
		Map<String, Object> response = new HashMap<>();
		Role role = Role.USER;
		try {
			List<GetUsersDTO> users = memberDao.findAllByRole(role);
			if (users.isEmpty()) {
				return CommonUtils.prepareResponse(response, "No Users Present", false);
			} else {
				response.put("Users", users);
				return CommonUtils.prepareResponse(response, "Users fetched successfully.", true);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to get the Users at the moment. Please try again.");
		}
	}

	@Override
	public Map<String, Object> updateUsersStatus(@Valid UpdateStatusDTO updateStatusDTO) {
		CommonUtils.logMethodEntry(this);
		String email = CommonUtils.normalizeUsername(updateStatusDTO.getEmail());
		CommonUtils.ValidateUserWithToken(email);
		Map<String, Object> response = new HashMap<>();

		if (userNotExist(email, response)) {
			return response;
		}

		try {
			List<Long> ids = updateStatusDTO.getIds();
			List<MemberEntity> users = memberDao.findAllByUserIdInAndRole(ids, Role.USER);
			if (users.size() != ids.size()) {
				throw new RuntimeException("Data mismatch. refresh the page and try again.");
			}
			int updateResult = memberDao.updateStatusForUserIds(ids, AccountStatus.ACTIVE, AccountStatus.INACTIVE);
			if (updateResult > 0) {
				return CommonUtils.prepareResponse(response, "Status updated Successfully", true);
			} else {
				return CommonUtils.prepareResponse(response, "Status cannot be updated, please try again", false);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to update user's account status. Please try again.");
		}
	}

	@Override
	public Map<String, Object> addCategory(String emailId, String category, MultipartFile image) throws IOException {
		CommonUtils.logMethodEntry(this);
		String email = CommonUtils.normalizeUsername(emailId);
		CommonUtils.ValidateUserWithToken(email);
		Map<String, Object> response = new HashMap<>();
		String uploadedPublicId = null;

		if (userNotExist(email, response)) {
			return response;
		}
		try {
			if (image.getSize() > 1 * 1024 * 1024) {
				throw new RuntimeException("Image must be less than 1 MB.");
			}

			// Upload to Cloudinary
			@SuppressWarnings("unchecked")
			Map<String, Object> uploadResult = (Map<String, Object>) cloudinary.uploader().upload(image.getBytes(),
					Map.of("folder", "wrap-and-wow/Categories/", "public_id", UUID.randomUUID().toString()));

			String imageUrl = (String) uploadResult.get("secure_url");
			String publicId = (String) uploadResult.get("public_id");
			uploadedPublicId = publicId;

			CategoriesEntity categoriesEntity = new CategoriesEntity(category, imageUrl);

			CategoriesEntity savedCategory = categoriesDao.save(categoriesEntity);
			if (savedCategory == null || savedCategory.getCategoryId() == null) {
				return CommonUtils.prepareResponse(response, "Failed to save the product. Please try again.", false);
			}
			response.put("Category", savedCategory);

			return CommonUtils.prepareResponse(response, "Category added Successfully", true);

		} catch (Exception e) {
			if (uploadedPublicId != null) {
				try {
					cloudinary.uploader().destroy(uploadedPublicId, Map.of());
				} catch (Exception destroyEx) {
					log.warning("Failed to delete Cloudinary image: " + uploadedPublicId);
				}
			}
			e.printStackTrace();
			throw new RuntimeException("Failed to add Category. PLease try again.");
		}
	}

	@Override
	@Transactional
	public Map<String, Object> updateCategory(String emailId, Long categoryId, String categoryName, MultipartFile image)
			throws IOException {
		CommonUtils.logMethodEntry(this);
		String email = CommonUtils.normalizeUsername(emailId);
		CommonUtils.ValidateUserWithToken(email);
		Map<String, Object> response = new HashMap<>();
		String newPublicId = null;

		if (userNotExist(email, response)) {
			return response;
		}

		Optional<CategoriesEntity> optionalCategory = categoriesDao.findById(categoryId);
		if (optionalCategory.isEmpty()) {
			return CommonUtils.prepareResponse(response, "Category not found. Please try again.", false);
		}

		CategoriesEntity existingCategory = optionalCategory.get();
		String oldPublicId = extractPublicId(existingCategory.getPath()); // We extract public_id from Cloudinary URL

		try {
			// If no new image, only update name
			if (image == null) {
				int updateResult = categoriesDao.updateCategoryById(categoryName, categoryId);
				if (updateResult > 0) {
					return CommonUtils.prepareResponse(response, "Category updated successfully.", true);
				} else {
					return CommonUtils.prepareResponse(response, "Failed to update category name.", false);
				}
			}

			// Validate image size
			if (image.getSize() > 1 * 1024 * 1024) {
				throw new RuntimeException("Image must be less than 1 MB.");
			}

			// Upload new image to Cloudinary
			newPublicId = UUID.randomUUID().toString();
			@SuppressWarnings("unchecked")
			Map<String, Object> uploadResult = (Map<String, Object>) cloudinary.uploader().upload(image.getBytes(),
					Map.of("folder", "wrap-and-wow/Categories", "public_id", newPublicId));

			String newImageUrl = (String) uploadResult.get("secure_url");

			// Attempt to update DB
			int updateResult = categoriesDao.updateCategoryImageById(categoryId, categoryName, newImageUrl);
			if (updateResult > 0) {
				// Delete old image from Cloudinary if DB update was successful
				if (oldPublicId != null) {
					try {
						cloudinary.uploader().destroy("wrap-and-wow/Categories/" + oldPublicId, Map.of());
					} catch (Exception destroyEx) {
						log.warning("Failed to delete old Cloudinary image: " + oldPublicId);
					}
				}

				return CommonUtils.prepareResponse(response, "Category updated successfully.", true);
			} else {
				// If DB update failed, delete new image to prevent orphaned image
				cloudinary.uploader().destroy("wrap-and-wow/Categories/" + newPublicId, Map.of());
				return CommonUtils.prepareResponse(response, "Failed to update category.", false);
			}

		} catch (Exception e) {
			// Clean up new image on failure
			if (newPublicId != null) {
				try {
					cloudinary.uploader().destroy("wrap-and-wow/Categories/" + newPublicId, Map.of());
				} catch (Exception destroyEx) {
					log.warning("Failed to delete new Cloudinary image on error: " + newPublicId);
				}
			}

			e.printStackTrace();
			throw new RuntimeException("Failed to update category. Please try again.");
		}
	}

	private String extractPublicId(String imageUrl) {
		CommonUtils.logMethodEntry(this);
		if (imageUrl == null || !imageUrl.contains("/wrap-and-wow/Categories/"))
			return null;

		try {
			String[] parts = imageUrl.split("/wrap-and-wow/Categories/");
			if (parts.length < 2)
				return null;
			String filename = parts[1].split("\\.")[0];
			return filename;
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public Map<String, Object> updateCategoryStatus(@Valid UpdateCategoryStatusDTO updateCategoryStatusDTO) {
		CommonUtils.logMethodEntry(this);
		String email = CommonUtils.normalizeUsername(updateCategoryStatusDTO.getEmail());
		CommonUtils.ValidateUserWithToken(email);
		Map<String, Object> response = new HashMap<>();

		if (userNotExist(email, response)) {
			return response;
		}

		try {
			Long id = updateCategoryStatusDTO.getId();
			Optional<CategoriesEntity> category = categoriesDao.findById(id);
			if (category.isEmpty()) {
				throw new RuntimeException("Data mismatch. refresh the page and try again.");
			}
			int updateResult = categoriesDao.updateStatusForUserId(id, CategoryStatus.ACTIVE, CategoryStatus.INACTIVE);
			if (updateResult > 0) {
				return CommonUtils.prepareResponse(response, "Status updated Successfully", true);
			} else {
				return CommonUtils.prepareResponse(response, "Status cannot be updated, please try again", false);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to update product's status. Please try again.");
		}
	}

	@Override
	@Transactional
	public Map<String, Object> addProduct(@Valid AddProductDTO addProductDTO) {
		CommonUtils.logMethodEntry(this);
		String email = CommonUtils.normalizeUsername(addProductDTO.getEmail());
		CommonUtils.ValidateUserWithToken(email);
		Map<String, Object> response = new HashMap<>();

		if (userNotExist(email, response)) {
			return response;
		}

		List<Double> prices = addProductDTO.getPrice();
		List<Float> discounts = addProductDTO.getDiscount();
		List<Double> finalPrices = addProductDTO.getFinalPrice();

		if (!(prices.size() == discounts.size() && discounts.size() == finalPrices.size())) {
			return CommonUtils.prepareResponse(response,
					"Fields mismatch in price, discount and final price, please try again.", false);
		}

		if (prices.size() > 5 && discounts.size() > 5 && finalPrices.size() > 5) {
			return CommonUtils.prepareResponse(response,
					"A maximum of 5 different prices can be specified, please try again.", false);
		}

		for (int i = 0; i < prices.size(); i++) {
			double price = prices.get(i);
			float discount = discounts.get(i);
			double expectedFinalPrice = price - (price * (discount / 100.0f));
			double roundedFinalPrice = new BigDecimal(expectedFinalPrice).setScale(2, RoundingMode.HALF_UP)
					.doubleValue();

			if (discount < 0 || discount > 100) {
				return CommonUtils.prepareResponse(response,
						"Invalid Discount value at index " + i + ", product cannot be added.", false);
			}

			if (roundedFinalPrice != finalPrices.get(i)) {
				return CommonUtils.prepareResponse(response,
						"Final price mismatch at index " + i + ", product cannot be added.", false);
			}
		}

		try {
			ProductEntity product = new ProductEntity(addProductDTO.getName(), addProductDTO.getDescription(),
					addProductDTO.getQuantity(), addProductDTO.getCategory(),
					addProductDTO.getProductStatus().equalsIgnoreCase("ACTIVE") ? ProductStatus.ACTIVE
							: ProductStatus.INACTIVE,
					email);

			ProductEntity savedProduct = productDao.save(product);
			if (savedProduct == null || savedProduct.getProductId() == null) {
				return CommonUtils.prepareResponse(response, "Failed to save the product. Please try again.", false);
			}

			List<ProductPriceEntity> priceEntities = new ArrayList<>();
			for (int i = 0; i < prices.size(); i++) {
				ProductPriceEntity priceEntity = new ProductPriceEntity();
				priceEntity.setPrice(prices.get(i));
				priceEntity.setDiscount(discounts.get(i));
				priceEntity.setFinalPrice(finalPrices.get(i));
				priceEntity.setProduct(savedProduct);
				priceEntities.add(priceEntity);
			}

			priceDao.saveAll(priceEntities);

			response.put("productId", savedProduct.getProductId());
			return CommonUtils.prepareResponse(response, "Product added successfully.", true);

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to add product. Please try again.");
		}
	}

	// only works for the 1st time when the product is also created and no existing
	// images are there
	@Override
	@Transactional
	public Map<String, Object> uploadImage(Long productId, List<MultipartFile> images) throws IOException {
		CommonUtils.logMethodEntry(this);
		Map<String, Object> response = new HashMap<>();

		if (productId == null || productId == 0) {
			throw new RuntimeException("ProductId not specified.");
		}

		Optional<ProductEntity> optionalProduct = productDao.findById(productId);
		ProductEntity product = optionalProduct.orElseThrow(() -> new RuntimeException("Product not found."));

		Optional<ProductImagesEntity> optionalImages = imageDao.findByProductId(productId);
		if (optionalImages.isPresent()) {
			deleteProductCascade(productId); // delete child data before product
			throw new RuntimeException("Images for the product already exist.");
		}

		if (images == null || images.isEmpty()) {
			deleteProductCascade(productId);
			throw new RuntimeException("No images received.");
		}

		if (images.size() > 5) {
			deleteProductCascade(productId);
			throw new RuntimeException("A maximum of 5 images can be uploaded.");
		}

		List<String> uploadedPublicIds = new ArrayList<>();

		try {
			for (MultipartFile image : images) {
				if (image.getSize() > 1 * 1024 * 1024) {
					throw new RuntimeException("Each image must be less than 1 MB.");
				}

				// Upload to Cloudinary
				@SuppressWarnings("unchecked")
				Map<String, Object> uploadResult = (Map<String, Object>) cloudinary.uploader().upload(image.getBytes(),
						Map.of("folder", "wrap-and-wow/products/" + productId, "public_id",
								UUID.randomUUID().toString()));

				String imageUrl = (String) uploadResult.get("secure_url");
				String publicId = (String) uploadResult.get("public_id");
				uploadedPublicIds.add(publicId);

				// Save image record
				ProductImagesEntity imageEntity = new ProductImagesEntity(imageUrl, product);
				imageDao.save(imageEntity);
			}

			return CommonUtils.prepareResponse(response, "Images uploaded successfully", true);

		} catch (Exception e) {
			for (String publicId : uploadedPublicIds) {
				try {
					cloudinary.uploader().destroy(publicId, Map.of());
				} catch (Exception destroyEx) {
					log.warning("Failed to delete Cloudinary image: " + publicId);
				}
			}

			deleteProductCascade(productId);

			log.severe("Image upload failed: " + e.getMessage());
			throw new RuntimeException("Failed to upload images. Please try again.");
		}
	}

	private void deleteProductCascade(Long productId) {
		CommonUtils.logMethodEntry(this);
		try {
			imageDao.deleteByProductId(productId);
			priceDao.deleteByProductId(productId);
			productDao.deleteById(productId);
		} catch (Exception e) {
			log.severe("Cascade delete failed: " + e.getMessage());
		}
	}

	@Override
	public Map<String, Object> updateProductsStatus(@Valid UpdateStatusDTO updateStatusDTO) {
		CommonUtils.logMethodEntry(this);
		String email = CommonUtils.normalizeUsername(updateStatusDTO.getEmail());
		CommonUtils.ValidateUserWithToken(email);
		Map<String, Object> response = new HashMap<>();

		if (userNotExist(email, response)) {
			return response;
		}

		try {
			List<Long> ids = updateStatusDTO.getIds();
			List<ProductEntity> products = productDao.findAllByProductIdIn(ids);
			if (products.size() != ids.size()) {
				throw new RuntimeException("Data mismatch. refresh the page and try again.");
			}
			int updateResult = productDao.updateStatusForUserIds(ids, ProductStatus.ACTIVE, ProductStatus.INACTIVE);
			if (updateResult > 0) {
				return CommonUtils.prepareResponse(response, "Status updated Successfully", true);
			} else {
				return CommonUtils.prepareResponse(response, "Status cannot be updated, please try again", false);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to update product's status. Please try again.");
		}
	}

	@Override
	public Map<String, Object> getProductList() {
		CommonUtils.logMethodEntry(this);
		Map<String, Object> response = new HashMap<>();
		try {
			List<ProductListDTOAdmin> products = productDao.getAdminProductListDTO();
			if (products.isEmpty()) {
				return CommonUtils.prepareResponse(response, "No products found.", false);
			} else {
				response.put("Products", products);
				return CommonUtils.prepareResponse(response, "Products fetched successfully.", true);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to get the Products at the moment. Please try again.");
		}
	}

	@Override
	public Map<String, Object> getProductById(@Valid long productId) {
		CommonUtils.logMethodEntry(this);
		Map<String, Object> response = new HashMap<>();
		try {
			Optional<ProductEntity> productOpt = productDao.findById(productId);
			if (productOpt.isEmpty()) {
				return CommonUtils.prepareResponse(response, "No product for the product id found.", false);
			} else {
				ProductEntity product = productOpt.get();

				response.put("Product", product);
				return CommonUtils.prepareResponse(response, "Product fetched successfully.", true);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to get the Products at the moment. Please try again.");
		}
	}

	@Override
	@Transactional
	public Map<String, Object> updateProduct(@Valid UpdateProductDTO updateProductDTO) {
		CommonUtils.logMethodEntry(this);
		String email = CommonUtils.normalizeUsername(updateProductDTO.getEmail());
		CommonUtils.ValidateUserWithToken(email);
		Map<String, Object> response = new HashMap<>();

		if (userNotExist(email, response)) {
			return response;
		}

		List<Double> prices = updateProductDTO.getPrice();
		List<Float> discounts = updateProductDTO.getDiscount();
		List<Double> finalPrices = updateProductDTO.getFinalPrice();

		if (!(prices.size() == discounts.size() && discounts.size() == finalPrices.size())) {
			return CommonUtils.prepareResponse(response,
					"Fields mismatch in price, discount and final price, please try again.", false);
		}

		if (prices.size() > 5 && discounts.size() > 5 && finalPrices.size() > 5) {
			return CommonUtils.prepareResponse(response,
					"A maximum of 5 different prices can be specified, please try again.", false);
		}

		for (int i = 0; i < prices.size(); i++) {
			double price = prices.get(i);
			float discount = discounts.get(i);
			double expectedFinalPrice = price - (price * (discount / 100.0f));
			double roundedFinalPrice = new BigDecimal(expectedFinalPrice).setScale(2, RoundingMode.HALF_UP)
					.doubleValue();

			if (discount < 0 || discount > 100) {
				return CommonUtils.prepareResponse(response,
						"Invalid Discount value at index " + i + ", product cannot be added.", false);
			}

			if (roundedFinalPrice != finalPrices.get(i)) {
				return CommonUtils.prepareResponse(response, "Final price mismatch at index " + i + ". Expected: "
						+ roundedFinalPrice + ", Provided: " + finalPrices.get(i), false);
			}
		}

		try {
			ProductEntity product = new ProductEntity(updateProductDTO.getProductId(), updateProductDTO.getName(),
					updateProductDTO.getDescription(), updateProductDTO.getQuantity(), updateProductDTO.getCategory(),
					updateProductDTO.getProductStatus().equalsIgnoreCase("ACTIVE") ? ProductStatus.ACTIVE
							: ProductStatus.INACTIVE,
					email);

			int updatedProductStatus = productDao.updateProductById(product.getProductId(), product.getName(),
					product.getDescription(), product.getAvailableQty(), product.getCategory(),
					product.getProductStatus(), product.getUpdatedBy(), LocalDateTime.now());
			if (updatedProductStatus == 0) {
				return CommonUtils.prepareResponse(response, "Failed to update the product. Please try again.", false);
			}

			Optional<ProductEntity> updatedProductOpt = productDao.findById(product.getProductId());
			if (updatedProductOpt.isEmpty()) {
				return CommonUtils.prepareResponse(response, "Product not found. Please try again.", false);
			}
			ProductEntity updatedProduct = updatedProductOpt.get();

			int deletedResult = priceDao.deleteByProductId(updatedProduct.getProductId());
			if (deletedResult == 0) {
				return CommonUtils.prepareResponse(response, "Failed to update the product. Please try again.", false);
			}

			List<ProductPriceEntity> priceEntities = new ArrayList<>();
			for (int i = 0; i < prices.size(); i++) {
				ProductPriceEntity priceEntity = new ProductPriceEntity();
				priceEntity.setPrice(prices.get(i));
				priceEntity.setDiscount(discounts.get(i));
				priceEntity.setFinalPrice(finalPrices.get(i));
				priceEntity.setProduct(updatedProduct);
				priceEntities.add(priceEntity);
			}

			priceDao.saveAll(priceEntities);

			response.put("productId", updatedProduct.getProductId());
			return CommonUtils.prepareResponse(response, "Product updated successfully.", true);

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to update product. Please try again.");
		}
	}

	@Override
	@Transactional
	public Map<String, Object> updateProductImage(Long productId, List<Long> imageIds, List<MultipartFile> images)
			throws IOException {
		CommonUtils.logMethodEntry(this);
		Map<String, Object> response = new HashMap<>();

		if (productId == null || productId == 0) {
			throw new RuntimeException("ProductId not specified.");
		}

		Optional<ProductEntity> optionalProduct = productDao.findById(productId);
		ProductEntity product = optionalProduct.orElseThrow(() -> new RuntimeException("Product not found."));

		if (images == null)
			images = new ArrayList<>();
		if (imageIds == null)
			imageIds = new ArrayList<>();

		int existingImagesCount = imageDao.countByProduct_ProductId(productId);
		if (existingImagesCount - imageIds.size() + images.size() > 5) {
			throw new RuntimeException("Total number of images after update cannot exceed 5.");
		}

		List<String> uploadedPublicIds = new ArrayList<>();

		try {
			List<ProductImagesEntity> newImageEntities = new ArrayList<>();

			// Upload new images to Cloudinary
			for (MultipartFile image : images) {
				if (image.getSize() > 1 * 1024 * 1024) {
					throw new RuntimeException("Each image must be less than 1 MB.");
				}

				@SuppressWarnings("unchecked")
				Map<String, Object> uploadResult = (Map<String, Object>) cloudinary.uploader().upload(image.getBytes(),
						Map.of("folder", "wrap-and-wow/products/" + productId, "public_id",
								UUID.randomUUID().toString()));

				String imageUrl = (String) uploadResult.get("secure_url");
				String publicId = (String) uploadResult.get("public_id");

				uploadedPublicIds.add(publicId);

				ProductImagesEntity imageEntity = new ProductImagesEntity(imageUrl, product);
				newImageEntities.add(imageEntity);
			}

			imageDao.saveAll(newImageEntities);

			// Delete selected old images
			for (Long imageId : imageIds) {
				Optional<ProductImagesEntity> optionalImage = imageDao.findById(imageId);
				if (optionalImage.isEmpty()) {
					return CommonUtils.prepareResponse(response, "Image could not be traced for image Id: " + imageId,
							false);
				}

				ProductImagesEntity oldImage = optionalImage.get();
				imageDao.delete(oldImage);

				// Extract public_id from URL
				String publicId = extractPublicId(oldImage.getPath());

				String fullCloudinaryId = "wrap-and-wow/products/" + productId + "/" + publicId;

				try {
					cloudinary.uploader().destroy(fullCloudinaryId, Map.of());
				} catch (Exception ex) {
					log.warning("Failed to delete Cloudinary image: " + fullCloudinaryId);
				}
			}

			return CommonUtils.prepareResponse(response, "Images updated successfully", true);

		} catch (Exception e) {
			// Rollback Cloudinary uploads
			for (String publicId : uploadedPublicIds) {
				try {
					cloudinary.uploader().destroy(publicId, Map.of());
				} catch (Exception ex) {
					log.warning("Failed to rollback Cloudinary image: " + publicId);
				}
			}

			log.severe("Failed to update product images: " + e.getMessage());
			e.printStackTrace();
			throw new RuntimeException("Failed to update product images. Please try again.");
		}
	}

	@Override
	@Transactional
	public Map<String, Object> getAlertData() {
		CommonUtils.logMethodEntry(this);
		Map<String, Object> response = new HashMap<>();
		try {
			long outOfStockQty = 0;
			long lowStockQty = 0;
			long inactiveUsers = 0;
			long inactiveAdmins = 0;
			List<Long> availableQty = productDao.getAvailableQty();
			if (!availableQty.isEmpty()) {
				for (long qty : availableQty) {
					if (qty == 0) {
						outOfStockQty++;
					} else {
						lowStockQty++;
					}
				}
			}

			response.put("outOfStockQty", outOfStockQty);
			response.put("lowStockQty", lowStockQty);

			List<Role> inactiveMembers = memberDao.getInactiveMembers();
			if (!inactiveMembers.isEmpty()) {
				for (Role role : inactiveMembers) {
					if (role.equals(Role.USER)) {
						inactiveUsers++;
					} else {
						inactiveAdmins++;
					}
				}
			}

			response.put("inactiveUsers", inactiveUsers);
			response.put("inactiveAdmins", inactiveAdmins);

			return CommonUtils.prepareResponse(response, "Alert Data fetched successfully.", true);

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to get the Alert Data at the moment. Please try again.");
		}
	}

	@Override
	@Transactional
	public Map<String, Object> getAnalyticsSummary() {
		CommonUtils.logMethodEntry(this);
		Map<String, Object> response = new HashMap<>();
		try {
			Double totalRevenue = Optional.ofNullable(orderDao.sumTotalAmount()).orElse(0.0);
			Long totalProductsSold = Optional.ofNullable(orderItemDao.sumAllQuantity()).orElse(0L);
			Long totalOrders = Optional.ofNullable(orderDao.count()).orElse(0L);

			if (totalOrders == 0) {
				return CommonUtils.prepareResponse(response, "No orders available for analytics.", false);
			}

			Double avgOrderValue = BigDecimal.valueOf(totalRevenue / totalOrders).setScale(2, RoundingMode.HALF_UP)
					.doubleValue();

			response.put("totalRevenue", totalRevenue);
			response.put("totalProductsSold", totalProductsSold);
			response.put("averageOrderValue", avgOrderValue);

			return CommonUtils.prepareResponse(response, "Analytics Summary fetched successfully.", true);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to fetch the Analytics Summary at the moment. Please try again.");
		}
	}

	@Override
	@Transactional
	public Map<String, Object> getAnalyticsGraphs() {
		CommonUtils.logMethodEntry(this);
		Map<String, Object> response = new HashMap<>();
		int year = Year.now().getValue();
		try {
			List<Map<String, Object>> monthlyRevenue = orderDao.getMonthlyRevenue(year);
			List<Map<String, Object>> monthlyProductsSold = orderItemDao.getMonthlyProductsSold(year);
			List<Map<String, Object>> monthlyUserOnboarded = memberDao.getMonthlyUserOnboarded(year);
			List<Map<String, Object>> productCounts = productDao.getProductsPerCategory();
			List<String> allCategories = categoriesDao.getAllCategoryNames();

			Map<String, Integer> countMap = new HashMap<>();
			for (Map<String, Object> entry : productCounts) {
				String category = (String) entry.get("category");
				Integer count = ((Number) entry.get("count")).intValue();
				countMap.put(category, count);
			}

			List<Map<String, Object>> productsPerCategory = new ArrayList<>();
			for (String category : allCategories) {
				Map<String, Object> categoryMap = new HashMap<>();
				categoryMap.put("category", category);
				categoryMap.put("count", countMap.getOrDefault(category, 0));
				productsPerCategory.add(categoryMap);
			}

			response.put("monthlyRevenue", monthlyRevenue);
			response.put("monthlyProductsSold", monthlyProductsSold);
			response.put("monthlyUserOnboarded", monthlyUserOnboarded);
			response.put("productsPerCategory", productsPerCategory);

			return CommonUtils.prepareResponse(response, "Analytics Graph Data fetched successfully.", true);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to fetch Analytics Graphs. Please try again.");
		}
	}

}
