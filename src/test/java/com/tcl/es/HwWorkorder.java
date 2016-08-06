package com.tcl.es;

public class HwWorkorder { 
	private String id;
	private String tid;//租户id
	private String skill_id;//业务技能id
	private String shop_id;//门店id
	private String cpro_id;//受理产品id
	private String shopids;//派工前 case单经过的门店集合 冗余字段
	private String worksn;//工单编号
	private String addresspath;//地区路径
	private String customername;//客户姓名
	private String customertel;//客户所有电话同case表
	private String bigclass;//客户大类同case
	private String proscate_id;//产品小类同case
	private String caller;//来电号码
	private String createtime;//创建时间
	private String updatetime;//更新时间
	private String transfertime;//转工单时间
	private String case_code;//case单编码
	private String case_id;//case id
	private String engine_id;//工单最终所属工程师id
	private String source_objid;//源转工单者
	private String dealstate;//处理状态，存编码
	private String dealsource;//处理来源
	private Integer worktype;//工单类型，0下发，1抢单
	private Integer sendnum;//下发数量
	private Integer istransferwork;//是否转 工单 1是 0否
	private String asktime;//要求时间段
	private String askdate;//要求日期
	private String accepttime;//接/抢单时间
	private String departuretime;//出发时间
	private String reachtime;//到达时间
	private String finishtime;//完成时间
	private String replytime;//回单时间
	private String closetime;//关闭时间
	private String closereason;//关闭原因
	private String planstarttime;//计划上门开始时间
	private String planendtime;//计划上门结束时间
	private Integer changenum;//改约次数
	private String create_username;//创建人
	private Integer brand_id;//品牌大类
	private Integer probcate_id;//产品大类
	private Integer dembcate_id;//需求大类
	private Integer case_level;//case级别
	private Integer business_id;//单据类型
	private String phoneext;//电话后两位
	private Integer sms_receipt;//短信回执,-1-未发送,0-确认接受,1-已发送,2-发送失败
	private String extfield;
	private Integer replysource;//回单来源
	private Integer isrepair;//1为勾选需拉修 0为普通 2为库修工单
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTid() {
		return tid;
	}
	public void setTid(String tid) {
		this.tid = tid;
	}
	public String getSkill_id() {
		return skill_id;
	}
	public void setSkill_id(String skill_id) {
		this.skill_id = skill_id;
	}
	public String getShop_id() {
		return shop_id;
	}
	public void setShop_id(String shop_id) {
		this.shop_id = shop_id;
	}
	public String getCpro_id() {
		return cpro_id;
	}
	public void setCpro_id(String cpro_id) {
		this.cpro_id = cpro_id;
	}
	public String getShopids() {
		return shopids;
	}
	public void setShopids(String shopids) {
		this.shopids = shopids;
	}
	public String getWorksn() {
		return worksn;
	}
	public void setWorksn(String worksn) {
		this.worksn = worksn;
	}
	public String getAddresspath() {
		return addresspath;
	}
	public void setAddresspath(String addresspath) {
		this.addresspath = addresspath;
	}
	public String getCustomername() {
		return customername;
	}
	public void setCustomername(String customername) {
		this.customername = customername;
	}
	public String getCustomertel() {
		return customertel;
	}
	public void setCustomertel(String customertel) {
		this.customertel = customertel;
	}
	public String getBigclass() {
		return bigclass;
	}
	public void setBigclass(String bigclass) {
		this.bigclass = bigclass;
	}
	public String getProscate_id() {
		return proscate_id;
	}
	public void setProscate_id(String proscate_id) {
		this.proscate_id = proscate_id;
	}
	public String getCaller() {
		return caller;
	}
	public void setCaller(String caller) {
		this.caller = caller;
	}
	public String getCreatetime() {
		return createtime;
	}
	public void setCreatetime(String createtime) {
		this.createtime = createtime;
	}
	public String getUpdatetime() {
		return updatetime;
	}
	public void setUpdatetime(String updatetime) {
		this.updatetime = updatetime;
	}
	public String getTransfertime() {
		return transfertime;
	}
	public void setTransfertime(String transfertime) {
		this.transfertime = transfertime;
	}
	public String getCase_code() {
		return case_code;
	}
	public void setCase_code(String case_code) {
		this.case_code = case_code;
	}
	public String getCase_id() {
		return case_id;
	}
	public void setCase_id(String case_id) {
		this.case_id = case_id;
	}
	public String getEngine_id() {
		return engine_id;
	}
	public void setEngine_id(String engine_id) {
		this.engine_id = engine_id;
	}
	public String getSource_objid() {
		return source_objid;
	}
	public void setSource_objid(String source_objid) {
		this.source_objid = source_objid;
	}
	public String getDealstate() {
		return dealstate;
	}
	public void setDealstate(String dealstate) {
		this.dealstate = dealstate;
	}
	public String getDealsource() {
		return dealsource;
	}
	public void setDealsource(String dealsource) {
		this.dealsource = dealsource;
	}
	public Integer getWorktype() {
		return worktype;
	}
	public void setWorktype(Integer worktype) {
		this.worktype = worktype;
	}
	public Integer getSendnum() {
		return sendnum;
	}
	public void setSendnum(Integer sendnum) {
		this.sendnum = sendnum;
	}
	public Integer getIstransferwork() {
		return istransferwork;
	}
	public void setIstransferwork(Integer istransferwork) {
		this.istransferwork = istransferwork;
	}
	public String getAsktime() {
		return asktime;
	}
	public void setAsktime(String asktime) {
		this.asktime = asktime;
	}
	public String getAskdate() {
		return askdate;
	}
	public void setAskdate(String askdate) {
		this.askdate = askdate;
	}
	public String getAccepttime() {
		return accepttime;
	}
	public void setAccepttime(String accepttime) {
		this.accepttime = accepttime;
	}
	public String getDeparturetime() {
		return departuretime;
	}
	public void setDeparturetime(String departuretime) {
		this.departuretime = departuretime;
	}
	public String getReachtime() {
		return reachtime;
	}
	public void setReachtime(String reachtime) {
		this.reachtime = reachtime;
	}
	public String getFinishtime() {
		return finishtime;
	}
	public void setFinishtime(String finishtime) {
		this.finishtime = finishtime;
	}
	public String getReplytime() {
		return replytime;
	}
	public void setReplytime(String replytime) {
		this.replytime = replytime;
	}
	public String getClosetime() {
		return closetime;
	}
	public void setClosetime(String closetime) {
		this.closetime = closetime;
	}
	public String getClosereason() {
		return closereason;
	}
	public void setClosereason(String closereason) {
		this.closereason = closereason;
	}
	public String getPlanstarttime() {
		return planstarttime;
	}
	public void setPlanstarttime(String planstarttime) {
		this.planstarttime = planstarttime;
	}
	public String getPlanendtime() {
		return planendtime;
	}
	public void setPlanendtime(String planendtime) {
		this.planendtime = planendtime;
	}
	public Integer getChangenum() {
		return changenum;
	}
	public void setChangenum(Integer changenum) {
		this.changenum = changenum;
	}
	public String getCreate_username() {
		return create_username;
	}
	public void setCreate_username(String create_username) {
		this.create_username = create_username;
	}
	public Integer getBrand_id() {
		return brand_id;
	}
	public void setBrand_id(Integer brand_id) {
		this.brand_id = brand_id;
	}
	public Integer getProbcate_id() {
		return probcate_id;
	}
	public void setProbcate_id(Integer probcate_id) {
		this.probcate_id = probcate_id;
	}
	public Integer getDembcate_id() {
		return dembcate_id;
	}
	public void setDembcate_id(Integer dembcate_id) {
		this.dembcate_id = dembcate_id;
	}
	public Integer getCase_level() {
		return case_level;
	}
	public void setCase_level(Integer case_level) {
		this.case_level = case_level;
	}
	public Integer getBusiness_id() {
		return business_id;
	}
	public void setBusiness_id(Integer business_id) {
		this.business_id = business_id;
	}
	public String getPhoneext() {
		return phoneext;
	}
	public void setPhoneext(String phoneext) {
		this.phoneext = phoneext;
	}
	public Integer getSms_receipt() {
		return sms_receipt;
	}
	public void setSms_receipt(Integer sms_receipt) {
		this.sms_receipt = sms_receipt;
	}
	public String getExtfield() {
		return extfield;
	}
	public void setExtfield(String extfield) {
		this.extfield = extfield;
	}
	public Integer getReplysource() {
		return replysource;
	}
	public void setReplysource(Integer replysource) {
		this.replysource = replysource;
	}
	public Integer getIsrepair() {
		return isrepair;
	}
	public void setIsrepair(Integer isrepair) {
		this.isrepair = isrepair;
	}

 
}
