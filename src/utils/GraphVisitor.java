package utils;

import java.io.IOException;

public interface GraphVisitor {
	void visit(Graph g,int vertex) throws IOException;
	void println(String str);
	void fileclose();
}
