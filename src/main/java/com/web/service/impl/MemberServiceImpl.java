package com.web.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.web.DTO.ContactUsDTO;
import com.web.DTO.ProductListDTOUser;
import com.web.DTO.UsernameDTO;
import com.web.ServiceExt.CallLoginService;
import com.web.dao.CategoriesDao;
import com.web.dao.ContactRequestDao;
import com.web.dao.MemberDao;
import com.web.dao.ProductDao;
import com.web.entity.CategoriesEntity;
import com.web.entity.ContactRequestsEntity;
import com.web.entity.MemberEntity;
import com.web.entity.MemberEntity.Role;
import com.web.entity.ProductEntity;
import com.web.entity.ProductPriceEntity;
import com.web.service.MemberService;
import com.web.utility.AppException;
import com.web.utility.CommonUtils;
import com.web.utility.EmailService;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@Service
public class MemberServiceImpl implements MemberService {

	@Autowired
	CallLoginService callLoginService;

	@Autowired
	private MemberDao memberDao;

	@Autowired
	private CategoriesDao categoriesDao;

	@Autowired
	private ProductDao productDao;

	@Autowired
	private ContactRequestDao contactDao;

	@Autowired
	private EmailService emailService;

	public static final String BOT_USERNAME = "aibot@wrap-wow.com";

	@Override
	@Transactional
	public Map<String, Object> landingPageData() {
		CommonUtils.logMethodEntry(this);
		Map<String, Object> response = new HashMap<>();
		List<CategoriesEntity> categories = new ArrayList<>();
		try {
			categories = categoriesDao.findAll();
			if (categories.isEmpty()) {
				return CommonUtils.prepareResponse(response, "No Categories Present", false);
			} else {
				response.put("Categories", categories);
			}

			PageRequest pageRequest = PageRequest.of(0, 5);
			List<ProductEntity> BestSellingroducts = productDao.getTopSellingProducts(pageRequest);
			if (BestSellingroducts.isEmpty()) {
				return CommonUtils.prepareResponse(response, "No products found.", false);
			} else {
				List<ProductListDTOUser> dtoList = BestSellingroducts.stream().map(p -> {
					Double minPrice = p.getPrices().stream().map(ProductPriceEntity::getFinalPrice)
							.min(Double::compareTo).orElse(null);

					return new ProductListDTOUser(p.getProductId(), p.getName(), p.getCategory(), p.getImages(),
							minPrice, p.getProductStatus(), p.getAvailableQty());
				}).collect(Collectors.toList());
				response.put("BestSellingProducts", dtoList);
			}

			List<ProductEntity> expensiveProducts = productDao.getMostExpensiveProducts(pageRequest);
			if (expensiveProducts.isEmpty()) {
				return CommonUtils.prepareResponse(response, "No products found.", false);
			} else {
				List<ProductListDTOUser> dtoList = expensiveProducts.stream().map(p -> {
					Double minPrice = p.getPrices().stream().map(ProductPriceEntity::getFinalPrice)
							.min(Double::compareTo).orElse(null);

					return new ProductListDTOUser(p.getProductId(), p.getName(), p.getCategory(), p.getImages(),
							minPrice, p.getProductStatus(), p.getAvailableQty());
				}).collect(Collectors.toList());
				response.put("ExpensiveProducts", dtoList);
			}

			return CommonUtils.prepareResponse(response, "Landing Page Data fetched successfully.", true);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to get data for Landing Page. Please try again.");
		}
	}

	@Override
	public Map<String, Object> userExistsCheck(@Valid UsernameDTO usernameDTO) {
		String username = CommonUtils.normalizeUsername(usernameDTO.getEmail());
		CommonUtils.logMethodEntry(this, "User Exists Check Request for: " + username);
		HashMap<String, Object> response = new HashMap<>();

		if (username.equalsIgnoreCase(BOT_USERNAME)) {
			return CommonUtils.prepareResponse(response, "Username reserved, Try with different username.", false);
		}

		Optional<MemberEntity> userOpt = memberDao.getUserByEmail(username);
		if (userOpt.isPresent()) {
			MemberEntity user = userOpt.get();
			response.put("userId", user.getUserId());
			response.put("joinedAt", user.getCreatedAt());
			return CommonUtils.prepareResponse(response, "User exists in Wrap-Wow.", true);
		} else {
			return CommonUtils.prepareResponse(response, "User does not exists in Wrap-Wow.", false);
		}
	}

