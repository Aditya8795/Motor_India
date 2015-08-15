package in.motorindiaonline.motorindia.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;

import in.motorindiaonline.motorindia.R;
import in.motorindiaonline.motorindia.ServerInteraction.RetrieveJSON;
import in.motorindiaonline.motorindia.Utilities.MotorIndiaPreferences;

public class DisplayArticle extends ActionBarActivity implements RetrieveJSON.MyCallbackInterface {

    String title = "";
    String body = "";
    String date = "";
    ProgressBar mProgressBar;

    String AdvtURL = "http://www.infineon.com/cms/en/applications/automotive/safety/tpms/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_article);

        // To get the icon on the back button in the top left corner
        android.support.v7.app.ActionBar supportActionBar = getSupportActionBar();
        assert supportActionBar != null;
        supportActionBar.setDisplayHomeAsUpEnabled(true);

        supportActionBar.setIcon(R.drawable.mi_logo);


        // launch thread to fetch article content
        Intent intent = getIntent();
        String articleID = intent.getStringExtra(MotorIndiaPreferences.ARTICLE_ID);
        String url = "http://www.motorindiaonline.in/mobapp/?a_id="+articleID;
        new RetrieveJSON(this).execute(url);

        // We already have the title corresponding to the article in the ListView, use that and set the actionBar title
        title = intent.getStringExtra(MotorIndiaPreferences.ARTICLE_TITLE);
        supportActionBar.setTitle(title);
        // This sets the Title of the article just after the Image.
        // the actual content is set after the callback is done. for reasons given there.
        //TextView t = (TextView)findViewById(R.id.textViewTitle);
        //t.setText(title);

        ImageView imageView = (ImageView) findViewById(R.id.imageViewMainArticle);
        String imageURL = intent.getStringExtra(MotorIndiaPreferences.IMAGE_URL);
        // This starts the asynchronous call for the main article image
        Picasso.with(this).load(imageURL).placeholder(R.drawable.white).error(R.drawable.error).fit().centerCrop().into(imageView);

        ImageView banner = (ImageView) findViewById(R.id.imageViewAdvertisement);
        SharedPreferences prefs = getSharedPreferences(MotorIndiaPreferences.BANNER_JSON_ARRAY, MODE_PRIVATE);
        String strJson = prefs.getString("JSONData", "0");//second parameter is necessary ie.,Value to return if this preference does not exist.
        Log.i("BANNER", "we have banner data stored (1/0) => " + strJson);
        assert strJson != null;
        if(!strJson.equals("0")){
            Log.i("BANNER","fetched bannerData");
            try {
                JSONArray BannerData = new JSONArray(strJson);
                int no = prefs.getInt("keepTrack", -1);
                Picasso.with(this).load(BannerData.getJSONObject(no).getString("image_url")).placeholder(R.drawable.advt).error(R.drawable.error).into(banner);
                AdvtURL = BannerData.getJSONObject(no).getString("site_url");
                Log.i("BANNER",AdvtURL);
                Log.i("BANNER",Integer.toString(no));
                Log.i("BANNER",BannerData.getJSONObject(no).getString("image_url"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        mProgressBar = (ProgressBar)findViewById(R.id.articleProgressBar);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_display_article, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent myIntent = new Intent(DisplayArticle.this, MySettings.class);
            this.startActivity(myIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void launchBrowser(View view) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(AdvtURL));
        startActivity(i);
    }

    @Override
    public void onRequestCompleted(JSONArray result) {
        try {
            body = result.getJSONObject(0).getString("content");
            //date = "Date: "+result.getJSONObject(0).getString("date")+"\n\nAuthor: "+result.getJSONObject(0).getString("author");
            date = "Date: "+result.getJSONObject(0).getString("date");
        } catch (JSONException e) {
            // Auto-generated catch block
            e.printStackTrace();
        }
        mProgressBar.setVisibility(ProgressBar.GONE);

        // I set the title here even when I have the title during OnCreate as it looks better if both title and body were set at the same time
        // And the loading animation being superimposed on the title doesn't look good
        TextView t = (TextView)findViewById(R.id.textViewTitle);
        t.setText(title);
        TextView b = (TextView)findViewById(R.id.textViewBody);
        b.setText(Html.fromHtml(body));
        TextView d = (TextView)findViewById(R.id.textViewDate);
        d.setText(date);
    }
}
