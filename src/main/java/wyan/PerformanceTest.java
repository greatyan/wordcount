package wyan;

import java.util.Random;

public class PerformanceTest {

    private String randomWords(int numberOfWords) {
	StringBuilder sb = new StringBuilder();
	Random random = new Random();
	for (int i = 0; i < numberOfWords; i++) {
	    char[] word = new char[random.nextInt(8) + 3];
	    for (int j = 0; j < word.length; j++) {
		word[j] = (char) ('a' + random.nextInt(26));
	    }
	    sb.append(" ");
	    sb.append(word);
	}
	return sb.toString();
    }

    private long run(int count) {
	String text = randomWords(count);
	long start = System.currentTimeMillis();
	new WordCounter().count(text, 1000);
	long end = System.currentTimeMillis();
	return end - start;
    }

    public void test() {

	int count = 2;
	for (int i = 0; i < 22; i++) {
	    long time = run(count);
	    System.out.println(count + "\t" + time);
	    count = count * 2;
	}
    }

    public static void main(String[] args) {
	new PerformanceTest().test();
    }

}
