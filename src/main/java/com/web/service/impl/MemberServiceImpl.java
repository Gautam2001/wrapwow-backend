package com.web.service.impl;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.web.DTO.ContactUsDTO;
import com.web.DTO.MemberLoginDTO;
import com.web.DTO.ProductListDTOUser;
import com.web.DTO.ResetPasswordDTO;
import com.web.DTO.SignupDTO;
import com.web.dao.CategoriesDao;
import com.web.dao.ContactRequestDao;
import com.web.dao.MemberDao;
import com.web.dao.ProductDao;
import com.web.entity.CategoriesEntity;
import com.web.entity.ContactRequestsEntity;
import com.web.entity.MemberEntity;
import com.web.entity.MemberEntity.AccountStatus;
import com.web.entity.MemberEntity.Role;
import com.web.entity.ProductEntity;
import com.web.entity.ProductImagesEntity;
import com.web.entity.ProductPriceEntity;
import com.web.service.MemberService;
import com.web.utility.AppConfigProperties;
import com.web.utility.DataConstants;
import com.web.utility.jwt.JwtUtil;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@Service
public class MemberServiceImpl implements MemberService {

	@Autowired
	private AppConfigProperties appConfig;

	@Autowired
	private MemberDao memberDao;

	@Autowired
	private CategoriesDao categoriesDao;

	@Autowired
	private ProductDao productDao;

	@Autowired
	private ContactRequestDao contactDao;

	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	BCryptPasswordEncoder passwordEncoder;

	private static final SecureRandom secureRandom = new SecureRandom();

	private Map<String, Object> prepareResponse(Map<String, Object> response, String message, boolean isSuccess) {
		response.put("result", message);
		response.put("resultStatus", isSuccess ? DataConstants.SUCCESS_STATUS : DataConstants.FAIL_STATUS);
		return response;
	}

