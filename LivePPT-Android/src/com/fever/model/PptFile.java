package com.fever.model;

public class PptFile {
	/**
	 * PPT文件类
	 * PPT文件信息的设置和查询
	 * @author Felix
	 */
	private Long pptId;	//id
	private boolean pptStatus=false; //转换状态 
	private int pptPageCount;//页码数
	private Long pptSize;//ppt文件大小
	private String pptTitle=""; //ppt名称
	private String pptTime=""; //ppt生成时间
	
	public PptFile(){};
	public PptFile(Long pptId,boolean pptStatus,int pptPageCount,Long pptSize,String pptTitle,String pptTime)
	{
		this.pptId=pptId;
		this.pptStatus=pptStatus;
		this.pptSize=pptSize;
		this.pptTime=pptTime;
		this.pptTitle=pptTitle;
	}
	public void setPptId(Long pptId)
	{
		this.pptId=pptId;
	}
	public void setPptStatus(boolean pptStatus)
	{
		this.pptStatus=pptStatus;
	}
	public void setPptPageCount(int pptPageCount)
	{
		this.pptPageCount=pptPageCount;
	}
	public void setPptSize(Long pptSize)
	{
		this.pptSize=pptSize;
	}
	public void setPptTitle(String pptTitle)
	{
		this.pptTitle=pptTitle;
	}
	public void setPptTime(String pptTime)
	{
		this.pptTime=pptTime;
	}
	public Long getPptId()
	{
		return pptId;
	}
	public boolean getPptStatus()
	{
		return pptStatus;
	}
	public int getPptPageCount()
	{
		return pptPageCount;		
	}
	public Long getPptSize()
	{
		return pptSize;
	}
	public String getPptTitle()
	{
		return pptTitle;
	}
	public String getPptTime()
	{
		return pptTime;
	}

}
