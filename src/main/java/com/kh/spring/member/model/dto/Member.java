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
public class Member {
	@NonNull
	private String memberId;
	@NonNull
	private String password;
	@NonNull
	private String name;
	private Gender gender;
	@DateTimeFormat(pattern = "yyyy-MM-dd") //LocalDate에서 필요한 처리.
	private LocalDate birthday;
	private String email;
	@NonNull
	private String phone;
	private String address;
	private String[] hobby;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private boolean enabled;
}
