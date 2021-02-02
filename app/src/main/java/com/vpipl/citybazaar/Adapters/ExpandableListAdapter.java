package com.vpipl.citybazaar.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.vpipl.citybazaar.R;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Mukesh on 27/12/2019.
 */

public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private Context _context;
    private List<String> _listDataHeader;

    // header titles
    // child data in format of header title, child title

    private HashMap<String, List<String>> _listDataChild;

    public ExpandableListAdapter(Context context, List<String> listDataHeader, HashMap<String, List<String>> listChildData) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition)).get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        final String childText = (String) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.explist_child_item, null);
        }

        TextView txtListChild = convertView.findViewById(R.id.lblListItem);
        txtListChild.setText(childText);
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.explist_group_item, null);
        }

        TextView lblListHeader = convertView.findViewById(R.id.lblListHeader);
        lblListHeader.setText(headerTitle);

//        headerTitle = lblListHeader.getText().toString().trim();

        ImageView imageView4 = convertView.findViewById(R.id.imageView4);

        if (headerTitle.equalsIgnoreCase("Dashboard"))
            imageView4.setImageDrawable(_context.getResources().getDrawable(R.drawable.ic_nav_dashboard));
        else if (headerTitle.equalsIgnoreCase("My Profile"))
            imageView4.setImageDrawable(_context.getResources().getDrawable(R.drawable.ic_nav_profile));
        else if (headerTitle.equalsIgnoreCase("My Network"))
            imageView4.setImageDrawable(_context.getResources().getDrawable(R.drawable.ic_nav_network));
        else if (headerTitle.equalsIgnoreCase("Business Plan"))
            imageView4.setImageDrawable(_context.getResources().getDrawable(R.drawable.ic_nav_business_plan));
        else if (headerTitle.equalsIgnoreCase("Document"))
            imageView4.setImageDrawable(_context.getResources().getDrawable(R.drawable.ic_nav_document));
        else if (headerTitle.equalsIgnoreCase("Wallet Section"))
            imageView4.setImageDrawable(_context.getResources().getDrawable(R.drawable.ic_nav_wallet));
        else if (headerTitle.equalsIgnoreCase("Order Section"))
            imageView4.setImageDrawable(_context.getResources().getDrawable(R.drawable.ic_nav_order));
        else if (headerTitle.equalsIgnoreCase("E-Pin Management"))
            imageView4.setImageDrawable(_context.getResources().getDrawable(R.drawable.ic_nav_e_pin));
        else if (headerTitle.equalsIgnoreCase("Reward Module"))
            imageView4.setImageDrawable(_context.getResources().getDrawable(R.drawable.ic_nav_reward));
        else if (headerTitle.equalsIgnoreCase("Incentive Module"))
            imageView4.setImageDrawable(_context.getResources().getDrawable(R.drawable.ic_nav_repurchase));
        else if (headerTitle.equalsIgnoreCase("My Payout"))
            imageView4.setImageDrawable(_context.getResources().getDrawable(R.drawable.ic_nav_payout));
        else if (headerTitle.equalsIgnoreCase("Enquiry"))
            imageView4.setImageDrawable(_context.getResources().getDrawable(R.drawable.ic_nav_enquiry));
        else if (headerTitle.equalsIgnoreCase("Logout"))
            imageView4.setImageDrawable(_context.getResources().getDrawable(R.drawable.icon_logout));

        else if (headerTitle.equalsIgnoreCase("Generated/Issued Pin Details"))
            imageView4.setImageDrawable(_context.getResources().getDrawable(R.drawable.nav_icon_epin_generate));
        else if (headerTitle.equalsIgnoreCase("Topup/E-Pin Detail"))
            imageView4.setImageDrawable(_context.getResources().getDrawable(R.drawable.nav_icon_epin_details));
        else if (headerTitle.equalsIgnoreCase("E-Pin Transfer"))
            imageView4.setImageDrawable(_context.getResources().getDrawable(R.drawable.nav_icon_epin_transfer));
        else if (headerTitle.equalsIgnoreCase("E-Pin Received Detail"))
            imageView4.setImageDrawable(_context.getResources().getDrawable(R.drawable.nav_icon_epin_received_details));
        else if (headerTitle.equalsIgnoreCase("E-Pin Transfer Detail"))
            imageView4.setImageDrawable(_context.getResources().getDrawable(R.drawable.nav_icon_epin_transfer_details));
        else if (headerTitle.equalsIgnoreCase("E-Pin Request"))
            imageView4.setImageDrawable(_context.getResources().getDrawable(R.drawable.nav_icon_epin_details));
        else if (headerTitle.equalsIgnoreCase("E-Pin Request Detail"))
            imageView4.setImageDrawable(_context.getResources().getDrawable(R.drawable.nav_icon_order_list));

        ImageView imageView3 = convertView.findViewById(R.id.imageView3);

         if (getChildrenCount(groupPosition) > 0) {
         //   imageView3.setImageResource(R.drawable.ic_expand_more_white);
            imageView3.setImageResource(R.drawable.ic_arrow_drop_down_grey);
            imageView3.setVisibility(View.VISIBLE);
        } else
            imageView3.setVisibility(View.GONE);

        if (isExpanded) {
          //  imageView3.setImageResource(R.drawable.ic_expand_less_white);
            imageView3.setImageResource(R.drawable.ic_arrow_drop_up_grey);
        } else {
        }

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}