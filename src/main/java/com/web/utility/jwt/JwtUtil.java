package com.web.utility.jwt;

import java.security.PublicKey;

import org.springframework.stereotype.Component;

import com.web.utility.CommonUtils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

@Component
public class JwtUtil {

	private final PublicKey publicKey;

	public JwtUtil() throws Exception {
		super();
		this.publicKey = RsaKeyUtil.loadPublicKey();
	}

	public Claims validateToken(String token) {
		CommonUtils.logMethodEntry(this);
		Claims claims = Jwts.parserBuilder().setSigningKey(publicKey).build().parseClaimsJws(token).getBody();

		return claims;
	}

}
