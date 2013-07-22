package com.fever.model;

/**
 * 会议类 管理会议相关信息
 * @author Felix
 */
public class Meeting {
	public Long id;	
	public PptFile ppt;	
	public User founder;
	public String topic = "";
	public Long currentPageIndex = (long) 1;
	public Meeting(){};


    /**
     * 获取会议Id
     * @param
     * @return 会议id
     * last modified: Frank
     */
	public Long getMeetingId(){
		return id;
	}


    /**
     * 获取会议PPT文件
     * @param
     * @return PPT文件
     * last modified: Frank
     */
	public PptFile getMeetingPpt(){
		return ppt;
	}


    /**
     * 获取会议发起用户
     * @param
     * @return 会议发起用户founder
     * last modified: Frank
     */
	public User getMeetingFounder(){
		return founder;
	}


    /**
     * 获取会议主题
     * @param
     * @return 会议主题topic
     * last modified: Frank
     */
	public String getMeetingTopic(){
		return topic;
	}


    /**
     * 获取PPT当前页码
     * @param
     * @return PPT当前页码
     * last modified: Frank
     */
	public Long getCurrentPageIndex(){
		return currentPageIndex;
	}


    /**
     * 设置会议Id
     * @param id 会议Id
     * @return
     * last modified: Frank
     */
	public void setMeetingId(Long id){
		this.id=id;
	}


    /**
     * 设置会议PPT文件
     * @param ppt PPT文件
     * @return
     * last modified: Frank
     */
	public void setMeetingPpt(PptFile ppt){
		 this.ppt=ppt;
	}


    /**
     * 设置会议发起用户
     * @param founder 会议发起用户
     * @return
     * last modified: Frank
     */
	public void setMeetingFounder(User founder){
		this.founder=founder;
	}


    /**
     * 设置会议主题
     * @param topic 会议主题
     * @return
     * last modified: Frank
     */
	public void setMeetingTopic(String topic){
		this.topic=topic;
	}


    /**
     * 设置会议当前页
     * @param currentPageIndex 当前页码
     * @return
     * last modified: Frank
     */
	public void setCurrentPageIndex(Long currentPageIndex){
		this.currentPageIndex=currentPageIndex;
	}
	
}
