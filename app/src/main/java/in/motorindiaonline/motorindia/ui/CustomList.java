package in.motorindiaonline.motorindia.ui;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import in.motorindiaonline.motorindia.R;

public class CustomList extends ArrayAdapter<String> {

    private final Activity context;
    private final String[] title;
    private final String[] imageurl;

    //constructor of this class, this called when we initialize the adapter
    public CustomList(Activity context,String[] title, String[] imageurl) {
        super(context, R.layout.single_line, title);
        this.context = context;
        this.title = title;
        this.imageurl = imageurl;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        //TODO WHY??
        View rowView= inflater.inflate(R.layout.single_line, null, true);
        TextView txtTitle = (TextView) rowView.findViewById(R.id.articleTitle);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
        Picasso.with(context).load(imageurl[position]).placeholder(R.drawable.loading).error(R.drawable.error).resize(240, 180).centerInside().into(imageView);
        // set the according title for this row by selecting the title from 'position'
        txtTitle.setText(title[position]);
        return rowView;
    }
}
