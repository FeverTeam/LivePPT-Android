package com.app.base;

/**
 * 参与会议类
 * @author Felix
 *
 */
public class Attender {
	
	
	public Meeting meeting;
	public User user;	
	
	public Attender(Meeting meeting, User user)
	{
		this.meeting = meeting;
		this.user = user;
	}

}
