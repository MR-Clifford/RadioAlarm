package xml_mike.radioalarm.managers.parsers;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedList;

public class PLSParser {
	//private static Logger log = Logger.getLogger(PLSParser.class);

	public LinkedList<String> getStreamingUrl(String url) {
		LinkedList<String> murls = new LinkedList<String>();
		try {
			return getStreamingUrl(getConnection(url));
		} catch (MalformedURLException e) {
			Log.e("parser", e.toString());
		} catch (IOException e) {
			Log.e("parser", e.toString());
		}
		murls.add(url);
		return murls;
	}

	public LinkedList<String> getStreamingUrl(URLConnection conn) {
		final BufferedReader br;
		String murl = null;
		LinkedList<String> murls = new LinkedList<String>();
		try {
			br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			while (true) {
				try {
					String line = br.readLine();

					if (line == null) {
						break;
					}
					murl = parseLine(line);
					if (murl != null && !murl.equals("")) {
						Log.e("parser", "Adding URL: " + murl);
						murls.add(murl);

					}
				} catch (IOException e) {
					Log.e("parser", e.toString());
				}
			}
		} catch (MalformedURLException e) {
			Log.e("parser", e.toString());
		} catch (IOException e) {
			Log.e("parser", e.toString());
		}
		murls.add(conn.getURL().toString());
		return murls;
	}

	private String parseLine(String line) {
		if (line == null) {
			return null;
		}
		String trimmed = line.trim();
		if (trimmed.indexOf("http") >= 0) {
			String res = trimmed.substring(trimmed.indexOf("http"));
			if(res.toUpperCase().endsWith("MSWMEXT=.ASF"))
			{
				Log.e("parser", "URL ends with MSWExt=.asf " + res);
				if(res.toUpperCase().startsWith("HTTP://"))
				{
					Log.e("parser", "URL ends with MSWExt=.asf " + res + " and starts wtih 'http://' " + res);
					res = "mmsh://" +  res.substring(7);
					Log.e("parser", "URL 'http://' with 'mmsh://' " + res);
				}
			}
			return res;
		}
		return "";
	}

	private URLConnection getConnection(String url) throws IOException {
		URLConnection mUrl = new URL(url).openConnection();
		return mUrl;
	}

}
