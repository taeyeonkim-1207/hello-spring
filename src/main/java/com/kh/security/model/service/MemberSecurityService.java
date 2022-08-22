package com.kh.security.model.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.kh.security.model.dao.MemberSecurityDao;
import com.kh.spring.member.model.dto.Member;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MemberSecurityService implements UserDetailsService {

	@Autowired
	MemberSecurityDao memberSecurityDao;
	/**
	 * username으로 해당 회원 조회(member, authority)
	 * - 조회된 회원이 없는 경우 UsernameNotFoundException 예외 던지기
	 * */
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Member member = memberSecurityDao.loadUserByUsername(username);
		
		if(member == null) 
			throw new UsernameNotFoundException(username);
		
		log.info("member = {}", member);
		return member;
	}

}
