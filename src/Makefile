JAVA=`which java`
JAVAC=`which javac`
DOT=`which dot`
TRED=`which tred`

dot2tex/MakeTexs.class:
	${JAVAC} -cp jgrapht-jdk1.6.jar:. dot2tex/MakeTexs.java
	
run:	dot2tex/MakeTexs.class
	@${JAVA} -jar cgsuite.jar -q -s latticeMaker.cgs lattice
	@${JAVA} cgt2dot.CGT2DOT lattice
	@${TRED} lattice_closure.dot > lattice_tred.dot
	@${DOT} -Tgv lattice_tred.dot > lattice.gv
	@${JAVA} -cp .:jgrapht-jdk1.6.jar dot2tex.MakeTexs lattice.gv graph.tex irreds.tex
	@${JAVA} tinylabels.TinyLabels graph.tex 3
	@${JAVA} tinylabels.TinyLabels irreds.tex 3
	@rm lattice
	@rm lattice_closure.dot
	@rm lattice_tred.dot     
	@rm lattice.gv

clean:
	rm dot2tex/MakeTexs.class
