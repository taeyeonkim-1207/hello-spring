package com.kh.spring.board.model.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString(callSuper = true)
public class Board extends BoardEntity {
	private int attachCount;
	private List<Attachment> attachments = new ArrayList<>();

	public Board(int no, String title, String memberId, String content, int readCount, LocalDateTime createdAt,
			LocalDateTime updatedAt, int attachCount) {
		super(no, title, memberId, content, readCount, createdAt, updatedAt);
		this.attachCount = attachCount;
	}
	
	public void add(Attachment attach) {
		this.attachments.add(attach);
	}
	
	
}
