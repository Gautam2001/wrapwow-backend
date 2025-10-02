package com.web.utility;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;

import com.web.dao.MemberDao;
import com.web.entity.MemberEntity;

public class CommonUtils {

	private static final Logger log = LoggerFactory.getLogger(CommonUtils.class);

	public static void ValidateUserWithToken(String username) {
		String tokenUser = SecurityContextHolder.getContext().getAuthentication().getName();
		if (!tokenUser.equals(username)) {
			throw new AppException("Access denied: Token does not match requested user.", HttpStatus.FORBIDDEN);
		}
	}

	public static String normalizeUsername(String username) {
		if (username == null || username.trim().isEmpty()) {
			throw new AppException("Username is required", HttpStatus.BAD_REQUEST);
		}

		return username.trim().toLowerCase();
	}

	public static void ensureUserDoesNotExist(MemberDao memberDao, String username) {
		Optional<MemberEntity> userOptional = memberDao.getUserByEmail(username);
		if (userOptional.isPresent()) {
			throw new AppException("User already exists. please Login.", HttpStatus.CONFLICT);
		}
	}

	public static MemberEntity fetchUserIfExists(MemberDao memberDao, String username, String message) {
		return memberDao.getUserByEmail(username).orElseThrow(() -> new AppException(message, HttpStatus.BAD_REQUEST));
	}

	public static void logMethodEntry(Object caller) {
		String className = caller.getClass().getSimpleName();
		log.info("Inside {}.{}", className, getCallingMethodName());
	}

	public static void logMethodEntry(Object caller, String message) {
		String className = caller.getClass().getSimpleName();
		log.info("Inside {}.{}() → {}", className, getCallingMethodName(), message);
	}

	private static String getCallingMethodName() {
		return Thread.currentThread().getStackTrace()[3].getMethodName();
	}

	public static Map<String, Object> prepareResponse(Map<String, Object> response, String message, boolean success) {
		response.put("status", success ? "0" : "1");
		response.put("message", message);
		return response;
	}

	public static void logError(Exception ex) {
		StackTraceElement origin = ex.getStackTrace()[0];
		log.error("Exception in {}.{}() → {}", origin.getClassName(), origin.getMethodName(), ex.getMessage(), ex);
	}

	public static ResponseEntity<Map<String, Object>> buildErrorResponse(HttpStatus status, String message,
			Object errors) {
		Map<String, Object> body = new LinkedHashMap<>();
		body.put("timestamp", Instant.now());
		body.put("status", status.value());
		body.put("error", status.getReasonPhrase());
		body.put("message", message);
		if (errors != null)
			body.put("details", errors);

		return new ResponseEntity<>(body, status);
	}

}
