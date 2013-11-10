package net.cloudslides.app.utils;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;

import android.graphics.Path;
import android.view.View;

public class MyPathUtils {

    /**
     * 返回x坐标相对于图像坐标轴的比例（十万分比）
     * 例如x坐标1相对于800宽度的图像坐标轴的比例为十万分之125
     * @param x 坐标
     * @param v 视图对象
     * @return 十万分比(整数)(int) ((x/getWidth())*100000);
     */
    public static int getXRatio(float x,View v)
    {
    	return (int) ((x/v.getWidth())*100000);
    }
    
    
    /**
     * 返回y坐标相对于图像坐标轴的比例（十万分比）
     * 例如y坐标1相对于480高度的图像坐标轴的比例为十万分之208
     * @param y 坐标
     * @param v 视图对象
     * @return 十万分比(整数)(int) ((y/getHeight())*100000);
     */
    public static int getYRatio(float y,View v)
    {
    	return (int) ((y/v.getHeight())*100000);
    }
    
    /**
     * 根据比例获取对应图像坐标轴的X坐标值
     * @param xRatio 比例值（十万分比）
     * @param v视图对象
     * @return ((float)xRatio)*getWidth()/100000;
     */
    public static float parseRatioToX(int xRatio,View v)
    {
    	return ((float)xRatio)*v.getWidth()/100000;
    }
    /**
     * 根据比例获取对应图像坐标轴的y坐标值
     * @param yRatio 比例值（十万分比）
     * @param v视图对象
     * @return ((float)yRatio)*getHeight()/100000;
     */
    public static float parseRatioToY(int yRatio,View v)
    {
    	return ((float)yRatio)*v.getHeight()/100000;
    }
    
    /**
     * 将一个原始的整型数组转换为增量存储的数组
     * 对于笔迹坐标庞大的数据长度，可以大大降低其存储的空间(主要是网路IO)
     * 例如: 12345,234,12348,235  ---转化为增量存储--->12345,234,3,1
     * 
     * 【考虑到存储的坐标值为x，y相间存储，故此增量模型的步长为2】
     * 
     * @param originList
     * @return 转换后的增量存储数组
     * @author Felix
     */
    public static ArrayList<Integer> getIncrementalList(ArrayList<Integer>originList)
    {
    	ArrayList<Integer> iList = new ArrayList<Integer>();
    	
    	for(int i=0;i<originList.size();i++)
    	{
    		if(i<2)
    		{
    			iList.add(originList.get(i));
    		}
    		else
    		{
    			iList.add(originList.get(i)-originList.get(i-2));//相间增量，步长2
    		}
    	}    	
    	return iList;
    }
    /**
     * 还原增量模型为原始数据数组，注意步长为2
     * @param list
     * @author Felix
     * @return 
     */
    public static ArrayList<Integer> resetIncrementalList(ArrayList<Integer>list)
    {
    	for(int i = 0 ;i<list.size();i++)
    	{
    		if(i>=2)
    		{
    			list.set(i, list.get(i)+list.get(i-2));
    		}
    	}
    	return list;
    }
    /**
     * 根据坐标比例数组生成一条笔迹路径
     * @param points 坐标比例数组
     * @param v 视图对象
     * @author Felix
     * @return
     */
    public static Path createPath(ArrayList<Integer> points,View v)
    {
    	float preX = 0,preY = 0;
    	float x,y;
    	float cX,cY;
    	
    	Path mPath = new Path();
    	for(int i = 0;i<points.size();i+=2)
    	{
    		x=parseRatioToX(points.get(i), v);
    		y=parseRatioToY(points.get(i+1), v);//每次取两个点写，x y
    		if(i<2)
    		{
    			mPath.moveTo(x, y);
    		}
    		else
    		{
    			 cX = (x + preX) / 2;
                 cY = (y + preY) / 2;
                 mPath.quadTo(preX, preY, cX, cY);
    		}
    		preX=x;
    		preY=y;    		
    	}	    	
    	return mPath;
    }
    
    /**
     * 将JSONArray 转 ArrayList<Integer>
     * @param jsa
     * @return  ArrayList<Integer>
     * @author Felix
     */
    public static ArrayList<Integer> parseJSONArrayToList(JSONArray jsa)
    {
		ArrayList<Integer> list = new ArrayList<Integer>();
		for(int i = 0 ;i<jsa.length();i++)
		{
			try 
			{
				list.add(jsa.getInt(i));
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return list;
    }
} 
