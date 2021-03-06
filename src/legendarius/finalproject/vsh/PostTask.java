package legendarius.finalproject.vsh;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

/** This is an asynchronous task sending a POST Request to Clarifai server */
/** in order to receive an access token, which is needed to use the API */



public class PostTask extends AsyncTask<String, String, String> {
	
	private HttpClient httpclient;
	
	@Override
	protected String doInBackground(String... data) {
		// TODO Auto-generated method stub

		try {
	    	httpclient = new DefaultHttpClient();
	    	HttpPost httppost = new HttpPost("https://api.clarifai.com/v1/token/");
	    	
	    	List<NameValuePair> nvp = new ArrayList<NameValuePair>(3);
	  		nvp.add(new BasicNameValuePair("client_id", Credentials.CLIENT_ID));
	   		nvp.add(new BasicNameValuePair("client_secret", Credentials.CLIENT_SECRET));
	   		nvp.add(new BasicNameValuePair("grant_type", "client_credentials"));
	   		
	    	httppost.setEntity(new UrlEncodedFormEntity(nvp));
	    		
	    	HttpResponse response = httpclient.execute(httppost);
	    	String resp = EntityUtils.toString(response.getEntity());
	    	Log.i("TokenAttempt", resp);
	    	
	    	JSONObject jo = new JSONObject(resp);
	    	String accessToken = (String)jo.get("access_token");
	    	
	    	Credentials.setAccessToken(accessToken);
	    	Log.i("parsed access_token: ", accessToken);
	    	Log.i("Credentials AT", Credentials.accessToken);
	    	
		} catch (ClientProtocolException e) {} catch (IOException e) {} catch (JSONException e) {}
		
		return null;
	}
}
