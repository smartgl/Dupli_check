package user_profile;

public class BrowseBehavior {
    long time;
    long ip;
    String ca;
    int cid;
    int pcat;
    int page_id;
    String page_name;
//	 int rf_page_id;
//	 String rf_page_name;
//	 String tpl;
//	 String host;
//	 String rf_host;
//	 int abclass;
//	 String ga_csr;
//	 String ga_ccn;	
//	 String ga_cmd;
//	 String ga_ctr;
//	 String log_dt;
    
    long timespan;


    BrowseBehavior(long time, long ip, String ca, int cid, int pcat, int page_id, String page_name, int rf_page_id, String rf_page_name, String tpl, String host, String rf_host, int abclass, String ga_csr, String ga_ccn, String ga_cmd, String ga_ctr, String log_dt) {
        this.time = time;
        this.ip = ip;
        this.ca = ca;
        this.cid = cid;
        this.pcat = pcat;
        this.page_id = page_id;
        this.page_name = page_name;
//		 this.rf_page_id=rf_page_id;
//		 this.rf_page_name=rf_page_name;
//		 this.tpl=tpl;
//		 this.host=host;
//		 this.rf_host=rf_host;
//		 this.abclass=abclass;
//		 this.ga_csr=ga_csr;
//		 this.ga_ccn=ga_ccn;	
//		 this.ga_cmd=ga_cmd;
//		 this.ga_ctr=ga_ctr;
//		 this.log_dt=log_dt;


    }


    public String toString(String[] Index4Pageid) {
        String ret = "{time:" + this.time + ",page_id:" + Index4Pageid[this.page_id] + ",page_name:" + this.page_name + "}";
        return ret;
    }

    public int getPageId() {
        return page_id;
    }
    

}
