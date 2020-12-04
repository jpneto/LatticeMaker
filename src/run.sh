#!/bin/sh
# place the location of the following tools:
JAVA=`which java`
DOT=`which dot`
TRED=`which tred`

# ************************************************************************
# Run GCSuite to make the lattice
echo "running cgsuite..."
${JAVA} -jar cgsuite.jar -q -s latticeMaker.cgs lattice

# Translates the entire lattice (in CGTSuite format) into a DOT format
echo "translating lattice..." 
${JAVA} cgt2dot.CGT2DOT lattice

# Makes the transitive reduction
echo "making transitive reduction..."
${TRED} lattice_closure.dot > lattice_tred.dot

# Makes a DOT with (x,y) coordinates of each lattice's node
echo "convert to gv"
${DOT} -Tgv lattice_tred.dot > lattice.gv

# Translate the lattice into PSTRICKS format
echo "convert to pstricks"
${JAVA} -cp .:jgrapht-jdk1.6.jar dot2tex.MakeTexs lattice.gv graph.tex irreds.tex

# Make lattice with tiny labels and respective caption (a latex table)
# The second argument refers to the number of columns of the caption
echo "tinylabels..."
${JAVA} tinylabels.TinyLabels graph.tex 3
${JAVA} tinylabels.TinyLabels irreds.tex 3

# Delete temporary files
echo "cleanup..."
rm lattice
rm lattice_closure.dot
rm lattice_tred.dot     
rm lattice.gv
