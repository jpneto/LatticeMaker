package dot2tex;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector; // LIFO -> for breadth searches
import java.util.Locale;
import java.util.Scanner;
import java.util.Set;

import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.ClassBasedEdgeFactory;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.alg.DirectedNeighborIndex;

/*
Algoritmo para encontrar os irredutíveis:

Dado o grafo
1. para cada nó x, calcular o chapéu de x, x^, **ordenado** (em largura)
2. com os chapéus de todos os elementos, calcular a tabela de \/:
    2.1 x \/ y = min (x^ \cap y^) 
3. para encontrar os redutíveis:
    se x \/ y = a e a != x e a != y então a é redutível
4. os irredutíveis são os que não são redutíveis

*/

//  Text typical format:
//
//	digraph G {
//		graph [rankdir=BT];
//		node [label="\N"];
//		graph [bb="0,0,2430,468"];
//		node1 [label=0, pos="1261,18", width="0.75", height="0.5"];
//		node8 [label="{1*|*,*2,*3}", pos="1035,90", width="1.6111", height="0.5"];
//		node15 [label="{1*2|*,*2,*3}", pos="1407,90", width="1.75", height="0.5"];
//		...
//		node1 -> node8 [pos="e,1075.6,77.075 1236.4,25.842 1200.3,37.343 1132.2,59.038 1085.2,73.992"];
//		node1 -> node15 [pos="e,1375.2,74.342 1282.9,28.779 1304.8,39.588 1339.1,56.491 1366,69.801"];
//      ...
//	}

public class MakeTexs {
	
	private static double SCALE = 0.05; // to scale down the original positions
	
	private static int WIDTH = 100;
	
	@SuppressWarnings("rawtypes")
	private static DirectedGraph<Node, Edge> g;
	private static DirectedNeighborIndex<Node, Edge> h; // cache of g
	
	private static HashMap<Integer,Node> nodes;
	private static Set<Node> nodesSet;
	private static Table<Integer> joins;
	private static Set<Integer> reducibleSet;
	private static Set<Integer> irreducibleSet;
	
	//private static Node[][] joins, meets;
	
	private static Node processNodeLine(String line) {

		String[] vs = line.split("[ =\t]");
		
		String label;
		if (vs[3].contains("\""))
		    label = vs[3].substring(1,vs[3].length()-2);  // node8 [label="{1*|*,*2,*3}" ...
		else
			label = vs[3].substring(0,vs[3].length()-1);  // node1 [label=0, ...
			
		int id = Integer.parseInt(vs[1].substring(4));
		String[] coords = vs[5].split("[,]"); 
		int x = Integer.parseInt(coords[0].substring(1));
		int y = Integer.parseInt(coords[1].substring(0,coords[1].length()-1));

		return new Node(id, label, x, y); 
	}
	
	////////////////////////////////////////////////////////////////////////

	private static void processEdgeLine(String line) {

		String[] vs = line.split("[ =\t]");
		
		int id1 = Integer.parseInt(vs[1].substring(4));;
		int id2 = Integer.parseInt(vs[3].substring(4));;
		
		nodesSet = g.vertexSet();
		// since I cannot use Set to easily extract nodes, let me put them into a hashmap
		nodes = new HashMap<Integer,Node>();
		for(Node node : nodesSet)
			nodes.put(node.getId(), node);
		
		g.addEdge(nodes.get(id1), nodes.get(id2), new Edge<Node>(nodes.get(id1), nodes.get(id2)));
	}
	
	////////////////////////////////////////////////////////////////////////

