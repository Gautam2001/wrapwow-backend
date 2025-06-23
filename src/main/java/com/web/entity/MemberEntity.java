package com.web.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "members")
public class MemberEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_id")
	private Long userId;

	@NotNull(message = "Email cannot be Empty")
	@Email(message = "Invalid email format")
	@Column(nullable = false, unique = true)
	private String email;

	@NotNull(message = "Name cannot be Empty")
	@Column(nullable = false)
	private String name;

	@NotNull(message = "Password cannot be Empty")
	@Column(nullable = false)
	private String password;

	@Column
	private String otp;

	@Column
	private LocalDateTime otpGeneratedAt;

	@Column
	private Boolean otpVerified;

	@NotNull(message = "DOB cannot be blank")
	@JsonFormat(pattern = "dd/MM/yyyy")
	@Column(nullable = false)
	private LocalDate dob;

	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@Column(nullable = false)
	private LocalDateTime updatedAt;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Role role;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private AccountStatus accountStatus;

	public enum Role {
		USER, ADMIN
	}

	public enum AccountStatus {
		ACTIVE, INACTIVE
	}

	@PrePersist
	protected void onCreate() {
		this.createdAt = LocalDateTime.now();
		this.updatedAt = LocalDateTime.now();
		this.accountStatus = AccountStatus.ACTIVE;
		this.otpVerified = false;
	}

	@PreUpdate
	protected void onUpdate() {
		this.updatedAt = LocalDateTime.now();
	}

	public MemberEntity(
			@NotNull(message = "Email cannot be Empty") @Email(message = "Invalid email format") String email,
			@NotNull(message = "Password cannot be Empty") String password) {
		super();
		this.email = email;
		this.password = password;
	}

	public MemberEntity(
			@NotNull(message = "Email cannot be Empty") @Email(message = "Invalid email format") String email,
			@NotNull(message = "Name cannot be Empty") String name,
			@NotNull(message = "Password cannot be Empty") String password,
			@NotNull(message = "DOB cannot be blank") LocalDate dob, Role role) {
		super();
		this.email = email;
		this.name = name;
		this.password = password;
		this.dob = dob;
		this.role = role;
	}

}
