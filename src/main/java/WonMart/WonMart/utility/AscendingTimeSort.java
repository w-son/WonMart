package WonMart.WonMart.utility;

import WonMart.WonMart.domain.Letter;

import java.util.Comparator;

public class AscendingTimeSort implements Comparator<Letter> {
    @Override
    public int compare(Letter o1, Letter o2) {
        return o1.getLetterTime().compareTo(o2.getLetterTime());
    }
}
