package com.web.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.web.DTO.ContactUsDTO;
import com.web.DTO.MemberLoginDTO;
import com.web.DTO.ResetPasswordDTO;
import com.web.DTO.SignupDTO;
import com.web.service.MemberService;
import com.web.utility.DataConstants;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/member")
public class MemberController {

	@Autowired
	MemberService memberService;

	@GetMapping("/landingPageData")
	public ResponseEntity<Map<String, Object>> landingPageData() {
		Map<String, Object> response = new HashMap<>();

		response.put("message", DataConstants.SUCCESS_Message);
		response.put("status", DataConstants.SUCCESS_STATUS);

		response.put("resultString", memberService.landingPageData());

		return ResponseEntity.ok(response);
	}

	@PostMapping("/signup")
	public ResponseEntity<Map<String, Object>> signup(@RequestBody @Valid SignupDTO signupDTO) {
		Map<String, Object> response = new HashMap<>();
		response.put("message", DataConstants.SUCCESS_Message);
		response.put("status", DataConstants.SUCCESS_STATUS);

		response.put("resultString", memberService.signup(signupDTO));
		return ResponseEntity.ok(response);
	}

	@PostMapping("/login")
	public ResponseEntity<Map<String, Object>> Login(@RequestBody @Valid MemberLoginDTO loginDTO) {
		Map<String, Object> response = new HashMap<>();

		response.put("message", DataConstants.SUCCESS_Message);
		response.put("status", DataConstants.SUCCESS_STATUS);

		response.put("resultString", memberService.memberLogin(loginDTO));

		return ResponseEntity.ok(response);
	}

	@PostMapping("/sendOtp")
	public ResponseEntity<Map<String, Object>> sendOtp(@RequestParam String email) {
		Map<String, Object> response = new HashMap<>();
		if (email == null || email.isBlank() || !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
			response.put("message", DataConstants.FAIL_Message);
			response.put("status", DataConstants.FAIL_STATUS);
			return ResponseEntity.badRequest().body(response);
		}
		response.put("message", DataConstants.SUCCESS_Message);
		response.put("status", DataConstants.SUCCESS_STATUS);

		response.put("resultString", memberService.sendOtp(email));

		return ResponseEntity.ok(response);
	}

	@PostMapping("/validateOtp")
	public ResponseEntity<Map<String, Object>> validateOtp(@RequestBody @Valid MemberLoginDTO memberLoginDTO) {
		Map<String, Object> response = new HashMap<>();
		response.put("message", DataConstants.SUCCESS_Message);
		response.put("status", DataConstants.SUCCESS_STATUS);

		response.put("resultString", memberService.validateOtp(memberLoginDTO));

		return ResponseEntity.ok(response);
	}

	@PostMapping("/forgotPassword")
	public ResponseEntity<Map<String, Object>> ForgotPassword(@RequestBody @Valid MemberLoginDTO forgotPasswordDTO) {
		Map<String, Object> response = new HashMap<>();
		response.put("message", DataConstants.SUCCESS_Message);
		response.put("status", DataConstants.SUCCESS_STATUS);

		response.put("resultString", memberService.forgotPassword(forgotPasswordDTO));

		return ResponseEntity.ok(response);
	}

	@PostMapping("/resetPassword")
	public ResponseEntity<Map<String, Object>> userResetPassword(
			@RequestBody @Valid ResetPasswordDTO resetPasswordDTO) {
		Map<String, Object> response = new HashMap<>();
		response.put("message", DataConstants.SUCCESS_Message);
		response.put("status", DataConstants.SUCCESS_STATUS);

		response.put("resultString", memberService.resetPassword(resetPasswordDTO));

		return ResponseEntity.ok(response);
	}

	@PostMapping("/contactUs")
	public ResponseEntity<Map<String, Object>> contactUs(@RequestBody @Valid ContactUsDTO contactUsDTO) {
		Map<String, Object> response = new HashMap<>();
		response.put("message", DataConstants.SUCCESS_Message);
		response.put("status", DataConstants.SUCCESS_STATUS);

		response.put("resultString", memberService.contactUs(contactUsDTO));

		return ResponseEntity.ok(response);
	}

	@GetMapping("/getCategories")
	public ResponseEntity<Map<String, Object>> getAllCategories() {
		Map<String, Object> response = new HashMap<>();
		response.put("message", DataConstants.SUCCESS_Message);
		response.put("status", DataConstants.SUCCESS_STATUS);

		response.put("resultString", memberService.getAllCategories());

		return ResponseEntity.ok(response);
	}

	@GetMapping("/getCategoryNames")
	public ResponseEntity<Map<String, Object>> getCategoryNames() {
		Map<String, Object> response = new HashMap<>();
		response.put("message", DataConstants.SUCCESS_Message);
		response.put("status", DataConstants.SUCCESS_STATUS);

		response.put("resultString", memberService.getCategoryNames());

		return ResponseEntity.ok(response);
	}

	@GetMapping("/getProducts")
	public ResponseEntity<Map<String, Object>> getProducts() {
		Map<String, Object> response = new HashMap<>();
		response.put("message", DataConstants.SUCCESS_Message);
		response.put("status", DataConstants.SUCCESS_STATUS);

		response.put("resultString", memberService.getProducts());

		return ResponseEntity.ok(response);
	}

}
