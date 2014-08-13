package user_profile;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class AntiFraudGraph {
	public void init() throws IOException, ClassNotFoundException, SQLException{
		BufferedReader br1 = new BufferedReader(new FileReader("./file/dfs.txt"));
		BufferedReader br2 = new BufferedReader(new FileReader("./file/namemap.txt"));
		HashSet<ArrayList> clusterset = new HashSet<ArrayList>();
		HashMap<String,String> namemap= new HashMap<String,String>();
		try {
			
			String data2 = br2.readLine();//�?次读入一行，直到读入null为文件结�?
			 while(data2 !=null){
				 //System.out.println(data2);
				 String[] a=data2.split("\t",-1);
				 namemap.put(a[0], a[1]+"\t"+a[2]);
				 data2=br2.readLine();
			 } 
			 
			DB newdb= new DB();
			HashMap<String,String> dworders= new HashMap<String,String>();
			newdb.RunSqlCmd("SELECT user_mobile,status from dw_orders");
		    
		    FileWriter out = new FileWriter("./file/cluster_status.txt");
			
		     while(newdb.rs.next()) {
		    	 dworders.put("mobile\t"+newdb.rs.getString("user_mobile"), newdb.rs.getString("status"));
		     }
			
			String data1 = br1.readLine();//�?次读入一行，直到读入null为文件结�?
			ArrayList<String> tmparr= new ArrayList<String>();
			 while(data1 !=null){
				 String[] a= data1.split("\n",-1);
				 //System.out.println(a[0]);
				 //System.out.println(namemap.get(a[0]));
				 if(data1.contains("cluster"))
					 tmparr.clear();
				 else{
					 if(data1.contains("cnt")){
					 	clusterset.add(tmparr);
					 	for(String str: tmparr){
					 		System.out.println(str);
					 		out.write(str+"\n");
					 	}
					 	System.out.println();
					 	out.write("\n");
					 	tmparr.clear();
					 }
					 else{
						 tmparr.add(namemap.get(a[0])+"\t"+dworders.get(namemap.get(a[0])));
					 }
				 }
				 data1=br1.readLine();
			 }
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}
	public static void main(String[] args) throws ClassNotFoundException, SQLException{
		AntiFraudGraph afg=new AntiFraudGraph();
		try {
			afg.init();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	

}
