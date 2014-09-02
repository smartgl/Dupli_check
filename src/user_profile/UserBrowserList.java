package user_profile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class UserBrowserList {
    private ArrayList<BrowseBehavior> lst = new ArrayList<BrowseBehavior>();
    final private int MAX_PAGENUM = 195;
    ////private double[][] MarkovMatrix=new double[MAX_PAGENUM][MAX_PAGENUM];

    private Map<Integer, HashMap<Integer, Integer>> MarkovMatrix = new HashMap<Integer, HashMap<Integer, Integer>>();
    private int[] rowSum = new int[MAX_PAGENUM];

    public void init() {
        //��ʼ�����Ʒ����
//		 for(int i=0;i<this.MAX_PAGENUM;i++)
//				for(int j=0;j<this.MAX_PAGENUM;j++)
//					this.MarkovMatrix[i][j]=0.0;

    }

    public ArrayList<BrowseBehavior> getLst(){
    	return lst;
    }
    public void add(BrowseBehavior bb) {
        lst.add(bb);
    }

    public int size() {
        return lst.size();
    }

    public BrowseBehavior get(int i) {
        return lst.get(i);
    }
    /*
     * 设置转移概率矩阵，HMM用
     */

    public void SetMatrix() {
        for (int i = 0; i < lst.size() - 1; i++) {
            if (MarkovMatrix.containsKey(lst.get(i).getPageId())) {
                if (MarkovMatrix.get(lst.get(i).getPageId()).containsKey(lst.get(i + 1).getPageId())) {
                    int tmp = MarkovMatrix.get(lst.get(i).getPageId()).get(lst.get(i + 1).getPageId());
                    tmp += 1;
                    MarkovMatrix.get(lst.get(i).getPageId()).put(lst.get(i + 1).getPageId(), tmp);
                    rowSum[lst.get(i).getPageId()]++;
                } else {
                    int tmp = 1;
                    MarkovMatrix.get(lst.get(i).getPageId()).put(lst.get(i + 1).getPageId(), tmp);
                    rowSum[lst.get(i).getPageId()]++;
                }
            } else {
                HashMap<Integer, Integer> tmp = new HashMap<Integer, Integer>();
                tmp.put(lst.get(i + 1).getPageId(), 1);
                MarkovMatrix.put(lst.get(i).getPageId(), tmp);
                rowSum[lst.get(i).getPageId()] = 1;
            }
        }

    }
    
    /*
     * 计算每个页面停留时间，并赋值给相应的BrowseBehavior
     */
    public void setTimeSpan(){
    	for(int i=0;i<(lst.size()-1);i++){
    		lst.get(i).timespan=Math.abs(lst.get(i+1).time-lst.get(i).time);
    	}
    	lst.get(lst.size()-1).timespan=30;
    }

    public String toString(String[] Index4Pageid) {
        String ret = "";
        for (int i = 0; i < this.size(); i++)
            ret += (this.lst.get(i).toString(Index4Pageid) + ",");

        ret += ";Matrix:";

        for (int i : MarkovMatrix.keySet()) {
            for (int j : MarkovMatrix.get(i).keySet()) {
                if (MarkovMatrix.get(i).get(j) > 0.01) {
                    ret += Index4Pageid[i] + "->" + Index4Pageid[j] + ":" + MarkovMatrix.get(i).get(j) + ",";
                }
            }

        }
        return ret;
    }

    public Map<Integer, HashMap<Integer, Integer>> getTransMatrix() {
        return MarkovMatrix;
    }

    public int[] getRowSum() {
        return rowSum;
    }
    
}