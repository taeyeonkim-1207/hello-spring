package com.kh.spring.demo.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kh.spring.demo.model.dto.Dev;
import com.kh.spring.demo.model.service.DemoService;

import lombok.extern.slf4j.Slf4j;

/**
 * GET /dev
 * GET /dev/1
 * GET /dev/email/honggd@naver.com
 * GET /dev/lang/C
 * GET /dev/lang/Java
 * 
 * 
 * POST /dev
 * 
 * PUT /dev
 * 
 * DELETE /dev/1
 * 
 * @RestController
 * - @Controller + 모든 메소드에 @ResponseBody 처리가능
 *
 */
@RestController 
@Slf4j
@RequestMapping("/dev")
public class DevRestController {
	
	@Autowired
	DemoService demoService;
	
	@GetMapping
	public List<Dev> dev(){
		return demoService.selectDevList();
	}
	
	@GetMapping("/{no}")
	public ResponseEntity<?> dev(@PathVariable int no) {
		log.debug("no = {}", no);
		Dev dev = demoService.selectOneDev(no);
		if(dev != null)
			return ResponseEntity.ok(dev);
		else 
			return ResponseEntity.notFound().build(); // builder패턴
	}
	
	/**
	 * 전체 조회후 handler에서 필터링처리할 것.
	 * - 해당언어 가능한 개발자가 없다면 404 처리할것. 
	 * 
	 * @param lang
	 * @return
	 */
	@GetMapping("/lang/{lang}")
	public ResponseEntity<?> dev(@PathVariable String lang){
		log.debug("lang = {}", lang);
		
		List<Dev> devList = demoService.selectDevList();
		List<Dev> resultList = new ArrayList<>();
		for(Dev dev : devList) {
			List<String> langList = Arrays.asList(dev.getLang());
			if(containsIgnoreCase(langList, lang)) {
				resultList.add(dev);
			}
		}
		if(resultList.isEmpty())
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		
		return ResponseEntity.ok(resultList);
	}
	
	private boolean containsIgnoreCase(List<String> strList, String str) {
		for(String s : strList) {
			if(s != null && s.equalsIgnoreCase(str))
				return true;
		}
		return false;
	}
	
	/**
	 * @RequestBody
	 * - 요청메세지의 json데이터를 MessageConverter에 의해 java객체로 변환 처리후 핸들러에 전달
	 * 
	 */
	@PostMapping
	public ResponseEntity<?> dev(@RequestBody Dev dev){
		log.debug("dev = {}", dev);
		int result = demoService.insertDev(dev);
		Dev savedDev = demoService.selectOneDev(dev.getNo());
		return ResponseEntity.status(HttpStatus.CREATED).body(savedDev);
	}
	
	@PutMapping
	public ResponseEntity<?> updateDev(@RequestBody Dev dev){
		log.debug("dev = {}", dev);
		int result = demoService.updateDev(dev);
		Dev updatedDev = demoService.selectOneDev(dev.getNo());
		return ResponseEntity.ok().body(updatedDev);
	}
	
	@PatchMapping
	public ResponseEntity<?> updatePartialDev(@RequestBody Dev dev){
		log.debug("dev = {}", dev);
		int result = demoService.updatePartialDev(dev);
		Dev updatedDev = demoService.selectOneDev(dev.getNo());
		return ResponseEntity.ok().body(updatedDev);
	}
	
	@DeleteMapping("/{no}")
	public ResponseEntity<?> deleteDev(@PathVariable int no){
		int result = demoService.deleteDev(no);
		Map<String, Object> map = new HashMap<>();
		map.put("result", "success");
		return ResponseEntity.ok(map);
	}
	
}
