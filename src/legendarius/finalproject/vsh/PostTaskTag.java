package legendarius.finalproject.vsh;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

/** Server returns TOKEN_INVALID */

//https://androidtutorialmagic.wordpress.com/oauth2-tutorial-in-android/curl-to-http-post-request-in-android-volley/

public class PostTaskTag extends AsyncTask<String, String, String> {
	
	private HttpClient httpclient;
	private boolean useURL;
	
	@Override
	protected String doInBackground(String... data) {
		// TODO Auto-generated method stub

		try {
			// change this value in code to determine whether online pic will be used
			// or offline pic (found in my personal smartphone) will be used
			useURL = true;
			
	    	httpclient = new DefaultHttpClient();
	    	HttpPost httppost = new HttpPost(new URI("https://api.clarifai.com/v1/tag/"));
	    	httppost.addHeader(BasicScheme.authenticate(
	    			new UsernamePasswordCredentials("Authorization: Bearer ", "XgAEnBXe0l0ki5kgOcVhxlYZi1veXX"),
	    			HTTP.UTF_8, false));
	    	
	    	List<NameValuePair> nvp = new ArrayList<NameValuePair>(1);
	    	
	    	// determines whether the URL example will be used or the local file on smartphone
	    	if (useURL == true)
	    		{ nvp.add(new BasicNameValuePair("url", "https://samples.clarifai.com/metro-north.jpg")); }
	    	else 
	    		{ nvp.add(new BasicNameValuePair("encoded_data", "@" + CamController.SCAVENGER_FILE)); }
	   		
	  		Log.i("Directory check", CamController.SCAVENGER_FILE);
	  		
	  		// might check the contents of nvp here
	  		
	  		
	    	httppost.setEntity(new UrlEncodedFormEntity(nvp));
	    		
	    	HttpResponse response = httpclient.execute(httppost);
	    	String resp = EntityUtils.toString(response.getEntity());
	    	Log.i("TagTest", resp); // Token invalid, authentication error returned
	    	
	    	JSONObject jo = new JSONObject(resp);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
}
