package com.web.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SendMessageDTO {

	@NotBlank(message = "Sender is required")
	@Email(message = "Invalid Email format")
	private String sender;

	@NotBlank(message = "Message is required")
	private String content;

}
