package com.kh.spring.builder.pattern;

import java.time.LocalDate;

/**
 * Builder Pattern
 * - GoF의 디자인패턴 - 생성패턴
 * - 필드가 여러개일 경우, 필드값을 개별적으로 등록후 객체를 생성하는 방법
 * 
 *
 */
public class User {
	private long code; // PK
	private String username; // 유저아이디
	private String password;
	private String name;
	private LocalDate birthday;
	private String phone;
	private boolean married;
	
	public User() {
		super();
		// TODO Auto-generated constructor stub
	}

	public User(long code, String username, String password, String name, LocalDate birthday, String phone,
			boolean married) {
		super();
		this.code = code;
		this.username = username;
		this.password = password;
		this.name = name;
		this.birthday = birthday;
		this.phone = phone;
		this.married = married;
	}
	
	public static class Builder {
		private long code; // PK
		private String username; // 유저아이디
		private String password;
		private String name;
		private LocalDate birthday;
		private String phone;
		private boolean married;
		
		public Builder code(long code) {
			this.code = code;
			return this; // Builder객체 자신을 반환
		}
		
		public Builder username(String username) {
			this.username = username;
			return this; 
		}
		public Builder password(String password) {
			this.password = password;
			return this; 
		}
		public Builder name(String name) {
			this.name = name;
			return this; 
		}
		public Builder birthday(LocalDate birthday) {
			this.birthday = birthday;
			return this; 
		}
		public Builder phone(String phone) {
			this.phone = phone;
			return this; 
		}
		public Builder married(boolean married) {
			this.married = married;
			return this; 
		}
		
		public User build() {
			return new User(code, username, password, name, birthday, phone, married);
		}
	}
	
	
	
	public static Builder builder() {
		return new Builder();
	}

	@Override
	public String toString() {
		return "User [code=" + code + ", username=" + username + ", password=" + password + ", name=" + name
				+ ", birthday=" + birthday + ", phone=" + phone + ", married=" + married + "]";
	}
	
}
