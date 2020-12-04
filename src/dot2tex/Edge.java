package dot2tex;

import org.jgrapht.graph.DefaultEdge;

public class Edge<V> extends DefaultEdge {

	private static final long serialVersionUID = 1L;

	private V v1;
    private V v2;

    public Edge(V v1, V v2) {
        this.v1 = v1;
        this.v2 = v2;
    }

    @SuppressWarnings("unchecked")
	public V getSource() {
        return (V) super.getSource();
    }

    @SuppressWarnings("unchecked")
	public V getTarget() {
        return (V) super.getTarget();
    }

    public String toString() {
        return ((Node)v1).getId() + "->" + ((Node)v2).getId();
    }
}
