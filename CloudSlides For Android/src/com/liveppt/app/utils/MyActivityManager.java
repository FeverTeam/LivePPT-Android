package com.liveppt.app.utils;

import java.util.LinkedList;
import java.util.List;
import android.app.Activity;
import android.app.Application;

/**
 * 维护app的所有Activity，退出时直接调用exit()
 * 在Activity初始化时调用add();
 * @author Felix
 *
 */

public class MyActivityManager extends Application
{
    private static MyActivityManager instance;
    private List<Activity> activityList= new LinkedList<Activity>();
    private MyActivityManager()
    {

    }
    public static MyActivityManager getInstance()
    {
        if(instance==null)
        {
            instance=new MyActivityManager();
        }
        return instance;
    }
    public void add(Activity activity)
    {
        activityList.add(activity);
    }
    public void exit()
    {
        for(Activity activity:activityList)
        {
            activity.finish();
        }
        System.exit(0);
    }

}
