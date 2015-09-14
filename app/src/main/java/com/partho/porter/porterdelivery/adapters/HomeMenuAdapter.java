package com.partho.porter.porterdelivery.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.partho.porter.porterdelivery.R;
import com.partho.porter.porterdelivery.utils.ui.CustomTextView;

import java.util.HashMap;
import java.util.List;

/**
 * Created by partho on 22/8/15.
 */
public class HomeMenuAdapter extends BaseAdapter {

    private Activity activity;
    private List<HashMap<String,String>> menus;

    public HomeMenuAdapter(Activity activity, List<HashMap<String, String>> menus)
    {
        this.activity=activity;
        this.menus=menus;
    }

    @Override
    public int getCount() {
        return menus.size();
    }


    class ViewHolder
    {
        CustomTextView menuIcon;
        TextView menuName;
    }


    @Override
    public View getView(int pos, View convertView, ViewGroup parent) {

        ViewHolder holder;
        HashMap<String, String> menu = menus.get(pos);
        if(convertView==null)
        {
            convertView= LayoutInflater.from(activity).inflate(R.layout.menu_item,parent,false);
            holder=new ViewHolder();
            holder.menuIcon=(CustomTextView)convertView.findViewById(R.id.porter_menu_icon);
            holder.menuName=(TextView)convertView.findViewById(R.id.porter_menu_title);
            convertView.setTag(holder);
        }
        else
        {
            holder=(ViewHolder)convertView.getTag();
        }
        holder.menuName.setText(menu.get("title"));
        holder.menuIcon.setText(menu.get("icon"));
        return convertView;
    }





    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return menus.get(position);
    }
}
