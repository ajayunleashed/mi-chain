package com.ey.taxchain;

import android.annotation.TargetApi;
import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class Activity_MakePymt extends Activity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taxchain_makepayment);

    }

    public void onPay(View v){
        AsyncHttpClient client = new AsyncHttpClient();
        JSONObject jsonParams = new JSONObject();
        StringEntity entity=null;
        System.out.println("Started");
        try{
            String url="http://10.168.3.251:10009/api/taxchain/O=PartyC,L=Paris,C=FR/O=PartyA,L=London,C=GB/create-pact";
            jsonParams.put("referenceNumber", "a00001");
            jsonParams.put("totalAmount", "3.14");
            entity = new StringEntity(jsonParams.toString());
            entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
        /*client.get("http://10.168.3.251:10009/api/taxchain/pacts", null, new AsyncHttpResponseHandler() {*/
            client.put(this,url, entity,"application/json", new AsyncHttpResponseHandler() {
            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public void onSuccess(String response) {
                System.out.println("response for get Data:"+response);
                HashMap<String, ArrayList> mapTable = new HashMap<String, ArrayList>();


            }

            @Override
            public void onFailure(int statusCode, Throwable error,
                                  String content) {
                System.out.println(" Exception in get Data: Status code is"+statusCode);
                System.out.println(" Exception content in get Data. Content is :"+content);
                System.out.println(" Exception content in get Data:"+error.getMessage());
                error.printStackTrace();

            }
        });
        }catch (Exception e){
            System.out.println("Started"+e.getStackTrace().toString());
            e.printStackTrace();
        }

    }
}
