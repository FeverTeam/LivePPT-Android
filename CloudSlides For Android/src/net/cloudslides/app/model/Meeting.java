package net.cloudslides.app.model;

/**
 * 会议类
 * @author Felix
 *
 */
public class Meeting {

	public Long id;	
	public PptFile ppt;	
	public User founder;
	public String topic = "";
	public Long currentPageIndex = (long) 1;
	public Meeting(){};
	
	public Long getMeetingId()
	{
		return id;
	}
	public PptFile getMeetingPpt()
	{
		return ppt;
	}
	public User getMeetingFounder()
	{
		return founder;
	}
	public String getMeetingTopic()
	{
		return topic;
	}
	public Long getCurrentPageIndex()
	{
		return currentPageIndex;
	}	
	
	public void setMeetingId(Long id)
	{
		this.id=id;
	}
	public void setMeetingPpt(PptFile ppt)
	{
		 this.ppt=ppt;
	}
	public void setMeetingFounder(User founder)
	{
		this.founder=founder;
	}
	public void setMeetingTopic(String topic)
	{
		this.topic=topic;
	}
	public void setCurrentPageIndex(Long currentPageIndex)
	{
		this.currentPageIndex=currentPageIndex;
	}	
}
