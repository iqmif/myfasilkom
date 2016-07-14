package id.ac.ui.cs.mysunshine;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class ForecastFragment extends Fragment {
    ArrayAdapter<String> mForecastAdapter;
    List<String> weekForecast;
    View rootView;
    public ForecastFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateWeather();
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.forecastfragment,menu);
    }

    private void updateWeather(){
        FetchWeatherTask weatherTask = new FetchWeatherTask();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String location = prefs.getString(getString(R.string.pref_location_key),getString(R.string.pref_location_default));
        weatherTask.execute(location);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_refresh){
            updateWeather();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        rootView = inflater.inflate(R.layout.fragment_main, container, false);
        String[] als = {"cek cek cek"};
        weekForecast = Arrays.asList(als);

        //sudah diupdate pake arraylist kosong
        mForecastAdapter = new ArrayAdapter<String>(
                getActivity(),R.layout.list_item_forecast,R.id.list_item_forecast_textview,new ArrayList<String>()
        );

        //get reference to listview and attach this adapter to listview
        ListView listView = (ListView) rootView.findViewById(R.id.listview_forecast);
        listView.setAdapter(mForecastAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String forecast = mForecastAdapter.getItem(position);
                Toast.makeText(getActivity(), forecast, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(),DetailActivity.class)
                        .putExtra(Intent.EXTRA_TEXT,forecast);
                startActivity(intent);
            }
        });
        return rootView;
    }

    public class FetchWeatherTask extends AsyncTask<String, Void, String[]>{
        private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();

        @Override
        protected String[] doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String forecastJsonStr = null;

            String format = "json";
            String unit = "metric";
            String appid = "fed2d2ce4c212a96f5cbb56f80ce8805";
            int numDays = 7;

            try{
                final String FORECAST_BASE_URL = "http://api.openweathermap.org/data/2.5/forecast/daily?";
                final String QUERY_PARAM = "q";
                final String FORMAT_PARAM = "mode";
                final String UNITS_PARAM = "units";
                final String DAYS_PARAM = "cnt";
                final String APPID_PARAM = "APPID";

                Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                        .appendQueryParameter(QUERY_PARAM,params[0])
                        .appendQueryParameter(FORMAT_PARAM,format)
                        .appendQueryParameter(UNITS_PARAM,unit)
                        .appendQueryParameter(DAYS_PARAM,Integer.toString(numDays))
                        .appendQueryParameter(APPID_PARAM,appid)
                        .build();

                URL url = new URL(builtUri.toString());
                //URL url = new URL("http://api.openweathermap.org/data/2.5/forecast/daily?q=jakarta&mode=json&units=metric&cnt=7&APPID=fed2d2ce4c212a96f5cbb56f80ce8805");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                //read the inputStream
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if(inputStream==null){
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while((line = reader.readLine()) != null){
                    buffer.append(line+"\n");
                }

                if(buffer.length()==0){
                    return null;
                }

                forecastJsonStr = buffer.toString();
                //forecastJsonStr = "{\"city\":{\"id\":3093133,\"name\":\"Lodz\",\"coord\":{\"lon\":19.466669,\"lat\":51.75},\"country\":\"PL\",\"population\":0},\"cod\":\"200\",\"message\":0.0385,\"cnt\":7,\"list\":[{\"dt\":1465984800,\"temp\":{\"day\":23.55,\"min\":15.69,\"max\":25.31,\"night\":15.69,\"eve\":25.31,\"morn\":20.57},\"pressure\":1000.06,\"humidity\":85,\"weather\":[{\"id\":501,\"main\":\"Rain\",\"description\":\"moderate rain\",\"icon\":\"10d\"}],\"speed\":1.96,\"deg\":169,\"clouds\":12,\"rain\":5.96},{\"dt\":1466071200,\"temp\":{\"day\":20.51,\"min\":13.52,\"max\":24.18,\"night\":19.14,\"eve\":24.18,\"morn\":13.52},\"pressure\":1006.27,\"humidity\":83,\"weather\":[{\"id\":500,\"main\":\"Rain\",\"description\":\"light rain\",\"icon\":\"10d\"}],\"speed\":4.82,\"deg\":226,\"clouds\":8,\"rain\":0.23},{\"dt\":1466157600,\"temp\":{\"day\":25.4,\"min\":15.53,\"max\":25.4,\"night\":15.53,\"eve\":22.56,\"morn\":16.68},\"pressure\":999.87,\"humidity\":70,\"weather\":[{\"id\":500,\"main\":\"Rain\",\"description\":\"light rain\",\"icon\":\"10d\"}],\"speed\":8.21,\"deg\":169,\"clouds\":20,\"rain\":1.63},{\"dt\":1466244000,\"temp\":{\"day\":24.45,\"min\":16.09,\"max\":24.45,\"night\":16.09,\"eve\":24.38,\"morn\":20.27},\"pressure\":1011.02,\"humidity\":0,\"weather\":[{\"id\":500,\"main\":\"Rain\",\"description\":\"light rain\",\"icon\":\"10d\"}],\"speed\":4.64,\"deg\":224,\"clouds\":9},{\"dt\":1466330400,\"temp\":{\"day\":26.18,\"min\":17.02,\"max\":26.18,\"night\":17.02,\"eve\":22.8,\"morn\":21.92},\"pressure\":1009.53,\"humidity\":0,\"weather\":[{\"id\":501,\"main\":\"Rain\",\"description\":\"moderate rain\",\"icon\":\"10d\"}],\"speed\":1.4,\"deg\":96,\"clouds\":38,\"rain\":8.43},{\"dt\":1466416800,\"temp\":{\"day\":23.74,\"min\":16.23,\"max\":23.74,\"night\":16.23,\"eve\":21.32,\"morn\":18.2},\"pressure\":1007.4,\"humidity\":0,\"weather\":[{\"id\":501,\"main\":\"Rain\",\"description\":\"moderate rain\",\"icon\":\"10d\"}],\"speed\":6.31,\"deg\":9,\"clouds\":74,\"rain\":5.15},{\"dt\":1466503200,\"temp\":{\"day\":16.72,\"min\":10.98,\"max\":17.88,\"night\":10.98,\"eve\":14.4,\"morn\":17.88},\"pressure\":1001.15,\"humidity\":0,\"weather\":[{\"id\":502,\"main\":\"Rain\",\"description\":\"heavy intensity rain\",\"icon\":\"10d\"}],\"speed\":8.52,\"deg\":264,\"clouds\":85,\"rain\":17.6}]}";
                Log.v(LOG_TAG,"forecast json string: "+forecastJsonStr);

            }catch(IOException ioe){
                Log.e("ForecastFragment","Error",ioe);
                //return null;
            }finally {
                //should not be here
//                forecastJsonStr = "{\"city\":{\"id\":3093133,\"name\":\"Lodz\",\"coord\":{\"lon\":19.466669,\"lat\":51.75},\"country\":\"PL\",\"population\":0},\"cod\":\"200\",\"message\":0.0385,\"cnt\":7,\"list\":[{\"dt\":1465984800,\"temp\":{\"day\":23.55,\"min\":15.69,\"max\":25.31,\"night\":15.69,\"eve\":25.31,\"morn\":20.57},\"pressure\":1000.06,\"humidity\":85,\"weather\":[{\"id\":501,\"main\":\"Rain\",\"description\":\"moderate rain\",\"icon\":\"10d\"}],\"speed\":1.96,\"deg\":169,\"clouds\":12,\"rain\":5.96},{\"dt\":1466071200,\"temp\":{\"day\":20.51,\"min\":13.52,\"max\":24.18,\"night\":19.14,\"eve\":24.18,\"morn\":13.52},\"pressure\":1006.27,\"humidity\":83,\"weather\":[{\"id\":500,\"main\":\"Rain\",\"description\":\"light rain\",\"icon\":\"10d\"}],\"speed\":4.82,\"deg\":226,\"clouds\":8,\"rain\":0.23},{\"dt\":1466157600,\"temp\":{\"day\":25.4,\"min\":15.53,\"max\":25.4,\"night\":15.53,\"eve\":22.56,\"morn\":16.68},\"pressure\":999.87,\"humidity\":70,\"weather\":[{\"id\":500,\"main\":\"Rain\",\"description\":\"light rain\",\"icon\":\"10d\"}],\"speed\":8.21,\"deg\":169,\"clouds\":20,\"rain\":1.63},{\"dt\":1466244000,\"temp\":{\"day\":24.45,\"min\":16.09,\"max\":24.45,\"night\":16.09,\"eve\":24.38,\"morn\":20.27},\"pressure\":1011.02,\"humidity\":0,\"weather\":[{\"id\":500,\"main\":\"Rain\",\"description\":\"light rain\",\"icon\":\"10d\"}],\"speed\":4.64,\"deg\":224,\"clouds\":9},{\"dt\":1466330400,\"temp\":{\"day\":26.18,\"min\":17.02,\"max\":26.18,\"night\":17.02,\"eve\":22.8,\"morn\":21.92},\"pressure\":1009.53,\"humidity\":0,\"weather\":[{\"id\":501,\"main\":\"Rain\",\"description\":\"moderate rain\",\"icon\":\"10d\"}],\"speed\":1.4,\"deg\":96,\"clouds\":38,\"rain\":8.43},{\"dt\":1466416800,\"temp\":{\"day\":23.74,\"min\":16.23,\"max\":23.74,\"night\":16.23,\"eve\":21.32,\"morn\":18.2},\"pressure\":1007.4,\"humidity\":0,\"weather\":[{\"id\":501,\"main\":\"Rain\",\"description\":\"moderate rain\",\"icon\":\"10d\"}],\"speed\":6.31,\"deg\":9,\"clouds\":74,\"rain\":5.15},{\"dt\":1466503200,\"temp\":{\"day\":16.72,\"min\":10.98,\"max\":17.88,\"night\":10.98,\"eve\":14.4,\"morn\":17.88},\"pressure\":1001.15,\"humidity\":0,\"weather\":[{\"id\":502,\"main\":\"Rain\",\"description\":\"heavy intensity rain\",\"icon\":\"10d\"}],\"speed\":8.52,\"deg\":264,\"clouds\":85,\"rain\":17.6}]}";
                //Log.v(LOG_TAG,"forecast json string: "+forecastJsonStr);
                //--------------

                if(urlConnection!=null){
                    urlConnection.disconnect();
                }
                if(reader!=null){
                    try{
                        reader.close();
                    }catch (final IOException ioe){
                        Log.e("ForecastFragment","Error closing stream",ioe);
                    }
                }
            }
            //forecastJsonStr = "{\"city\":{\"id\":3093133,\"name\":\"Lodz\",\"coord\":{\"lon\":19.466669,\"lat\":51.75},\"country\":\"PL\",\"population\":0},\"cod\":\"200\",\"message\":0.0385,\"cnt\":7,\"list\":[{\"dt\":1465984800,\"temp\":{\"day\":23.55,\"min\":15.69,\"max\":25.31,\"night\":15.69,\"eve\":25.31,\"morn\":20.57},\"pressure\":1000.06,\"humidity\":85,\"weather\":[{\"id\":501,\"main\":\"Rain\",\"description\":\"moderate rain\",\"icon\":\"10d\"}],\"speed\":1.96,\"deg\":169,\"clouds\":12,\"rain\":5.96},{\"dt\":1466071200,\"temp\":{\"day\":20.51,\"min\":13.52,\"max\":24.18,\"night\":19.14,\"eve\":24.18,\"morn\":13.52},\"pressure\":1006.27,\"humidity\":83,\"weather\":[{\"id\":500,\"main\":\"Rain\",\"description\":\"light rain\",\"icon\":\"10d\"}],\"speed\":4.82,\"deg\":226,\"clouds\":8,\"rain\":0.23},{\"dt\":1466157600,\"temp\":{\"day\":25.4,\"min\":15.53,\"max\":25.4,\"night\":15.53,\"eve\":22.56,\"morn\":16.68},\"pressure\":999.87,\"humidity\":70,\"weather\":[{\"id\":500,\"main\":\"Rain\",\"description\":\"light rain\",\"icon\":\"10d\"}],\"speed\":8.21,\"deg\":169,\"clouds\":20,\"rain\":1.63},{\"dt\":1466244000,\"temp\":{\"day\":24.45,\"min\":16.09,\"max\":24.45,\"night\":16.09,\"eve\":24.38,\"morn\":20.27},\"pressure\":1011.02,\"humidity\":0,\"weather\":[{\"id\":500,\"main\":\"Rain\",\"description\":\"light rain\",\"icon\":\"10d\"}],\"speed\":4.64,\"deg\":224,\"clouds\":9},{\"dt\":1466330400,\"temp\":{\"day\":26.18,\"min\":17.02,\"max\":26.18,\"night\":17.02,\"eve\":22.8,\"morn\":21.92},\"pressure\":1009.53,\"humidity\":0,\"weather\":[{\"id\":501,\"main\":\"Rain\",\"description\":\"moderate rain\",\"icon\":\"10d\"}],\"speed\":1.4,\"deg\":96,\"clouds\":38,\"rain\":8.43},{\"dt\":1466416800,\"temp\":{\"day\":23.74,\"min\":16.23,\"max\":23.74,\"night\":16.23,\"eve\":21.32,\"morn\":18.2},\"pressure\":1007.4,\"humidity\":0,\"weather\":[{\"id\":501,\"main\":\"Rain\",\"description\":\"moderate rain\",\"icon\":\"10d\"}],\"speed\":6.31,\"deg\":9,\"clouds\":74,\"rain\":5.15},{\"dt\":1466503200,\"temp\":{\"day\":16.72,\"min\":10.98,\"max\":17.88,\"night\":10.98,\"eve\":14.4,\"morn\":17.88},\"pressure\":1001.15,\"humidity\":0,\"weather\":[{\"id\":502,\"main\":\"Rain\",\"description\":\"heavy intensity rain\",\"icon\":\"10d\"}],\"speed\":8.52,\"deg\":264,\"clouds\":85,\"rain\":17.6}]}";

            try{
                return getWeatherDataFromJson(forecastJsonStr,7);
            }catch (JSONException e){
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String[] result) {
//            if(result != null){
//                mForecastAdapter.clear();
//                for(String dayForecastStr : result){
//                    mForecastAdapter.add(dayForecastStr);
//                }
//            }
            List<String> list = new ArrayList<String>();

            if(result != null){
                for(String s : result){
                    list.add(s);
                }
                mForecastAdapter = new ArrayAdapter<String>(
                        getActivity(),R.layout.list_item_forecast,R.id.list_item_forecast_textview,list
                );
                ListView listView = (ListView) rootView.findViewById(R.id.listview_forecast);
                listView.setAdapter(mForecastAdapter);
            }
        }

        /* The date/time conversion code is going to be moved outside the asynctask later,
                * so for convenience we're breaking it out into its own method now.
                */
        private String getReadableDateString(long time){
            // Because the API returns a unix timestamp (measured in seconds),
            // it must be converted to milliseconds in order to be converted to valid date.
            SimpleDateFormat shortenedDateFormat = new SimpleDateFormat("EEE MMM dd");
            return shortenedDateFormat.format(time);
        }

        /**
         * Prepare the weather high/lows for presentation.
         */
        private String formatHighLows(double high, double low) {
            //ditambahkan dengan konversi yang benar
            //berdasarkan preferensi user
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String unitType = sharedPrefs.getString(getString(R.string.pref_units_key),getString(R.string.pref_units_metric));

            if(unitType.equals(getString(R.string.pref_units_imperial))){
                high = (high * 1.8) + 32;
                low = (low * 1.8) + 32;
            }else if(!unitType.equals(getString(R.string.pref_units_metric))){
                Log.d(LOG_TAG,"unit type not found: "+unitType);
            }
            // For presentation, assume the user doesn't care about tenths of a degree.
            long roundedHigh = Math.round(high);
            long roundedLow = Math.round(low);

            String highLowStr = roundedHigh + "/" + roundedLow;
            return highLowStr;
        }

        /**
         * Take the String representing the complete forecast in JSON Format and
         * pull out the data we need to construct the Strings needed for the wireframes.
         *
         * Fortunately parsing is easy:  constructor takes the JSON string and converts it
         * into an Object hierarchy for us.
         */
        private String[] getWeatherDataFromJson(String forecastJsonStr, int numDays)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String OWM_LIST = "list";
            final String OWM_WEATHER = "weather";
            final String OWM_TEMPERATURE = "temp";
            final String OWM_MAX = "max";
            final String OWM_MIN = "min";
            final String OWM_DESCRIPTION = "main";

            JSONObject forecastJson = new JSONObject(forecastJsonStr);
            JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);

            // OWM returns daily forecasts based upon the local time of the city that is being
            // asked for, which means that we need to know the GMT offset to translate this data
            // properly.

            // Since this data is also sent in-order and the first day is always the
            // current day, we're going to take advantage of that to get a nice
            // normalized UTC date for all of our weather.

            Time dayTime = new Time();
            dayTime.setToNow();

            // we start at the day returned by local time. Otherwise this is a mess.
            int julianStartDay = Time.getJulianDay(System.currentTimeMillis(), dayTime.gmtoff);

            // now we work exclusively in UTC
            dayTime = new Time();

            String[] resultStrs = new String[numDays];
            for(int i = 0; i < weatherArray.length(); i++) {
                // For now, using the format "Day, description, hi/low"
                String day;
                String description;
                String highAndLow;

                // Get the JSON object representing the day
                JSONObject dayForecast = weatherArray.getJSONObject(i);

                // The date/time is returned as a long.  We need to convert that
                // into something human-readable, since most people won't read "1400356800" as
                // "this saturday".
                long dateTime;
                // Cheating to convert this to UTC time, which is what we want anyhow
                dateTime = dayTime.setJulianDay(julianStartDay+i);
                day = getReadableDateString(dateTime);

                // description is in a child array called "weather", which is 1 element long.
                JSONObject weatherObject = dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
                description = weatherObject.getString(OWM_DESCRIPTION);

                // Temperatures are in a child object called "temp".  Try not to name variables
                // "temp" when working with temperature.  It confuses everybody.
                JSONObject temperatureObject = dayForecast.getJSONObject(OWM_TEMPERATURE);
                double high = temperatureObject.getDouble(OWM_MAX);
                double low = temperatureObject.getDouble(OWM_MIN);

                highAndLow = formatHighLows(high, low);
                resultStrs[i] = day + " - " + description + " - " + highAndLow;
            }

            for (String s : resultStrs) {
                Log.v(LOG_TAG, "Forecast entry: " + s);
            }
            return resultStrs;

        }
    }


}
