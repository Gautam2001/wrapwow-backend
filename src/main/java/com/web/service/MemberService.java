package com.web.service;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.web.DTO.ContactUsDTO;
import com.web.DTO.UsernameDTO;

import jakarta.validation.Valid;

@Component
public interface MemberService {

	Map<String, Object> landingPageData();

	Map<String, Object> userExistsCheck(@Valid UsernameDTO usernameDTO);

	Map<String, Object> joinApp(@Valid UsernameDTO usernameDTO);

	Map<String, Object> contactUs(@Valid ContactUsDTO contactUsDTO);

	Map<String, Object> getAllCategories();

	Map<String, Object> getCategoryNames();

	Map<String, Object> getProducts();

}
