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

##Document

All documents are generated in maven site, including:

* [Surefire Report](surefire-report.html)
* [JavaDocs](apidocs/index.html)
* [JavaCoCo Test](jacoco/index.html)
* [FindBugs](findbugs.html)

