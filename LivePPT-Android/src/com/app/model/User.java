package com.app.model;
import java.util.List;

public class User {
	/**
	 * 用户信息类
	 */
	public String userName="";	
	public String userEmail="";
	public String userPassword="";
	public Long userId;
	public List<PptFile> myPpts=null;
	public List<Meeting> myFoundedMeeting=null;	
	public List<Meeting> myParticipatedMeeting=null;
	
	
	public User(String userName,Long userId)
	{
		this.userName=userName;		
		this.userId=userId;
	}
	
	
	public User() {}
	
	public String getUserName()
	{
		return userName;
	}
	public void setUserName(String userName)
	{
		this.userName=userName;
	}
	public String getUserEmail()
	{
		return userEmail;		
	}
	public void setUserEmail(String userEmail)
	{
		this.userEmail=userEmail;		
	}
	
	public String getUserPassWord()
	{
		return userPassword;
	}
	public void setUserPassWord(String userPassWord)
	{
		this.userPassword=userPassWord;
	}
	public Long getUserId()
	{
		return userId;
	}
	public void setUserId(Long userId)
	{
		this.userId=userId;
	}
	public List<PptFile>getPpts()
	{
		return myPpts;
	}
	public void setPpts(List<PptFile> myPpts)
	{
		this.myPpts=myPpts;
	}
	public List<Meeting> getFoundedMeeting()
	{
		return myFoundedMeeting;
	}
	public void setFoundedMeeting(List<Meeting> myFoundedMeeting)
	{
		this.myFoundedMeeting=myFoundedMeeting;
	}
	
	public List<Meeting> getParticipatedMeeting()
	{
		return myParticipatedMeeting;
	}
	public void setParticipatedMeeting(List<Meeting> myAttenders)
	{
		this.myParticipatedMeeting=myAttenders;
	}
	
	

}
