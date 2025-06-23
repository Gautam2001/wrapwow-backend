package com.web.service;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.web.DTO.ContactUsDTO;
import com.web.DTO.MemberLoginDTO;
import com.web.DTO.ResetPasswordDTO;
import com.web.DTO.SignupDTO;

import jakarta.validation.Valid;

@Component
public interface MemberService {

	Map<String, Object> landingPageData();

	Map<String, Object> signup(@Valid SignupDTO signupDTO);

	Map<String, Object> memberLogin(@Valid MemberLoginDTO loginDTO);

	Map<String, Object> sendOtp(String email);

	Map<String, Object> validateOtp(@Valid MemberLoginDTO memberLoginDTO);

	Map<String, Object> forgotPassword(@Valid MemberLoginDTO forgotPasswordDTO);

	Map<String, Object> resetPassword(@Valid ResetPasswordDTO resetPasswordDTO);

	Map<String, Object> contactUs(@Valid ContactUsDTO contactUsDTO);

	Map<String, Object> getAllCategories();

	Map<String, Object> getCategoryNames();

	Map<String, Object> getProducts();

}
