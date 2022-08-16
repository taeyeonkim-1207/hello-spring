package com.kh.spring.proxy;

public class ProxyMain {

//	의존주입
	FooService fooService = new FooProxy(new FooServiceImpl(), new Aspect());
			
			
	public static void main(String[] args) {
		new ProxyMain().test();
	}


	private void test() {
		String name = fooService.getName();
		System.out.println(name);
	}

}
class FooProxy implements FooService{

	FooService fooService;
	Aspect aspect;
	
	public FooProxy(FooService fooService, Aspect aspect) {
		this.fooService = fooService;
		this.aspect = aspect;
	}
	
	@Override
	public String getName() {
		// before
		aspect.beforeAdvice();
		
		String name = fooService.getName(); // 주기능 joinPoint 실행
		
		//after
		return aspect.afterAdvice(name);
	}
	
}

class Aspect{
	public void beforeAdvice() {
		System.out.println("before!!");
	}
	public String afterAdvice(String str) {
		System.out.println("after!!");
		return str.toUpperCase();
	}
}
interface FooService {
	String getName();
	
}
class FooServiceImpl implements FooService{
	@Override
	public String getName() {
		return "abcde";
	}
}
