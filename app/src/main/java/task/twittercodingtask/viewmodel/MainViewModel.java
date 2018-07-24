package task.twittercodingtask.viewmodel;

import android.app.Activity;
import android.databinding.ObservableInt;
import android.location.Location;
import android.os.AsyncTask;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import task.twittercodingtask.R;
import task.twittercodingtask.data.BaseListener;
import task.twittercodingtask.module.TrendBean;
import task.twittercodingtask.utils.GPSUtils;
import task.twittercodingtask.utils.HttpUtil;

/**
 * @Author Qing Guo
 * @Date 2018/7/18
 * @Description
 */
public class MainViewModel implements ModelLifeCycle {
    public ObservableInt progressVisibility;
    public ObservableInt recyclerViewVisibility;
    private TwitterListener listener;
    private Activity activity;
    public List<TrendBean> list;
    public GPSUtils util;
    private LocationAsyncTask locationAsyncTask;


    public MainViewModel(Activity activity, TwitterListener listener) {
        this.activity = activity;
        this.listener = listener;
        util = new GPSUtils(activity);
        progressVisibility = new ObservableInt(View.INVISIBLE);
        recyclerViewVisibility = new ObservableInt(View.INVISIBLE);
        locationAsyncTask = new LocationAsyncTask();
        locationAsyncTask.execute();
    }

    @Override
    public void onResume() {
        if (listener != null) {
            listener.onInit(list);
        }
    }

    @Override
    public void onPause() {

    }

    @Override
    public void onDestroy() {
        listener = null;
        list = null;
    }

    /***
     * Listener to for the method of the list
     */
    public interface TwitterListener extends BaseListener {
        void onUpdate(List<TrendBean> data);

        void onInit(List<TrendBean> data);

    }

    //parse json data to list trend bean
    public List<TrendBean> parseStringJsonToObject(String json) {
        list = new ArrayList<>();
        if (json == null) {
            listener.onError(activity.getResources().getString(R.string.error_head), activity
                    .getResources()
                    .getString(R.string.null_data));
        } else {
            try {
                json = json.substring(1, json.length() - 1);
                JSONArray array = new JSONArray(new JSONObject(json).getString("trends"));
                if (array != null) {
                    for (int i = 0; i < array.length(); i++) {
                        TrendBean bean = new TrendBean();
                        JSONObject object = array.getJSONObject(i);
                        bean.setName(object.get("name").toString());
                        bean.setQuery(object.get("query").toString());
                        bean.setUrl(object.get("url").toString());
                        bean.setVolume(object.get("tweet_volume").toString());
                        list.add(bean);
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    //get location async task
    public class LocationAsyncTask extends AsyncTask<Void, Void, Location> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressVisibility.set(View.VISIBLE);
            recyclerViewVisibility.set(View.INVISIBLE);
        }

        @Override
        protected void onPostExecute(Location location) {
            super.onPostExecute(location);
            progressVisibility.set(View.INVISIBLE);
            recyclerViewVisibility.set(View.VISIBLE);
            if (location == null) {
                listener.onError(activity.getResources().getString(R.string.error_head), activity
                        .getResources().getString(R.string.null_location));
            } else {
                TrendAsyncTask task = new TrendAsyncTask();
                task.execute(location);
            }
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected Location doInBackground(Void... voids) {
            Location location = util.getLocation();
            return location;
        }
    }

    //asynctask to fetch Trend
    public class TrendAsyncTask extends AsyncTask<Location, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressVisibility.set(View.VISIBLE);
            recyclerViewVisibility.set(View.INVISIBLE);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressVisibility.set(View.INVISIBLE);
            recyclerViewVisibility.set(View.VISIBLE);
            if (s == null || s.length() == 0) {
                listener.onError(activity.getResources().getString(R.string.error_head), activity
                        .getResources().getString(R.string.null_data));
            } else {
                list = parseStringJsonToObject(s);
                //init view after finishes loading data
                if (listener != null) {
                    listener.onInit(list);
                }
            }
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected String doInBackground(Location... locations) {
            String trendJson = null;
            try {
                String cityName = util.getCityNameByGeo(locations[0]
                        .getLatitude(), locations[0].getLongitude());
                String woeid = HttpUtil.getWOEID(cityName);
                trendJson = HttpUtil.getTrend(woeid);
                return trendJson;
            } catch (Exception e) {
                e.printStackTrace();
            }

            return trendJson;
        }
    }
}