	public static void readDot(String input) throws IOException {

		Scanner sc = new Scanner(new BufferedReader( new FileReader(input) ));
		String line;
		
		sc.nextLine(); //  digraph G {
		sc.nextLine(); //	 graph [rankdir=BT];
		sc.nextLine(); //	 node [label="\N"];
		line = sc.nextLine(); //    graph [bb="0,0,270,612"];  <-- x0,y0,xn,yn
		
//		String[] vs = line.split("[,\"]");
//		SCALEX = WIDTH / Double.parseDouble(vs[3]);
//		int HEIGTH = Integer.parseInt(vs[4]) * WIDTH / Integer.parseInt(vs[3]);
//		SCALEY = HEIGTH / Double.parseDouble(vs[4]);
		
		// read nodes
		while (sc.hasNext()) {
			line = sc.nextLine();
		    if (line.contains(" -> ")) { // found an edge, file's 1st part ended
		    	processEdgeLine(line);
		    	break;
		    } else
		        g.addVertex(processNodeLine(line));
		}
		
		// read remaining edges
		while (sc.hasNext()) {
			line = sc.nextLine();
			if (line.contains(" -> "))
	          processEdgeLine(line);
		}
		
		sc.close();       
	}

	
	////////////////////////////////////////////////////////////////////////
	// \rput(77.85,8.1){\rnode{node11}{$\{1\StarGame2\,\mid\,\StarGame,\StarGame2\}$}}
	// \ncLine[nodesep=3pt]{-}{node15}{node13}
	
	// For String.format cf. http://docs.oracle.com/javase/tutorial/java/data/numberformat.html
	public static void writePStricks(String output) throws IOException {
		
		BufferedWriter out = new BufferedWriter( new FileWriter( output ));
		
		/*
    		Para marcar os irredutíveis, "basta" usar um \framebox{} no latex
		*/
		String rput = "\\rput(%5.2f,%5.2f){\\rnode{node%d}{$%s$}}";
		
		for(Node node : nodes.values()) {
			double x = node.getX() * SCALE;
			double y = node.getY() * SCALE;
			String line = String.format(Locale.ENGLISH, rput, x, y, node.getId(), node.getLabelLatex());
			out.write(line.replace(" ", ""));
			out.newLine();			
		}
		
		String ncLine = "\\ncLine[nodesep=3pt]{-}{node%d}{node%d}";
		
		for(Edge<Node> edge : g.edgeSet()) {
			
			int id1 = edge.getSource().getId();
			int id2 = edge.getTarget().getId();

			String line = String.format(ncLine, id1, id2);
			out.write(line);
			out.newLine();			
		}

		out.close();		
	}
	
	public static void writeIrreds(String output) throws IOException {
		
		BufferedWriter out = new BufferedWriter( new FileWriter( output ));
		
		/*
    		Para marcar os irredutíveis, "basta" usar um \framebox{} no latex
		*/
		String rput = "\\rput(%5.2f,%5.2f){\\rnode{node%d}{$%s$}}";
		String irredput = "\\rput(%5.2f,%5.2f){\\rnode{node%d}{\\framebox{$%s$}}}";
		for(Node node : nodes.values()) {
			double x = node.getX() * SCALE;
			double y = node.getY() * SCALE;
			String fmt = irreducibleSet.contains(node.id) ? irredput : rput;
			
			String line = String.format(Locale.ENGLISH, fmt, x, y, node.getId(), node.getLabelLatex());
			out.write(line.replace(" ", ""));
			out.newLine();			
		}
		
		String ncLine = "\\ncLine[nodesep=3pt]{-}{node%d}{node%d}";
		
		for(Edge<Node> edge : g.edgeSet()) {
			
			int id1 = edge.getSource().getId();
			int id2 = edge.getTarget().getId();

			String line = String.format(ncLine, id1, id2);
			out.write(line);
			out.newLine();			
		}

		out.close();		
	}
	

    /*
        Aux methods to makeJoinMatrix
    */
    
