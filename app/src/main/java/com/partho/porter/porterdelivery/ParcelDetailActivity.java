package com.partho.porter.porterdelivery;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.util.Linkify;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.partho.porter.porterdelivery.fragments.SendMessageFragment;
import com.partho.porter.porterdelivery.models.JsonToModel;
import com.partho.porter.porterdelivery.models.Parcel;
import com.partho.porter.porterdelivery.utils.Utils;
import com.partho.porter.porterdelivery.utils.ui.BitmapLruCache;
import com.partho.porter.porterdelivery.utils.ui.CustomTextView;
import org.json.JSONException;
import org.json.JSONObject;

public class ParcelDetailActivity extends AppCompatActivity implements OnMapReadyCallback {

    private JSONObject parcelJson;
    private Parcel parcel;
    private Toolbar toolbar;
    private static final String TAG="porter_prcl_detail";
    private ImageLoader imageLoader;
    private NetworkImageView parcelImage;
    private TextView eta, phone, weight, price, quantity;
    private CardView color;
    private CustomTextView share,link,message;
    private ScrollView porterScroller;
    private ImageView veil;
    private SupportMapFragment mapFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parcel_detail);
        initWidgets();
        porterScroller.requestDisallowInterceptTouchEvent(false);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        try {
            parcelJson=new JSONObject(getIntent().getStringExtra("parcel"));
            parcel= JsonToModel.getParcel(parcelJson);
            getSupportActionBar().setTitle(parcel.getName());
            getSupportActionBar().setSubtitle(parcel.getType());
            color.setCardBackgroundColor(Color.parseColor(parcel.getColor()));
            parcelImage.setImageUrl(parcel.getImage(), imageLoader);
            eta.setText(Utils.dateFromTimestamp(parcel.getDate() * 1000));
            phone.setText(parcel.getPhone() + "");
            Linkify.addLinks(phone, Linkify.PHONE_NUMBERS);
            quantity.setText(parcel.getQuantity() + "");
            weight.setText(parcel.getWeight() + "kg");
            price.setText(parcel.getPrice()+"");
            share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, parcel.getLink());
                    sendIntent.setType("text/plain");
                    startActivity(sendIntent);
                }
            });
            link.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(parcel.getLink()));
                    startActivity(browserIntent);
                }
            });
            message.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    SendMessageFragment sms = new SendMessageFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("link", parcel.getLink());
                    sms.setArguments(bundle);
                    sms.setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_Light_Dialog);
                    sms.show(getSupportFragmentManager().beginTransaction(), "SendSms");

                }
            });
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.porter_parcel_map);
            mapFragment.getMapAsync(this);
            veil.setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    int action = event.getAction();
                    switch (action) {
                        case MotionEvent.ACTION_DOWN:
                            // Disallow ScrollView to intercept touch events.
                            porterScroller.requestDisallowInterceptTouchEvent(true);
                            // Disable touch on transparent view
                            return false;

                        case MotionEvent.ACTION_UP:
                            // Allow ScrollView to intercept touch events.
                            porterScroller.requestDisallowInterceptTouchEvent(false);
                            return true;

                        case MotionEvent.ACTION_MOVE:
                            porterScroller.requestDisallowInterceptTouchEvent(true);
                            return false;

                        default:
                            return true;
                    }
                }
            });


        }
        catch (JSONException jse)
        {
            jse.printStackTrace();

        }
    }

    private void initWidgets()
    {
        toolbar=(Toolbar)findViewById(R.id.porter_toolbar);
        color=(CardView)findViewById(R.id.porter_parcel_color);
        eta=(TextView)findViewById(R.id.porter_parcel_eta);
        price=(TextView)findViewById(R.id.porter_parcel_price);
        phone=(TextView)findViewById(R.id.porter_parcel_phone);
        weight=(TextView)findViewById(R.id.porter_parcel_weight);
        quantity=(TextView)findViewById(R.id.porter_parcel_quantity);
        parcelImage=(NetworkImageView)findViewById(R.id.porter_parcel_image);
        imageLoader=new ImageLoader(PorterApplication.getInstance().getAppRequestQueue(),new BitmapLruCache());
        share=(CustomTextView)findViewById(R.id.porter_parcel_share);
        link=(CustomTextView)findViewById(R.id.porter_parcel_link);
        message=(CustomTextView)findViewById(R.id.porter_parcel_sms);
        porterScroller=(ScrollView)findViewById(R.id.porter_scroller);
        veil=(ImageView)findViewById(R.id.porter_veil_image);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng latLng =new LatLng(parcel.getLiveLocation().getLatitude(), parcel.getLiveLocation().getLongitude());
        googleMap.addMarker(new MarkerOptions().position(latLng).title(parcel.getName()));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,14.0f));
        //Can add animating logic here for moving marker. Current API is not friendly for this.
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem settings = menu.getItem(0);
        settings.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id)
        {
            case android.R.id.home:
                finish();
        }


        return super.onOptionsItemSelected(item);
    }
}
