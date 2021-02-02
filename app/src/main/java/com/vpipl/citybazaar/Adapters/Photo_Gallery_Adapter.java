package com.vpipl.citybazaar.Adapters;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.vpipl.citybazaar.PhotoGalleryDetailsActivity;
import com.vpipl.citybazaar.R;
import com.vpipl.citybazaar.Utils.AppUtils;

import java.util.ArrayList;
import java.util.HashMap;

public class Photo_Gallery_Adapter extends RecyclerView.Adapter<Photo_Gallery_Adapter.MyViewHolder> {

    public ArrayList<HashMap<String, String>> photo_list;
    LayoutInflater inflater = null;
    Context context;

    public Photo_Gallery_Adapter(Context con, ArrayList<HashMap<String, String>> list) {
        photo_list = list;
        inflater = (LayoutInflater) con.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        context = con;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(inflater.inflate(R.layout.photo_gallery_adapter, parent, false));
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        try {

            if (!photo_list.get(position).get("Name").equalsIgnoreCase("")) {
                holder.txt_name.setVisibility(View.VISIBLE);
            } else {
                holder.txt_name.setVisibility(View.GONE);
            }
            holder.txt_name.setSelected(true);
            holder.txt_name.setSingleLine(true);

            String html = photo_list.get(position).get("Name");
            holder.txt_name.setText(Html.fromHtml(html));
            AppUtils.loadImage(context, photo_list.get(position).get("Photo_Url"), holder.iv_photo);

            holder.ll_main.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, PhotoGalleryDetailsActivity.class);
                    // intent.putExtra("id" , photo_list.get(position).get("id"));
                    intent.putExtra("id", "" + position);
                    intent.putExtra("List", photo_list);
                    context.startActivity(intent);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return photo_list.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txt_name;
        ImageView iv_photo;
        LinearLayout ll_main;

        public MyViewHolder(View view) {
            super(view);
            iv_photo = (ImageView) view.findViewById(R.id.iv_photo);
            txt_name = (TextView) view.findViewById(R.id.txt_name);
            ll_main = (LinearLayout) view.findViewById(R.id.ll_main);
        }
    }
}