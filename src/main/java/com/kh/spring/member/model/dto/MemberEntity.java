package com.kh.spring.member.model.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import com.kh.spring.demo.model.dto.Gender;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

// dto는 bean으로 만들어 사용하지 않을 것, POJO스타일
// Getter, Setter, RequiredArgsConstructor, ToString, EqualsAndHashCode, lombok.Value
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberEntity {
	@NonNull
	protected String memberId;
	@NonNull
	protected String password;
	@NonNull
	protected String name;
	protected Gender gender;
	@DateTimeFormat(pattern = "yyyy-MM-dd") //LocalDate에서 필요한 처리.
	protected LocalDate birthday;
	protected String email;
	@NonNull
	protected String phone;
	protected String address;
	protected String[] hobby;
	protected LocalDateTime createdAt;
	protected LocalDateTime updatedAt;
	protected boolean enabled;
}
