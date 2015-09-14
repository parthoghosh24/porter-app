package com.partho.porter.porterdelivery;

import android.app.Application;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by partho on 29/8/15.
 */
public class PorterApplication extends Application{

    private static PorterApplication sInstance;
    private RequestQueue appRequestQueue;

    @Override
    public void onCreate() {
        super.onCreate();
        appRequestQueue = Volley.newRequestQueue(getApplicationContext());
        if (sInstance == null)
        {
            sInstance = this;
        }
    }

    public synchronized static PorterApplication getInstance()
    {
        return sInstance;
    }

    public RequestQueue getAppRequestQueue()
    {
        return appRequestQueue;
    }
}
