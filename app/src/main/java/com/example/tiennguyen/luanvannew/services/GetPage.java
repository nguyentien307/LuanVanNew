package com.example.tiennguyen.luanvannew.services;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

/**
 * Created by TIENNGUYEN on 11/20/2017.
 */

public class GetPage extends AsyncTask<String, Void, Document>
{
    private ProgressDialog dialog;
    DataDownloadListener dataDownloadListener;
    Context ctx;
    public GetPage(Context ctx)
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
    protected Document doInBackground(String... param)
    {
        Document document = null;
        try {
            document = Jsoup.connect(param[0]).get();
            return document;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Document results)
    {
        if(results != null)
        {
            dataDownloadListener.dataDownloadedSuccessfully(results);
        }
        else
            dataDownloadListener.dataDownloadFailed();
    }



    public interface DataDownloadListener {
        void dataDownloadedSuccessfully(Document data);
        void dataDownloadFailed();
    }
}