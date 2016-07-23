package com.example.ahmeds.youtubeplayer;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by 100488982 on 11/21/2015.
 */

public class DownloadYTDataTask extends AsyncTask<String, Void, YTVideo> {
    private YTDataListener listener = null;
    private Exception exception = null;
    //private YTVideo data=null;

    public DownloadYTDataTask(YTDataListener listener) {
        this.listener = listener;
    }

    @Override
    protected YTVideo doInBackground(String... params) {
        YTVideo ytvideo = null;
        try {
            // parse out the data
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = dbFactory.newDocumentBuilder();
            Log.d("InternetResourcesSample", "url: " + params[0]);
            URL url = new URL(params[0]);
            BufferedReader r = new BufferedReader(new InputStreamReader( url.openStream()));

            String data=r.readLine();
            System.out.println(data);
            JSONObject jdata= (JSONObject) new JSONTokener(data).nextValue();
            //URL imageUrl = new URL(jdata.getString("thumbnail_url") );
            //HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            //connection.connect();
            ytvideo= new YTVideo(jdata.getString("title"),jdata.getString("author_name"), "https://www.youtube.com/embed/"+ params[0].substring(params[0].lastIndexOf("?v=")+3),getLogoImage(jdata.getString("thumbnail_url")) );

        } catch (Exception e) {
            e.printStackTrace();
            exception = e;
        }

        return ytvideo;
    }
    private byte[] getLogoImage(String url){
        try {
            URL imageUrl = new URL(url);
            URLConnection ucon = imageUrl.openConnection();

            InputStream is = ucon.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] dataBuffer = new byte[1024 * 16];
            int size = 0;
            while ((size = bis.read(dataBuffer)) != -1) {
                out.write(dataBuffer, 0, size);
            }
            return out.toByteArray();
        } catch (Exception e) {
            Log.d("ImageManager", "Error: " + e.toString());
            return null;
        }

    }

    private String stripTags(String code) {
        return code; // for now
    }

    @Override
    protected void onPostExecute(YTVideo result) {
        if (exception != null) {
            exception.printStackTrace();
        }

        Log.d("InternetResourcesSample", "price: " + result);
        listener.showData(result);
    }
}


