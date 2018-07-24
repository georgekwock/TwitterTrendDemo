package task.twittercodingtask.utils;

import android.util.Base64;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * @Author Qing Guo
 * @Date 2018/7/18
 * @Description HttpURLConnection util class
 */
public class HttpUtil {
    private static final String TAG = "HTTPUtil";

    /**
     * Get request
     *
     * @param requestUrl
     * @return
     */
    public static String sendGet(String requestUrl) {
        try {
            URL url = new URL(requestUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoOutput(false);
            connection.setDoInput(true);
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.connect();
            int responseCode = connection.getResponseCode();
            if (HttpURLConnection.HTTP_OK == responseCode) {
                StringBuffer buffer = new StringBuffer();
                String readLine;
                BufferedReader responseReader;
                responseReader = new BufferedReader(new InputStreamReader(connection
                        .getInputStream()));
                while ((readLine = responseReader.readLine()) != null) {
                    buffer.append(readLine).append("\n");
                }
                responseReader.close();
                Log.d("HttpGET", buffer.toString());
                //JSONObject result = new JSONObject(buffer.toString());
                return buffer.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //get WOEID from Yahoo
//    public static String getWOEID(String appId, double lat, double lng) {
//        String url = "http://where.yahooapis.com/v1/places.q('" + lat + "," + lng + "')?appid="
//                + appId + "&format=json";
//        String woeid = sendGet(url);
//        return woeid;
//    }

    public static String getWOEID(String name) throws Exception {
        URL url = new URL("https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20geo" +
                ".places%20where%20text%3D%22Place%20" + name + "%20County%22&format=xml");
        URLConnection connection = url.openConnection();
        Document doc = stringToDOM(connection.getInputStream());
        Node location = doc.getElementsByTagName("name").item(0);
        Node node = doc.getElementsByTagName("woeid").item(0);
        String woeid = node.getTextContent();
        System.out.println(location.getTextContent() + "\t"
                + node.getNodeName() + ":" + woeid);
        return woeid;
    }

    public static Document stringToDOM(InputStream input) {
        try {
            DocumentBuilder db = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder();
            Document doc = db.parse(input);
            return doc;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    //get trends from nearby city
    public static String getTrend(String id) {
        String response = null;
        URL url = null;
        try {
            //if the location has no trend data of the id, then use id 1 as default.
            url = new URL(Constant.URL_TRENDING + "id");
            if (httprequest(url) == null) {
                url = new URL(Constant.URL_TRENDING + "1");
            }
            response = httprequest(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return response;
    }

    public static String httprequest(URL url) {
        HttpURLConnection httpConnection = null;
        BufferedReader bufferedReader = null;
        StringBuilder response = new StringBuilder();
        try {
            httpConnection = (HttpURLConnection) url.openConnection();
            httpConnection.setRequestMethod("GET");

            String jsonString = appAuthentication();
            JSONObject jsonObjectDocument = new JSONObject(jsonString);
            String token = jsonObjectDocument.getString("token_type") + " "
                    + jsonObjectDocument.getString("access_token");
            httpConnection.setRequestProperty("Authorization", token);
            httpConnection.setRequestProperty("Authorization", token);
            httpConnection.setRequestProperty("Content-Type",
                    "application/json");
            httpConnection.connect();
            int code = httpConnection.getResponseCode();
            InputStream inputStream = httpConnection.getInputStream();
            bufferedReader = new BufferedReader(new InputStreamReader(
                    inputStream));

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                response.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (httpConnection != null) {
                httpConnection.disconnect();
            }
        }
        return response.toString();
    }

    //get app authentication token from twitter
    public static String appAuthentication() {

        HttpURLConnection httpConnection = null;
        OutputStream outputStream = null;
        BufferedReader bufferedReader = null;
        StringBuilder response = null;

        try {
            URL url = new URL(Constant.URL_AUTHENTICATION);
            httpConnection = (HttpURLConnection) url.openConnection();
            httpConnection.setRequestMethod("POST");
            httpConnection.setDoOutput(true);
            httpConnection.setDoInput(true);

            String accessCredential = Constant.CONSUMER_KEY + ":"
                    + Constant.CONSUMER_SECRET;
            String authorization = "Basic "
                    + Base64.encodeToString(accessCredential.getBytes(),
                    Base64.NO_WRAP);
            String param = "grant_type=client_credentials";

            httpConnection.addRequestProperty("Authorization", authorization);
            httpConnection.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded;charset=UTF-8");
            httpConnection.connect();

            outputStream = httpConnection.getOutputStream();
            outputStream.write(param.getBytes());
            outputStream.flush();
            outputStream.close();
            // int statusCode = httpConnection.getResponseCode();
            // String reason =httpConnection.getResponseMessage();

            bufferedReader = new BufferedReader(new InputStreamReader(
                    httpConnection.getInputStream()));
            String line;
            response = new StringBuilder();

            while ((line = bufferedReader.readLine()) != null) {
                response.append(line);
            }

            Log.d(TAG,
                    "POST response code: "
                            + String.valueOf(httpConnection.getResponseCode()));
            Log.d(TAG, "JSON response: " + response.toString());

        } catch (Exception e) {
            Log.e(TAG, "POST error: " + Log.getStackTraceString(e));

        } finally {
            if (httpConnection != null) {
                httpConnection.disconnect();
            }
        }
        return response.toString();
    }
}
