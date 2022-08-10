package com.kh.spring.member.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.kh.spring.member.model.dto.Member;
import com.kh.spring.member.model.service.MemberService;

import lombok.extern.slf4j.Slf4j;

// 클래스레벨
@Controller
@RequestMapping("/member")
@Slf4j
public class MemberController {

//	의존주입
	@Autowired
	private MemberService memberService;

	@Autowired
	private BCryptPasswordEncoder bcryptPasswordEncoder; 

//	회원가입 등록폼 요청
//	@RequestMapping(path = "/memberEnroll.do", method = RequestMethod.GET)
//	GET방식만 처리하는 Mapping
	@GetMapping("/memberEnroll.do")
	public String memberEnroll() {
		return "member/memberEnroll";
	}

/**
 * $2a$10$1xd28yVpSGNPQ1IlD/vpR.IprxkggLwelxKWL13s2ec9a63i2LjyS
 * 
 * - $2a$ 알고리즘 타입
 * - 10$ 옵션값(비용이 높을수록 속도가 오래걸리고, 메모리 사용량이 많다)
 * - 1xd28yVpSGNPQ1IlD/vpR. 22자리 randow salt
 * - IprxkggLwelxKWL13s2ec9a63i2LjyS 31자리 hashing + encoding처리
 *
 * */
//	DML redirect
	@PostMapping("/memberEnroll.do")
	public String memberEnroll(Member member, RedirectAttributes redirectAttr) {
		try {
			log.debug("member = {}", member);
			
//			비밀번호 암호화
			String rawPassword = member.getPassword();
			String encodedPassword = bcryptPasswordEncoder.encode(rawPassword);
			member.setPassword(encodedPassword);
			log.debug("encodedPassword = {}", encodedPassword);
			
			int result = memberService.insertMember(member);
			redirectAttr.addFlashAttribute("msg", "회원 가입이 정상적으로 처리되었습니다");
			return "redirect:/";
		} catch (Exception e) {
			log.error("회원등록 오류: " + e.getMessage(), e);
//			spring container에게 알림
			throw e; 
		}
	}
}
