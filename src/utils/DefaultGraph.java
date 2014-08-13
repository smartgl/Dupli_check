package utils;

import java.io.IOException;
import java.util.Arrays;

import utils.Graph.Edge;

public class DefaultGraph implements Graph {
	private static class _Edge implements Edge{
		private static final _Edge NullEdge=new _Edge();
		int from;
		int to;
		int weight;
		_Edge nextEdge;
		
		private _Edge(){
			weight=Integer.MAX_VALUE;
		}
		
		_Edge(int from,int to,int weight){
			this.from=from;
			this.to=to;
			this.weight=weight;
		}
		
		public int getWeight(){
			return weight;
		}
	}
	
	private int numVertexes;
	private String[] labels;
	private int numEdges;
	
	private static class _EdgeStaticQueue{
		_Edge first;
		_Edge last;
	}
	
	private _EdgeStaticQueue[] edgeQueues;
	
	private boolean[] visitTags;
	
	public DefaultGraph(int numVertexes){
		if(numVertexes<1){
			throw new IllegalArgumentException();
		}
		
		this.numVertexes=numVertexes;
		this.visitTags=new boolean[numVertexes];
		this.labels=new String[numVertexes];
		for(int i=0;i<numVertexes;i++){
			labels[i]=i+"";
		}
		
		this.edgeQueues=new _EdgeStaticQueue[numVertexes];
		for(int i=0;i<numVertexes;i++){
			edgeQueues[i]=new _EdgeStaticQueue();
			edgeQueues[i].first=edgeQueues[i].last=_Edge.NullEdge;
		}
		
		this.numEdges=0;
	}
	
	@Override
	public boolean isEdge(Edge edge){
		boolean ret=!(edge==_Edge.NullEdge);
		return ret;
		
	}

	@Override
	
	public int edgeNum(){
		return numEdges;
	}
	
	
	@Override
	
	public Edge firstEdge(int vertex){
		if(vertex>=numVertexes) throw new IllegalArgumentException();
		
		return edgeQueues[vertex].first;
	}
	
	@Override
	public Edge nextEdge(Edge edge){
		return ((_Edge)edge).nextEdge;
	}
	
	@Override 
	public int fromVertex(Edge edge){
		return ((_Edge)edge).from;
	}
	
	@Override
	public void setEdge(int from, int to,int weight){
		//no ring here
		if(from<0||from>=numVertexes||to<0||to>=numVertexes||weight<0||from==to)throw new IllegalArgumentException();
		_Edge edge=new _Edge(from,to,weight);
		edge.nextEdge=_Edge.NullEdge;
		if(edgeQueues[from].first==_Edge.NullEdge)
			edgeQueues[from].first=edge;
		else
			edgeQueues[from].last.nextEdge=edge;
		edgeQueues[from].last=edge;
		
	}
	
	@Override
	public int getEdgeNum(int vertex){
		int ret=0;
		for(Edge e=firstEdge(vertex);isEdge(e);e=nextEdge(e)){
			ret++;			
		}
		return ret;
	}
	
	@Override
	public int toVertex(Edge edge){
		return ((_Edge)edge).to;
	}
	
	@Override
	public String getVertexLabel(int vertex){
		return labels[vertex];
	}
	
	@Override
	public void assignLabels(String[] labels){
		System.arraycopy(labels, 0, this.labels, 0, labels.length);
	}
	
	private int cnt=0;
	@Override
	public void deepFirstTravel(GraphVisitor visitor) throws IOException{
		Arrays.fill(visitTags, false);
		int clustercnt=0;
		for(int i=0;i<numVertexes;i++){
			if(!visitTags[i]){
				visitor.println("cluster==========:"+clustercnt+"\n");
				System.out.println("cluster==========:"+clustercnt+"\n");
				
				do_DFS(i,visitor);
				if(this.cnt>getEdgeNum(i))
					visitor.println("cnt:"+this.cnt+" "+getEdgeNum(i)+"\n");
				this.cnt=0;
				clustercnt++;
			}
		}
	}
	
	private final void do_DFS(int v,GraphVisitor visitor) throws IOException{
		//first visit this vertex
		visitor.visit(this, v);
		visitTags[v]=true;
		for(Edge e=firstEdge(v);isEdge(e);e=nextEdge(e)){
			
			if(!visitTags[toVertex(e)]){
				this.cnt++;
				do_DFS(toVertex(e),visitor);
				
			}		
		}
	}

	@Override
	public int vertexesNum() {
		
		return this.numVertexes;
	}
	
	private static class _IntQueue{
		private static class _IntQueueNode{
			_IntQueueNode next;
			int value;
		}
		
		_IntQueueNode first;
		_IntQueueNode last;
		
		void add(int i)
		{
			_IntQueueNode node = new _IntQueueNode();
			node.value=i;
			node.next=null;
			if(first==null)first=node;
			else last.next=node;
			last=node;
		}
		
		boolean isEmpty()
		{
			return first==null;
		}
		
		int remove(){
			int val=first.value;
			if(first==last)
				first=last=null;
			else
				first=first.next;
			return val;
		}
	}

	@Override
	public void breathFirstTravel(GraphVisitor visitor) throws IOException {
		Arrays.fill(visitTags, false);
		
		for(int i=0;i<numVertexes;i++){
			if(!visitTags[i]){
				do_BFS(i,visitor);
			}
		}
		
	}
	
	private void do_BFS(int v,GraphVisitor visitor) throws IOException{
		_IntQueue queue = new _IntQueue();
		queue.add(v);
		
		while(!queue.isEmpty()){
			int fromV=queue.remove();
			visitor.visit(this, fromV);
			visitTags[fromV]=true;
			if(true==visitTags[fromV]){
				visitor.println("there is ring\n");
				//System.exit(0);
			}
			for(Edge e=firstEdge(fromV);isEdge(e);e=nextEdge(e)){
				if(!visitTags[toVertex(e)]){
					queue.add(toVertex(e));
				}
			}
		}
	}
	
	public static void main(String[] args){
		DefaultGraph g=new DefaultGraph(5);
		g.setEdge(0, 2, 0);
		g.setEdge(0, 3, 0);
		g.setEdge(2, 1, 0);
		g.setEdge(2, 3, 0);
		g.setEdge(1, 4, 0);
		g.setEdge(1, 3, 0);
		g.setEdge(3, 2, 0);
		g.setEdge(3, 1, 0);
		
		GraphVisitor visitor=new GraphVisitor(){
			@Override
			public void visit(Graph g,int vertex){
				System.out.println(g.getVertexLabel(vertex)+" ");
			}

			@Override
			public void fileclose() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void println(String str) {
				// TODO Auto-generated method stub
				
			}
		};
		
		System.out.println("DFS:");
		try {
			g.deepFirstTravel(visitor);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("BFS:");
		try {
			g.breathFirstTravel(visitor);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

}
