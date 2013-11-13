package net.cloudslides.app.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

public class ChatInfo
{
	public String publisherEmail;
	public String publisherDisplayName;
	public String chatText;
	public String time;
	public ChatInfo(String str)
	{
		parser(str);
	}
	public String getFormatTime()
	{
		Date date = new Date(Long.valueOf(this.time));
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.US);
		return format.format(date);
	}
	
	private void parser(String json)
	{
		try 
		{
			JSONObject jso = new JSONObject(json);
			this.publisherDisplayName=jso.getString("pdn");
			this.chatText = jso.getString("text");
			this.time = jso.getString("time");
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public String formatJSON()
	{
		return "{\"pe\":"+publisherEmail +
                ",\"pdn\":" + publisherDisplayName +
                ",\"text\":" + chatText + ",\"time\":"+time+"}";
	}
	
}