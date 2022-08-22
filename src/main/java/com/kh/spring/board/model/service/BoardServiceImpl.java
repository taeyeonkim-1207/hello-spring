package com.kh.spring.board.model.service;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.spring.board.model.dao.BoardDao;
import com.kh.spring.board.model.dto.Attachment;
import com.kh.spring.board.model.dto.Board;

import lombok.extern.slf4j.Slf4j;

@Transactional(rollbackFor = Exception.class)
@Service
@Slf4j
public class BoardServiceImpl implements BoardService {

	@Autowired
	BoardDao boardDao;
	
	@Override
	public List<Board> selectBoardList(Map<String, Integer> param) {
//		mybatis에서 제공하는 페이징처리객체 RowBounds
//		offset limit
		int limit = param.get("limit");
		int offset = (param.get("cPage")-1) * limit; //몇 개를 건너 뛰는지
		RowBounds rowBounds = new RowBounds(offset, limit);
		
		return boardDao.selectBoardList(rowBounds);
	}
	
	@Override
	public int getTotalContent() {
		return boardDao.getTotalContent();
	}
	 
	@Override
	public int insertBoard(Board board) {
//		insert board
		int result = boardDao.insertBoard(board);
		log.debug("board#no = {}", board.getNo());
		
//		insert attachment * n
		List<Attachment> attachments = board.getAttachments(); 
		if(!attachments.isEmpty()) {
			for(Attachment attach : attachments) {
				attach.setBoardNo(board.getNo());
				result = insertAttachment(attach);
			}
		}
		return result;
	 }
	
	@Override
	public int insertAttachment(Attachment attach) {
		return boardDao.insertAttachment(attach);
	}
	
	@Override
	public Board selectOneBoard(int no) {
		// Board 조회
//		Board board = boardDao.selectOneBoard(no);

		// List<Attachment> 조회
//		List<Attachment> attachments = boardDao.selectAttachmentListByBoardNo(no);
//		board.setAttachments(attachments);
		return boardDao.selectOneBoardCollection(no);
				
		
	}
	
	@Override
	public Attachment selectOneAttachment(int no) {
		return boardDao.selectOneAttachment(no);
	}
	
	@Override
	public int deleteAttachment(int attachNo) {
		return boardDao.deleteAttachment(attachNo);
	}
	
	@Override
	public int updateBoard(Board board) {
//		update board
		int result = boardDao.updateBoard(board);
		
//		insert attachment
		List<Attachment> attachments = board.getAttachments();
		if(attachments != null && attachments.isEmpty()) {
			for(Attachment attach : attachments) {
				result = insertAttachment(attach);
			}
		}
		return result;
	}
}
