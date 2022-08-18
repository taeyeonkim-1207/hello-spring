package com.kh.spring.board.controller;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.kh.spring.board.common.HelloSpringUtils;
import com.kh.spring.board.model.dto.Attachment;
import com.kh.spring.board.model.dto.Board;
import com.kh.spring.board.model.service.BoardService;

import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
@RequestMapping("/board")
public class BoardController {

	@Autowired
	BoardService boardService;

//  생명주기가 가장 긴 scope 객체 ServletContext : 스프링빈을 관리하는 servlet-context와는 무관하다
	@Autowired
	ServletContext application;

	@Autowired
	ResourceLoader resourceLoader;
	
	@GetMapping("/boardList.do")
	public void boardList(@RequestParam(defaultValue = "1") int cPage, Model model, HttpServletRequest request) {
//		1. content영역
		Map<String, Integer> param = new HashMap<>();
		int limit = 10;
		param.put("cPage", cPage);
		param.put("limit", limit); // 한 페이지에 10건씩
		List<Board> list = boardService.selectBoardList(param);
		log.debug("list = {}", list);
		model.addAttribute("list", list);

//		2.pagebar영역
		int totalContent = boardService.getTotalContent(); // 전체 게시글 수
		log.debug("totalContent = {}", totalContent);
		String url = request.getRequestURI(); // /spring/board/boardList.do
		String pagebar = HelloSpringUtils.getPagebar(cPage, limit, totalContent, url);
		model.addAttribute("pagebar", pagebar);
	}

	@GetMapping("/boardForm.do")
	public void boardForm() {

	}

	@PostMapping("/boardEnroll.do")
	public String boardEnroll(
			Board board, 
			@RequestParam(name = "upFile") List<MultipartFile> upFileList, 
			RedirectAttributes redirectAttr) 
					throws IllegalStateException, IOException {

		for (MultipartFile upFile : upFileList) {
//			log.debug("upFile = {}", upFile);
//			log.debug("upFile#name = {}", upFile.getName()); // upFile
//			log.debug("upFile#name = {}", upFile.getOriginalFilename());
//			log.debug("upFile#size = {}", upFile.getSize());

			if (!upFile.isEmpty()) {

//			a. 서버컴퓨터에 저장
				String saveDirectory = application.getRealPath("/resources/upload/board");
				String renamedFilename = HelloSpringUtils.getRenamedFilename(upFile.getOriginalFilename()); // 20220816_19546165_123txt
				File destFile = new File(saveDirectory, renamedFilename);
				upFile.transferTo(destFile); // 해당경로에 파일 저장

//			b. DB저장을 위해 Attachment객체 생성
				Attachment attach = new Attachment(upFile.getOriginalFilename(), renamedFilename);
				board.add(attach);
			}
		}

		log.debug("board = {}", board);

		// db저장
		int result = boardService.insertBoard(board);

		redirectAttr.addFlashAttribute("msg", "게시글을 성공적으로 저장했습니다.");

		return "redirect:/board/boardList.do";
	}
	
	@GetMapping("/boardDetail.do")
	public void boardDetail(@RequestParam int no, Model model) {
		// Board조회 - Attachment
		Board board = boardService.selectOneBoard(no);
		log.debug("board = {}", board);
		model.addAttribute("board", board);
	}
	
	/**
	 * 
	 * Resource
	 * 다음 구현체들의 추상화레이어를 제공한다.
	 * 
	 * - 웹상 자원 UrlResource
	 * - classpath 자원 ClassPathResource
	 * - 서버컴퓨터 자원 FileSystemResource
	 * - ServletContext (web root) 자원 ServletContextResource
	 * - 입출력 자원 InputStreamResource
	 * - 이진데이터 자원 ByteArrayResource
	 * @throws IOException 
	 * 
	 * @ResponseBody - 핸들러의 반환된 자바객체를 응답메세지 바디에 직접 출력하는 경우
	 * 
	 * 
	 */
	@GetMapping(path = "/fileDownload.do", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	@ResponseBody
	public Resource fileDownload(@RequestParam int no, HttpServletResponse response) throws IOException {
		Attachment attach = boardService.selectOneAttachment(no);
		log.debug("attach = {}", attach);
		
		String saveDirectory = application.getRealPath("/resources/upload/board");
		File downFile = new File(saveDirectory, attach.getRenamedFilename());
		String location = "file:" + downFile; // File#toString은 파일의 절대경로 반환
		Resource resource = resourceLoader.getResource(location);
		log.debug("resource = {}", resource);
		log.debug("resource#file = {}", resource.getFile());
		
		// 응답헤더 작성
		response.setContentType("application/octet-stream; charset=utf-8");
		String filename = new String(attach.getOriginalFilename().getBytes("utf-8"), "iso-8859-1");
		response.addHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"");
		
		return resource;
	}
	
	@GetMapping("/boardUpdate.do")
	public void boardUpdate(@RequestParam int no, Model model) {
		Board board = boardService.selectOneBoard(no);
		model.addAttribute("board", board);
	}
	
	/**
	 * - 게시글 수정
	 * - 첨부파일 삭제(파일삭제 && attachment row 제거)
	 * - 첨부파일 추가
	 * 
	 * @return
	 */
	
	@PostMapping("/boardUpdate.do")
	public String boardUpdate() {
		return "redirect:/board/boardDetail.do";
	}
	
	
}
