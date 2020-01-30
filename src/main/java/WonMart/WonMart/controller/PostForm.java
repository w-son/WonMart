package WonMart.WonMart.controller;

import WonMart.WonMart.domain.Member;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;

@Getter @Setter
public class PostForm {

    private String title;

    private int price;

    private String body;

    private String image;

}
