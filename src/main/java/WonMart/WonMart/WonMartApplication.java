package WonMart.WonMart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WonMartApplication {

	/*
	 build.gradle 정보
	 - spring boot 버전 : 2.2.2가 아닌 2.1.9 사용
	 - apache httpcore httpcore httpclient 추가
	 - apache commons 추가
	 - jackson core 추가
	 - querydsl 추가 (4, 44, 51-68)
	 - .gitignore 관련 https://emflant.tistory.com/127

	 주석 작성 요령
	 - 엔티티끼리 중복되는 함수에 대한 설명 주석은 모든 엔티티에 작성하지 않고 Member관련 클래스에 작성
	*/
	public static void main(String[] args) {
		SpringApplication.run(WonMartApplication.class, args);
	}

}
