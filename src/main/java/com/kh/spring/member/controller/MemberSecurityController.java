package com.kh.spring.member.controller;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.kh.security.model.service.MemberSecurityService;
import com.kh.spring.member.model.dto.Member;
import com.kh.spring.member.model.service.MemberService;

import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/member")
@Slf4j
public class MemberSecurityController {
	
	@Autowired
	private MemberService memberService;
	
	@Autowired
	private MemberSecurityService memberSecurityService;
	
	@Autowired
	private BCryptPasswordEncoder bcryptPasswordEncoder;
	
	// @RequestMapping(path = "/member/memberEnroll.do", method = RequestMethod.GET)
	@GetMapping("/memberEnroll.do")
	public String memberEnroll() {
		return "member/memberEnroll";
	}
	
	/**
	 * $2a$10$2.AdYu08nfVhU89v8PyfsuF0kObCQvGCdJiR3I5p1dSQMY81FfD6O
	 * 
	 * - $2a$ 알고리즘타입
	 * - 10$ 옵션 (비용이 높을수록 속도가 오래걸리고, 메모리사용량이 많다)
	 * - 2.AdYu08nfVhU89v8Pyfsu 22자리 random salt
	 * - F0kObCQvGCdJiR3I5p1dSQMY81FfD6O 31자리 hashing + encoding처리
	 * 
	 */
	@PostMapping("/memberEnroll.do")
	public String memberEnroll(Member member, RedirectAttributes redirectAttr) {
		try {
			log.debug("member = {}", member);
			
			// 비밀번호 암호화
			String rawPassword = member.getPassword();
			String encodedPassword = bcryptPasswordEncoder.encode(rawPassword);
			member.setPassword(encodedPassword);
			log.debug("encodedPassword = {}", encodedPassword);
			
			int result = memberService.insertMember(member);
			redirectAttr.addFlashAttribute("msg", "회원 가입이 정상적으로 처리되었습니다.");
			return "redirect:/";
		} catch(Exception e) {
			log.error("회원등록 오류 : " + e.getMessage(), e);
			throw e;
		}
	}
	
	/**
	 * viewName이 null인 경우, 요청url을 기준으로 jsp위치를 추론한다.
	 * 
	 *  /member/memberLogin.do
	 *  -> member/memberLogin 
	 */
	@GetMapping("/memberLogin.do")
	public void memberLogin() {
		
	}
	
	@PostMapping("/memberLoginSuccess.do")
	public String memberLoginSuccess(HttpSession session) {
		log.debug("memberLoginSuccess.do 호출!");
		// 로그인 후처리
		String location = "/";
		
		// security가 관리하는 다음 리다이렉트 url
		SavedRequest savedRequest = (SavedRequest) session.getAttribute("SPRING_SECURITY_SAVED_REQUEST");
		if(savedRequest != null) {
			location = savedRequest.getRedirectUrl();
		}
		log.debug("location = {}", location);
		return "redirect:" + location;
	}
	
	/**
	 * security가 관리하는 인증된 사용자 정보
	 * 
	 * 
	 * @param mav
	 * @return
	 */
	// @GetMapping("/memberDetail.do")
	public ModelAndView memberDetail(ModelAndView mav) {
		// SecurityContextHolder로부터 직접 가져오는 방법
		SecurityContext securityContext = SecurityContextHolder.getContext();
		Authentication authentication =  securityContext.getAuthentication();
		Object principal = authentication.getPrincipal();
		Object credentials = authentication.getCredentials();
		Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
		log.debug("principal = {}", principal);
		log.debug("credentials = {}", credentials);
		log.debug("authorities = {}", authorities);
		
		mav.setViewName("member/memberDetail");
		return mav;
	}
	
	@GetMapping("/memberDetail.do")
	public ModelAndView memberDetail(Authentication authentication, ModelAndView mav) {
		
		Object principal = authentication.getPrincipal();
		Object credentials = authentication.getCredentials();
		Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
		log.debug("principal = {}", principal);
		log.debug("credentials = {}", credentials);
		log.debug("authorities = {}", authorities);
		
		mav.setViewName("member/memberDetail");
		return mav;
	}
	
	/**
	 * ResponseEntity
	 * - @ResponseBody 기능
	 * - 핸들러에서 응답코드, 응답헤더, 메세지바디를 자유롭게 제어할 수 있도록 도와주는 객체
	 * - 메세지바디에 작성할 자바객체는 messageConverter빈에 의해 json으로 처리됨.
	 */
	@PostMapping("/checkIdDuplicate.do")
	public ResponseEntity<?> checkIdDuplicate3(@RequestParam String memberId) {
		Member member = memberService.selectOneMember(memberId);
		boolean available = member == null;
		
		Map<String, Object> map = new HashMap<>();
		map.put("memberId", memberId);
		map.put("available", available);
		
		return ResponseEntity.status(HttpStatus.OK).body(map);
	}
	
	@PostMapping("/memberUpdate.do")
	public String memberUpdate(@ModelAttribute Member member, RedirectAttributes redirectAttr, Model model) {
		log.debug("member = {}", member);
		// 1. db row 수정
		int result = memberService.updateMember(member);
		
		UserDetails updatedMember = memberSecurityService.loadUserByUsername(member.getMemberId());
		// 2. authentication 수정
		Authentication newAuthentication = new UsernamePasswordAuthenticationToken(
				updatedMember,
				updatedMember.getPassword(),
				updatedMember.getAuthorities()
				);
		
		SecurityContextHolder.getContext().setAuthentication(newAuthentication);
		
		redirectAttr.addFlashAttribute("msg", "회원정보를 성공적으로 수정했습니다.");
		return "redirect:/member/memberDetail.do";
	}
}





