package com.web.dao;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.web.DTO.GetUsersDTO;
import com.web.entity.MemberEntity;
import com.web.entity.MemberEntity.AccountStatus;
import com.web.entity.MemberEntity.Role;

import jakarta.transaction.Transactional;

@Repository
public interface MemberDao extends JpaRepository<MemberEntity, Long> {

	Optional<MemberEntity> getUserByEmail(String email);

	Optional<MemberEntity> getUserByEmailAndPassword(String email, String password);

	Optional<MemberEntity> getUserByEmailAndOtp(String email, String otp);

	Optional<MemberEntity> getUserByEmailAndOtpVerified(String email, boolean OtpVerified);

	List<MemberEntity> findAllByUserIdInAndRole(List<Long> ids, Role user);

	@Query("SELECT new com.web.DTO.GetUsersDTO(m.userId, m.name, m.email, m.dob, m.accountStatus) FROM MemberEntity m WHERE m.role = :role")
	List<GetUsersDTO> findAllByRole(@Param("role") Role role);

	@Modifying
	@Transactional
	@Query("UPDATE MemberEntity m SET m.password = :password, m.otpVerified = false WHERE m.email = :email")
	int updatePasswordOtpVerifiedByEmail(@Param("password") String password, @Param("email") String email);

	@Modifying
	@Transactional
	@Query("UPDATE MemberEntity m SET m.password = :password WHERE m.email = :email")
	int updatePasswordByEmail(@Param("password") String password, @Param("email") String email);

	@Modifying
	@Transactional
	@Query("UPDATE MemberEntity m SET m.otp = :otp, m.otpGeneratedAt = :otpGeneratedAt, m.otpVerified = :otpVerified WHERE m.email = :email")
	int updateOtpAndotpGeneratedAtByEmail(@Param("otp") String otp, @Param("otpGeneratedAt") LocalDateTime now,
			@Param("otpVerified") boolean otpVerified, @Param("email") String email);

	@Modifying
	@Transactional
	@Query("UPDATE MemberEntity m SET m.otpVerified = :otpVerified WHERE m.email = :email AND m.otp = :otp")
	int updateOtpVerifiedByEmailAndOtp(@Param("otpVerified") boolean otpVerified, @Param("email") String email,
			@Param("otp") String otp);

	@Modifying
	@Transactional
	@Query("UPDATE MemberEntity m SET m.accountStatus = CASE WHEN m.accountStatus = :active THEN :inactive ELSE :active END WHERE m.userId IN :ids")
	int updateStatusForUserIds(@Param("ids") List<Long> ids, @Param("active") AccountStatus active,
			@Param("inactive") AccountStatus inactive);

	@Query("SELECT m.role FROM MemberEntity m where m.accountStatus = 'INACTIVE'")
	List<Role> getInactiveMembers();

	@Query("SELECT MONTH(m.createdAt) as month, COUNT(m) as userCount "
			+ "FROM MemberEntity m WHERE YEAR(m.createdAt) = :year AND m.role = 'USER' GROUP BY MONTH(m.createdAt)")
	List<Map<String, Object>> getMonthlyUserOnboarded(@Param("year") int year);

}