    public static Integer firstCommon(Vector<Integer> x, Vector<Integer> y) {
        Vector<Integer> a;
        Vector<Integer> b;
        if (x.size() < y.size()) {
            a = x;
            b = y;
        } else {
            a = y;
            b = x;
        }
        
        for (Integer i : a) {
            if (b.contains(i)) return i;
        }
        
        return -1;
    }
	////////////////////////////////////////////////////////////////////////
	// Build join matrix a \/ b
	public static void makeJoinMatrix() {
	
	    /*
	        STEP 1: Compute the "roof" of every element
	            the roof of x is the set of y : x \/ y = y
	    */
		HashMap<Integer,Vector<Integer>> roof = new HashMap<Integer,Vector<Integer>>();
		
		for (Node n : nodesSet) {
		    Vector<Integer> closed = new Vector<Integer>();
		    Vector<Integer> open = new Vector<Integer>();
		    open.add(n.id);
		    while ( !open.isEmpty() ) {
		        Integer a = open.firstElement();
		        open.remove(0);
                if ( !closed.contains(a) ) {
    		        closed.add(a);
	    	        Set<Node> sa = h.successorsOf( nodes.get(a) );
		            if ( !sa.isEmpty() ) {
		                for (Node b : sa) {
		                    if (!open.contains(b.id)) open.add( b.id );
		                }
		            }
		        }
		    }
		    
		    roof.put(n.id, closed);
		}
		
		/* ASSERTION:
		    roof[ n.id ] = { y >= x } / sorted by the graph
		*/
		
		/*
		    STEP 2: Compute the joins using:
		        x \join y = min x^ \cap y^ ie:
		        x \join y is the first common element in x^ and y^
		*/
		joins = new Table<Integer>();
		for (Integer x : roof.keySet() ) {
		    for (Integer y : roof.keySet() ) {
		        if (joins.containsKey(y,x)) {
		            joins.put(x,y, joins.get(y,x));
		        } else {
		            Integer z = firstCommon(roof.get(x),roof.get(y));
		            joins.put(x,y, z);
		        }
		    }
		}
		
		/*
		    STEP 3: Compute the reducible elements using:
		    a reducible iff exists x, y : x != a and y != a and a = x \/ y
		    
		*/		
		reducibleSet = new HashSet<Integer>();
		for (Node x : nodesSet) {
		    for (Node y : nodesSet) {
		        if (y.id > x.id) {
		            Integer a = joins.get(x.id,y.id);
		            if (a != x.id && a != y.id) {
		                reducibleSet.add( a );
		            }
		        }
		    }
		}
		
		/*
		    STEP 4: The irreducibles are the not reducibles
		*/
		
		irreducibleSet = new HashSet<Integer>();
		for (Node x : nodesSet) {
		    if ( !reducibleSet.contains(x.id) ) irreducibleSet.add(x.id);
		}
	}
	
	// find the Node which is the join of a \/ b
//	public static Node join(Node a, Node b) {
//		
//		// it's a symmetric operation, so if already computed...
//		if (joins[a.getId()][b.getId()] != null) 
//		  return joins[a.getId()][b.getId()];
//		
//		if (a.getId() == b.getId())  // a \/ a == a
//			return a;
//		
//		Set<DefaultEdge> out_a = g.outgoingEdgesOf(a);
//		Set<DefaultEdge> out_b = g.outgoingEdgesOf(b);
//		
//		// but I want the outgoing nodes:
//
//		// TODO:
//		
//		//////////////////
//		
//		TopologicalOrderIterator<Node, DefaultEdge> nodeIterator = 
//				new TopologicalOrderIterator<Node, DefaultEdge>(g);
//		
//		while (nodeIterator.hasNext()) {
//			Node node = nodeIterator.next();
//			
//			if (out_a.contains(node) && out_b.contains(node)) // !right: out_* have edges
//				return node;
//		}
//		
//		return null;
//	}
	
	////////////////////////////////////////////////////////////////////////

    public static void findIrreds() {
        irreducibleSet = new HashSet<Integer>();
        for (Node v : nodesSet) {
            if (h.predecessorListOf(v).size() == 1) irreducibleSet.add(v.id);
        }
    }
    
	@SuppressWarnings("rawtypes")
	public static void main(String[] args) throws IOException {
		
		g = new DefaultDirectedGraph<Node, Edge>(
                new ClassBasedEdgeFactory<Node, Edge>(Edge.class));
		
		readDot(args[0]); 
		writePStricks(args[1]);
		
		if (args.length == 4) {
			WIDTH = Integer.parseInt(args[3]); 
		}
		
		// here we already have the lattice in GraphT object g
		h = new DirectedNeighborIndex<Node, Edge>( g );
		findIrreds();	
		writeIrreds(args[2]);
		
		//System.out.println(g.toString());
	}
	

}

////////////////////////////////////////////////////////////////////////
//Assume two classes
//
//A.class 
//B.class
//
//jar them into a .jar file
//
//jar cvf AB.jar A.class B.class
//
//run either of them
//
//java -classpath AB.jar A
//java -classpath AB.jar B

