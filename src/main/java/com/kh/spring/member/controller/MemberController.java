package com.kh.spring.member.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
	
//	회원가입 등록폼 요청
//	@RequestMapping(path = "/memberEnroll.do", method = RequestMethod.GET)
//	GET방식만 처리하는 Mapping
	@GetMapping("/memberEnroll.do")
	public String memberEnroll() {
		return "member/memberEnroll";
	}
}
