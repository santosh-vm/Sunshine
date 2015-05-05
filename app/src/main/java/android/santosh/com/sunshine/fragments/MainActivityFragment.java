package android.santosh.com.sunshine.fragments;

import android.os.Bundle;
import android.santosh.com.sunshine.R;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by Santosh on 5/5/15.
 */
public class MainActivityFragment extends Fragment {
    private ArrayList<String> weekForecast;
    private ListView weekForecastListView;
    private ArrayAdapter<String> mForecastArrayAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        bindUIElements(rootView);
        initializeWeekForecastArrayList();
        buildListView();
        return rootView;
    }

    private void bindUIElements(View rootView){
        weekForecastListView = (ListView)rootView.findViewById(R.id.listview_forecast);
    }

    private void initializeWeekForecastArrayList(){
        weekForecast = new ArrayList<String>()
                                {
                                    {
                                        add("Today - Sunny - 88/63");
                                        add("Tomorrow - Foggy - 70/46");
                                        add("Weds - Cloudy - 72/63");
                                        add("Thurs - Rainy - 64/51");
                                        add("Fri - Foggy - 70/46");
                                        add("Sat = Sunny - 76/68");
                                    }
                                };
    }

    private void buildListView(){
        mForecastArrayAdapter = new ArrayAdapter<>(getActivity(),R.layout.list_item_forecast,R.id.list_item_forecast_textview,weekForecast);
        weekForecastListView.setAdapter(mForecastArrayAdapter);
    }
}