	private boolean memberNotExist(String email, Map<String, Object> response) {
		Optional<MemberEntity> userOptional = memberDao.getUserByEmail(email);
		try {
			if (userOptional.isEmpty()) {
				prepareResponse(response, "Member does not exist.", false);
				return true;
			} else {
				prepareResponse(response, "Member already exists, try logging in.", true);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to validate Member. Please try again.");
		}
		return false;
	}

	@Override
	@Transactional
	public Map<String, Object> landingPageData() {
		Map<String, Object> response = new HashMap<>();
		List<CategoriesEntity> categories = new ArrayList<>();
		String baseurl = appConfig.getBaseURL();
		try {
			categories = categoriesDao.findAll();
			if (categories.isEmpty()) {
				return prepareResponse(response, "No Categories Present", false);
			} else {
				for (CategoriesEntity category : categories) {
					String fullPath = category.getPath();
					if (fullPath.startsWith("http")) {
						continue;
					}

					String fileName = fullPath.substring(fullPath.lastIndexOf("\\") + 1);
					String folder = fullPath.split("\\\\")[2];
					String imageUrl = baseurl + "images/" + folder + "/" + fileName;
					category.setPath(imageUrl);
				}
				response.put("Categories", categories);
			}

			PageRequest pageRequest = PageRequest.of(0, 5);
			List<ProductEntity> BestSellingroducts = productDao.getTopSellingProducts(pageRequest);
			if (BestSellingroducts.isEmpty()) {
				return prepareResponse(response, "No products found.", false);
			} else {
				for (ProductEntity product : BestSellingroducts) {
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
				}
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
				return prepareResponse(response, "No products found.", false);
			} else {
				for (ProductEntity product2 : expensiveProducts) {
					if (product2.getImages() != null) {
						for (ProductImagesEntity image2 : product2.getImages()) {
							String fullPath = image2.getPath();
							if (fullPath.startsWith("http")) {
								continue;
							}

							String fileName = fullPath.substring(fullPath.lastIndexOf("\\") + 1);
							String folder = fullPath.split("\\\\")[2];
							String imageUrl = baseurl + "images/" + folder + "/" + fileName;
							image2.setPath(imageUrl);
						}
					}
				}
				List<ProductListDTOUser> dtoList = expensiveProducts.stream().map(p -> {
					Double minPrice = p.getPrices().stream().map(ProductPriceEntity::getFinalPrice)
							.min(Double::compareTo).orElse(null);

					return new ProductListDTOUser(p.getProductId(), p.getName(), p.getCategory(), p.getImages(),
							minPrice, p.getProductStatus(), p.getAvailableQty());
				}).collect(Collectors.toList());
				response.put("ExpensiveProducts", dtoList);
			}

			return prepareResponse(response, "Landing Page Data fetched successfully.", true);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to get data for Landing Page. Please try again.");
		}
	}

	@Override
	public Map<String, Object> signup(@Valid SignupDTO signupDTO) {
		Map<String, Object> response = new HashMap<>();

		try {
			Optional<MemberEntity> userOptional = memberDao.getUserByEmail(signupDTO.getEmail());

			if (!userOptional.isEmpty()) {
				Role memberRole = userOptional.get().getRole();
				return prepareResponse(response, memberRole + " account already exists with the Email, try logging in.",
						false);
			}

			Role role;
			try {
				role = Role.valueOf(signupDTO.getRole().toUpperCase());
			} catch (IllegalArgumentException e) {
				return prepareResponse(response, "Invalid role provided.", false);
			}

			MemberEntity memberEntity = new MemberEntity(signupDTO.getEmail(), signupDTO.getName(),
					passwordEncoder.encode(signupDTO.getPassword()), signupDTO.getDob(), role);

			MemberEntity savedMember = memberDao.save(memberEntity);
			if (savedMember == null || savedMember.getUserId() == null) {
				return prepareResponse(response, "Failed to save the member. Please try again.", false);
			}

			return prepareResponse(response, role + " signup Successfully", true);

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to save the member. Please try again.");
		}
	}

	@Override
	public Map<String, Object> memberLogin(@Valid MemberLoginDTO loginDTO) {
		Map<String, Object> response = new HashMap<>();

		try {

			MemberEntity memberEntity = new MemberEntity(loginDTO.getEmail(), loginDTO.getPassword());
			Optional<MemberEntity> getMember = memberDao.getUserByEmail(memberEntity.getEmail());
			if (getMember.isPresent()) {
				MemberEntity checkUserPassword = getMember.get();
				String encryptedPassword = checkUserPassword.getPassword();
				if (passwordEncoder.matches(memberEntity.getPassword(), encryptedPassword)) {

					String role = getMember.get().getRole().toString();

					if (role == "ADMIN" && checkUserPassword.getAccountStatus() == AccountStatus.INACTIVE) {
						return prepareResponse(response, "Admin account is Inactive.", false);
					}

					Authentication authentication = authenticationManager.authenticate(
							new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), loginDTO.getPassword()));

					UserDetails userDetails = (UserDetails) authentication.getPrincipal();

					String token = jwtUtil.generateToken(userDetails.getUsername(),
							userDetails.getAuthorities().stream().findFirst().get().getAuthority());
					response.put("token", token);
					response.put("role", role);
					response.put("email", checkUserPassword.getEmail());
					response.put("name", checkUserPassword.getName());
					response.put("accountStatus", checkUserPassword.getAccountStatus());
					return prepareResponse(response, "Login Successfull.", true);
				} else {
					return prepareResponse(response, "Incorrect Password, try logging in again.", false);
				}
			} else {
				return prepareResponse(response, "Member does not exist, Signup", false);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to Login Members. Please try again.");
		}
	}

