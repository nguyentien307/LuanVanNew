package com.example.tiennguyen.luanvannew.services;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by TIENNGUYEN on 10/13/2017.
 */

public class GetData extends AsyncTask<String, Void, JSONObject>
{
    private ProgressDialog dialog;
    DataDownloadListener dataDownloadListener;
    Context ctx;
    public GetData(Context ctx)
    {
        //Constructor may be parametric
//        this.dialog = new ProgressDialog(ctx);
//        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        this.ctx = ctx;

    }

    public void setDataDownloadListener(DataDownloadListener dataDownloadListener) {
        this.dataDownloadListener = dataDownloadListener;
    }

    @Override
    protected void onPreExecute() {
//        this.dialog.setMessage("Please wait");
//        this.dialog.show();
    }

    @Override
    protected JSONObject doInBackground(String... param)
    {
        // do your task...
        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {
            URL url = new URL(param[0]);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();


            InputStream stream = connection.getInputStream();

            reader = new BufferedReader(new InputStreamReader(stream));

            StringBuffer buffer = new StringBuffer();
            String line = "";

            while ((line = reader.readLine()) != null) {
                buffer.append(line+"\n");
            }
            JSONObject results = new JSONObject(buffer.toString());
            return results;


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(JSONObject results)
    {
        if(results != null)
        {
            dataDownloadListener.dataDownloadedSuccessfully(results);
        }
        else
            dataDownloadListener.dataDownloadFailed();
    }



    public static interface DataDownloadListener {
        void dataDownloadedSuccessfully(JSONObject data);
        void dataDownloadFailed();
    }
}

