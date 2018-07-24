package task.twittercodingtask.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @Author Qing Guo
 * @Date 2018/7/18
 * @Description GPS Location Utils
 */
public class GPSUtils {

    private volatile static GPSUtils uniqueInstance;
    private LocationManager locationManager;
    private String provider;
    private Location location;
    private Activity mActivity;

    public GPSUtils(Activity activity) {
        mActivity = activity;
        initLocation();
    }

    public void initLocation() {
        locationManager = (LocationManager) mActivity.getSystemService(Context.LOCATION_SERVICE);
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission
                        .ACCESS_FINE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                        (mActivity,
                                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager
                        .PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(mActivity, new String[]{android.Manifest
                            .permission
                            .ACCESS_FINE_LOCATION, android.Manifest.permission
                            .ACCESS_COARSE_LOCATION}, 1);
                    return;
                } else {
                    List<String> providerList = locationManager.getProviders(true);
                    if (providerList.contains(LocationManager.GPS_PROVIDER)) {
                        provider = LocationManager.GPS_PROVIDER;
                    } else if (providerList.contains(LocationManager.NETWORK_PROVIDER)) {
                        provider = LocationManager.NETWORK_PROVIDER;
                    } else {
                        return;
                    }
                    locationManager.requestLocationUpdates(provider, 3000, 10, locationListener);
                    location = locationManager.getLastKnownLocation(provider);

                }
            }
        });

    }

    //remove update manager when finishes
    public Location getLocation() {
        remove();
        return location;
    }


    public Map<String, String> showLocation(Location location) {
        if (location == null) return null;
        Map<String, String> map = new HashMap<>();
        map.put("longitude", String.valueOf(location.getLongitude()));
        map.put("latitude", String.valueOf(location.getLatitude()));
        return map;
    }

    public LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            showLocation(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    public void remove() {
        if (locationManager != null) {
            locationManager.removeUpdates(locationListener);
        }
    }

    //get the city name by lat and lng.
    public String getCityNameByGeo(double lat, double lng) {
        Geocoder geocoder = new Geocoder(mActivity, Locale.getDefault());
        List<Address> addresses;
        String cityName = null;
        try {
            addresses = geocoder.getFromLocation(lat, lng, 1);
            if (addresses.size() > 0) {
                cityName = addresses.get(0).getAdminArea();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cityName;
    }
}


