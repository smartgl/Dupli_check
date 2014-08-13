package utils;

import java.io.IOException;

public interface Graph {
	//mark
	public static interface Edge{
		public int getWeight();
	}
	
	int vertexesNum();
	
	int edgeNum();
	boolean isEdge(Edge edge);
	void setEdge(int from, int to,int weight);
	Edge firstEdge(int vertex);
	Edge nextEdge(Edge edge);
	int toVertex(Edge edge);
	int fromVertex(Edge edge);
	String getVertexLabel(int vertex);
	void assignLabels(String[] labels);
	void deepFirstTravel(GraphVisitor visitor) throws IOException;
	void breathFirstTravel(GraphVisitor visitor) throws IOException;
	int getEdgeNum(int vertex);
	

}
