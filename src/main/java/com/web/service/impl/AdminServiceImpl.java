package com.web.service.impl;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
import com.web.utility.AppConfigProperties;
import com.web.utility.DataConstants;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.extern.java.Log;

@Service
@Log
public class AdminServiceImpl implements AdminService {

	@Autowired
	private AppConfigProperties appConfig;

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

	@Autowired
	BCryptPasswordEncoder passwordEncoder;

	private Map<String, Object> prepareResponse(Map<String, Object> response, String message, boolean isSuccess) {
		response.put("result", message);
		response.put("resultStatus", isSuccess ? DataConstants.SUCCESS_STATUS : DataConstants.FAIL_STATUS);
		return response;
	}

	private boolean userNotExist(String email, Map<String, Object> response) {
		try {
			Optional<MemberEntity> userOptional = memberDao.getUserByEmail(email);

			if (userOptional.isEmpty()) {
				prepareResponse(response, "Member does not exist.", false);
				return true;
			}

			String role = userOptional.get().getRole().toString();

			if ("USER".equals(role)) {
				prepareResponse(response, "Login with Admin Credentials to access further.", false);
				return true;
			} else {
				prepareResponse(response, "Admin already exists, try logging in.", true);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to validate admin. Please try again.");
		}
		return false;
	}

	@Override
	public Map<String, Object> getAllUsers() {
		Map<String, Object> response = new HashMap<>();
		List<MemberEntity> users = new ArrayList<>();
		try {
			users = memberDao.findAll();
			if (users.isEmpty()) {
				return prepareResponse(response, "No Members Present", false);
			} else {
				return prepareResponse(response, users.toString(), true);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to get all Members. Please try again.");
		}
	}

	@Override
	public Map<String, Object> getAdmins() {
		Map<String, Object> response = new HashMap<>();
		Role role = Role.ADMIN;
		try {
			List<GetUsersDTO> admins = memberDao.findAllByRole(role);
			if (admins.isEmpty()) {
				return prepareResponse(response, "No Admins Present", false);
			} else {
				response.put("Admins", admins);
				return prepareResponse(response, "Admins fetched successfully.", true);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to get the Admins at the moment. Please try again.");
		}
	}

	@Override
	public Map<String, Object> updateAdminsStatus(@Valid UpdateStatusDTO updateStatusDTO) {
		Map<String, Object> response = new HashMap<>();

		if (userNotExist(updateStatusDTO.getEmail(), response)) {
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
				return prepareResponse(response, "Status updated Successfully", true);
			} else {
				return prepareResponse(response, "Status cannot be updated, please try again", false);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to update admin's account status. Please try again.");
		}
	}

	@Override
	public Map<String, Object> getUsers() {
		Map<String, Object> response = new HashMap<>();
		Role role = Role.USER;
		try {
			List<GetUsersDTO> users = memberDao.findAllByRole(role);
			if (users.isEmpty()) {
				return prepareResponse(response, "No Users Present", false);
			} else {
				response.put("Users", users);
				return prepareResponse(response, "Users fetched successfully.", true);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to get the Users at the moment. Please try again.");
		}
	}

	@Override
	public Map<String, Object> updateUsersStatus(@Valid UpdateStatusDTO updateStatusDTO) {
		Map<String, Object> response = new HashMap<>();

		if (userNotExist(updateStatusDTO.getEmail(), response)) {
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
				return prepareResponse(response, "Status updated Successfully", true);
			} else {
				return prepareResponse(response, "Status cannot be updated, please try again", false);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to update user's account status. Please try again.");
		}
	}

	@Override
	public Map<String, Object> addCategory(String emailId, String category, MultipartFile image) throws IOException {
		Map<String, Object> response = new HashMap<>();
		String uploadDIR = appConfig.getImageUploadDIR();
		Path uploadedFilePath = null;

		if (userNotExist(emailId, response)) {
			return response;
		}
		try {
			if (image.getSize() > 1 * 1024 * 1024) {
				throw new RuntimeException("Image must be less than 1 MB.");
			}

			File directory = new File(uploadDIR + "Categories");
			if (!directory.exists()) {
				directory.mkdirs();
			}

			String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmssSSS").format(new Date());
			String originalFileName = image.getOriginalFilename();
			String fileName = timeStamp + "_" + originalFileName;

			Path filePath = Paths.get(uploadDIR + "Categories", fileName);

			Files.write(filePath, image.getBytes());
			uploadedFilePath = filePath;

			CategoriesEntity categoriesEntity = new CategoriesEntity(category, filePath.toString());

			CategoriesEntity savedCategory = categoriesDao.save(categoriesEntity);
			if (savedCategory == null || savedCategory.getCategoryId() == null) {
				return prepareResponse(response, "Failed to save the product. Please try again.", false);
			}
			response.put("Category", savedCategory);

			return prepareResponse(response, "Category added Successfully", true);

		} catch (Exception e) {
			if (uploadedFilePath != null) {
				try {
					Files.deleteIfExists(uploadedFilePath);
				} catch (IOException ioEx) {
					log.severe("Failed to delete orphaned image: " + uploadedFilePath.toString());
					ioEx.printStackTrace();
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
		Map<String, Object> response = new HashMap<>();
		String uploadDIR = appConfig.getImageUploadDIR();
		Path uploadedFilePath = null;

		if (userNotExist(emailId, response)) {
			return response;
		}

		Optional<CategoriesEntity> optionalCategory = categoriesDao.findById(categoryId);
		if (optionalCategory.isEmpty()) {
			return prepareResponse(response, "Category not found. Please try again.", false);
		}
		CategoriesEntity category = optionalCategory.get();

		if (image == null) {
			int updateResult = categoriesDao.updateCategoryById(categoryName, categoryId);
			if (updateResult > 0) {
				return prepareResponse(response, "Category updated Successfully", true);
			} else {
				return prepareResponse(response, "Category Name cannot be updated, please try again", false);
			}
		}

		try {
			Path deletePath = Paths.get(category.getPath());
			try {
				Files.deleteIfExists(deletePath);
			} catch (IOException ioEx) {
				log.severe("Failed to delete image: " + deletePath.toString());
				ioEx.printStackTrace();
				throw new RuntimeException("Failed to delete image: " + deletePath.toString());
			}

			if (image.getSize() > 1 * 1024 * 1024) {
				throw new RuntimeException("Image must be less than 1 MB.");
			}

			File directory = new File(uploadDIR + "Categories");
			if (!directory.exists()) {
				directory.mkdirs();
			}

			String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmssSSS").format(new Date());
			String originalFileName = image.getOriginalFilename();
			String fileName = timeStamp + "_" + originalFileName;

			Path filePath = Paths.get(uploadDIR + "Categories", fileName);

			Files.write(filePath, image.getBytes());
			uploadedFilePath = filePath;

			CategoriesEntity categoriesEntity = new CategoriesEntity(categoryId, categoryName, filePath.toString());

			int updatedCategory = categoriesDao.updateCategoryImageById(categoriesEntity.getCategoryId(),
					categoriesEntity.getCategory(), categoriesEntity.getPath());
			if (updatedCategory > 0) {
				return prepareResponse(response, "Category updated Successfully", true);
			} else {
				return prepareResponse(response, "Category Name cannot be updated, please try again", false);
			}
		} catch (Exception e) {
			if (uploadedFilePath != null) {
				try {
					Files.deleteIfExists(uploadedFilePath);
				} catch (IOException ioEx) {
					log.severe("Failed to delete orphaned image: " + uploadedFilePath.toString());
					ioEx.printStackTrace();
				}
			}
			e.printStackTrace();
			throw new RuntimeException("Failed to add Category. PLease try again.");
		}
	}

	@Override
	public Map<String, Object> updateCategoryStatus(@Valid UpdateCategoryStatusDTO updateCategoryStatusDTO) {
		Map<String, Object> response = new HashMap<>();

		if (userNotExist(updateCategoryStatusDTO.getEmail(), response)) {
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
				return prepareResponse(response, "Status updated Successfully", true);
			} else {
				return prepareResponse(response, "Status cannot be updated, please try again", false);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to update product's status. Please try again.");
		}
	}

	@Override
	@Transactional
	public Map<String, Object> addProduct(@Valid AddProductDTO addProductDTO) {
		Map<String, Object> response = new HashMap<>();

		if (userNotExist(addProductDTO.getEmail(), response)) {
			return response;
		}

		List<Double> prices = addProductDTO.getPrice();
		List<Float> discounts = addProductDTO.getDiscount();
		List<Double> finalPrices = addProductDTO.getFinalPrice();

		if (!(prices.size() == discounts.size() && discounts.size() == finalPrices.size())) {
			return prepareResponse(response, "Fields mismatch in price, discount and final price, please try again.",
					false);
		}

		if (prices.size() > 5 && discounts.size() > 5 && finalPrices.size() > 5) {
			return prepareResponse(response, "A maximum of 5 different prices can be specified, please try again.",
					false);
		}

		for (int i = 0; i < prices.size(); i++) {
			double price = prices.get(i);
			float discount = discounts.get(i);
			double expectedFinalPrice = price - (price * (discount / 100.0f));
			double roundedFinalPrice = new BigDecimal(expectedFinalPrice).setScale(2, RoundingMode.HALF_UP)
					.doubleValue();

			if (discount < 0 || discount > 100) {
				return prepareResponse(response, "Invalid Discount value at index " + i + ", product cannot be added.",
						false);
			}

			if (roundedFinalPrice != finalPrices.get(i)) {
				return prepareResponse(response, "Final price mismatch at index " + i + ", product cannot be added.",
						false);
			}
		}

		try {
			ProductEntity product = new ProductEntity(addProductDTO.getName(), addProductDTO.getDescription(),
					addProductDTO.getQuantity(), addProductDTO.getCategory(),
					addProductDTO.getProductStatus().equalsIgnoreCase("ACTIVE") ? ProductStatus.ACTIVE
							: ProductStatus.INACTIVE,
					addProductDTO.getEmail());

			ProductEntity savedProduct = productDao.save(product);
			if (savedProduct == null || savedProduct.getProductId() == null) {
				return prepareResponse(response, "Failed to save the product. Please try again.", false);
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
			return prepareResponse(response, "Product added successfully.", true);

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
		Map<String, Object> response = new HashMap<>();
		if (productId == null || productId == 0) {
			throw new RuntimeException("ProductId not specified.");
		}

		Optional<ProductEntity> optionalProduct = productDao.findById(productId);
		ProductEntity product = optionalProduct.orElseThrow(() -> new RuntimeException("Product not found."));

		Optional<ProductImagesEntity> optionalImages = imageDao.findByProductId(productId);
		if (optionalImages.isPresent()) {
			productDao.deleteById(productId);
			throw new RuntimeException("Images for the product Already exists.");
		}

		if (images == null || images.isEmpty()) {
			productDao.deleteById(productId);
			throw new RuntimeException("No images received.");
		}

		if (images.size() > 5) {
			productDao.deleteById(productId);
			throw new RuntimeException("A maximum of 5 images can be uploaded.");
		}

		List<Path> uploadedFilePaths = new ArrayList<>();
		String uploadDIR = appConfig.getImageUploadDIR();

		try {
			File directory = new File(uploadDIR + productId);
			if (!directory.exists()) {
				directory.mkdirs();
			}

			for (MultipartFile image : images) {
				if (image.getSize() > 1 * 1024 * 1024) {
					throw new RuntimeException("Each image must be less than 1 MB.");
				}

				String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmssSSS").format(new Date());
				String originalFilename = image.getOriginalFilename();
				String fileName = timeStamp + "_" + originalFilename;

				Path filePath = Paths.get(uploadDIR + productId, fileName);

				Files.write(filePath, image.getBytes());
				uploadedFilePaths.add(filePath);

				ProductImagesEntity imageEntity = new ProductImagesEntity(filePath.toString(), product);
				imageDao.save(imageEntity);
			}

			return prepareResponse(response, "Images uploaded successfully", true);

		} catch (Exception e) {
			for (Path path : uploadedFilePaths) {
				try {
					Files.deleteIfExists(path);
				} catch (IOException ioEx) {
					log.severe("Failed to delete orphaned image: " + path.toString());
					ioEx.printStackTrace();
				}
			}
			try {
				productDao.deleteById(productId);
			} catch (Exception deleteEx) {
				log.severe("Failed to delete product after image upload failure: " + deleteEx.getMessage());
			}
			log.severe("Failed to upload images: " + e.getMessage());
			e.printStackTrace();
			throw new RuntimeException("Failed to upload images. Please try again.");
		}
	}

	@Override
	public Map<String, Object> updateProductsStatus(@Valid UpdateStatusDTO updateStatusDTO) {
		Map<String, Object> response = new HashMap<>();

		if (userNotExist(updateStatusDTO.getEmail(), response)) {
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
				return prepareResponse(response, "Status updated Successfully", true);
			} else {
				return prepareResponse(response, "Status cannot be updated, please try again", false);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to update product's status. Please try again.");
		}
	}

	@Override
	public Map<String, Object> getProductList() {
		Map<String, Object> response = new HashMap<>();
		try {
			List<ProductListDTOAdmin> products = productDao.getAdminProductListDTO();
			if (products.isEmpty()) {
				return prepareResponse(response, "No products found.", false);
			} else {
				response.put("Products", products);
				return prepareResponse(response, "Products fetched successfully.", true);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to get the Products at the moment. Please try again.");
		}
	}

	@Override
	public Map<String, Object> getProductById(@Valid long productId) {
		Map<String, Object> response = new HashMap<>();
		String baseurl = appConfig.getBaseURL();
		try {
			Optional<ProductEntity> productOpt = productDao.findById(productId);
			if (productOpt.isEmpty()) {
				return prepareResponse(response, "No product for the product id found.", false);
			} else {
				ProductEntity product = productOpt.get();
				if (product.getImages() != null) {
					for (ProductImagesEntity image : product.getImages()) {
						String fullPath = image.getPath();
						if (fullPath.startsWith("http")) {
							continue;
						}

						String fileName = fullPath.substring(fullPath.lastIndexOf("\\") + 1);
						String folder = fullPath.split("\\\\")[2];
						String imageUrl = baseurl + "images/" + folder + "/" + fileName;
						image.setPath(imageUrl);
					}
				}
				response.put("Product", product);
				return prepareResponse(response, "Product fetched successfully.", true);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to get the Products at the moment. Please try again.");
		}
	}

	@Override
	@Transactional
	public Map<String, Object> updateProduct(@Valid UpdateProductDTO updateProductDTO) {
		Map<String, Object> response = new HashMap<>();

		if (userNotExist(updateProductDTO.getEmail(), response)) {
			return response;
		}

		List<Double> prices = updateProductDTO.getPrice();
		List<Float> discounts = updateProductDTO.getDiscount();
		List<Double> finalPrices = updateProductDTO.getFinalPrice();

		if (!(prices.size() == discounts.size() && discounts.size() == finalPrices.size())) {
			return prepareResponse(response, "Fields mismatch in price, discount and final price, please try again.",
					false);
		}

		if (prices.size() > 5 && discounts.size() > 5 && finalPrices.size() > 5) {
			return prepareResponse(response, "A maximum of 5 different prices can be specified, please try again.",
					false);
		}

		for (int i = 0; i < prices.size(); i++) {
			double price = prices.get(i);
			float discount = discounts.get(i);
			double expectedFinalPrice = price - (price * (discount / 100.0f));
			double roundedFinalPrice = new BigDecimal(expectedFinalPrice).setScale(2, RoundingMode.HALF_UP)
					.doubleValue();

			if (discount < 0 || discount > 100) {
				return prepareResponse(response, "Invalid Discount value at index " + i + ", product cannot be added.",
						false);
			}

			if (roundedFinalPrice != finalPrices.get(i)) {
				return prepareResponse(response, "Final price mismatch at index " + i + ". Expected: "
						+ roundedFinalPrice + ", Provided: " + finalPrices.get(i), false);
			}
		}

		try {
			ProductEntity product = new ProductEntity(updateProductDTO.getProductId(), updateProductDTO.getName(),
					updateProductDTO.getDescription(), updateProductDTO.getQuantity(), updateProductDTO.getCategory(),
					updateProductDTO.getProductStatus().equalsIgnoreCase("ACTIVE") ? ProductStatus.ACTIVE
							: ProductStatus.INACTIVE,
					updateProductDTO.getEmail());

			int updatedProductStatus = productDao.updateProductById(product.getProductId(), product.getName(),
					product.getDescription(), product.getAvailableQty(), product.getCategory(),
					product.getProductStatus(), product.getUpdatedBy(), LocalDateTime.now());
			if (updatedProductStatus == 0) {
				return prepareResponse(response, "Failed to update the product. Please try again.", false);
			}

			Optional<ProductEntity> updatedProductOpt = productDao.findById(product.getProductId());
			if (updatedProductOpt.isEmpty()) {
				return prepareResponse(response, "Product not found. Please try again.", false);
			}
			ProductEntity updatedProduct = updatedProductOpt.get();

			int deletedResult = priceDao.deleteByProductId(updatedProduct.getProductId());
			if (deletedResult == 0) {
				return prepareResponse(response, "Failed to update the product. Please try again.", false);
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
			return prepareResponse(response, "Product updated successfully.", true);

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to update product. Please try again.");
		}
	}

	@Override
	@Transactional
	public Map<String, Object> updateProductImage(Long productId, List<Long> imageIds, List<MultipartFile> images)
			throws IOException {
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

		List<Path> uploadedFilePaths = new ArrayList<>();
		String uploadDIR = appConfig.getImageUploadDIR();

		try {
			File directory = new File(uploadDIR + productId);
			if (!directory.exists()) {
				directory.mkdirs();
			}

			List<ProductImagesEntity> imagesEntities = new ArrayList<>();
			if (images.size() > 0) {
				for (MultipartFile image : images) {
					if (image.getSize() > 1 * 1024 * 1024) {
						throw new RuntimeException("Each image must be less than 1 MB.");
					}

					String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmssSSS").format(new Date());
					String originalFilename = image.getOriginalFilename();
					String fileName = timeStamp + "_" + originalFilename;

					Path filePath = Paths.get(uploadDIR + productId, fileName);

					Files.write(filePath, image.getBytes());
					uploadedFilePaths.add(filePath);

					ProductImagesEntity imageEntity = new ProductImagesEntity(filePath.toString(), product);
					imagesEntities.add(imageEntity);
				}
			}
			imageDao.saveAll(imagesEntities);
			if (!imageIds.isEmpty()) {
				for (Long imageId : imageIds) {
					Optional<ProductImagesEntity> optionalImage = imageDao.findById(imageId);
					if (optionalImage.isEmpty()) {
						return prepareResponse(response, "Image could not be traced for image Id: " + imageId, true);
					}
					ProductImagesEntity deleteImage = optionalImage.get();
					Path deletePath = Paths.get(deleteImage.getPath());
					try {
						Files.deleteIfExists(deletePath);
					} catch (IOException ioEx) {
						log.severe("Failed to delete image: " + deletePath.toString());
						ioEx.printStackTrace();
						throw new RuntimeException("Failed to delete image: " + deletePath.toString());
					}
					imageDao.delete(deleteImage);
				}
			}
			return prepareResponse(response, "Images updated successfully", true);

		} catch (Exception e) {
			for (Path path : uploadedFilePaths) {
				try {
					Files.deleteIfExists(path);
				} catch (IOException ioEx) {
					log.severe("Failed to delete orphaned image: " + path.toString());
					ioEx.printStackTrace();
				}
			}
			log.severe("Failed to upload images: " + e.getMessage());
			e.printStackTrace();
			throw new RuntimeException("Failed to upload images. Please try again.");
		}
	}

	@Override
	@Transactional
	public Map<String, Object> getAlertData() {
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

			return prepareResponse(response, "Alert Data fetched successfully.", true);

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to get the Alert Data at the moment. Please try again.");
		}
	}

	@Override
	@Transactional
	public Map<String, Object> getAnalyticsSummary() {
		Map<String, Object> response = new HashMap<>();
		try {
			Double totalRevenue = Optional.ofNullable(orderDao.sumTotalAmount()).orElse(0.0);
			Long totalProductsSold = Optional.ofNullable(orderItemDao.sumAllQuantity()).orElse(0L);
			Long totalOrders = Optional.ofNullable(orderDao.count()).orElse(0L);

			if (totalOrders == 0) {
				return prepareResponse(response, "No orders available for analytics.", false);
			}

			Double avgOrderValue = BigDecimal.valueOf(totalRevenue / totalOrders).setScale(2, RoundingMode.HALF_UP)
					.doubleValue();

			response.put("totalRevenue", totalRevenue);
			response.put("totalProductsSold", totalProductsSold);
			response.put("averageOrderValue", avgOrderValue);

			return prepareResponse(response, "Analytics Summary fetched successfully.", true);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to fetch the Analytics Summary at the moment. Please try again.");
		}
	}

	@Override
	@Transactional
	public Map<String, Object> getAnalyticsGraphs() {
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

			return prepareResponse(response, "Analytics Graph Data fetched successfully.", true);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to fetch Analytics Graphs. Please try again.");
		}
	}

}
