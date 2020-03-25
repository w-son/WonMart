package WonMart.WonMart.utility;

import WonMart.WonMart.domain.Post;

import java.util.Comparator;

public class PostDescendingTimeSort implements Comparator<Post> {
    @Override
    public int compare(Post o1, Post o2) {
        return o2.getPostTime().compareTo(o1.getPostTime());
    }
}
