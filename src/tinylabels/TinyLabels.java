package tinylabels;

import java.util.*;
import java.io.*;

public class TinyLabels {
	
	private static ArrayList<LabelNode> vLabels = new ArrayList<LabelNode>(); 
	
	private static void processLine(String line) {
		
		// tipical line:
		// \rput(7.1,11.7){\rnode{node11}{$\DownGame$}}
		
		String[] vs = line.split("[(,){}]");
		double x = Double.parseDouble(vs[1]);
		double y = Double.parseDouble(vs[2]);
		
		String sNode = vs[5].split("e")[1];
		int node = Integer.parseInt(sNode);
		
		vs = line.split("[$]");
		
		vLabels.add(new LabelNode(x,y,node,"$"+vs[1]+"$", line.contains("framebox")));
		
	}
	
	public static void main(String[] args) throws IOException {

		String[] vFileName = args[0].split("[.]");
		String outFileName = vFileName[0] + "_tiny." + vFileName[vFileName.length-1];
		String line="";
		
		Scanner sc = new Scanner(new BufferedReader( new FileReader(args[0]) ));
		BufferedWriter out = new BufferedWriter( new FileWriter( outFileName ));
		
		// read 1st part; \rput's
		while (sc.hasNext()) {
			line = sc.next();
		    if (line.substring(1,3).equals("nc")) // found a \ncLine, file's 1st part ended
		    	break;
			processLine(line);
		}
		
		// now, all nodes are in vLabels, we need to sort them, and rename them before
		// writing the pstricks information again
		
		Collections.sort(vLabels, new LabelComparator());
		
		int gameNumber = 0;
		int numberOfDigits = (int) Math.ceil(Math.log10(vLabels.size()));
		for(LabelNode ln : vLabels) {
			// write information. Eg. \rput(7.1,11.7){\rnode{node11}{$g0$}}

			String fmt = "%0" + numberOfDigits + "d";
			String gameLabel = String.format(fmt,gameNumber);
			
			String line1 = "\\rput(" + ln.x + "," + ln.y + ")" +
			            "{\\rnode{node" + ln.nodeId + "}";
			            
			if (ln.irreducible) line1 += "{\\framebox{$g_{" + gameLabel + "}$}}}"; else line1 += "{$g_{" + gameLabel + "}$}}";
			
			out.write(line1);			
			out.newLine();
			gameNumber++;
			
		}
		
		out.write(line); // the first \ncLine was not written
		out.newLine();
		while (sc.hasNext()) { // the rest is not modified
			out.write(sc.next());
			out.newLine();
		}
		
		sc.close();
		out.close();
		
		////////////////
		
		int columns;
		
		if (args.length>1)
			columns = Integer.parseInt(args[1]);
		else
			columns = 3;
		
		// Now it is time to make the lattice caption (in latex)
		outFileName = vFileName[0] + "_labels." + vFileName[vFileName.length-1];
		out = new BufferedWriter( new FileWriter( outFileName ));
		
		String header = "||"; 
		for(int i=0;i<columns;i++)
			header += "c | c || ";
		out.write("\\begin{tabular}{"+ header + "}");
		out.newLine();
		
		out.write("\\hline");
		out.newLine();
		
		gameNumber = 0;
		for(LabelNode ln : vLabels) {

			String fmt = "%0" + numberOfDigits + "d";
			String gameLabel = "$g_{" + String.format(fmt,gameNumber) + "}$";		
			out.write(gameLabel + " & " + ln.game);
			gameNumber++;

			if (gameNumber % columns == 0) {
				out.write(" \\\\");
				out.newLine();
				out.write("\\hline");
				out.newLine();
			} else
				out.write(" & ");

			
        }

		if (gameNumber % columns != 0) {
			while (gameNumber % columns != 0) {
				out.write(" & ");
				gameNumber++;
				if (gameNumber % columns == 0) {
					out.write(" \\\\");
					out.newLine();
					out.write("\\hline");
				} else
					out.write(" & ");
			}			
		}

		out.write("\\end{tabular}");
		out.newLine();

		out.close();
		
	}

}

//\begin{tabular}{ | c | c || c | c | }
//\hline
// 1 & $g_1$ & 3 & $g_1$ \\
// 1 & $g_1$ & 3 & $g_1$ \\
// \hline
//\end{tabular}

class LabelNode implements Comparable<LabelNode> {
	double x, y; // coordinates
	int nodeId;
	String game;
	boolean irreducible;
	
	public LabelNode(double x, double y, int nodeId, String game, boolean irreducible) {
		this.x = x;
		this.y = y;
		this.nodeId = nodeId;
		this.game = game;
		this.irreducible = irreducible;
	}
	
	@Override
	public int compareTo(LabelNode label) {
		if (y>label.y)
			return 1;
		else if (y<label.y)
			return -1;
		else if (x > label.x)
			return 1;
		else if (x < label.x)
			return -1;
		
		return 0;
	}
}

class LabelComparator implements Comparator<LabelNode> {

	@Override
	public int compare(LabelNode label0, LabelNode label1) {
		if (label0.y>label1.y)
			return 1;
		else if (label0.y<label1.y)
			return -1;
		else if (label0.x > label1.x)
			return 1;
		else if (label0.x < label1.x)
			return -1;
		return 0;
	}
	
}
