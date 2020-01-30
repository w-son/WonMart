# WonMart

중고거래장터 웹 어플리케이션

## Models
* 각 모델은 고유의 id값(Long)을 지닌다

### __Post__ 
* Member와 ManyToOne 관계

속성 | 의미 
:---:|:---:
`title` | 게시글 제목
`price` | 상품의 가격
`body` | 상품설명 및 사진
`postTime` | 게시글을 올린 시간

### __Member__ 
* Post와 OneToMany 관계
* Letter과 OneToMany 관계
* 카카오 로그인 api를 활용

속성 | 의미 
:---:|:---:
`nickName` | 중복 불가능한 닉네임
`address` | 사는 지역
`phoneNum` | 연락처
`email` | 이메일

### __Letter__
* Member과 ManyToOne 관계

속성 | 의미 
:---:|:---:
`sender` | 보내는 이
`receiver` | 받는 이
`letterTime` | 쪽찌가 생성된 시간
`body` | 쪽찌의 내용

### __Address__
* Embedded 엔티티 

속성 | 의미 
:---:|:---:
`city` | 도시
`street` | 도로 