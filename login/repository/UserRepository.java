package com.javappa.startappa.startappalight.login.repository;

import java.util.Date;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.javappa.startappa.startappalight.application.domain.User;

@Repository("LoginUserRepository")
public interface UserRepository extends JpaRepository<User, Long> {

	User findByEmail(String email);

	User findByToken(String token);

	String UPDATE_EXPIRED_TOKENS = "UPDATE User u SET u.token = null WHERE u.token is NOT NULL " +
																						"AND u.tokenTime < :tokenTime";

	@Modifying
	@Transactional
	@Query(UPDATE_EXPIRED_TOKENS)
	int updateExpiredTokens(@Param("tokenTime") Date tokenTime);
}
