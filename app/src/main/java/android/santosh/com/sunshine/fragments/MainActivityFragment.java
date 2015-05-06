package android.santosh.com.sunshine.fragments;


import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.santosh.com.sunshine.R;
import android.santosh.com.sunshine.utils.JSONDataExtractor;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Santosh on 5/5/15.
 */
public class MainActivityFragment extends Fragment {
    private List<String> weekForecast;
    private ListView weekForecastListView;
    private ArrayAdapter<String> mForecastArrayAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.forecastfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_refresh){
            FetchWeatherTask fetchWeatherTask = new FetchWeatherTask("json","metric",7);
            fetchWeatherTask.execute("90292");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    class FetchWeatherTask extends AsyncTask<String, Void, String[]> {
        private String LOG_TAG = FetchWeatherTask.class.getSimpleName();
        private String format, units;
        private int numDays;
        private JSONDataExtractor jsonDataExtractor;

        FetchWeatherTask(String format, String units, int numDays){
            this.format = format;
            this.units = units;
            this.numDays = numDays;
            jsonDataExtractor = new JSONDataExtractor();
        }

        @Override
        protected String[] doInBackground(String... params) {
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String forecastJsonStr = null;

            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                final String FORECAST_BASE_URL = "http://api.openweathermap.org/data/2.5/forecast/daily";
                final String QUERY_PARAM = "q";
                final String FORMAT_PARAM = "mode";
                final String UNITS_PARAM = "units";
                final String DAYS_PARAM = "cnt";

                Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                        .appendQueryParameter(QUERY_PARAM, params[0])
                        .appendQueryParameter(FORMAT_PARAM, format)
                        .appendQueryParameter(UNITS_PARAM,units)
                        .appendQueryParameter(DAYS_PARAM,Integer.toString(numDays))
                        .build();
                URL url = new URL(builtUri.toString());

                //Log.v(LOG_TAG, "Built URI " + builtUri.toString());

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                forecastJsonStr = buffer.toString();
                //Log.d(LOG_TAG,"Forecast JSON String: "+forecastJsonStr);
                try {
                    return jsonDataExtractor.getWeatherDataFromJson(forecastJsonStr, numDays);
                }
                catch(JSONException je){
                    Log.e(LOG_TAG, "JSONException", je);
                }
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally{
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String[] result) {
            super.onPostExecute(result);
            if(result!=null && result.length>0){
                mForecastArrayAdapter.clear();
                if(Build.VERSION.SDK_INT > 10 ) {
                    mForecastArrayAdapter.addAll(result);
                }else {
                    for (String dayForecastStr : result) {
                        mForecastArrayAdapter.add(dayForecastStr);
                    }
                }
            }
        }
    }
}
