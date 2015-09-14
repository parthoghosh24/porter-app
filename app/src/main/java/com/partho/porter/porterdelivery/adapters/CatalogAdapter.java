package com.partho.porter.porterdelivery.adapters;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.partho.porter.porterdelivery.ParcelDetailActivity;
import com.partho.porter.porterdelivery.PorterApplication;
import com.partho.porter.porterdelivery.R;
import com.partho.porter.porterdelivery.models.Parcel;
import com.partho.porter.porterdelivery.utils.ui.BitmapLruCache;

import java.util.List;

/**
 * Created by partho on 22/8/15.
 */
public class CatalogAdapter extends RecyclerView.Adapter<CatalogAdapter.CatalogItemViewHolder>{

    private Activity mContext;
    private List<Parcel> parcels;
    private ImageLoader imageLoader;
    private static final String TAG="porter_cat_adptr";

    public CatalogAdapter(Activity mContext, List<Parcel> parcels)
    {
        this.mContext=mContext;
        this.parcels = parcels;
        imageLoader=new ImageLoader(PorterApplication.getInstance().getAppRequestQueue(),new BitmapLruCache());
    }

    @Override
    public CatalogItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.parcel_item,parent,false);
        return new CatalogItemViewHolder(itemLayoutView);
    }

    @Override
    public void onBindViewHolder(CatalogItemViewHolder holder, final int position) {
            holder.parcelImage.setImageUrl(parcels.get(position).getImage(), imageLoader);
            holder.parcelName.setText(parcels.get(position).getName());
            holder.parcelWeight.setText(parcels.get(position).getWeight() + "kg");
            holder.parcelPrice.setText(parcels.get(position).getPrice() + "");
            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, ParcelDetailActivity.class);
                    intent.putExtra("parcel", parcels.get(position).getJsonObject());
                    mContext.startActivity(intent);
                }
            });
    }

    @Override
    public int getItemCount() {
        return parcels.size();
    }

    public static class CatalogItemViewHolder extends RecyclerView.ViewHolder
    {
        public NetworkImageView parcelImage;
        public TextView parcelName, parcelWeight, parcelPrice,parcelDistance;
        public View view;

        public CatalogItemViewHolder(View itemView)
        {
            super(itemView);
            parcelImage =(NetworkImageView)itemView.findViewById(R.id.porter_catalog_image);
            parcelWeight =(TextView)itemView.findViewById(R.id.porter_parcel_catalog_weight);
            parcelName =(TextView)itemView.findViewById(R.id.porter_parcel_catalog_title);
            parcelPrice =(TextView)itemView.findViewById(R.id.porter_parcel_catalog_price);
            view=itemView;
        }


    }
}
