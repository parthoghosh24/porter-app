package com.partho.porter.porterdelivery;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.partho.porter.porterdelivery.utils.ui.CustomTextView;


import org.json.JSONObject;


public class SplashActivity extends AppCompatActivity {

    Animation fadeIn = null;
    CustomTextView truck = null;
    private static final String PORTER_URL="https://porter.0x10.info/api/parcel?type=json&query=list_parcel";
    private static final String TAG="porter_splash";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        fadeIn= AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left);
        truck=(CustomTextView)findViewById(R.id.porter_app_intro);
        truck.startAnimation(fadeIn);
        FetchPorterParcels request = new FetchPorterParcels();
        request.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 10, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        PorterApplication.getInstance().getAppRequestQueue().add(request);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_splash, menu);
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

    class FetchPorterParcels extends JsonObjectRequest
    {
        public FetchPorterParcels()
        {
            super(Method.GET,
                    PORTER_URL,
                    new JSONObject(),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            Log.e(TAG, "IN HERE..............");
                            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                            intent.putExtra("parcels",response.toString());
//                            Toast.makeText(getApplicationContext(),response.toString(),Toast.LENGTH_SHORT).show();
                            startActivity(intent);
                            finish();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e(TAG,"here "+error.getLocalizedMessage());
                            if(error.networkResponse!=null)
                            {
                                Log.e(TAG,"from server->"+new String(error.networkResponse.data));
                            }
                            Toast.makeText(getApplicationContext(), "Please try again!", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}
