package WonMart.WonMart.utility;

import WonMart.WonMart.domain.Letter;

import java.util.Comparator;

public class LetterDescendingTimeSort implements Comparator<Letter> {
    @Override
    public int compare(Letter o1, Letter o2) {
        return o2.getLetterTime().compareTo(o1.getLetterTime());
    }
}
