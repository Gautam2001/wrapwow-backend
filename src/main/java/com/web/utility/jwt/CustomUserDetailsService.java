package com.web.utility.jwt;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.web.dao.MemberDao;
import com.web.entity.MemberEntity;

@Service
public class CustomUserDetailsService implements UserDetailsService {

	private final MemberDao userDao;

	public CustomUserDetailsService(MemberDao userDao) {
		super();
		this.userDao = userDao;
	}

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		MemberEntity user = userDao.getUserByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
		return new CustomUserDetails(user);
	}

}
