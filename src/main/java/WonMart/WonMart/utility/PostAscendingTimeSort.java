package WonMart.WonMart.utility;

import WonMart.WonMart.domain.Post;

import java.util.Comparator;

public class PostAscendingTimeSort implements Comparator<Post> {
    @Override
    public int compare(Post o1, Post o2) {
        return o1.getPostTime().compareTo(o2.getPostTime());
    }
}
