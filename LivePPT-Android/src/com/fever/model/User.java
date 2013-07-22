package com.fever.model;

import java.util.List;

/**
 * 用户信息类，管理用户相关信息
 * @author Felix
 */
public class User {
	public String userName="";	
	public String userEmail="";
	public String userPassword="";
	public Long userId;
	public List<PptFile> myPpts=null;
	public List<Meeting> myFoundedMeeting=null;	
	public List<Meeting> myParticipatedMeeting=null;


    /**
     * 用户信息构造器
     * @param userName 用户账号
     * @param userId 用户ID
     * @return
     * last modified: Frank
     */
	public User(String userName,Long userId){
		this.userName=userName;		
		this.userId=userId;
	}


    /**
     * 用户信息空构造器
     * @param
     * @param
     * @return
     * last modified: Frank
     */
	public User() {}


    /**
     * 获取用户名
     * @param
     * @return 用户名username
     * last modified: Frank
     */
	public String getUserName(){
		return userName;
	}


    /**
     * 设置用户名
     * @param userName 用户账号
     * @return
     * last modified: Frank
     */
	public void setUserName(String userName){
		this.userName=userName;
	}


    /**
     * 获取用户邮箱号
     * @param
     * @return 邮箱号userEmail
     * last modified: Frank
     */
	public String getUserEmail(){
		return userEmail;		
	}


    /**
     * 设置用户邮箱号
     * @param userEmail 用户邮箱号
     * @return
     * last modified: Frank
     */
	public void setUserEmail(String userEmail){
		this.userEmail=userEmail;		
	}


    /**
     * 获取用户密码
     * @param
     * @return 用户密码UserPassword
     * last modified: Frank
     */
	public String getUserPassWord(){
		return userPassword;
	}


    /**
     * 设置用户密码
     * @param userPassWord 用户密码
     * @return
     * last modified: Frank
     */
	public void setUserPassWord(String userPassWord){
		this.userPassword=userPassWord;
	}


    /**
     * 获取用户ID
     * @param
     * @return 用户ID userId
     * last modified: Frank
     */
	public Long getUserId(){
		return userId;
	}


    /**
     * 设置用户Id
     * @param userId 用户Id
     * @return
     * last modified: Frank
     */
	public void setUserId(Long userId){
		this.userId=userId;
	}


    /**
     * 获取用户上传到服务器的PPT文件
     * @param
     * @return PPT文件
     * last modified: Frank
     */
	public List<PptFile>getPpts(){
		return myPpts;
	}


    /**
     * 设置用户上传到服务器的PPT文件
     * @param myPpts PPT文件集合
     * @return
     * last modified: Frank
     */
	public void setPpts(List<PptFile> myPpts){
		this.myPpts=myPpts;
	}


    /**
     * 返回用户创立的会议列表
     * @param
     * @return 用户创立的会议列表
     * last modified: Frank
     */
	public List<Meeting> getFoundedMeeting(){
		return myFoundedMeeting;
	}


    /**
     * 设置用户主持创立的会议列表
     * @param myFoundedMeeting 用户创立的会议列表
     * @return
     * last modified: Frank
     */
	public void setFoundedMeeting(List<Meeting> myFoundedMeeting){
		this.myFoundedMeeting=myFoundedMeeting;
	}


    /**
     * 返回用户参加的会议列表
     * @param
     * @return 用户参加的会议列表
     * last modified: Frank
     */
	public List<Meeting> getParticipatedMeeting(){
		return myParticipatedMeeting;
	}


    /**
     * 设置用户参加的会议列表
     * @param myAttenders 用户参加的会议列表
     * @return
     * last modified: Frank
     */
	public void setParticipatedMeeting(List<Meeting> myAttenders){
		this.myParticipatedMeeting=myAttenders;
	}

}
