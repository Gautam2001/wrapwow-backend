package com.web.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.web.DTO.ContactUsDTO;
import com.web.DTO.UsernameDTO;
import com.web.service.MemberService;
import com.web.utility.CommonUtils;
import com.web.utility.DataConstants;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/member")
public class MemberController {

	@Autowired
	MemberService memberService;

	@GetMapping("/ping")
	public ResponseEntity<Map<String, Object>> ping() {
		CommonUtils.logMethodEntry(this);
		Map<String, Object> response = new HashMap<>();

		response.put("message", DataConstants.SUCCESS_Message);
		response.put("status", DataConstants.SUCCESS_STATUS);

		response.put("resultString", "pong");

		return ResponseEntity.ok(response);
	}

	@GetMapping("/landingPageData")
	public ResponseEntity<Map<String, Object>> landingPageData() {
		CommonUtils.logMethodEntry(this);
		Map<String, Object> response = new HashMap<>();

		response.put("message", DataConstants.SUCCESS_Message);
		response.put("status", DataConstants.SUCCESS_STATUS);

		response.put("resultString", memberService.landingPageData());

		return ResponseEntity.ok(response);
	}

	@GetMapping("/exists")
	public ResponseEntity<Map<String, Object>> userExistsCheck(@RequestBody @Valid UsernameDTO usernameDTO) {
		CommonUtils.logMethodEntry(this);
		Map<String, Object> response = new HashMap<>();
		response.put("message", DataConstants.SUCCESS_Message);
		response.put("status", DataConstants.SUCCESS_STATUS);

		response.put("resultString", memberService.userExistsCheck(usernameDTO));

		return ResponseEntity.ok(response);
	}

	@PostMapping("/join")
	public ResponseEntity<Map<String, Object>> joinApp(@RequestBody @Valid UsernameDTO usernameDTO) {
		CommonUtils.logMethodEntry(this);
		Map<String, Object> response = new HashMap<>();
		response.put("message", DataConstants.SUCCESS_Message);
		response.put("status", DataConstants.SUCCESS_STATUS);

		response.put("resultString", memberService.joinApp(usernameDTO));
		return ResponseEntity.ok(response);
	}

	@PostMapping("/contactUs")
	public ResponseEntity<Map<String, Object>> contactUs(@RequestBody @Valid ContactUsDTO contactUsDTO) {
		CommonUtils.logMethodEntry(this);
		Map<String, Object> response = new HashMap<>();
		response.put("message", DataConstants.SUCCESS_Message);
		response.put("status", DataConstants.SUCCESS_STATUS);

		response.put("resultString", memberService.contactUs(contactUsDTO));

		return ResponseEntity.ok(response);
	}

	@GetMapping("/getCategories")
	public ResponseEntity<Map<String, Object>> getAllCategories() {
		CommonUtils.logMethodEntry(this);
		Map<String, Object> response = new HashMap<>();
		response.put("message", DataConstants.SUCCESS_Message);
		response.put("status", DataConstants.SUCCESS_STATUS);

		response.put("resultString", memberService.getAllCategories());

		return ResponseEntity.ok(response);
	}

	@GetMapping("/getCategoryNames")
	public ResponseEntity<Map<String, Object>> getCategoryNames() {
		CommonUtils.logMethodEntry(this);
		Map<String, Object> response = new HashMap<>();
		response.put("message", DataConstants.SUCCESS_Message);
		response.put("status", DataConstants.SUCCESS_STATUS);

		response.put("resultString", memberService.getCategoryNames());

		return ResponseEntity.ok(response);
	}

	@GetMapping("/getProducts")
	public ResponseEntity<Map<String, Object>> getProducts() {
		CommonUtils.logMethodEntry(this);
		Map<String, Object> response = new HashMap<>();
		response.put("message", DataConstants.SUCCESS_Message);
		response.put("status", DataConstants.SUCCESS_STATUS);

		response.put("resultString", memberService.getProducts());

		return ResponseEntity.ok(response);
	}

}
