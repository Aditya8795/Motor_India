package in.motorindiaonline.motorindia.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import in.motorindiaonline.motorindia.R;
import in.motorindiaonline.motorindia.Utilities.CommonData;
import in.motorindiaonline.motorindia.Utilities.MotorIndiaPreferences;

public class MainMenu extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    String url = CommonData.SITE_URL;

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    public static final String TAG = "Main Menu";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        /*update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();
        */

        //TODO never hardcode
        if(position == 17 || position == 18){
            if(position == 17){
                url = "http://www.motorindiaonline.in/catalogs-brochures/";
            }
            if(position == 18){
                url = "http://www.motorindiaonline.in/subscribe/";
            }

            Log.i(TAG,"you will be shown a image");
            // Create a new fragment and replace container
            Fragment fragment = new ImageFragment();
            Bundle args = new Bundle();
            args.putInt(MotorIndiaPreferences.CATEGORY_NUMBER, position);
            fragment.setArguments(args);

            // Insert the fragment by replacing any existing fragment
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.container, fragment)
                    .commit();
            return;
        }

        // Create a new fragment and replace container
        Fragment fragment = new LoadingFragment();
        Bundle args = new Bundle();
        args.putInt(MotorIndiaPreferences.CATEGORY_NUMBER, position);
        fragment.setArguments(args);

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commit();

    }

    public void onSectionAttached(int number) {
        //TODO Add categories here
        switch (number) {
            case 0:
                mTitle = getString(R.string.title_section1);
                break;
            case 1:
                mTitle = getString(R.string.title_section2);
                break;
            case 2:
                mTitle = getString(R.string.title_section3);
                break;
            case 3:
                mTitle = getString(R.string.title_section4);
                break;
            case 4:
                mTitle = getString(R.string.title_section5);
                break;
            case 5:
                mTitle = getString(R.string.title_section6);
                break;
            case 6:
                mTitle = getString(R.string.title_section7);
                break;
            case 7:
                mTitle = getString(R.string.title_section8);
                break;
            case 8:
                mTitle = getString(R.string.title_section9);
                break;
            case 9:
                mTitle = getString(R.string.title_section10);
                break;
            case 10:
                mTitle = getString(R.string.title_section11);
                break;
            case 11:
                mTitle = getString(R.string.title_section12);
                break;
            case 12:
                mTitle = getString(R.string.title_section12);
                break;
            case 13:
                mTitle = getString(R.string.title_section14);
                break;
            case 14:
                mTitle = getString(R.string.title_section15);
                break;
            case 15:
                mTitle = getString(R.string.title_section16);
                break;
            case 16:
                mTitle = getString(R.string.title_section17);
                break;
            case 17:
                mTitle = getString(R.string.title_section18);
                break;
            case 18:
                mTitle = getString(R.string.title_section19);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        // Deprecated - TODO loads of deprecated API being used
        // I may have to move to using toolbar, rather than a actionbar
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main_menu, menu);
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
            //TODO launch intent to settings page
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void launchBrowser(View view) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

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
            View rootView = inflater.inflate(R.layout.fragment_main_menu, container, false);

            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainMenu) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

    /**
     *  The fragment to hold Articles
     */
    public static class ArticleFragment extends Fragment {

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static ArticleFragment newInstance(int sectionNumber) {
            ArticleFragment fragment = new ArticleFragment();
            Bundle args = new Bundle();
            args.putInt(MotorIndiaPreferences.CATEGORY_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public ArticleFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_articlelist, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainMenu) activity).onSectionAttached(
                    getArguments().getInt(MotorIndiaPreferences.CATEGORY_NUMBER));
        }
    }

    /**
     *  The fragment to hold Articles
     */
    public static class ImageFragment extends Fragment {

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static ImageFragment newInstance(int sectionNumber) {
            ImageFragment fragment = new ImageFragment();
            Bundle args = new Bundle();
            args.putInt(MotorIndiaPreferences.CATEGORY_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public ImageFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_image, container, false);

            Integer position = getArguments().getInt(MotorIndiaPreferences.CATEGORY_NUMBER);
            ImageView img = (ImageView)rootView.findViewById(R.id.imageView);
            // TODO never hardcode
            if(position == 17){
                Log.i(TAG,"Setting catalog picture");
                img.setImageResource(R.drawable.cata);
            }
            else if(position == 18){
                Log.i(TAG,"Setting subscribe photo");
                img.setImageResource(R.drawable.subscribe);
            }

            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainMenu) activity).onSectionAttached(
                    getArguments().getInt(MotorIndiaPreferences.CATEGORY_NUMBER));
        }
    }

    /**
     *  The fragment to show a loading symbol
     */
    public static class LoadingFragment extends Fragment {

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static LoadingFragment newInstance(int sectionNumber) {
            LoadingFragment fragment = new LoadingFragment();
            Bundle args = new Bundle();
            args.putInt(MotorIndiaPreferences.CATEGORY_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public LoadingFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_loading, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainMenu) activity).onSectionAttached(
                    getArguments().getInt(MotorIndiaPreferences.CATEGORY_NUMBER));
        }
    }

}
