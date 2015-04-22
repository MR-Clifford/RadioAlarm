package xml_mike.radioalarm.managers.parsers;

import android.util.Log;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedList;

public class FileParser {
	
	//private static Logger log = Logger.getLogger(FileParser.class);
	public static String getURL(String url)
	{
		String uURL = url.toUpperCase();
		if(uURL.endsWith(".FLAC"))
		{
			Log.e("parser", "FLAC File: " + url);
			return url;
		}
		else if(uURL.endsWith(".MP3"))
		{
			Log.e("parser", "MP3 File: " + url);
			return url;
		}
		else if(uURL.endsWith(".WAV"))
		{
			Log.e("parser", "WAV File: " + url);
			return url;
		}
		else if (uURL.endsWith(".M4A"))
		{
			Log.e("parser", "M4A File: " + url);
			return url;
		}
		else if(uURL.endsWith(".PLS"))
		{
			Log.e("parser", "PLS File: " + url);
			PLSParser pls = new PLSParser();
			LinkedList<String> urls = pls.getStreamingUrl(url);
			if(urls.size()>0)
			{
				return urls.get(0);
			}
		}
		else if(uURL.endsWith(".M3U"))
		{
			Log.e("parser", "M3U File: " + url);
			M3UParser m3u = new M3UParser();
			LinkedList<String> urls = m3u.getStreamingUrl(url);
			if((urls.size()>0))
			{
				return urls.get(0);
			}
		}
		else if(uURL.endsWith(".ASX"))//|| url.toUpperCase().endsWith("WMA_UK_CONCRETE"))
		{
			ASXParser asx = new ASXParser(); 
			Log.e("parser", "ASX File: " + url);
			LinkedList<String> urls = asx.getStreamingUrl(url);
			if((urls.size()>0))
			{
				return urls.get(0);
			}
		}
		else
		{
			URLConnection conn = getConnection(url);
			if(conn!=null)
			{
                Log.e("parser", "URL: " + url + " Headers: " + conn.getHeaderFields());
				String content_disp = conn.getHeaderField("Content-Disposition");
				Log.e("parser", "ContentDisposition:" + content_disp);
				String content_type = conn.getContentType();
				if(content_type !=null)
				{
					content_type = content_type.toUpperCase();
				}
				if(content_disp !=null && content_disp.toUpperCase().endsWith("M3U"))
				{
					Log.e("parser", "M3U File: " + url);
					M3UParser m3u = new M3UParser();
					LinkedList<String> urls = m3u.getStreamingUrl(conn);
					if(urls.size()> 0)
					{
						return urls.getFirst();
					}
				}
				
				else if(content_type != null && content_type.contains("AUDIO/X-SCPLS"))
				{
					Log.e("parser", "PLS File: " + url);
					PLSParser pls = new PLSParser();
					LinkedList<String> urls = pls.getStreamingUrl(conn);
					if(urls.size()> 0)
					{
						return urls.getFirst();
					}
				}
				else if(content_type != null && content_type.contains("VIDEO/X-MS-ASF"))
				{
					ASXParser asx = new ASXParser(); 
					Log.e("parser", "ASX File: " + url);
					LinkedList<String> urls = asx.getStreamingUrl(url);
					if((urls.size()>0))
					{
						return urls.get(0);
					}
					Log.e("parser", "ContentType was VIDEO/X-MS-ASF but could not parse .asx file, attempt to parse as .PLS File ");
					PLSParser pls = new PLSParser();
					urls = pls.getStreamingUrl(url);
					if((urls.size()>0))
					{
						return urls.get(0);
					}
				}
				else if(content_type != null && content_type.contains("AUDIO/MPEG"))
				{
					Log.e("parser", "MPEG File: " + url);
					return url;
				}
				else if (content_type != null && content_type.contains("AUDIO/X-MPEGURL"))
				{
					Log.e("parser", "M3U File: " + url);
					M3UParser m3u = new M3UParser();
					LinkedList<String> urls = m3u.getStreamingUrl(url);
					if((urls.size()>0))
					{
						return urls.get(0);
					}
				}
				else
				{

				}
			}
		}
		return url;
	}
	
	private static URLConnection getConnection(String url)
	{
		URLConnection mUrl;
		try {
			mUrl = new URL(url).openConnection();
			return mUrl;
		} catch (MalformedURLException e) {
			Log.e("parser", e.toString());
		} catch (IOException e) {
			Log.e("parser", e.toString());
		}
		return null;
	}

}
