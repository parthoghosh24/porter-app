package com.partho.porter.porterdelivery;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.partho.porter.porterdelivery.adapters.CatalogAdapter;
import com.partho.porter.porterdelivery.adapters.HomeMenuAdapter;
import com.partho.porter.porterdelivery.models.JsonToModel;
import com.partho.porter.porterdelivery.models.Parcel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;


public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, SearchView.OnCloseListener {

    private static final String PORTER_URL="https://porter.0x10.info/api/parcel?type=json&query=list_parcel";
    private static final String PORTER_API_COUNT="https://porter.0x10.info/api/parcel?type=json&query=api_hits";
    private static final String TAG="porter_home";
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle menuToggle;
    private String catalog;
    private JSONObject parcelsCatalog;
    private ArrayList<Parcel> parcelModels;
    private RecyclerView parcelsCatalogView;
    private LinearLayoutManager parcelsCatalogLayoutManager;
    private CatalogAdapter catalogAdapter;
    private TextView parcelsCount, apiCount;
    private Spinner sortSelector;
    private Toolbar toolbar;
    private HomeMenuAdapter homeMenuAdapter;
    private List<HashMap<String,String>> menus;
    private MenuItem mSearchAction;
    private boolean isSearchOpened = false;
    private EditText porterSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initWidget();
        menuToggle=new ActionBarDrawerToggle(this,mDrawerLayout,toolbar,R.string.drawer_open,R.string.drawer_close){
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                invalidateOptionsMenu();
                syncState();
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
                syncState();
            }
        };

        mDrawerLayout.setDrawerListener(menuToggle);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        menuToggle.syncState();
        String sortChoices[]={"Name","Value","Weight"};
        ArrayAdapter<String> sortSelectorAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,sortChoices);
        sortSelectorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortSelector.setAdapter(sortSelectorAdapter);
        sortSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

                switch (pos) {

                    case 0: //Name
                        Collections.sort(parcelModels, new Comparator<Parcel>() {
                            @Override
                            public int compare(Parcel parcel, Parcel parcel2) {
                                return parcel.getName().compareToIgnoreCase(parcel2.getName());
                            }
                        });
                        catalogAdapter = new CatalogAdapter(MainActivity.this, parcelModels);
                        catalogAdapter.notifyDataSetChanged();
                        parcelsCatalogView.setAdapter(catalogAdapter);

                        break;

                    case 1: //Value
                        Collections.sort(parcelModels, new Comparator<Parcel>() {
                            @Override
                            public int compare(Parcel parcel, Parcel parcel2) {
                                return parcel.getPrice().compareTo(parcel2.getPrice());
                            }
                        });
                        catalogAdapter = new CatalogAdapter(MainActivity.this, parcelModels);
                        catalogAdapter.notifyDataSetChanged();
                        parcelsCatalogView.setAdapter(catalogAdapter);
                        break;

                    case 2: //Weight
                        Collections.sort(parcelModels, new Comparator<Parcel>() {
                            @Override
                            public int compare(Parcel parcel, Parcel parcel2) {
                                return parcel.getWeight().compareTo(parcel2.getWeight());
                            }
                        });
                        catalogAdapter = new CatalogAdapter(MainActivity.this, parcelModels);
                        catalogAdapter.notifyDataSetChanged();
                        parcelsCatalogView.setAdapter(catalogAdapter);
                        break;

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    @Override
    public boolean onClose() {
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    private void initWidget()
    {
        toolbar=(Toolbar)findViewById(R.id.porter_toolbar);
        mDrawerLayout=(DrawerLayout)findViewById(R.id.porter_drawer);
        mDrawerList=(ListView)findViewById(R.id.porter_home_nav_menu);
        parcelsCatalogView =(RecyclerView)findViewById(R.id.porter_parcels_catalog);
        apiCount=(TextView)findViewById(R.id.porter_api_count);
        parcelsCount =(TextView)findViewById(R.id.porter_parcel_count);
        parcelsCatalogLayoutManager =new LinearLayoutManager(this);
        parcelsCatalogLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        parcelsCatalogView.setLayoutManager(parcelsCatalogLayoutManager);
        sortSelector=(Spinner)findViewById(R.id.porter_catalog_sort_selector);
        menus=new ArrayList<>();
        HashMap<String, String> homeMap = new HashMap<>();
        homeMap.put("icon", getResources().getString(R.string.fa_home));
        homeMap.put("title","Home");
        menus.add(homeMap);
        homeMenuAdapter=new HomeMenuAdapter(this,menus);
        mDrawerList.setAdapter(homeMenuAdapter);
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                    mDrawerLayout.closeDrawer(mDrawerList);

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        catalog=getIntent().getStringExtra("parcels");
        if(TextUtils.isEmpty(catalog))
        {
            refreshList();
        }
        else
        {
            try{
                parcelsCatalog =new JSONObject(catalog);
                JSONArray parcelList=parcelsCatalog.getJSONArray("parcels");
                parcelsCount.setText(parcelList.length() + "");
                parcelModels = JsonToModel.getParcels(parcelList);
                catalogAdapter = new CatalogAdapter(this, parcelModels);
                parcelsCatalogView.setAdapter(catalogAdapter);
            }
            catch (JSONException jse)
            {
                Log.e(TAG, jse.getLocalizedMessage());
            }
        }

        refreshAPICount();

    }

    private void refreshList()
    {
        FetchPorterParcels request = new FetchPorterParcels();
        request.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 10, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        PorterApplication.getInstance().getAppRequestQueue().add(request);

    }

    private void refreshAPICount()
    {
        FetchPorterAPICount count = new FetchPorterAPICount();
        count.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 10, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        PorterApplication.getInstance().getAppRequestQueue().add(count);

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        menuToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        menuToggle.onConfigurationChanged(newConfig);
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
        int id = item.getItemId();

        switch (id)
        {
            case android.R.id.home:
                if(mDrawerLayout.isDrawerOpen(mDrawerList))
                {
                    mDrawerLayout.closeDrawer(mDrawerList);
                }
                else
                {
                    mDrawerLayout.openDrawer(mDrawerList);
                }
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
                            parcelsCatalog =response;
                            parcelsCount.setText(response.length() + "");

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

    class FetchPorterAPICount extends JsonObjectRequest
    {
        public FetchPorterAPICount()
        {
            super(Method.GET,
                    PORTER_API_COUNT,
                    new JSONObject(),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            Log.e(TAG, "IN HERE..............");
                            try {
                                int count = Integer.parseInt(response.getString("api_hits"));
                                apiCount.setText(count+"");
                            }
                            catch (JSONException jse)
                            {
                                Log.e(TAG,jse.getLocalizedMessage());
                            }

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
                            Toast.makeText(getApplicationContext(),"Please try again!",Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}
