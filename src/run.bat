@echo off

rem ************************************************************************
rem save system variables
SET OLDPATH=%PATH%

rem place the location of the following tools:
SET CGT_JAVA="C:\Program Files\Java\jre6\bin"
SET CGT_GRAPHVIZ="C:\Program Files\Graphviz 2.28\bin"

rem prepare path for access of Java & Python & GraphViz tools
SET PATH=%PATH%;.;%CGT_JAVA%;%CGT_GRAPHVIZ%

rem ************************************************************************
rem Run GCSuite to make the lattice
java -jar cgsuite.jar -q -s latticeMaker.cgs lattice

rem Translates the entire lattice (in CGTSuite format) into a DOT format
java cgt2dot.CGT2DOT lattice

rem Makes the transitive reduction
tred lattice_closure.dot > lattice_tred.dot

rem Makes a DOT with (x,y) coordinates of each lattice's node
dot -Tgv lattice_tred.dot > lattice.gv

rem Translate the lattice into PSTRICKS format
java -cp .;jgrapht-jdk1.6.jar dot2tex.MakeTexs lattice.gv graph.tex irreds.tex

rem Make lattice with tiny labels and respective caption (a latex table)
rem The second argument refers to the number of columns of the caption
java tinylabels.TinyLabels graph.tex 3
java tinylabels.TinyLabels irreds.tex 3

rem Delete temporary files
del lattice
del lattice_closure.dot
del lattice_tred.dot     
del lattice.gv

rem ************************************************************************
rem cleanup
SET CGT_JAVA=
SET CGT_CGSUITE=

rem restore system variables
SET PATH=%OLDPATH%

pause