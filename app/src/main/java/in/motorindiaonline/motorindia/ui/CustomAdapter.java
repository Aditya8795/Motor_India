package in.motorindiaonline.motorindia.ui;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;

import in.motorindiaonline.motorindia.R;
import in.motorindiaonline.motorindia.Utilities.MotorIndiaPreferences;


public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {
    private static final String TAG = "CustomAdapter";

    private static JSONArray articleJSONArray;
    private static Context c;

    /**
     * Provide a reference to the type of views that you are using (custom ViewHolder)
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;
        private final ImageView imageView;

        public ViewHolder(View v) {
            super(v);
            // Define click listener for the ViewHolder's View.
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Element " + getPosition() + " clicked.");
                    try {
                        String articleID = articleJSONArray.getJSONObject(getPosition()).getString("id");
                        String articleTitle = articleJSONArray.getJSONObject(getPosition()).getString("title");
                        String imageURL = articleJSONArray.getJSONObject(getPosition()).getString("image");
                        Log.i(TAG,"the id of the article is"+articleID);
                        Intent intent = new Intent(c,DisplayArticle.class);
                        intent.putExtra(MotorIndiaPreferences.ARTICLE_ID, articleID);
                        intent.putExtra(MotorIndiaPreferences.ARTICLE_TITLE, articleTitle);
                        intent.putExtra(MotorIndiaPreferences.IMAGE_URL, imageURL);
                        c.startActivity(intent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            textView = (TextView) v.findViewById(R.id.articleTitle);
            imageView = (ImageView) v.findViewById(R.id.icon);

            c = v.getContext();
        }

        public TextView getTextView() {
            return textView;
        }

        public ImageView getImageView() {
            return imageView;
        }
    }

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param dataSet String[] containing the data to populate views to be used by RecyclerView.
     */
    public CustomAdapter(String[] dataSet) {
        try {
            articleJSONArray = new JSONArray(dataSet[0]);
        } catch (JSONException e) {
            Log.i(TAG,"parsing to JSONArray failed");
            e.printStackTrace();
        }
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view.
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.single_line, viewGroup, false);

        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        Log.d(TAG, "Element " + position + " set.");
        // Get element from your dataSet at this position and replace the contents of the view
        // with that element
        String title = "Failed to fetch";
        String imgURL = "http://www.motorindiaonline.in/wp-content/uploads/2012/11/mi-logo-Copy-450-red.png";
        try {
            title = articleJSONArray.getJSONObject(position).getString("title");
            imgURL = articleJSONArray.getJSONObject(position).getString("image");
        } catch (JSONException e) {
            Log.i(TAG,"we don't have title or/and image in this JSON -> "+articleJSONArray.toString());
            Log.i(TAG,"So its okay to ignore the following org.json.JSONException: No value for title");
            e.printStackTrace();
        }
        viewHolder.getTextView().setText(title);
        if(imgURL.isEmpty()){
            imgURL = "http://www.motorindiaonline.in/wp-content/uploads/2012/11/mi-logo-Copy-450-red.png";
        }
        //Picasso.with(c).load(imgURL).placeholder(R.drawable.loading).error(R.drawable.error).resize(240, 180).centerInside().into(viewHolder.getImageView());
        Picasso.with(c).load(imgURL).placeholder(R.drawable.loading).error(R.drawable.error).resize(240, 180).into(viewHolder.getImageView());

    }

    // Return the size of your dataSet (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return articleJSONArray.length();
    }
}
