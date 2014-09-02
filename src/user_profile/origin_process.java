package user_profile;

import utils.SetOperation;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;


public class origin_process {


    final private int MAX_PAGENUM = 194;
    private Map<String, UserBrowserList> Map_user_behavior = new HashMap<String, UserBrowserList>();

    private static String[] Index4Pageid = new String[194];

    private int[][] transMatrix = new int[MAX_PAGENUM][MAX_PAGENUM];

    public void initIndex4Pageid(String filename) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(filename));
        String data = br.readLine();////一次读入一行，直到读入null为文件结束
        int ind = 0;
        while (data != null) {
            Index4Pageid[ind++] = data;
            data = br.readLine();
        }
        br.close();
    }

    public int findIndex4Pageid(String str) {
        int low = 0, high = Index4Pageid.length;
        int ind = MAX_PAGENUM / 2;
        while (!str.equals(Index4Pageid[ind]) && (low != high) && !str.equals(Index4Pageid[ind + 1])) {
            if (Integer.valueOf(Index4Pageid[ind]) > Integer.valueOf(str)) {
                high = ind;
                ind = low + (high - low) / 2;
            } else {
                low = ind;
                ind = low + (high - low) / 2;
            }
        }
        if (str.equals(Index4Pageid[ind]) || str.equals(Index4Pageid[ind + 1])) {
            if (str.equals(Index4Pageid[ind]))
                return ind;
            else
                return ind + 1;
        } else
            return -1;

    }

    public void init(String filename, String filename1) throws IOException, InterruptedException {
        System.out.println(filename);
        initIndex4Pageid(filename1);
        BufferedReader br = new BufferedReader(new FileReader(filename));
        String data = br.readLine();//一次读入一行，直到读入null为文件结束
        data = br.readLine();
        while (data != null) {
            //Thread.sleep(30);
            //System.out.println(linecount++);
            String[] vs = data.split("\t", -1);

            try {
                if (!vs[2].isEmpty()) {

                    BrowseBehavior userbb = new BrowseBehavior(Long.valueOf(vs[1]), Long.valueOf(vs[0]), vs[3], Integer.valueOf(vs[4]), Integer.valueOf(vs[5]), findIndex4Pageid(vs[6]), vs[7], Integer.valueOf(vs[8]), vs[9], vs[10], vs[11], vs[12], Integer.valueOf(vs[13]), vs[14], vs[15], vs[16], vs[17], vs[18]);

                    if (Map_user_behavior.containsKey(vs[2])) {
                        Map_user_behavior.get(vs[2]).add(userbb);
                    } else {
                        UserBrowserList tmp = new UserBrowserList();
                        tmp.init();
                        tmp.add(userbb);
                        Map_user_behavior.put(vs[2], tmp);
                        tmp = null;
                    }

                    userbb = null;
                }

            } catch (Exception e) {
                //System.out.println(data.substring(data.indexOf(data.split("\r")[1])+11, data.indexOf(data.split("\t")[1])+40));
                System.out.println(data);
            }

            data = br.readLine();

        }

        Iterator<String> iterator = Map_user_behavior.keySet().iterator();
        while (iterator.hasNext()) {
            String rid = (String) iterator.next();
            if (Map_user_behavior.get(rid).size() < 3) {
                iterator.remove();        //删除点击次数过少的用户不进入map
                Map_user_behavior.remove(rid);             
            }
            else
            {
            	Map_user_behavior.get(rid).setTimeSpan(); //计算用户的间隔时间
            }
        }

        for (String rid : Map_user_behavior.keySet()) {
            Map_user_behavior.get(rid).SetMatrix();
            //System.out.print("rid:"+rid+";cnt:"+Map_user_behavior.get(rid).size()+";behavior:{");

            //System.out.print(Map_user_behavior.get(rid).toString(Index4Pageid)+",");
            //System.out.println("}");
            //System.out.print("\""+rid+"\",");
        }
        br.close();
    }

    public void arrfilter(ArrayList<BrowseBehavior> arrIn) {

    }

    /*
     * 获得全量的转移概率矩阵
     */
    public void gettransMatrix() {
        int[] rowsum = new int[MAX_PAGENUM];
        for (String rid : Map_user_behavior.keySet()) {
            for (int i : Map_user_behavior.get(rid).getTransMatrix().keySet()) {
                for (int j : Map_user_behavior.get(rid).getTransMatrix().get(i).keySet()) {
                    if (Map_user_behavior.get(rid).getTransMatrix().get(i).get(j) > 0.01) {
                        transMatrix[i][j]++;
                        rowsum[i]++;
                    }
                }

            }
        }

        Map<Double, ArrayList<String>> sortMatrix = new TreeMap<Double, ArrayList<String>>();

        //按照转移概率排序
        for (int i = 0; i < MAX_PAGENUM; i++)
            for (int j = 0; j < MAX_PAGENUM; j++) {
                if (sortMatrix.containsKey(Double.valueOf(transMatrix[i][j]) )) {
                    if (rowsum[i] != 0.0)
                        sortMatrix.get(Double.valueOf(transMatrix[i][j]) ).add(Index4Pageid[i] + "->" + Index4Pageid[j]);
                } else {
                    if (rowsum[i] != 0.0) {
                        ArrayList<String> tmplst = new ArrayList<String>();
                        tmplst.add(Index4Pageid[i] + "->" + Index4Pageid[j]);
                        sortMatrix.put(Double.valueOf(transMatrix[i][j]) , tmplst);
                    }
                }
            }
        for (double p : sortMatrix.keySet())

            System.out.println(p + ":" + sortMatrix.get(p));

    }

    /*
     * dataPre4Eclat:关联算法的数据准备
	 * numThresh:频繁1项集的次数限制，次数要小于该限制的话不进入倒排索引eclat
     */
    public Map<Integer, HashSet<String>> dataPre4Eclat(int numThresh) {
        Map<Integer, HashSet<String>> retMap = new TreeMap<Integer, HashSet<String>>();

        for (String rid : Map_user_behavior.keySet()) {
            for (int i = 0; i < Map_user_behavior.get(rid).size(); i++) {
                if (retMap.containsKey(Map_user_behavior.get(rid).get(i).getPageId())) {
                    if (!rid.isEmpty()) retMap.get(Map_user_behavior.get(rid).get(i).getPageId()).add(rid);
                } else {
                    HashSet<String> tmp = new HashSet<String>();
                    if (!rid.isEmpty()) tmp.add(rid);
                    retMap.put(Map_user_behavior.get(rid).get(i).getPageId(), tmp);
                    tmp = null;
                }
            }
        }

        Iterator<Integer> iterator = retMap.keySet().iterator();
        while (iterator.hasNext()) {
            int ind = iterator.next();
            if (retMap.get(ind).size() < numThresh) {
                iterator.remove();        //一项集不满足的情况下进行剪枝
                retMap.remove(ind);
            }
        }

        return retMap;
    }

    /*
     * eclat:利用倒排索引的关联分析算法
     * countThresh:支持度限制
     * lenThresh:最大频繁子集的最大长度限制，满足这个长度后即输出
     */
    public Set<HashSet<Integer>> Eclat(int countThresh, int lenThresh) {

        Map<HashSet<Integer>, HashSet<String>> EclatMap = new HashMap<HashSet<Integer>, HashSet<String>>();
        Map<Integer, HashSet<String>> indMap = dataPre4Eclat(3);
        int maxlen = 0;
        //EclatMap的初始化，一项初始化
        for (int ind : indMap.keySet()) {
            HashSet<Integer> hashS = new HashSet<Integer>();
            hashS.add(ind);
            EclatMap.put(hashS, indMap.get(ind));
        }

        while (maxlen < lenThresh && EclatMap.size() > 0) {
            maxlen = 0;
            Map<HashSet<Integer>, HashSet<String>> tmp = new HashMap<HashSet<Integer>, HashSet<String>>();
            Iterator<HashSet<Integer>> itr = EclatMap.keySet().iterator();

            while (itr.hasNext()) {
                Iterator<HashSet<Integer>> inneritr = EclatMap.keySet().iterator();
                HashSet<Integer> i = itr.next();
                while (inneritr.hasNext()) {
                    HashSet<Integer> j = inneritr.next();
                    if (i == j) continue;
                    HashSet<Integer> union = (HashSet<Integer>) SetOperation.union(i, j);
                    HashSet<String> inter = (HashSet<String>) SetOperation.intersection(EclatMap.get(i), EclatMap.get(j));
                    if (inter.size() > countThresh) tmp.put(union, inter);

                }
            }

            //剪枝
            //EclatMap.clear();
            EclatMap.clear();
            for (HashSet<Integer> ind : tmp.keySet()) {
                if (tmp.get(ind).size() > countThresh) {
                    EclatMap.put(ind, tmp.get(ind));
                    if (ind.size() > maxlen)
                        maxlen = ind.size();
                }

            }

            tmp = null;
        }
        Set<HashSet<Integer>> retSet = EclatMap.keySet();
        for (HashSet<Integer> k : retSet) {
            System.out.print("frequent Set:[");
            for (Integer v : k)
                System.out.print(Index4Pageid[v] + ",");
            System.out.println("]");
        }
        return retSet;

    }
    
    /*
     * 获得用户的相似矩阵：利用用户的浏览轨迹求轨迹间相似度
     */
    public double[][] getSimiMatrix(){
    	int tmpcount=0;
    	double simSum=0.0;
    	int MapLen=Map_user_behavior.size();
    	double[][] ret=new double[MapLen][MapLen];
    	String[] ridArr = new String[MapLen];
    	Map_user_behavior.keySet().toArray(ridArr);
    	for(int i=0;i<MapLen;i++){
    		ret[i][i]=1.0;
    	}
    	for(int i=0;i<MapLen;i++){
    		//System.out.println("");
    		for(int j=i+1;j<MapLen;j++){
    			ret[i][j]=ComputeSimi(Map_user_behavior.get(ridArr[i]).getLst(),Map_user_behavior.get(ridArr[j]).getLst());
    			simSum+=ret[i][j];
    			if(ret[i][j]>0.05)
    				tmpcount++;
    			//System.out.print(ret[i][j]+" ");
    			ret[j][i]=ret[i][j];
    		}
    	}
    	
    	System.out.println(2.0*tmpcount/(Map_user_behavior.size()-1)/Map_user_behavior.size());
    	System.out.println(2.0*simSum/(Map_user_behavior.size()-1)/Map_user_behavior.size());

    	return ret;
    }
    /*
     * 获得两个用户的轨迹相似度：修正的lcs
     */
    public double ComputeSimi(ArrayList<BrowseBehavior> lst1,ArrayList<BrowseBehavior> lst2){
    	double[][] similarity=new double[lst1.size()+1][lst2.size()+1];
    	for(int i = 1; i <= lst1.size(); i++)
        {
            for(int j = 1; j <= lst2.size(); ++j)
            {
                if(lst1.get(i-1).page_id == lst2.get(j-1).page_id)
                {
                	similarity[i][j] = similarity[i-1][j-1] + Math.exp(-1*Math.abs(lst1.get(i-1).timespan/60-lst2.get(j-1).timespan/60));
                }else if(similarity[i][j-1] > similarity[i-1][j])
                {
                	similarity[i][j] = similarity[i][j-1];
                }else
                {
                	similarity[i][j] = similarity[i-1][j];
                }
            }
        }
    	return (similarity[lst1.size()-1][lst2.size()-1]/Math.max(lst1.size(), lst2.size()));
    }
    
    public static void main(String[] args) throws IOException, InterruptedException {
        origin_process a = new origin_process();
        System.out.println(args[0]);
        System.out.println(System.currentTimeMillis());
        a.init(args[0], args[1]);
        System.out.println(System.currentTimeMillis());

        //a.gettransMatrix();
        a.Eclat(Integer.valueOf(args[2]), Integer.valueOf(args[3]));
        //a.Eclat(Integer.valueOf(30), Integer.valueOf(5));
        
        //a.getSimiMatrix();
    }

}
