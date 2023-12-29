package com.example.roading;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.*;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity {

    private GoogleMap mMap;
    public MapView mapView;
    private Button goNavigation;
    private final List<String> locations = new ArrayList<>();
    public static String google_url = "https://www.google.com/maps/dir";

    private static final String DIRECTIONS_API_URL = "https://maps.googleapis.com/maps/api/directions/json?";
    private static final String GEOCODE_API_URL = "https://maps.googleapis.com/maps/api/geocode/json?";
    private static final String API_KEY = "AIzaSyCIFTk8uoDoPrnMdpBfqvxEde3O8F2XOus";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mapview);

        goNavigation = findViewById(R.id.goNavigation);
        mapView = findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();

        Intent intent = getIntent();
        String nodeText = intent.getStringExtra("node_name");
        String urlText = intent.getStringExtra("url");
        //node.add(nodeText);

        //String location= node.get(0);
        for (String node: nodeText.split("_")){
            locations.add(node);
            google_url += "/"+node;
        }

        mapView.getMapAsync(new com.google.android.gms.maps.OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {
                mMap = googleMap;
                // 獲取地名對應的座標
                GeocodeTask geocodeTask = new GeocodeTask();
                geocodeTask.execute(locations.toArray(new String[0]));
                goNavigation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        // 創建一個Intent，ACTION_VIEW表示要查看的內容
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        // 將連結轉換為Uri對象
                        Uri uri = Uri.parse(urlText);
                        // 將Uri對象設置為Intent的數據
                        intent.setData(uri);
                        // 啟動Intent，打開瀏覽器或相應的應用程序以查看連結
                        startActivity(intent);
                    }
                });
            }
        });
    }

    // 地名轉坐標的異部任務
    private class GeocodeTask extends AsyncTask<String, Void, List<LatLng>> {
        @Override
        protected List<LatLng> doInBackground(String... locations) {
            List<LatLng> coordinates = new ArrayList<>();
            for (String location : locations) {
                try {
                    String geocodeUrl = getGeocodeUrl(location);
                    String geocodeData = downloadUrl(geocodeUrl);
                    LatLng coordinate = parseGeocodeResponse(geocodeData);
                    if (coordinate != null) {
                        coordinates.add(coordinate);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return coordinates;
        }

        @Override
        protected void onPostExecute(List<LatLng> coordinates) {
            // 如果成功獲取了地名對應的坐標
            if (coordinates.size() == locations.size()) {
                // 設置地圖的初始位置和缩放級別為起點
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(coordinates.get(0), 12);
                mMap.moveCamera(cameraUpdate);

                // 添加標記
                for (int i = 0; i < coordinates.size(); i++) {
                    mMap.addMarker(new MarkerOptions().position(coordinates.get(i)).title(locations.get(i)));
                }

                // 開始路線規劃
                String directionsUrl = getDirectionsUrl(coordinates);
                DownloadTask downloadTask = new DownloadTask();
                downloadTask.execute(directionsUrl);
            } else {
                Toast.makeText(getApplicationContext(), "無法獲取所有地點的座標", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // 獲取 Directions API 請求 URL
    private String getDirectionsUrl(List<LatLng> coordinates) {
        String strOrigin = "origin=" + coordinates.get(0).latitude + "," + coordinates.get(0).longitude;
        String strDestination = "destination=" + coordinates.get(coordinates.size() - 1).latitude + "," + coordinates.get(coordinates.size() - 1).longitude;
        StringBuilder strWaypoints = new StringBuilder("waypoints=");
        for (int i = 1; i < coordinates.size() - 1; i++) {
            strWaypoints.append(coordinates.get(i).latitude).append(",").append(coordinates.get(i).longitude).append("|");
        }
        strWaypoints = new StringBuilder(strWaypoints.substring(0, strWaypoints.length() - 1));

        String sensor = "sensor=false";
        String mode = "mode=driving";
        String key = "key=" + API_KEY;

        return DIRECTIONS_API_URL + strOrigin + "&" + strDestination + "&" + strWaypoints + "&" + sensor + "&" + mode + "&" + key;
    }

    private String getGeocodeUrl(String location) {
        String formattedLocation = location.replace(" ", "+");
        String sensor = "sensor=false";
        String key = "key=" + API_KEY;
        return GEOCODE_API_URL + "address=" + formattedLocation + "&" + sensor + "&" + key;
    }

    private String downloadUrl(String strUrl) throws Exception {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();

            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuilder sb = new StringBuilder();
            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            data = sb.toString();
            br.close();
        } finally {
            if (iStream != null) {
                iStream.close();
            }
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return data;
    }

    private LatLng parseGeocodeResponse(String geocodeData) {
        try {
            JSONObject jsonObject = new JSONObject(geocodeData);
            if (jsonObject.has("results")) {
                JSONArray results = jsonObject.getJSONArray("results");
                if (results.length() > 0) {
                    JSONObject result = results.getJSONObject(0);
                    JSONObject geometry = result.getJSONObject("geometry");
                    JSONObject location = geometry.getJSONObject("location");
                    double lat = location.getDouble("lat");
                    double lng = location.getDouble("lng");
                    return new LatLng(lat, lng);

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private class DownloadTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... url) {
            String data = "";
            try {
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            // 解析 Directions API 響應並繪製路線
            ParserTask parserTask = new ParserTask();
            parserTask.execute(result);

        }
    }

    private class ParserTask extends AsyncTask<String, Integer, List<LatLng>> {
        @Override
        protected List<LatLng> doInBackground(String... jsonData) {
            JSONObject jObject;
            List<LatLng> points = new ArrayList<>();

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();
                points = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return points;
        }

        @Override
        protected void onPostExecute(List<LatLng> result) {
            PolylineOptions lineOptions = new PolylineOptions();
            lineOptions.addAll(result);
            lineOptions.width(8);
            lineOptions.color(Color.BLACK);
            mMap.addPolyline(lineOptions);
        }
    }

    // 解析 Directions API 響應的幫助類
    private class DirectionsJSONParser {
        List<LatLng> parse(JSONObject jObject) {
            List<LatLng> points = new ArrayList<>();
            JSONArray jRoutes;
            JSONArray jLegs;
            JSONArray jSteps;

            try {
                jRoutes = jObject.getJSONArray("routes");

                for (int i = 0; i < jRoutes.length(); i++) {
                    jLegs = ((JSONObject) jRoutes.get(i)).getJSONArray("legs");

                    for (int j = 0; j < jLegs.length(); j++) {
                        jSteps = ((JSONObject) jLegs.get(j)).getJSONArray("steps");

                        for (int k = 0; k < jSteps.length(); k++) {
                            String polyline = "";
                            polyline = (String) ((JSONObject) ((JSONObject) jSteps.get(k)).get("polyline")).get("points");
                            List<LatLng> list = decodePoly(polyline);

                            for (int l = 0; l < list.size(); l++) {
                                points.add(new LatLng(list.get(l).latitude, list.get(l).longitude));
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return points;
        }

        private List<LatLng> decodePoly(String encoded) {
            List<LatLng> poly = new ArrayList<>();
            int index = 0, len = encoded.length();
            int lat = 0, lng = 0;

            while (index < len) {
                int b, shift = 0, result = 0;
                do {
                    b = encoded.charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (b >= 0x20);
                int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                lat += dlat;

                shift = 0;
                result = 0;
                do {
                    b = encoded.charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (b >= 0x20);
                int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                lng += dlng;

                LatLng p = new LatLng(lat / 1E5, lng / 1E5);
                poly.add(p);
            }
            return poly;
        }
    }
}

