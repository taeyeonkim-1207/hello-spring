package com.kh.spring.board.model.service;

import java.util.List;
import java.util.Map;

import com.kh.spring.board.model.dto.Attachment;
import com.kh.spring.board.model.dto.Board;

public interface BoardService {

	List<Board> selectBoardList(Map<String, Integer> param);

	int getTotalContent();

	int insertBoard(Board board);
	
	int insertAttachment(Attachment attachment);

	Board selectOneBoard(int no);

	Attachment selectOneAttachment(int no);

	int deleteAttachment(int attachNo);

	int updateBoard(Board board);

	
}