	@Override
	public Map<String, Object> sendOtp(String email) {
		Map<String, Object> response = new HashMap<>();

		try {
			Optional<MemberEntity> userOptional = memberDao.getUserByEmail(email);
			if (userOptional.isEmpty()) {
				prepareResponse(response, "Member does not exist.", false);
			}
			MemberEntity member = userOptional.get();
			if (member.getRole().equals(Role.ADMIN)) {
				return prepareResponse(response, "Forgot Password functionality not available for Admins", false);
			}
			String otp = String.format("%06d", secureRandom.nextInt(1000000));
			LocalDateTime now = LocalDateTime.now();
			boolean otpVerified = false;
			int updateResult = memberDao.updateOtpAndotpGeneratedAtByEmail(otp, now, otpVerified, member.getEmail());
			if (updateResult > 0) {
				response.put("OTP", otp);
				return prepareResponse(response, "OTP send Successfully", true);
			} else {
				return prepareResponse(response, "OTP cannot be send, please try again", false);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to Send OTP. Please try again.");
		}
	}

	@Override
	public Map<String, Object> validateOtp(@Valid MemberLoginDTO memberLoginDTO) {
		Map<String, Object> response = new HashMap<>();
		MemberEntity memberEntity = new MemberEntity();
		memberEntity.setEmail(memberLoginDTO.getEmail());
		memberEntity.setOtp(memberLoginDTO.getPassword());

		if (memberNotExist(memberEntity.getEmail(), response)) {
			return response;
		}
		try {
			Optional<MemberEntity> userOptional = memberDao.getUserByEmailAndOtp(memberEntity.getEmail(),
					memberEntity.getOtp());
			if (userOptional.isPresent()) {
				MemberEntity getUser = userOptional.get();
				if (getUser.getRole().equals(Role.ADMIN)) {
					return prepareResponse(response, "Forgot Password functionality not available for Admins", false);
				}
				LocalDateTime now = LocalDateTime.now();
				LocalDateTime otpGeneratedAt = getUser.getOtpGeneratedAt();
				if (otpGeneratedAt == null || now.isAfter(otpGeneratedAt.plusMinutes(10))) {
					return prepareResponse(response, "OTP Expired.", false);
				}

				int updateResult = memberDao.updateOtpVerifiedByEmailAndOtp(true, getUser.getEmail(), getUser.getOtp());
				if (updateResult > 0) {
					return prepareResponse(response, "OTP verified Successfully", true);
				} else {
					return prepareResponse(response, "OTP cannot be verified, please try again", false);
				}
			} else {
				return prepareResponse(response, "Incorrect OTP.", false);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to validate OTP. Please try again.");
		}
	}

	@Override
	public Map<String, Object> forgotPassword(@Valid MemberLoginDTO forgotPasswordDTO) {
		Map<String, Object> response = new HashMap<>();
		MemberEntity memberEntity = new MemberEntity(forgotPasswordDTO.getEmail(),
				passwordEncoder.encode(forgotPasswordDTO.getPassword()));

		if (memberNotExist(memberEntity.getEmail(), response)) {
			return response;
		}
		try {
			Optional<MemberEntity> userOptional = memberDao.getUserByEmailAndOtpVerified(memberEntity.getEmail(), true);
			if (userOptional.isPresent()) {
				MemberEntity getuser = userOptional.get();
				if (getuser.getRole().equals(Role.ADMIN)) {
					return prepareResponse(response, "Forgot Password functionality not available for Admins", false);
				}
				int updateResult = memberDao.updatePasswordOtpVerifiedByEmail(memberEntity.getPassword(),
						memberEntity.getEmail());
				if (updateResult > 0) {
					return prepareResponse(response, "Password updated Successfully", true);
				} else {
					return prepareResponse(response, "Password cannot be updated, please try again", false);
				}
			} else {
				return prepareResponse(response, "OTP verification skipped.", false);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to change password via forgot Password. Please try again.");
		}
	}

	@Override
	public Map<String, Object> resetPassword(@Valid ResetPasswordDTO resetPasswordDTO) {
		Map<String, Object> response = new HashMap<>();

		if (memberNotExist(resetPasswordDTO.getEmail(), response)) {
			return response;
		}
		try {
			MemberEntity memberEntity = new MemberEntity(resetPasswordDTO.getEmail(), resetPasswordDTO.getPassword());
			MemberEntity checkOldPassword = memberDao.getUserByEmail(memberEntity.getEmail()).get();
			String encryptedPassword = checkOldPassword.getPassword();
			if (passwordEncoder.matches(resetPasswordDTO.getOldPassword(), encryptedPassword)) {
				int updateResult = memberDao.updatePasswordByEmail(passwordEncoder.encode(memberEntity.getPassword()),
						memberEntity.getEmail());
				if (updateResult > 0) {
					return prepareResponse(response, "Password Reset Successfull", true);
				} else {
					return prepareResponse(response, "Password cannot be updated, please try again", false);
				}
			} else {
				return prepareResponse(response, "Incorrect Old Password Entered", false);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to reset the password. Please try again.");
		}

	}

	@Override
	public Map<String, Object> contactUs(@Valid ContactUsDTO contactUsDTO) {
		Map<String, Object> response = new HashMap<>();

		try {
			ContactRequestsEntity contactRequestsEntity = new ContactRequestsEntity(contactUsDTO.getName(),
					contactUsDTO.getEmail(), contactUsDTO.getMessage());

			ContactRequestsEntity contactSaved = contactDao.save(contactRequestsEntity);
			if (contactSaved == null) {
				return prepareResponse(response, "Failed to save the message. Please try again.", false);
			}

			return prepareResponse(response, " Thank you for the Feedback!", true);

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to save the member. Please try again.");
		}
	}

	@Override
	public Map<String, Object> getAllCategories() {
		Map<String, Object> response = new HashMap<>();
		List<CategoriesEntity> categories = new ArrayList<>();
		String baseurl = appConfig.getBaseURL();
		try {
			categories = categoriesDao.findAll();
			if (categories.isEmpty()) {
				return prepareResponse(response, "No Categories Present", false);
			} else {
				for (CategoriesEntity category : categories) {
					String fullPath = category.getPath();
					if (fullPath.startsWith("http")) {
						continue;
					}

					String fileName = fullPath.substring(fullPath.lastIndexOf("\\") + 1);
					String folder = fullPath.split("\\\\")[2];
					String imageUrl = baseurl + "images/" + folder + "/" + fileName;
					category.setPath(imageUrl);
				}
				response.put("Categories", categories);
				return prepareResponse(response, "Categories fetched successfully.", true);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to get all Members. Please try again.");
		}
	}

	@Override
	public Map<String, Object> getCategoryNames() {
		Map<String, Object> response = new HashMap<>();
		List<CategoriesEntity> categories = new ArrayList<>();
		try {
			categories = categoriesDao.findAll();
			if (categories.isEmpty()) {
				return prepareResponse(response, "No Categories Present", false);
			} else {
				List<Map<String, Object>> categoryList = categories.stream().map(cat -> {
					Map<String, Object> map = new HashMap<>();
					map.put("categoryId", cat.getCategoryId());
					map.put("category", cat.getCategory());
					map.put("categoryStatus", cat.getCategoryStatus());
					return map;
				}).collect(Collectors.toList());

				response.put("Categories", categoryList);
				return prepareResponse(response, "Categories fetched successfully.", true);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to get all Members. Please try again.");
		}
	}

	@Override
	public Map<String, Object> getProducts() {
		Map<String, Object> response = new HashMap<>();
		String baseurl = appConfig.getBaseURL();
		try {
			List<ProductEntity> products = productDao.findAll();

			if (products.isEmpty()) {
				return prepareResponse(response, "No Products Present", false);
			} else {
				for (ProductEntity product : products) {
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
				}

				response.put("Products", products);
				return prepareResponse(response, "Products fetched successfully.", true);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to get the Products at the moment. Please try again.");
		}
	}

}
