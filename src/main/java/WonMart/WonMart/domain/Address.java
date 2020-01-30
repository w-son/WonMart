package WonMart.WonMart.domain;

import lombok.Getter;

import javax.persistence.Embeddable;

// Embedded 클래스의 Setter는 닫아둔다
@Embeddable
@Getter
public class Address {

    private String city;
    private String street;

    // Embedded 형태의 클래스는 protected형 생성자도 추가적으로 선언
    protected Address() { }

    public Address(String city, String street) {
        this.city = city;
        this.street = street;
    }

}