	@Override
	public Map<String, Object> joinApp(@Valid UsernameDTO usernameDTO) {
		String username = CommonUtils.normalizeUsername(usernameDTO.getEmail());
		CommonUtils.logMethodEntry(this, "Join Messenger Request for: " + username);
		HashMap<String, Object> response = new HashMap<>();

		if (username.equalsIgnoreCase(BOT_USERNAME)) {
			return CommonUtils.prepareResponse(response, "Username reserved, Try with different username.", false);
		}

		Optional<HashMap<String, String>> nameOpt = callLoginService.checkUserExistsInLoginService(username);

		if (nameOpt.isEmpty()) {
			return CommonUtils.prepareResponse(response, "User does not exist, Please Signup.", false);
		}

		HashMap<String, String> userInfo = nameOpt.get();
		String name = userInfo.get("name");
		String role = userInfo.get("role");

		CommonUtils.ensureUserDoesNotExist(memberDao, username);

		MemberEntity user = new MemberEntity();
		user.setEmail(username);
		user.setName(name);

		try {
			user.setRole(Role.valueOf(role.toUpperCase()));
		} catch (IllegalArgumentException e) {
			throw new AppException("Invalid role: " + role, HttpStatus.BAD_REQUEST);
		}
		MemberEntity savedUser = memberDao.save(user);
		if (savedUser == null || savedUser.getUserId() == null) {
			throw new AppException("Failed to Join. Please try again.", HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return CommonUtils.prepareResponse(response, "User successfully joined Messenger.", true);
	}

	@Override
	public Map<String, Object> contactUs(@Valid ContactUsDTO contactUsDTO) {
		CommonUtils.logMethodEntry(this);
		Map<String, Object> response = new HashMap<>();

		try {
			ContactRequestsEntity contactRequestsEntity = new ContactRequestsEntity(contactUsDTO.getName(),
					contactUsDTO.getEmail(), contactUsDTO.getMessage());

			ContactRequestsEntity contactSaved = contactDao.save(contactRequestsEntity);
			if (contactSaved == null) {
				return CommonUtils.prepareResponse(response, "Failed to save the message. Please try again.", false);
			}
			try {

				emailService.sendFeedbackEmail(contactUsDTO.getName(), contactUsDTO.getEmail(),
						contactUsDTO.getMessage());
			} catch (Exception e) {
				throw new AppException("Email Failed. Feedback stored in Database", HttpStatus.BAD_REQUEST);
			}

			return CommonUtils.prepareResponse(response, " Thank you for the Feedback!", true);

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to save the member. Please try again.");
		}
	}

	@Override
	public Map<String, Object> getAllCategories() {
		CommonUtils.logMethodEntry(this);
		Map<String, Object> response = new HashMap<>();
		List<CategoriesEntity> categories = new ArrayList<>();
		try {
			categories = categoriesDao.findAll();
			if (categories.isEmpty()) {
				return CommonUtils.prepareResponse(response, "No Categories Present", false);
			} else {
				response.put("Categories", categories);
				return CommonUtils.prepareResponse(response, "Categories fetched successfully.", true);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to get all Members. Please try again.");
		}
	}

	@Override
	public Map<String, Object> getCategoryNames() {
		CommonUtils.logMethodEntry(this);
		Map<String, Object> response = new HashMap<>();
		List<CategoriesEntity> categories = new ArrayList<>();
		try {
			categories = categoriesDao.findAll();
			if (categories.isEmpty()) {
				return CommonUtils.prepareResponse(response, "No Categories Present", false);
			} else {
				List<Map<String, Object>> categoryList = categories.stream().map(cat -> {
					Map<String, Object> map = new HashMap<>();
					map.put("categoryId", cat.getCategoryId());
					map.put("category", cat.getCategory());
					map.put("categoryStatus", cat.getCategoryStatus());
					return map;
				}).collect(Collectors.toList());

				response.put("Categories", categoryList);
				return CommonUtils.prepareResponse(response, "Categories fetched successfully.", true);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to get all Members. Please try again.");
		}
	}

	@Override
	public Map<String, Object> getProducts() {
		CommonUtils.logMethodEntry(this);
		Map<String, Object> response = new HashMap<>();
		try {
			List<ProductEntity> products = productDao.findAll();

			if (products.isEmpty()) {
				return CommonUtils.prepareResponse(response, "No Products Present", false);
			} else {

				response.put("Products", products);
				return CommonUtils.prepareResponse(response, "Products fetched successfully.", true);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to get the Products at the moment. Please try again.");
		}
	}

}
