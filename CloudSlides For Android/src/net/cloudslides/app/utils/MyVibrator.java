package net.cloudslides.app.utils;

import net.cloudslides.app.HomeApp;
import android.content.Context;
import android.os.Vibrator;

public class MyVibrator {	
	/**
	 * 震动效果
	 * @param timeInMs 震动时间 毫秒
	 * @author Felix
	 */
	public static void doVibration(int timeInMs)
	{
		Vibrator v = (Vibrator) HomeApp.getMyApplication().getSystemService(Context.VIBRATOR_SERVICE);
		v.vibrate(timeInMs);
	}
}
