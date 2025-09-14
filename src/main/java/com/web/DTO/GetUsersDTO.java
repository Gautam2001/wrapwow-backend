package com.web.DTO;

import com.web.entity.MemberEntity.AccountStatus;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetUsersDTO {

	@NotBlank(message = "Id cannot be blank")
	private Long id;

	@NotBlank(message = "Name cannot be blank")
	@Size(min = 2, max = 50, message = "Name between 2 and 50 Characters")
	private String name;

	@NotBlank(message = "Email cannot be blank")
	@Email(message = "Invalid Email format")
	private String email;

	@NotBlank(message = "Account Status cannot be blank")
	private AccountStatus accountStatus;

}
