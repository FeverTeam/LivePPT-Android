package com.fever.model;

/**
 * PPT文件类
 * PPT文件信息的设置和查询
 * @author Felix
 */
public class PptFile {
	private Long pptId;	//id
	private boolean pptStatus=false; //转换状态 
	private int pptPageCount;//页码数
	private Long pptSize;//ppt文件大小
	private String pptTitle=""; //ppt名称
	private String pptTime=""; //ppt生成时间


    /**
     * PPT文件类空构造器
     * @param
     * @param
     * @return
     * last modified: Frank
     */
	public PptFile(){};


    /**
     * PPT文件类构造器
     * @param pptId PPT文件Id
     * @param pptStatus PPT转换状态
     * @param pptPageCount PPT页数
     * @param pptSize PPT文件大小
     * @param pptTime ppt生成时间
     * @param pptTitle  PPT名称
     * @return
     * last modified: Frank
     */
	public PptFile(Long pptId,boolean pptStatus,int pptPageCount,Long pptSize,String pptTitle,String pptTime){
		this.pptId=pptId;
		this.pptStatus=pptStatus;
		this.pptSize=pptSize;
		this.pptTime=pptTime;
		this.pptTitle=pptTitle;
	}


    /**
     * 设置PPTid
     * @param pptId
     * @return
     * last modified: Frank
     */
	public void setPptId(Long pptId){
		this.pptId=pptId;
	}


    /**
     * 设置PPT转换状态
     * @param pptStatus
     * @return
     * last modified: Frank
     */
	public void setPptStatus(boolean pptStatus){
		this.pptStatus=pptStatus;
	}


    /**
     * 设置PPT页数
     * @param pptPageCount 页数
     * @return
     * last modified: Frank
     */
	public void setPptPageCount(int pptPageCount){
		this.pptPageCount=pptPageCount;
	}


    /**
     * 设置PPT大小
     * @param pptSize 文件大小
     * @return
     * last modified: Frank
     */
	public void setPptSize(Long pptSize){
		this.pptSize=pptSize;
	}


    /**
     * 设置PPT名称
     * @param pptTitle 文件名称
     * @return
     * last modified: Frank
     */
	public void setPptTitle(String pptTitle){
		this.pptTitle=pptTitle;
	}


    /**
     * 设置PPT生成时间
     * @param pptTime
     * @return
     * last modified: Frank
     */
	public void setPptTime(String pptTime){
		this.pptTime=pptTime;
	}


    /**
     * 获取PPTid
     * @param
     * @return pptId
     * last modified: Frank
     */
	public Long getPptId(){
		return pptId;
	}


    /**
     * 获取PPT转换状态
     * @param
     * @return pptStatus
     * last modified: Frank
     */
	public boolean getPptStatus(){
		return pptStatus;
	}


    /**
     * 获取PPT页数大小
     * @param
     * @return pptPageCount
     * last modified: Frank
     */
	public int getPptPageCount(){
		return pptPageCount;		
	}


    /**
     * 获取PPT文件大小
     * @param
     * @return pptSize
     * last modified: Frank
     */
	public Long getPptSize(){
		return pptSize;
	}


    /**
     * 获取PPT名称
     * @param
     * @return pptTitle
     * last modified: Frank
     */
	public String getPptTitle(){
		return pptTitle;
	}


    /**
     * 获取PPT生成时间
     * @param
     * @return pptTime
     * last modified: Frank
     */
	public String getPptTime(){
		return pptTime;
	}

}
