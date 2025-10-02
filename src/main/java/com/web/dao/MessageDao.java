//package com.web.dao;
//
//import java.util.List;
//
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//
//import com.web.entity.MessageEntity;
//
//public interface MessageDao extends JpaRepository<MessageEntity, Long> {
//
//	@Query("SELECT m FROM MessageEntity m WHERE ((m.sender = :username AND m.receiver = :contactUsername) "
//			+ "OR (m.sender = :contactUsername AND m.receiver = :username)) AND (:cursorId IS NULL "
//			+ "OR m.messageId < :cursorId) ORDER BY m.sentAt DESC")
//	List<MessageEntity> getConversationBetweenUsers(@Param("username") String username,
//			@Param("contactUsername") String contactUsername, @Param("cursorId") Long cursorId, Pageable pageable);
//
//}
