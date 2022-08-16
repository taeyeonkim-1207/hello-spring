package com.kh.spring.todo.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.kh.spring.todo.model.dto.Todo;
import com.kh.spring.todo.model.service.TodoService;
import com.kh.spring.todo.model.service.TodoServiceImpl;

import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/todo")
@Slf4j
public class TodoController {

	@Autowired
	private TodoService todoService;
	
	/**
	 * AOP에서 사용하는 Proxy객체
	 * - 인터페이스 구현 객체 : jdk 동적 proxy객체 class com.sun.proxy.$Proxy180
	 * - 인터페이스 구현하지 않은 객체 : cglib라이브러리에서 생성한 프록시객체
	 * 
	 * 
	 */
	
	@GetMapping("/todoList.do")
	public ModelAndView todoList(ModelAndView mav) {
			//new TodoServiceImpl();
			log.debug("todoService = {}", new TodoServiceImpl().getClass()); //class com.kh.spring.todo.model.service.TodoServiceImpl
			log.debug("todoService = {}", todoService.getClass()); // class com.sun.proxy.$Proxy144
			
			List<Todo> list = todoService.selectTodoList();
			mav.addObject("list", list);
			
			return mav;
		}
		

	@PostMapping("/insertTodo.do")
	public String insertTodo(Todo todo, RedirectAttributes redirectAttr) {
			int result = todoService.insertTodo(todo);
			redirectAttr.addFlashAttribute("msg", "할일이 추가되었습니다");
			
			return "redirect:/todo/todoList.do";
		}
	
	
	@PostMapping("/updateTodo.do")
	public String updateTodo(@RequestParam int no, @RequestParam boolean isCompleted) {
		Map<String, Object> param = new HashMap<>();
		//alt+shift+z
		
			param.put("no", no);
			param.put("isCompleted", isCompleted);
			
			int result = todoService.updateTodo(param);
			return "redirect:/todo/todoList.do";
		}
}
