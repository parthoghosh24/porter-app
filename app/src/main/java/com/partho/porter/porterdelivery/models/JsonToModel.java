package com.partho.porter.porterdelivery.models;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by partho on 29/8/15.
 */
public class JsonToModel {
    private static final String TAG="json_to_model";

    /**
     * Parsing JSON Array and converting into parcel list
     *
     * @param list
     * @return
     */
    public static ArrayList<Parcel> getParcels(JSONArray list)
    {
        ArrayList<Parcel> parcels=new ArrayList<>();
        try {

            for(int index=0; index<list.length();++index)
            {
                JSONObject object=list.getJSONObject(index);
                Parcel car=getParcel(object);
                parcels.add(car);

            }
            return parcels;

        }
        catch (JSONException jse)
        {
            jse.printStackTrace();
            Log.e(TAG, jse.getLocalizedMessage());
            return null;
        }
        catch (Exception e)
        {

            e.printStackTrace();
            Log.e(TAG, e.getLocalizedMessage());
            return null;
        }

    }

    /**
     * Parsing JSON Object and converting into parcel
     *
     * @param object
     * @return
     */
    public static Parcel getParcel(JSONObject object)
    {
        Parcel parcel=null;
        try {
            parcel=new Parcel();
            parcel.setName(object.getString("name"));
            parcel.setImage(object.getString("image"));
            parcel.setDate(Long.parseLong(object.getString("date")));
            parcel.setType(object.getString("type"));
            String weight= object.getString("weight");
            weight = weight.replace("kg","");
            parcel.setWeight(Double.parseDouble(weight));
            parcel.setPhone(Long.parseLong(object.getString("phone")));
            String price=object.getString("price");
            price=price.replace(",","");
            parcel.setPrice(Double.parseDouble(price));
            parcel.setQuantity(Integer.parseInt(object.getString("quantity")));
            parcel.setColor(object.getString("color"));
            parcel.setLink(object.getString("link"));
            LiveLocation liveLocation = new LiveLocation();
            liveLocation.setLatitude(Double.parseDouble(object.getJSONObject("live_location").getString("latitude")));
            liveLocation.setLongitude(Double.parseDouble(object.getJSONObject("live_location").getString("longitude")));
            parcel.setLiveLocation(liveLocation);
            parcel.setJsonObject(object.toString());
            return parcel;


        }
        catch (JSONException jse)
        {
            jse.printStackTrace();
            Log.e(TAG, jse.getLocalizedMessage());

            return parcel;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Log.e(TAG, e.getLocalizedMessage());
            return parcel;
        }

    }
}
