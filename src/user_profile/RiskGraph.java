package user_profile;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;

import utils.DefaultGraph;
import utils.Graph;
import utils.GraphVisitor;

public class RiskGraph {
	private DB newdb;
	public HashMap<String,Integer> GIdMap=new HashMap<String,Integer>();
	//public HashMap<Integer,String> nameMap=new HashMap<Integer,String>();

	
	RiskGraph() throws ClassNotFoundException, SQLException{
		newdb= new DB();
	}
	public HashMap<String,String> getData(String sqlcmd,String keystr,String valstr) throws ClassNotFoundException, SQLException, IOException{
		
		HashMap<String,String> retMap=new HashMap<String,String>();
		 
	
	     String value = null;
	     String key = null;
	
	     FileWriter out = new FileWriter("./file/namemap.txt");
	     //newdb.RunSqlCmd("SELECT user_mobile,GROUP_CONCAT(DISTINCT CAST(rid AS CHAR)) rids from dw_orders GROUP BY user_mobile HAVING count(DISTINCT rid)>1");
	     newdb.RunSqlCmd(sqlcmd);
	     int cnt=0;
	     while(newdb.rs.next()) {
	
	     key=newdb.rs.getString(keystr);    //user_mobile
	     value = newdb.rs.getString(valstr);    
	
	     GIdMap.put(key, cnt);
	     out.write(cnt+"\t"+"mobile"+"\t"+key+"\n");
	     cnt++;
	      //name = new String(name.getBytes("ISO-8859-1"),"GB2312");
	
	      // 输出结果
	      //System.out.println(key + "\t" + value);
	     String[] valarr=value.split(",",-1);
			
			for(int i=0;i<valarr.length;i++){
				if(!GIdMap.containsKey(valarr[i])){
					GIdMap.put(valarr[i], cnt); //rid
					out.write(cnt+"\t"+"rid"+"\t"+valarr[i]+"\n");
					cnt++;
					
				}
			} 
	     
	     
	      retMap.put(key, value);
	     }
	     out.close();
	     return retMap;
	
	}
	
	public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException{
		RiskGraph rg= new RiskGraph();
		
		HashMap<String,String> tmpmap=rg.getData("SELECT user_mobile,GROUP_CONCAT(DISTINCT CAST(rid AS CHAR)) rids from dw_orders GROUP BY user_mobile HAVING count(DISTINCT rid)>1", "user_mobile", "rids");
		
		DefaultGraph g=new DefaultGraph(rg.GIdMap.size());
		
		for(String key: tmpmap.keySet()){
			//System.out.println(key+":"+tmpmap.get(key));
			String[] valarr=tmpmap.get(key).split(",",-1);
			
			for(int i=0;i<valarr.length;i++){
				//System.out.println(rg.GIdMap.get(key)+" "+rg.GIdMap.get(valarr[i]));
				if((!key.isEmpty())&&(!valarr[i].isEmpty())){
					//加边、无向
					g.setEdge(rg.GIdMap.get(key), rg.GIdMap.get(valarr[i]), 0);
					g.setEdge(rg.GIdMap.get(valarr[i]),rg.GIdMap.get(key),0);
				}
			}
			
		}
		
		GraphVisitor visitor=new GraphVisitor(){
		    FileWriter out = new FileWriter("./file/dfs.txt");
		   
			@Override		
			public void visit(Graph g,int vertex) throws IOException{
				
				System.out.println(g.getVertexLabel(vertex)+" ");
				out.append(g.getVertexLabel(vertex)+"\n");
				
			}
			@Override
			public void fileclose() {
				try {
					out.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			@Override
			public void println(String str) {
				// TODO Auto-generated method stub
				try {
					
					out.append(str);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		};
				
		//System.out.println("BFS:");
		//g.breathFirstTravel(visitor);
		
		System.out.println("DFS:");
		g.deepFirstTravel(visitor);
		visitor.fileclose();		
	}
	
}
