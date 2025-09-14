package com.web.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.web.DTO.AddToCartDTO;
import com.web.DTO.PlaceOrderDTO;
import com.web.DTO.PlaceOrderDTO.OrderItemDTO;
import com.web.DTO.ProductListDTOUser;
import com.web.DTO.UpdateCartQtyDTO;
import com.web.dao.CartDao;
import com.web.dao.CategoriesDao;
import com.web.dao.MemberDao;
import com.web.dao.OrderDao;
import com.web.dao.OrderItemDao;
import com.web.dao.ProductDao;
import com.web.dao.ProductPriceDao;
import com.web.entity.CartEntity;
import com.web.entity.CategoriesEntity;
import com.web.entity.CategoriesEntity.CategoryStatus;
import com.web.entity.MemberEntity;
import com.web.entity.MemberEntity.AccountStatus;
import com.web.entity.OrderEntity;
import com.web.entity.OrderItemEntity;
import com.web.entity.ProductEntity;
import com.web.entity.ProductPriceEntity;
import com.web.service.UserService;
import com.web.utility.CommonUtils;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private MemberDao memberDao;

	@Autowired
	private CategoriesDao categoriesDao;

	@Autowired
	private ProductDao productDao;

	@Autowired
	private ProductPriceDao priceDao;

	@Autowired
	private CartDao cartDao;

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

			if ("ADMIN".equals(role)) {
				CommonUtils.prepareResponse(response, "Login with User Credentials to access further.", false);
				return true;
			} else {
				CommonUtils.prepareResponse(response, "User already exists, try logging in.", true);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to validate User. Please try again.");
		}
		return false;
	}

	@Override
	@Transactional
	public Map<String, Object> getProductList() {
		CommonUtils.logMethodEntry(this);
		Map<String, Object> response = new HashMap<>();
		try {
			List<CategoriesEntity> categories = categoriesDao.findAll();
			List<ProductEntity> products = productDao.findAll();

			if (products.isEmpty() || categories.isEmpty()) {
				return CommonUtils.prepareResponse(response, "No products found.", false);
			}

			List<CategoriesEntity> activeCategories = categories.stream()
					.filter(cat -> CategoryStatus.ACTIVE == cat.getCategoryStatus()).collect(Collectors.toList());

			Set<String> activeCategoryNames = activeCategories.stream().map(CategoriesEntity::getCategory)
					.collect(Collectors.toSet());

			List<ProductEntity> filteredProducts = products.stream().filter(
					product -> product.getCategory() != null && activeCategoryNames.contains(product.getCategory()))
					.collect(Collectors.toList());

			List<ProductListDTOUser> dtoList = filteredProducts.stream().map(p -> {
				Double minPrice = p.getPrices().stream().map(ProductPriceEntity::getFinalPrice).min(Double::compareTo)
						.orElse(null);

				return new ProductListDTOUser(p.getProductId(), p.getName(), p.getCategory(), p.getImages(), minPrice,
						p.getProductStatus(), p.getAvailableQty());
			}).collect(Collectors.toList());

			response.put("Products", dtoList);
			return CommonUtils.prepareResponse(response, "Products fetched successfully.", true);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to get the Products at the moment. Please try again.");
		}
	}

	@Override
	public Map<String, Object> getBestSellingProductList() {
		CommonUtils.logMethodEntry(this);
		Map<String, Object> response = new HashMap<>();
		try {
			PageRequest pageRequest = PageRequest.of(0, 5);
			List<ProductEntity> products = productDao.getTopSellingProducts(pageRequest);
			if (products.isEmpty()) {
				return CommonUtils.prepareResponse(response, "No products found.", false);
			} else {
				List<ProductListDTOUser> dtoList = products.stream().map(p -> {
					Double minPrice = p.getPrices().stream().map(ProductPriceEntity::getFinalPrice)
							.min(Double::compareTo).orElse(null);

					return new ProductListDTOUser(p.getProductId(), p.getName(), p.getCategory(), p.getImages(),
							minPrice, p.getProductStatus(), p.getAvailableQty());
				}).collect(Collectors.toList());
				response.put("Products", dtoList);
				return CommonUtils.prepareResponse(response, "Best Selling Products fetched successfully.", true);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to get the Products at the moment. Please try again.");
		}
	}

	@Override
	public Map<String, Object> getMostExpensiveProductList() {
		CommonUtils.logMethodEntry(this);
		Map<String, Object> response = new HashMap<>();
		try {
			PageRequest pageRequest = PageRequest.of(0, 5);
			List<ProductEntity> products = productDao.getMostExpensiveProducts(pageRequest);
			if (products.isEmpty()) {
				return CommonUtils.prepareResponse(response, "No products found.", false);
			} else {
				List<ProductListDTOUser> dtoList = products.stream().map(p -> {
					Double minPrice = p.getPrices().stream().map(ProductPriceEntity::getFinalPrice)
							.min(Double::compareTo).orElse(null);

					return new ProductListDTOUser(p.getProductId(), p.getName(), p.getCategory(), p.getImages(),
							minPrice, p.getProductStatus(), p.getAvailableQty());
				}).collect(Collectors.toList());
				response.put("Products", dtoList);
				return CommonUtils.prepareResponse(response, "Best Selling Products fetched successfully.", true);
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
	public Map<String, Object> addUpdateToCart(@Valid AddToCartDTO addToCartDTO) {
		CommonUtils.logMethodEntry(this);
		String email = CommonUtils.normalizeUsername(addToCartDTO.getEmail());
		CommonUtils.ValidateUserWithToken(email);
		Map<String, Object> response = new HashMap<>();

		if (userNotExist(email, response)) {
			return response;
		}

		if (addToCartDTO.getQuantity() <= 0) {
			return CommonUtils.prepareResponse(response, "Quantity must be greater than zero.", false);
		}

		try {
			Optional<MemberEntity> userOptional = memberDao.getUserByEmail(email);
			if (userOptional.isEmpty()) {
				return CommonUtils.prepareResponse(response, "User does not exist.", false);
			}
			MemberEntity user = userOptional.get();

			Optional<ProductEntity> productOpt = productDao.findById(addToCartDTO.getProductId());
			if (productOpt.isEmpty()) {
				return CommonUtils.prepareResponse(response, "Product not found.", false);
			}
			ProductEntity product = productOpt.get();

			Optional<ProductPriceEntity> priceOpt = priceDao.findById(addToCartDTO.getPriceId());
			if (priceOpt.isEmpty()) {
				return CommonUtils.prepareResponse(response, "Price not found.", false);
			}
			ProductPriceEntity price = priceOpt.get();

			boolean priceBelongsToProduct = product.getPrices().stream()
					.anyMatch(p -> p.getPriceId() == price.getPriceId());

			if (!priceBelongsToProduct) {
				return CommonUtils.prepareResponse(response, "Invalid price selection for the selected product.",
						false);
			}

			long priceId = price.getPriceId();

			Optional<CartEntity> existingCart = cartDao.findByUserIdAndProductAndPriceId(user.getUserId(), product,
					priceId);

			CartEntity savedCart;

			long requestedQty = addToCartDTO.getQuantity();
			long alreadyInCartQty = existingCart.map(CartEntity::getQuantity).orElse(0L);
			long totalRequestedQty = requestedQty + alreadyInCartQty;

			if (totalRequestedQty > product.getAvailableQty()) {
				return CommonUtils.prepareResponse(response, "Cannot add more than available stock.", false);
			}

			if (existingCart.isPresent()) {
				CartEntity cart = existingCart.get();
				cart.setQuantity(totalRequestedQty);
				savedCart = cartDao.save(cart);
			} else {
				CartEntity newCart = new CartEntity(user.getUserId(), product, priceId, requestedQty);
				savedCart = cartDao.save(newCart);
			}

			if (savedCart == null) {
				throw new RuntimeException(
						"Failed to add/update the product in the cart at the moment. Please try again.");
			}

			response.put("Cart", savedCart);
			return CommonUtils.prepareResponse(response, "Product added/updated in cart successfully.", true);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to add the product to the cart at the moment. Please try again.");
		}
	}

	@Override
	@Transactional
	public Map<String, Object> getCart(@Valid String emailId) {
		CommonUtils.logMethodEntry(this);
		String email = CommonUtils.normalizeUsername(emailId);
		CommonUtils.ValidateUserWithToken(email);
		Map<String, Object> response = new HashMap<>();

		try {
			Optional<MemberEntity> userOptional = memberDao.getUserByEmail(email);
			if (userOptional.isEmpty()) {
				return CommonUtils.prepareResponse(response, "User does not exist.", false);
			}

			MemberEntity user = userOptional.get();
			long userId = user.getUserId();

			List<CartEntity> cart = cartDao.findByUserId(userId);
			if (cart.isEmpty()) {
				return CommonUtils.prepareResponse(response, "The Cart is empty for the userId.", false);
			}

			List<Map<String, Object>> cartResponseList = new ArrayList<>();

			for (CartEntity cartItem : cart) {
				ProductEntity product = cartItem.getProduct();

				long priceId = cartItem.getPriceId();
				Optional<ProductPriceEntity> priceOptional = priceDao.findById(priceId);
				if (priceOptional.isEmpty()) {
					return CommonUtils.prepareResponse(response, "Price does not exist.", false);
				}

				ProductListDTOUser productDto = new ProductListDTOUser(product.getProductId(), product.getName(),
						product.getCategory(), product.getImages(), 0, product.getProductStatus(),
						product.getAvailableQty());

				Map<String, Object> cartItemMap = new HashMap<>();
				cartItemMap.put("cartId", cartItem.getCartId());
				cartItemMap.put("quantity", cartItem.getQuantity());
				cartItemMap.put("price", priceOptional.get());
				cartItemMap.put("product", productDto);

				cartResponseList.add(cartItemMap);
			}

			response.put("Cart", cartResponseList);
			return CommonUtils.prepareResponse(response, "Cart fetched successfully.", true);

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to get the cart at the moment. Please try again.");
		}
	}

	@Override
	public Map<String, Object> getCartQty(@Valid String emailId) {
		CommonUtils.logMethodEntry(this);
		String email = CommonUtils.normalizeUsername(emailId);
		CommonUtils.ValidateUserWithToken(email);
		Map<String, Object> response = new HashMap<>();

		try {
			Optional<MemberEntity> userOptional = memberDao.getUserByEmail(email);
			if (userOptional.isEmpty()) {
				return CommonUtils.prepareResponse(response, "User does not exist.", false);
			}

			long userId = userOptional.get().getUserId();
			int cartQty = cartDao.countByUserId(userId);

			response.put("CartQty", cartQty);
			return CommonUtils.prepareResponse(response, "Cart quantity fetched successfully.", true);

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to fetch cart quantity. Please try again.");
		}
	}

	@Override
	public Map<String, Object> updateCartQty(@Valid UpdateCartQtyDTO updateCartQtyDTO) {
		CommonUtils.logMethodEntry(this);
		String email = CommonUtils.normalizeUsername(updateCartQtyDTO.getEmail());
		CommonUtils.ValidateUserWithToken(email);
		Map<String, Object> response = new HashMap<>();

		if (userNotExist(email, response)) {
			return response;
		}

		if (updateCartQtyDTO.getQuantity() < 0) {
			return CommonUtils.prepareResponse(response, "Quantity cannot be negative", false);
		}

		try {
			CartEntity cartEntity = new CartEntity(updateCartQtyDTO.getCartId(), updateCartQtyDTO.getQuantity());

			Optional<CartEntity> cartOptional = cartDao.findById(cartEntity.getCartId());
			if (cartOptional.isEmpty()) {
				return CommonUtils.prepareResponse(response, "Cart not found.", false);
			}

			if (cartEntity.getQuantity() == 0) {
				cartDao.deleteById(cartEntity.getCartId());
				return CommonUtils.prepareResponse(response, "Cart Item removed successfully.", true);
			}

			CartEntity cart = cartOptional.get();
			Long availableQty = cart.getProduct().getAvailableQty();
			if (availableQty < cartEntity.getQuantity()) {
				return CommonUtils.prepareResponse(response,
						"Not enough Quantity available. Available Stock: " + availableQty, false);
			}

			int updateStatus = cartDao.updateQuantityById(cartEntity.getCartId(), cartEntity.getQuantity());
			if (updateStatus > 0) {
				return CommonUtils.prepareResponse(response, "Quantity updated Successfully", true);
			} else {
				return CommonUtils.prepareResponse(response, "Quantity cannot be updated, please try again", false);
			}

		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to delete the product from cart at the moment. Please try again.");
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(
					"Failed to update the product quantity in cart at the moment. Please try again.");
		}
	}

	@Override
	@Transactional
	public Map<String, Object> placeOrder(@Valid PlaceOrderDTO checkoutDTO) {
		CommonUtils.logMethodEntry(this);
		String email = CommonUtils.normalizeUsername(checkoutDTO.getUserEmail());
		CommonUtils.ValidateUserWithToken(email);
		Map<String, Object> response = new HashMap<>();

		try {
			// Step 1: User validation
			if (userNotExist(email, response)) {
				return response;
			}

			Optional<MemberEntity> optionalUser = memberDao.getUserByEmail(email);
			if (optionalUser.isEmpty()) {
				return CommonUtils.prepareResponse(response, "User not found", false);
			}

			MemberEntity user = optionalUser.get();
			if (user.getAccountStatus() != AccountStatus.ACTIVE) {
				return CommonUtils.prepareResponse(response, "User account is not active", false);
			}

			// Step 2: Cart and item validation
			List<OrderItemDTO> items = checkoutDTO.getItems();
			if (items == null || items.isEmpty()) {
				return CommonUtils.prepareResponse(response, "No items to place order", false);
			}

			List<CartEntity> userCart = cartDao.findByUserId(user.getUserId());
			if (userCart.isEmpty()) {
				return CommonUtils.prepareResponse(response, "Your cart is empty", false);
			}

			Map<String, CartEntity> cartMap = new HashMap<>();
			for (CartEntity cart : userCart) {
				Long productId = cart.getProduct().getProductId();
				Long priceId = (cart.getPriceId() != null) ? cart.getPriceId() : 0L;
				String key = productId + "-" + priceId;
				cartMap.put(key, cart);
			}

			Map<Long, ProductEntity> productCache = new HashMap<>();
			Map<Long, ProductPriceEntity> priceCache = new HashMap<>();
			Map<Long, Integer> productQtyMap = new HashMap<>();
			double calculatedTotalAmount = 0;
			int totalQuantity = 0;

			for (OrderItemDTO item : items) {
				if (item.getQuantity() <= 0 || item.getPricePerUnit() <= 0) {
					return CommonUtils.prepareResponse(response,
							"Invalid quantity or price for product: " + item.getProductName(), false);
				}

				Long productId = item.getProductId();
				Long priceId = (item.getPriceId() != null) ? item.getPriceId() : 0L;
				String key = productId + "-" + priceId;

				// Validate item exists in cart
				CartEntity cartEntry = cartMap.get(key);
				if (cartEntry == null) {
					return CommonUtils.prepareResponse(response, "Product not found in cart: " + item.getProductName(),
							false);
				}

				// Fetch and validate price
				ProductPriceEntity priceEntity = priceCache.computeIfAbsent(priceId,
						id -> priceDao.findById(id).orElse(null));
				if (priceEntity == null) {
					return CommonUtils.prepareResponse(response, "Price does not exist for: " + item.getProductName(),
							false);
				}

				// Validate quantity and price match
				if (!Objects.equals(cartEntry.getQuantity(), item.getQuantity())
						|| Double.compare(item.getPricePerUnit(), priceEntity.getFinalPrice()) != 0) {
					return CommonUtils.prepareResponse(response,
							"Cart item mismatch for product: " + item.getProductName(), false);
				}

				// Fetch and cache product
				ProductEntity product = productCache.computeIfAbsent(productId,
						id -> productDao.findById(id).orElse(null));
				if (product == null) {
					return CommonUtils.prepareResponse(response, "Product not found: " + item.getProductName(), false);
				}

				// Validate again price-per-unit from input vs DB
				if (Double.compare(priceEntity.getFinalPrice(), item.getPricePerUnit()) != 0) {
					return CommonUtils.prepareResponse(response, "Price mismatch for product: " + item.getProductName(),
							false);
				}

				// Tally up totals
				calculatedTotalAmount += item.getPricePerUnit() * item.getQuantity();
				totalQuantity += item.getQuantity();
				productQtyMap.merge(productId, item.getQuantity().intValue(), Integer::sum);
			}

			// Step 3: Validate stock availability
			for (Map.Entry<Long, Integer> entry : productQtyMap.entrySet()) {
				ProductEntity product = productCache.get(entry.getKey());
				if (product.getAvailableQty() < entry.getValue()) {
					return CommonUtils.prepareResponse(response, "Not enough stock for product: " + product.getName(),
							false);
				}
			}

			// Step 4: Validate total amount
			if (Double.compare(calculatedTotalAmount, checkoutDTO.getTotalAmount()) != 0) {
				return CommonUtils.prepareResponse(response, "Total amount mismatch. Please refresh and try again.",
						false);
			}

			// Step 5: Create order
			OrderEntity order = new OrderEntity();
			order.setUserId(user.getUserId());
			order.setTotalAmount(calculatedTotalAmount);
			order.setTotalItems(totalQuantity);
			order.setOrderTime(LocalDateTime.now());
			order.setOrderStatus("PLACED");
			orderDao.save(order);

			// Step 6: Save order items and update stock
			for (OrderItemDTO item : items) {
				ProductEntity product = productCache.get(item.getProductId());
				ProductPriceEntity price = priceCache.get(item.getPriceId());

				OrderItemEntity orderItem = new OrderItemEntity();
				orderItem.setOrder(order);
				orderItem.setProduct(product);
				orderItem.setQuantity(item.getQuantity());
				orderItem.setTotalPrice(item.getTotalPrice());
				orderItem.setPricePerUnit(item.getPricePerUnit());
				if (price != null) {
					orderItem.setPriceId(price.getPriceId());
				}
				orderItemDao.save(orderItem);

				product.setAvailableQty(product.getAvailableQty() - item.getQuantity());
				product.setTotalOrderedQty(product.getTotalOrderedQty() + item.getQuantity());
				productDao.save(product);
			}

			// Step 7: Clear cart
			cartDao.deleteAllByUserId(user.getUserId());

			return CommonUtils.prepareResponse(response, "Order placed successfully", true);

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("An error occurred while placing the order. Please try again.");
		}
	}

	@Override
	public Map<String, Object> getOrders(@Valid String emailId) {
		CommonUtils.logMethodEntry(this);
		String email = CommonUtils.normalizeUsername(emailId);
		CommonUtils.ValidateUserWithToken(email);
		Map<String, Object> response = new HashMap<>();

		try {
			Optional<MemberEntity> userOptional = memberDao.getUserByEmail(email);
			if (userOptional.isEmpty()) {
				return CommonUtils.prepareResponse(response, "User does not exist.", false);
			}

			MemberEntity user = userOptional.get();
			long userId = user.getUserId();

			List<OrderEntity> orders = orderDao.findByUserIdOrderByOrderIdDesc(userId);
			if (orders.isEmpty()) {
				return CommonUtils.prepareResponse(response, "No past orders found for the user.", false);
			}

			List<Map<String, Object>> orderResponseList = new ArrayList<>();

			for (OrderEntity order : orders) {
				Map<String, Object> orderMap = new HashMap<>();
				orderMap.put("orderId", order.getOrderId());
				orderMap.put("orderStatus", order.getOrderStatus());
				orderMap.put("orderTime", order.getOrderTime());
				orderMap.put("totalAmount", order.getTotalAmount());
				orderMap.put("totalItems", order.getTotalItems());

				List<Map<String, Object>> orderItemsList = new ArrayList<>();

				for (OrderItemEntity orderItem : order.getItems()) {
					ProductEntity product = orderItem.getProduct();

					ProductListDTOUser productDto = new ProductListDTOUser(product.getProductId(), product.getName(),
							product.getCategory(), product.getImages(), 0, product.getProductStatus(),
							product.getAvailableQty());

					Map<String, Object> itemMap = new HashMap<>();
					itemMap.put("product", productDto);
					itemMap.put("quantity", orderItem.getQuantity());
					itemMap.put("pricePerUnit", orderItem.getPricePerUnit());
					itemMap.put("totalPrice", orderItem.getTotalPrice());

					orderItemsList.add(itemMap);
				}

				orderMap.put("items", orderItemsList);
				orderResponseList.add(orderMap);
			}

			response.put("orders", orderResponseList);
			return CommonUtils.prepareResponse(response, "Orders fetched successfully.", true);

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to fetch orders. Please try again.");
		}
	}

}
