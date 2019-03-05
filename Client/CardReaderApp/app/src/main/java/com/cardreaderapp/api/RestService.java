package com.cardreaderapp.api;

import android.os.AsyncTask;
import android.util.Log;

import com.cardreaderapp.models.Card;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.HttpsURLConnection;

public class RestService implements ICard {

    private Gson gson;
    private final String Server = "http://www.google.com/search?q=mkyong";
    private final String USER_AGENT = "Mozilla/5.0";
    private static final RestService service = new RestService();

    private RestService(){
        gson = new Gson();

    }

    public static RestService GetRestService(){
        return service;
    }

    @Override
    public Card GetCardData(String imageName, String base64EncodedData) throws ExecutionException, InterruptedException {
        //TODO: Send http request with base 64 encoded data

//        String cardData = "{" +
//                "\"personName\": \"Moti Levi\"," +
//                "\"phoneNumber\": \"08-678943212\"," +
//                "\"company\": \"Apple\"," +
//                "\"address\": \"Hamasger 4, Tel Aviv\"," +
//                "\"website\": \"www.apple.com\"," +
//                "\"email\": \"Moti.Levi22@gmail.com\"" +
//                "}";

//        String cardData = sendPostEncoded(imageName, base64EncodedData);
        SendImageTask sit = new SendImageTask();
        sit.execute(imageName, base64EncodedData);
        String cardData = sit.get();

        return gson.fromJson(cardData, Card.class); // deserializes json into card
    }

    // HTTP GET request
    private void sendGet() throws Exception {
        URL obj = new URL(Server);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // optional default is GET
        con.setRequestMethod("GET");

        //add request header
        con.setRequestProperty("User-Agent", USER_AGENT);

        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'GET' request to URL : " + Server);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        //print result
        System.out.println(response.toString());

    }

    private String sendPostEncoded(String imageName, String encodedImage)
    {
        //String yourURL = "https://us-central1-business-card-reader-230419.cloudfunctions.net/handler/users/555/images";
        String yourURL = "http://34.73.212.169:5000";

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("imageData", encodedImage);
            jsonObject.put("imageName", imageName);
            String data = jsonObject.toString();

            URL url = new URL(yourURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("POST");
            connection.setFixedLengthStreamingMode(data.getBytes().length);
            connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            OutputStream out = new BufferedOutputStream(connection.getOutputStream());
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
            writer.write(data);
            writer.flush();
            writer.close();
            out.close();
            connection.connect();

            InputStream in = new BufferedInputStream(connection.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    in, "UTF-8"));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            in.close();
            String result = sb.toString();
            Log.d("sendPostEncoded", "sendPostEncoded - Response from server = " + result);
            //Response = new JSONObject(result);
            connection.disconnect();
            return result;
        } catch (Exception e) {
            Log.d("sendPostEncoded", "sendPostEncoded - Error Encountered");
            e.printStackTrace();
        }
        return null;
    }

    // HTTP POST request
    private void sendPost() throws Exception {

        String url = "https://selfsolve.apple.com/wcResults.do";
        URL obj = new URL(url);
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

        //add reuqest header
        con.setRequestMethod("POST");
        con.setRequestProperty("User-Agent", USER_AGENT);
        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

        String urlParameters = "sn=C02G8416DRJM&cn=&locale=&caller=&num=12345";

        // Send post request
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(urlParameters);
        wr.flush();
        wr.close();

        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'POST' request to URL : " + url);
        System.out.println("Post parameters : " + urlParameters);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        //print result
        System.out.println(response.toString());

    }

    private class SendImageTask extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... strings) {
            String imageName = strings[0];
            String imageData = strings[1];
            String results = sendPostEncoded(imageName, imageData);
            return results;
        }

//        protected void onProgressUpdate(Integer... progress) {
//            setProgressPercent(progress[0]);
//        }

//        protected Void onPostExecute(String result) {
//            //showDialog("Downloaded " + result + " bytes");
//            Log.d("","on post execute " + result);
//        }
    }
}
