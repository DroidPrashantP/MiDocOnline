package com.midoconline.app.ui.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.midoconline.app.R;
import com.midoconline.app.services.GPSTracker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MapActivity extends FragmentActivity {

    private static final String GOOGLE_API_KEY = "AIzaSyDQWYY13MMJKe3mIl7AV06OegGEJY76W88";
    private static final int PERMISSION_REQUEST_CODE_LOCATION = 1;
    GoogleMap googleMap;
    double latitude = 0;
    double longitude = 0;
    private int PROXIMITY_RADIUS = 5000;
    // GPSTracker class
    GPSTracker gps;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //show error dialog if GoolglePlayServices not available
        if (!isGooglePlayServicesAvailable()) {
            finish();
        }
        setContentView(R.layout.activity_map);
        setToolbar();
        // create class object
        gps = new GPSTracker(this);
        // check if GPS enabled
        if(gps.canGetLocation()){
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();

            SupportMapFragment fragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.googleMap);
            googleMap = fragment.getMap();
            googleMap.setMyLocationEnabled(true);
            LatLng latLng = new LatLng(latitude, longitude);
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(12));
            if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION,getApplicationContext(),MapActivity.this)) {
                executePlaceRequest();
            }
            else {
                requestPermission(Manifest.permission.ACCESS_FINE_LOCATION,PERMISSION_REQUEST_CODE_LOCATION,getApplicationContext(),MapActivity.this);
            }

        }else{
            gps.showSettingsAlert();
        }
    }

    public  void requestPermission(String strPermission,int perCode,Context _c,Activity _a){

        if (ActivityCompat.shouldShowRequestPermissionRationale(_a,strPermission)){
            Toast.makeText(MapActivity.this,"GPS permission allows us to access location data. Please allow in App Settings for additional functionality.", Toast.LENGTH_LONG).show();
        } else {

            ActivityCompat.requestPermissions(_a,new String[]{strPermission},perCode);
        }
    }

    public static boolean checkPermission(String strPermission, Context _c, Activity _a){
        int result = ContextCompat.checkSelfPermission(_c, strPermission);
        if (result == PackageManager.PERMISSION_GRANTED){

            return true;

        } else {

            return false;

        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {

            case PERMISSION_REQUEST_CODE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    executePlaceRequest();
                } else {
                    Toast.makeText(getApplicationContext(),"Permission Denied, You cannot access location data.",Toast.LENGTH_LONG).show();
                }
                break;

        }
    }


    private void executePlaceRequest() {
        String type = "pharmacy|hospital";
        StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlacesUrl.append("location=" + latitude + "," + longitude);
        googlePlacesUrl.append("&radius=" + PROXIMITY_RADIUS);
        googlePlacesUrl.append("&types=" + type);
        googlePlacesUrl.append("&sensor=true");
        googlePlacesUrl.append("&key=" + GOOGLE_API_KEY);

        GooglePlacesReadTask googlePlacesReadTask = new GooglePlacesReadTask();
        Object[] toPass = new Object[2];
        toPass[0] = googleMap;
        toPass[1] = googlePlacesUrl.toString();
        Log.e("Place Url", googlePlacesUrl.toString());
        googlePlacesReadTask.execute(toPass);
    }

    private boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            GooglePlayServicesUtil.getErrorDialog(status, this, 0).show();
            return false;
        }
    }

    public void setToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        final android.app.ActionBar ab = getActionBar();
        ImageView nav_drawer = (ImageView) findViewById(R.id.nav_drawer);
        nav_drawer.setImageResource(R.drawable.ic_back);
        nav_drawer.setColorFilter(getResources().getColor(R.color.blue), android.graphics.PorterDuff.Mode.MULTIPLY);
        nav_drawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_map, menu);
        return true;
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

    public class GooglePlacesReadTask extends AsyncTask<Object, Integer, String> {
        String googlePlacesData = null;
        GoogleMap googleMap;

        @Override
        protected String doInBackground(Object... inputObj) {
            try {
                googleMap = (GoogleMap) inputObj[0];
                String googlePlacesUrl = (String) inputObj[1];
                googlePlacesData = read(googlePlacesUrl);
                Log.e("Response", " -- "+googlePlacesData);
            } catch (Exception e) {
                Log.d("Google Place Read Task", e.toString());
            }
            return googlePlacesData;
        }

        @Override
        protected void onPostExecute(String result) {
            PlacesDisplayTask placesDisplayTask = new PlacesDisplayTask();
            Object[] toPass = new Object[2];
            toPass[0] = googleMap;
            toPass[1] = result;
            placesDisplayTask.execute(toPass);
        }
    }

    public String read(String httpUrl) throws IOException {
        String httpData = "";
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;
        try {
            URL url = new URL(httpUrl);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();
            inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuffer stringBuffer = new StringBuffer();
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line);
            }
            httpData = stringBuffer.toString();
            bufferedReader.close();
        } catch (Exception e) {
            Log.d("reading Http url", e.toString());
        } finally {
            inputStream.close();
            httpURLConnection.disconnect();
        }
        return httpData;
    }

    public class PlacesDisplayTask extends AsyncTask<Object, Integer, List<HashMap<String, String>>> {

        JSONObject googlePlacesJson;
        GoogleMap googleMap;

        @Override
        protected List<HashMap<String, String>> doInBackground(Object... inputObj) {

            List<HashMap<String, String>> googlePlacesList = null;
            Places placeJsonParser = new Places();

            try {
                googleMap = (GoogleMap) inputObj[0];
                googlePlacesJson = new JSONObject((String) inputObj[1]);
                googlePlacesList = placeJsonParser.parse(googlePlacesJson);
            } catch (Exception e) {
                Log.d("Exception", e.toString());
            }
            return googlePlacesList;
        }

        @Override
        protected void onPostExecute(List<HashMap<String, String>> list) {
            googleMap.clear();
            for (int i = 0; i < list.size(); i++) {
                MarkerOptions markerOptions = new MarkerOptions();
                HashMap<String, String> googlePlace = list.get(i);
                double lat = Double.parseDouble(googlePlace.get("lat"));
                double lng = Double.parseDouble(googlePlace.get("lng"));
                String placeName = googlePlace.get("place_name");
                String vicinity = googlePlace.get("vicinity");
                LatLng latLng = new LatLng(lat, lng);
                markerOptions.position(latLng);
                markerOptions.title(placeName + " : " + vicinity);
                googleMap.addMarker(markerOptions);
            }
        }
    }


    public class Places {

        public List<HashMap<String, String>> parse(JSONObject jsonObject) {
            JSONArray jsonArray = null;
            try {
                jsonArray = jsonObject.getJSONArray("results");
                Log.e("Result", jsonArray.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return getPlaces(jsonArray);
        }

        private List<HashMap<String, String>> getPlaces(JSONArray jsonArray) {
            int placesCount = jsonArray.length();
            List<HashMap<String, String>> placesList = new ArrayList<HashMap<String, String>>();
            HashMap<String, String> placeMap = null;

            for (int i = 0; i < placesCount; i++) {
                try {
                    placeMap = getPlace((JSONObject) jsonArray.get(i));
                    placesList.add(placeMap);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return placesList;
        }

        private HashMap<String, String> getPlace(JSONObject googlePlaceJson) {
            HashMap<String, String> googlePlaceMap = new HashMap<String, String>();
            String placeName = "-NA-";
            String vicinity = "-NA-";
            String latitude = "";
            String longitude = "";
            String reference = "";

            try {
                if (!googlePlaceJson.isNull("name")) {
                    placeName = googlePlaceJson.getString("name");
                }
                if (!googlePlaceJson.isNull("vicinity")) {
                    vicinity = googlePlaceJson.getString("vicinity");
                }
                latitude = googlePlaceJson.getJSONObject("geometry").getJSONObject("location").getString("lat");
                longitude = googlePlaceJson.getJSONObject("geometry").getJSONObject("location").getString("lng");
                reference = googlePlaceJson.getString("reference");
                googlePlaceMap.put("place_name", placeName);
                googlePlaceMap.put("vicinity", vicinity);
                googlePlaceMap.put("lat", latitude);
                googlePlaceMap.put("lng", longitude);
                googlePlaceMap.put("reference", reference);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return googlePlaceMap;
        }
    }
}
