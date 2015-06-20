package in.motorindiaonline.motorindia.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import in.motorindiaonline.motorindia.R;
import in.motorindiaonline.motorindia.ServerInteraction.Retrivejson;
import in.motorindiaonline.motorindia.Utilities.AlertDialogManager;
import in.motorindiaonline.motorindia.Utilities.CommonData;
import in.motorindiaonline.motorindia.Utilities.ConnectionDetector;

public class ArticleList extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks, Retrivejson.MyCallbackInterface {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    ConnectionDetector connectionDetector;
    AlertDialogManager alert = new AlertDialogManager();

    // Due to the requirement for dynamic lists, i.e this stores the title's that we extract from JSON array that is send by the server.
    ArrayList<String> list_titles = new ArrayList<String>();
    // Same thing, we need a dynamic list for image URL's
    ArrayList<String> imgUrlList = new ArrayList<String>();
    // Similarly we need the list of id's for fetching the actual content - each article we represent in the list has a article id,
    // with which we fetch the article content. So we need this when we send the information about which article was clicked as EXTRA
    ArrayList<Integer> idList = new ArrayList<Integer>();

    // ListView handle for the list in the main navigation view activity
    ListView list;

    // Flag variable for keeping track whether the list should be cleared on next call of the function populate_list,
    // its set to 1 every time we change the category in the navigation drawer, and after that when the next callback from retrivejson occurs,
    // and depending on this value we clear the existing list of list_titles and image url's, thus the callback's info is set as the latest articles in that category

    // TODO BUT if we had launched a yet unfinished AsyncTask before switching to a category, when the article data from that AsyncTask is set for the selected category
    // Thus we should cancel all existing AsyncTask's before starting a new one from a Different category, otherwise they will mix up.
    // This problem is not there if we never scroll down
    int list_clear = 1;

    // till - this is the variable used to call the next set of articles,
    // till is updated to till+NO_TITLES and launchthreadstogettitles is called when we wish
    // to get the next set of article titles's and image URL's.
    int till = NO_TITLES + 1;

    // part of algo.. explain later
    // to make sure only 10 article are called not more than that on scroll down
    int oldtotal = NO_TITLES ,once = 1;

    // for loading animation
    ProgressDialog proggre;


    // GLOBAL CONSTANTS

