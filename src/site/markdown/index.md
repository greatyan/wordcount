#Project

A rule based word break utility. see [UAX #29: Unicode Text Segmentation - Unicode Consortium](http://unicode.org/reports/tr29/#Word_Boundaries) for reference.

It contains several components:

1. A parser to read [word boundary rules](http://unicode.org/reports/tr29/#Word_Boundary_Rules).
2. A parser to read [word break property](http://unicode.org/reports/tr29/#Table_Word_Break_Property_Values).
3. A utility to find break rules between characters.
4. A word break utility to execute the rules and generate word break.
5. A interface to normalize generated words, such as removing meaning less words (whitespace, CR/LF).
6. A Heap with fixed capacity to find top N elements.
7. A main application to read file/URL and export the top N words.

##Build

This project can only build on Java 8 with maven.
 
* run test

	mvn test

* build package
	
	mvn package

* build document
	
	mvn site

##Run

wyan.WordCounter is the main entry of the project. You can run the JAR file directly as:

	java -jar wordcount.jar <file|URL> <count>

Try following URL in different languages:

* [http://www.bbc.com/news](http://www.bbc.com/news)
* [http://www.bbc.com/zhongwen/simp/](http://www.bbc.com/zhongwen/simp/)
* [http://www.bbc.com/arabic/](http://www.bbc.com/arabic/)
* [http://www.bbc.com/hindi/](http://www.bbc.com/hindi/)
* [http://www.ynet.co.il/](http://www.ynet.co.il/)

## Performance
The total complexity of the algorithm is O(N). In detail:

1. Word breaking: O(N), N is the character count.
2. Word counting: O(N), N is the word count.
3. Top word selection: O(LOG(N)), N is the selected word count.

So the total complexity is O(N).

### Performance Testing
PerformanceTest.java can be used to test the word breaking performance. It evaluates the performance by count on dynamically generate random words. The select word number is fixed to 1000, the total word count doubles for each testing from 1024 to 4194304. The time used to run the test is around:

	   WORDS	| TIME(MS)
	---------------------
	    1024	|    0
	    2048	|    0
	    4096	|    0
	    8192	|   16
	   16384	|   16
	   32768	|   32
	   65536	|   42
	  131072	|   62
	  262144	|  175
	  524288	|  362
	 1048576	| 1110
	 2097152	| 2536
	 4194304	| 4807

Using following command to run the performance testing in you local machine (you may have different result).

	java -cp wordcount.jar PerformanceTest

##Document

All documents are generated in maven site, including:

* [Surefire Report](surefire-report.html)
* [JavaDocs](apidocs/index.html)
* [JavaCoCo Test](jacoco/index.html)
* [FindBugs](findbugs.html)

