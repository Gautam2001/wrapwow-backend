package com.web.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "contact_requests")
public class ContactRequestsEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "request_id")
	private Long requestId;

	@NotBlank(message = "Name cannot be Empty")
	@Column(nullable = false)
	private String name;

	@NotBlank(message = "Email cannot be Empty")
	@Email(message = "Invalid email format")
	@Column(nullable = false, unique = true)
	private String email;

	@NotBlank(message = "Message cannot be Empty")
	@Column(nullable = false, columnDefinition = "TEXT")
	private String message;

	public ContactRequestsEntity(@NotBlank(message = "Name cannot be Empty") String name,
			@NotBlank(message = "Email cannot be Empty") @Email(message = "Invalid email format") String email,
			@NotBlank(message = "Message cannot be Empty") String message) {
		super();
		this.name = name;
		this.email = email;
		this.message = message;
	}

}
