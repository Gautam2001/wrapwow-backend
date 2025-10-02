//package com.web.entity;
//
//import java.time.Instant;
//
//import jakarta.persistence.Column;
//import jakarta.persistence.Entity;
//import jakarta.persistence.GeneratedValue;
//import jakarta.persistence.GenerationType;
//import jakarta.persistence.Id;
//import jakarta.persistence.Table;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//@Data
//@NoArgsConstructor
//@Entity
//@Table(name = "messages")
//public class MessageEntity {
//
//	@Id
//	@GeneratedValue(strategy = GenerationType.IDENTITY)
//	private Long messageId;
//
//	@Column(nullable = false)
//	private String sender;
//
//	@Column(nullable = false)
//	private String receiver;
//
//	@Column(nullable = false, columnDefinition = "TEXT")
//	private String content;
//
//	@Column(name = "sent_at", nullable = false)
//	private Instant sentAt;
//
//	public MessageEntity(String sender, String receiver, String content) {
//		super();
//		this.sender = sender;
//		this.receiver = receiver;
//		this.content = content;
//		this.sentAt = Instant.now();
//	}
//}