    // this is the number of titles that are fetched in groups for now I am fetching articles in groups of ten, totally a matter a preference (is it?)
    final static int NO_TITLES = 14;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_list);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        connectionDetector = new ConnectionDetector(getApplicationContext());
        // Check if Internet present
        if (!connectionDetector.isConnectedInternet()) {
            // Here as I need the dialog to be a inner class to send the intent and call finish
            new AlertDialog.Builder(this)
                    .setTitle("Internet Connection Error")
                    .setMessage("Please connect to working Internet connection")
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Log.i(CommonData.TAG,"ok has been clicked");
                            Intent i = new Intent(Settings.ACTION_WIFI_SETTINGS);
                            startActivity(i);
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Log.i(CommonData.TAG,"Cancel has been clicked");
                            finish();
                        }
                    })
                    .setIcon(R.drawable.alert)
                    .setCancelable(false)
                    .show();
        }
        ActionBar actionBar = getSupportActionBar();
        actionBar.setLogo(R.drawable.icon_tentative);

    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();
    }

    // TODO ADD to this and in Strings.xml inorder to change navigation drawer categories
    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
            case 4:
                mTitle = getString(R.string.title_section4);
                break;
            case 5:
                mTitle = getString(R.string.title_section5);
                break;
            case 6:
                mTitle = getString(R.string.title_section6);
                break;
            case 7:
                mTitle = getString(R.string.title_section7);
                break;
            case 8:
                mTitle = getString(R.string.title_section8);
                break;
            case 9:
                mTitle = getString(R.string.title_section9);
                break;
            case 10:
                mTitle = getString(R.string.title_section10);
                break;
            case 11:
                mTitle = getString(R.string.title_section11);
                break;
            case 12:
                mTitle = getString(R.string.title_section12);
                break;
            case 13:
                mTitle = getString(R.string.title_section13);
                break;
            case 14:
                mTitle = getString(R.string.title_section14);
                break;
            case 15:
                mTitle = getString(R.string.title_section15);
                break;
            case 16:
                mTitle = getString(R.string.title_section16);
                break;
            case 17:
                mTitle = getString(R.string.title_section17);
                break;
            case 18:
                mTitle = getString(R.string.title_section18);
                break;
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Just call restoreActionBar, as we do list update there
            restoreActionBar();
            return true;
        }
        return true;
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(Html.fromHtml("<font color='#CC0000'><strong>MOTORINDIA</strong></font>"));
        actionBar.setIcon(R.drawable.icon_tentative);
        // TODO remove this to have the TITLE vary with category
        //actionBar.setTitle(mTitle);
        // TODO when we switch orientation  each time the action bar is set and the same AsyncTask is launched twice (or more?)
        if(connectionDetector.isConnectedInternet()){
            // because we don't want the last sections's titles to remain in the ListView we set it to clear by
            list_clear = 1;

            // As we need to show the latest articles from now, and use the old till to continue fetching articles
            till = NO_TITLES+1;

            // as once we load a set of articles in a particular category, once might be stuck as 0
            // this gets carried over to other categories (thats why categories weren't loading)
            //thus we need once to be 1 every time we open a new category
            once = 1;

            //proggre = new ProgressDialog(this);
            //proggre.setCancelable(true);
            //proggre.show();

            // And we launch the thread to get the list data for the category we just selected and switched to.(mTitle)
            launchthreadstogettitles(till, mTitle.toString());
        }
        else{
            // Internet Connection is not present
            // Here as I need the dialog to be a inner class to send the intent and call finish
            new AlertDialog.Builder(this)
                    .setTitle("Internet Connection Error")
                    .setMessage("Please connect to working Internet connection")
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Log.i(CommonData.TAG, "ok has been clicked");
                            Intent i = new Intent(Settings.ACTION_WIFI_SETTINGS);
                            startActivity(i);
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Log.i(CommonData.TAG,"Cancel has been clicked");
                            finish();
                        }
                    })
                    .setIcon(R.drawable.alert)
                    .setCancelable(false)
                    .show();
        }
    }

    /* SETTINGS CAN BE IMPLEMENTED LATER

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.article_list, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    */

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_article_list, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((ArticleList) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

    // New functions / Classes

    //a function to populate the list with titles from list_titles (as of now)
    public void populatelist(){
        if(connectionDetector.isConnectedInternet()){
            //this sets up the static array from the dynamic array cause the adapter's constructor needs a static list
            String[] titlearray = list_titles.toArray(new String[list_titles.size()]);
            String[] imageurl = imgUrlList.toArray(new String[imgUrlList.size()]);

            //calls the constructor to crate a adapter with the titles currently residing in 'list_titles' and images corresponding to the URL's in 'imgUrlList'
            CustomList adapter = new CustomList(ArticleList.this, titlearray, imageurl);

            //set "list" the handle, pointing to the respective views
            list=(ListView)findViewById(R.id.listViewArticle);

            //Some times we are asked to populate the list when no such 'list' exists!
            if(list == null){
                // fixed the crash on quick orientation change or basically when this function is called during the period of time when "ListView" is not part of the layout.
                // this happens as this function is called asynchronously, thus its possible to be called during the time the ListView is being drawn.
                return;
            }

            // set the ListView with the adapter we just now created
            list.setAdapter(adapter);

            // set the OnClick listener on to the list, so we can redirect the user to the main article content when he clicks a row on the list
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1,int position, long arg3) {

                    // lets handle the click send the id to Displayarticle activity where the article with ID = 'idList.get(position)' will br displayed
                    Intent intent = new Intent(ArticleList.this,DisplayArticle.class);
                    intent.putExtra(CommonData.EXTRA_MESSAGE, idList.get(position));
                    intent.putExtra(CommonData.EXTRA_URL, imgUrlList.get(position));
                    intent.putExtra(CommonData.EXTRA_TITLE, list_titles.get(position));
                    startActivity(intent);
                }
            });

            // set the OnScroll listener so we can automatically pull more articles as the user scrolls to the bottom of the list
            list.setOnScrollListener(new AbsListView.OnScrollListener(){
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {
                    //I Dont need this but they make me implement it
                }

                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                    //Log.i("debug","first "+Integer.toString(firstVisibleItem)+" visible Item count "+Integer.toString(visibleItemCount)+" Total item count "+Integer.toString(totalItemCount));

                    // if we are in the "Batteries" category where we only have 4 articles
                    if(mTitle.toString().equals(getString(R.string.title_section9))){
                        //we should return as the list is always in such a position that we request more articles
                        // this will cause a overload of asynctasks being launched.
                        return;
                        //TODO I recommend removing this unimportant category.
                        //seriously FOUR ARTICLES WITH 2 image link broken - https://gist.github.com/Aditya8795/6a07aca6fcab2131bf31#file-issue-1-md
                    }

                    // Some crazy ALGO to trigger a update (pull next NO_TITLES articles) when the user reaches the end of the list,
                    // and to ensure that this update runs only once for a particular end of the list. For example, when you reach
                    // the end of the list of NO_TITLES articles, the update triggers but then if they check IMMEDIATLY after that event if the condition
                    // was still true then countless number of update's would occur for a single end of list

                    // How it works? see there is this property that we shall make use of, when you are at the bottom of the list
                    // totalItemCount will be equal to firstVisibleItem + visibleItemCount
                    // why? think, firstVisibleItem will be index of the first visible row, and visibleItemCount is the numbers of rows in sight
                    // now this is the condition for the 'trigger' (am really proud of myself for having thought of this Let me know if u find a better way)
                    // but I can't let it happen continuously
                    // delay lets me do that, see check is 2,5,7,10,12 etc as articles are fetched and totalItemCount becomes 10,20,30 etc..
                    // so when we fetch lots of articles when we are still at the top with firstVisibleItem==0
                    // TODO the focus shifts to the first row every time the list is set. It has to stay at the place where the user was browsing
                    // Still for now this cause delay to jump to 0, making sure the "trigger" activates only once
                    // once apply's a separate layer of protection, it becomes 1 only when the total number of items in the list is no longer the old value
                    // why this layer?, see the delay only works after the list is set and the focus has been shifted, before that in mere milliseconds
                    // the loop would have called 100 titles or more ( it happened! ), so we wait for the list size to change (10 to 20 or 20 to 30, etc)
                    // then only will we even check if Internet is available at all, and finally fetch the next 10, by adding to the value of till.

                    // All rosy and sweet? no, see what if once is set to 0 and old total is 10, i.e you fetch more articles when the list has 10 titles
                    // but then quickly shift category, in the new category, you wont ever get once to become 1!
                    // this has been fixed.
                    int check = totalItemCount/4;
                    int delay;
                    if(firstVisibleItem>=check){
                        delay=1;
                    }
                    else{
                        delay=0;
                    }
                    if(totalItemCount==(firstVisibleItem+visibleItemCount) && delay==1){
                        //all so that it Doesn't do a lot of calls in a single call
                        if(once==1){
                            once=0;
                            // This part of the code is where the list grows, as the user scrolls down
                            // if there is no Internet
                            if(!connectionDetector.isConnectedInternet()){
                                // Internet Connection is not present
                                alert.showAlertDialog(ArticleList.this,
                                        "Internet Connection Error",
                                        "Please connect to working Internet connection", false);
                                return;
                            }
                            //start from 11th latest article when this is first called, then for subsequent calls
                            till=till+NO_TITLES;
                            //And start the threads
                            launchthreadstogettitles(till, mTitle.toString());
                            delay=0;
                            oldtotal=totalItemCount;
                            return;
                        }
                        if(oldtotal!=totalItemCount){
                            once=1;
                        }
                    }
                }
            });
        }
        else{
            // Internet Connection is not present
            // Here as I need the dialog to be a inner class to send the intent and call finish
            new AlertDialog.Builder(this)
                    .setTitle("Internet Connection Error")
                    .setMessage("Please connect to working Internet connection")
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Log.i(CommonData.TAG,"ok has been clicked");
                            Intent i = new Intent(Settings.ACTION_WIFI_SETTINGS);
                            startActivity(i);
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Log.i(CommonData.TAG,"Cancel has been clicked");
                            finish();
                        }
                    })
                    .setIcon(R.drawable.alert)
                    .setCancelable(false)
                    .show();
        }
    }

    //the function which starts the threads which in turn get the titles and set them as soon as we get them in the onrequestcompleted function
    // it gets the NO_TITLES articles before 'till' in the category 'cat' or NO_TITLES upto 'till'
    public void launchthreadstogettitles(int till, String cat ){

        // TODO CHANGE THE tags here to get appropriate server behavior - ie fetch different categories
        if(cat.equals(getString(R.string.title_section3))){
            cat="ConstructionEquipment";
        }
        else if(cat.equals(getString(R.string.title_section8))){
            // this is not sending the "correct" articles - https://gist.github.com/Aditya8795/6a07aca6fcab2131bf31#file-issue-2-md
            cat="Lubes";
        }

        //String link = "http://motorindiaonline.in/mobapp/?s_i="+Integer.toString(till-NO_TITLES)+"&e_i="+Integer.toString(till)+"&cat_i="+cat;
        String link = "http://motorindiaonline.in/mobapp/?s_i=1&e_i=15&cat_i=Trucks";
        new Retrivejson(this).execute(link);
    }

    @Override
    public void onRequestCompleted(JSONArray result) {
        // in case Internet failed etc, no result to work with
        if(result==null){
            return;
        }
        // I got the JSON! i just used a interface! Communication complete!
        // if list_clear==1 it means this is a new category, the data is not to appended to the current ListView
        // the list should be cleared and and these values should be the first in the category they are in.
        if(list_clear==1){
            // delete the stuff we have, to make sure we don't show some other categories's data here
            // TODO we need to save this somewhere and get the application working even when the user is not online
            // as we are getting the latest titles and image URL's for this particular category
            list_titles.clear();
            imgUrlList.clear();
            idList.clear();
            // now onwards further data is treated as part of the category thus is to appended.
            list_clear=0;
        }
        // CORNER CASE - CAUSE 'batteries' only has 4 articles we will go past the index if we iterate 'NO_TITLES' times
        int tempno;
        // if the category is battery, only iterate 4 times
        if(mTitle.toString().equals(getString(R.string.title_section9))){
            tempno=4;
        }
        else{
            // else business as usual, we get NO_TITLES number of data(title and image URL) in each callback
            tempno=NO_TITLES;
        }
        // go through the array of JSON objects and store the title's and image URL's in the respective variables
        for(int i=0;i<tempno;i++){
            try {
                //Just add the titles to the list_titles array
                String temp_title = result.getJSONObject(i).getString("title");
                //Log.i("debug",result.getJSONObject(i).getString("title"));
                list_titles.add(temp_title);
                // check for the case if the image URL is null, empty. if so use a known image
                if(result.getJSONObject(i).getString("image") == null || result.getJSONObject(i).getString("image").isEmpty()){
                    imgUrlList.add("http://spider.nitt.edu/~adityap/resources/images/motorindia_site.png");
                }else{
                    //fetch the image URL store it
                    imgUrlList.add(result.getJSONObject(i).getString("image"));
                }
                // get the ID for sending with the Intent (if the user wants to read that article)
                idList.add(result.getJSONObject(i).getInt("id"));
            } catch (JSONException e) {
                // nothing to here, *for now*
                e.printStackTrace();
            }
        }
        // stop loading
        //proggre.dismiss();
        //populate the list with the fetched data
        populatelist();
    }
}
