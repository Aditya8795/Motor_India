package in.motorindiaonline.motorindia.ServerInteraction;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Retrivejson extends AsyncTask<String, Void, JSONArray> {

    // Here I define the callback interface
    public interface MyCallbackInterface {
        //its supposed to send the JSON object on request completed
        public void onRequestCompleted(JSONArray result);
    }

    private MyCallbackInterface mCallback;

    public Retrivejson(MyCallbackInterface callback) {
        mCallback = callback;
    }

    public JSONArray getJSONFromUrl(String url) {

        // To handle low speed internet
        HttpParams httpParameters = new BasicHttpParams();
        // Set the timeout in milliseconds until a connection is established.
        // The default value is zero, that means the timeout is not used.
        int timeoutConnection = 3000;
        HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
        // Set the default socket timeout (SO_TIMEOUT)
        // in milliseconds which is the timeout for waiting for data.
        int timeoutSocket = 5000;
        HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

        DefaultHttpClient httpclient = new DefaultHttpClient(httpParameters);
        HttpPost httppost = new HttpPost(url);

        // Depends on your web service
        httppost.setHeader("Content-type", "application/json");
        InputStream inputStream = null;
        String result = null;
        try {
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();

            inputStream = entity.getContent();
            // JSON is UTF-8 by default BECAUSE I DIDNT USE THIS METHOD to try to fix issue #5
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
            StringBuilder sb = new StringBuilder();

            String line;
            while ((line = reader.readLine()) != null)
            {
                sb.append(line).append("\n");
            }
            result = sb.toString();
        } catch (Exception e) {
            // Oops
        }
        finally {
            try{
                if(inputStream != null)inputStream.close();
            }
            catch(Exception squish){
                Log.i("DEBUG","sqish! inputstream no work!");
            }
        }

        if(result == null){
            return null;
        }
        try {
            return new JSONArray(result);
        } catch (JSONException e) {
            Log.i("DEBUG", "JSON exception");
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected JSONArray doInBackground(String... params) {
        // this accepts multiple strings as argument the first string is fed into the function getJSONFromUrl where the whole "Work" takes place
        String url = params[0];
        return getJSONFromUrl(url);
    }

    @Override
    protected void onPostExecute(JSONArray result) {
        //So that i have the JSON array ready to use, i use the call back to send it to the 'customlist.java' ;)
        mCallback.onRequestCompleted(result);
    }
}
