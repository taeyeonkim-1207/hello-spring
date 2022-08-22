package com.kh.spring.builder.pattern;

import java.time.LocalDate;

public class UserMain {

	public static void main(String[] args) {
		User u1 = User.builder() // User.Builder 객체
				.code(1L)
				.username("honggd")
				.password("1234")
				.name("홍길동")
				.birthday(LocalDate.of(1999, 9, 9))
				.phone("01012345678")
				.married(false)
				.build(); // User객체 반환

		System.out.println(u1);
		
		User u2 = User.builder().username("honggd").password("1234").build();
		
		System.out.println(u2);
		
		Client client = Client.builder()
				.code(1L)
				.username("honggd")
				.password("1234")
				.name("홍길동")
				.birthday(LocalDate.of(1999, 9, 9))
				.phone("01012345678")
				.married(false)
				.build(); // User객체 반환

		System.out.println(client);
	}

}
