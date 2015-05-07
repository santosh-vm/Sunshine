package android.santosh.com.sunshine.fragments;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.santosh.com.sunshine.R;
import android.widget.TextView;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {
    private static final String LOG_TAG = DetailActivityFragment.class.getSimpleName();
    private TextView detailsTextView;
    private String mForecastStr;
    private ShareActionProvider mShareActionProvider;
    private static final String FORECAST_SHARE_HASHTAG = "#SunshineApp";

    public DetailActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Intent intent = getActivity().getIntent();
        if(intent!=null && intent.hasExtra(MainActivityFragment.FORECAST_INTENT_EXTRA_KEY)) {
            mForecastStr = intent.getStringExtra(MainActivityFragment.FORECAST_INTENT_EXTRA_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        bindUIElements(rootView);
        setDataInUIElements();
        return rootView;
    }

    private void bindUIElements(View rootView){
        detailsTextView = (TextView)rootView.findViewById(R.id.details_text);
    }

    private void setDataInUIElements(){
        if(mForecastStr !=null && !TextUtils.isEmpty(mForecastStr)) {
            detailsTextView.setText(mForecastStr);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.detailactivityfragment,menu);
        MenuItem shareMenuItem = menu.findItem(R.id.action_share);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(shareMenuItem);
        if(mShareActionProvider != null){
            mShareActionProvider.setShareIntent(createShareForecastIntent());
        } else {
            Log.d(LOG_TAG,"Share Action Provider is null?");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_share){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private Intent createShareForecastIntent(){
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT,mForecastStr + FORECAST_SHARE_HASHTAG);
        return shareIntent;
    }
}
