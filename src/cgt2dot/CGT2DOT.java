package cgt2dot;

import java.io.*;
import java.util.*;

public class CGT2DOT {

    public static void main(String[] args) throws IOException {

        // open text file with cgtsuite information
        Scanner sc = new Scanner(new BufferedReader( new FileReader(args[0]) ));
        StringBuilder games   = new StringBuilder(sc.next());
        StringBuilder lattice = new StringBuilder(sc.next());
        sc.close();

        games.deleteCharAt(0); // remove [
        games.deleteCharAt(games.length()-1); // remove ]
        lattice.deleteCharAt(0); // remove [
        lattice.deleteCharAt(lattice.length()-1); // remove ]

        // make relevant commas into semicolons
        int open = 0;
        for(int i=0;i<games.length();i++) {
          if (open==0 && games.charAt(i) == ',')
            games.setCharAt(i, ';');
          else if (games.charAt(i) == '{' || games.charAt(i) == '(')
            open++;
          else if (games.charAt(i) == '}' || games.charAt(i) == ')')
            open--;
        }

        open = 0;
        for(int i=0;i<lattice.length();i++) {
          if (open==0 && lattice.charAt(i) == ',')
            lattice.setCharAt(i, ';');
          else if (lattice.charAt(i) == '{' || lattice.charAt(i) == '(')
            open++;
          else if (lattice.charAt(i) == '}' || lattice.charAt(i) == ')')
            open--;
        }

        // create .dot from the cgtsuite text file
        BufferedWriter outF = new BufferedWriter( new FileWriter(args[0]+"_closure.dot") );
        outF.write("digraph G {\n\n");
        outF.write("  rankdir=BT;");

           // process string: first collect all different tokens
        Scanner sg = new Scanner(games.toString()).useDelimiter("\\s*[.;]\\s*");
        HashMap<String,Integer> labels = new HashMap<String,Integer>();
        int node = 1;
        
        while (sg.hasNext()) {
          String game = sg.next();
          labels.put(game, node);
          outF.write("  node"+node+" [label = \""+game+"\"];\n");
          node++;
        }      
        sg.close();

           // now, collect pairs of games (1st game < 2nd game)
        sg = new Scanner(lattice.toString()).useDelimiter("\\s*[.;]\\s*");

        while (sg.hasNext()) {
          String game1 = sg.next();
          String game2 = sg.next();
          int index1 = labels.get(game1);
          int index2 = labels.get(game2);
          outF.write("  node" + index1 + " -> node" + index2 + ";\n");
        }

        outF.write("}");
        outF.close();

    }
}
