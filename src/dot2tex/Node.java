package dot2tex;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Node {

	// Eg, to keep: node1 [label=0, pos="1261,18", width="0.75", height="0.5"];
	String label;   // "0"
	int id;         // 1
	int x,y;        // 1261,18  These are coordinates for a PSTRICKS presentation
	
	public Node(int id, String label, int x, int y) {
		super();
		this.id = id;
		this.label = label;
		this.x = x;
		this.y = y;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}
	
	/**
	 * Produces a latex translation of the CGSuite description
	 * Patterns cf. http://docs.oracle.com/javase/6/docs/api/java/util/regex/Pattern.html
	 * @return
	 */
	public String getLabelLatex() {
		String original = label;
		
		original = original.replace("{", "\\{");
		original = original.replace("}", "\\}");
		original = original.replace("+*", "*");
		
        // process fractions
        while (original.contains("/")) {
        	String[] parts = original.split("/");
        	
        	Pattern p1 = Pattern.compile("((.*?)(\\d+)$)");
        	Matcher m1 = p1.matcher(parts[0]);
        	m1.find(); 
        	
        	Pattern p2 = Pattern.compile("((\\d+)(.*))");
        	Matcher m2 = p2.matcher(parts[1]);
        	m2.find();
        	
        	original = String.format("%s\\Frac{%s}{%s}%s", m1.group(2), m1.group(3), m2.group(2), m2.group(3));
        	
        	for(int i=2;i<parts.length;i++)
        		original += "/" + parts[i];
        }

        // process PowTo...
        while (original.contains("PowTo")) {
        	String[] parts = original.split("PowTo\\(");
        	
        	Pattern p1 = Pattern.compile("((.*?)(,)(.*?)(\\))(.*)$)");
        	Matcher m1 = p1.matcher(parts[1]);
        	m1.find(); 
        	       	
        	original = String.format("%s\\POWTO{%s}{%s}%s", parts[0], m1.group(2), m1.group(4), m1.group(6));
        	
        	for(int i=2;i<parts.length;i++)
        		original += "PowTo(" + parts[i];
        }        
        
        // process Pow ...
        while (original.contains("Pow")) {
        	String[] parts = original.split("Pow\\(");
        	
        	Pattern p1 = Pattern.compile("((.*?)(,)(.*?)(\\))(.*)$)");
        	Matcher m1 = p1.matcher(parts[1]);
        	m1.find(); 
        	       	
        	original = String.format("%s\\POW{%s}{%s}%s", parts[0], m1.group(2), m1.group(4), m1.group(6));
        	
        	for(int i=2;i<parts.length;i++)
        		original += "Pow(" + parts[i];
        }      
        original = original.replace("POW", "Pow");
        
        original = original.replace("PowTO", "PowTo"); // this must not be done before!
        
        // process Tiny ...
        while (original.contains("Tiny")) {
        	String[] parts = original.split("Tiny\\(");
        	
        	Pattern p1 = Pattern.compile("((.*?)(\\))(.*)$)");
        	Matcher m1 = p1.matcher(parts[1]);
        	m1.find(); 
        	       	
        	original = String.format("%s\\TINY{%s}%s", parts[0], m1.group(2), m1.group(4));
        	
        	for(int i=2;i<parts.length;i++)
        		original += "Tiny(" + parts[i];
        }           
        original = original.replace("TINY", "Tiny");

        // process Miny ...
        while (original.contains("Miny")) {
        	String[] parts = original.split("Miny\\(");
        	
        	Pattern p1 = Pattern.compile("((.*?)(\\))(.*)$)");
        	Matcher m1 = p1.matcher(parts[1]);
        	m1.find(); 
        	       	
        	original = String.format("%s\\MINY{%s}%s", parts[0], m1.group(2), m1.group(4));
        	
        	for(int i=2;i<parts.length;i++)
        		original += "Miny(" + parts[i];
        }           
        original = original.replace("MINY", "Miny");
            
		original = original.replace("^", "\\UpGame");
		original = original.replace("v", "\\DownGame");
		original = original.replace("*", "\\StarGame");
		original = original.replace("+-", "\\MoreLess");
		original = original.replace("|||", "\\!\\mid\\mid\\mid\\!");
		original = original.replace("||",  "\\!\\mid\\mid\\!");
		original = original.replace("|",   "\\!\\mid\\!");
		
		return original;
	}
	
	public boolean equals(Object node) {
		return id == ((Node)node).id;
	}
	
	public String toString() {
		return "[label=" + label + ", id=node" + id + ", x=" + x + ", y=" + y +"]";
	}
}
