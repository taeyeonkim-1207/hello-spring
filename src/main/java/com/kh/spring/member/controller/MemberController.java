package com.kh.spring.member.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.kh.spring.member.model.dto.Member;
import com.kh.spring.member.model.service.MemberService;

import lombok.extern.slf4j.Slf4j;

// 클래스레벨
//@Controller
@RequestMapping("/member")
@Slf4j
@SessionAttributes({"loginMember"})

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
	
	/**
	 * viewName이 null인 경우, 요청url을 기준으로 jsp의 위치를 추론한다.
	 * 
	 * /member/memberLogin.do
	 * 	-> member/memberLogin
	 * 
	 * */
	@GetMapping("/memberLogin.do")
	public void memberLogin() {
		
	}
	
	@PostMapping("/memberLogin.do")
	public String memberLogin(
			@RequestParam String memberId, 
			@RequestParam String password, 
			Model model,
			RedirectAttributes redirectAttr) {
		
//		1. memberId로 회원조회
		Member member = memberService.selectOneMember(memberId);
		log.debug("member = {}", member);
		
		String location = "/";
//		2. member가 null이 아니면서 비밀번호가 일치하면 로그인 성공
		if(member != null && bcryptPasswordEncoder.matches(password, member.getPassword())) {
//			model을 통해 session scope에 속성 저장법: 클래스레벨에 @SessionAttributes 등록
			model.addAttribute("loginMember", member);
		}
//		로그인 실패
		else {
			redirectAttr.addFlashAttribute("msg", "아이디 또는 비밀번호가 일치하지 않습니다");
			location = "/member/memberLogin.do";
		}
		return "redirect:" + location;
		
	}
	
	/**
	 * @SessionAttributes로 세션 관리를 한다면, SessionStatus#setComplete으로 만료처리해야 한다.
	 * 
	 * */
	@GetMapping("/memberLogout.do")
	public String memberLogout(SessionStatus sessionStatus) {
//		session내에 저장된 model이 있는지 체크 
		if(!sessionStatus.isComplete()) {
			sessionStatus.setComplete();
		}
		
		return "redirect:/";
	}
	
	@GetMapping("/memberDetail.do")
	public ModelAndView memberDetail(ModelAndView mav, @SessionAttribute Member loginMember) {
		log.debug("loginMember = {}", loginMember);
		
		mav.setViewName("member/memberDetail");
		return mav;
	}
	
	@PostMapping("/memberUpdate.do")
	public String memberUpdate(@ModelAttribute Member member, RedirectAttributes redirectAttr, Model model) {
		log.debug("member = {}", member);
		int result = memberService.updateMember(member);
		model.addAttribute("loginMember", memberService.selectOneMember(member.getMemberId()));
		redirectAttr.addFlashAttribute("msg", "회원정보를 성공적으로 수정했습니다.");
		return "redirect:/member/memberDetail.do";
	}
	
	/**
	 * jsonView 빈을 통해 ajax 응답하기
	 * - model에 담긴 속성을 json 문자열로 변환해서 응답메세지 body에 출력한다.
	 * - BeanNameViewResolver를 통해서 viewName에 해당하는 빈을 찾는다.
	 * 
	 *  @return
	 * 
	 * */
	
//	@GetMapping("/checkIdDuplicate.do")
	public String checkIdDuplicate1(@RequestParam String memberId, Model model) {
		Member member = memberService.selectOneMember(memberId);
//		사용가능하다면 null반환(없다면 true,사용가능하다)
		boolean available = member == null;
		
//		사용자에게 전달할 데이터를 model 속성에 저장
		model.addAttribute("memberId", memberId);
		model.addAttribute("available", available);
		
		return "jsonView";
	}
	
	
	/**
	 * 
	 * - MessageConverter에 의해 리턴객체를 json으로 변환.
	 * 
	 * @ResponseBody
	 * - 핸들러의 리턴객체를 응답메세지 body에 작성한다.
	 * 
	 * */
	
//	@GetMapping("/checkIdDuplicate.do")
	@ResponseBody
	public Map<String, Object> checkIdDuplicate2(@RequestParam String memberId) {
		Member member = memberService.selectOneMember(memberId);
		boolean available = member == null;
				
		Map<String, Object> map = new HashMap<>();
		map.put("memberId", memberId);
		map.put("available", available);
		return map;
	}
	
	/**
	 * ResponseEntity
	 * - @ResponseBody 기능
	 * - 핸들러에서 응답코드, 응답헤더, 메세지바디를 자유롭게 제어할 수 있도록 도와주는 객체
	 * - 메세지 바디에 작성할 자바객체는 messageConverter빈에 의해 json으로 처리됨.
	 * 
	 * */
	
	@GetMapping("/checkIdDuplicate.do")
	@ResponseBody
	public ResponseEntity<?> checkIdDuplicate3(@RequestParam String memberId) {
		Member member = memberService.selectOneMember(memberId);
		boolean available = member == null;
		
		Map<String, Object> map = new HashMap<>();
		map.put("memberId", memberId);
		map.put("available", available);
		return ResponseEntity.status(HttpStatus.OK).body(map);
	}
	
}

