package com.example.coffeeshop;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class SewerAuthorizeAdapter extends ArrayAdapter<SewerAuthorize> {
    private Context mContext;
    int resource;

    public SewerAuthorizeAdapter(Context mContext, int resource, ArrayList<SewerAuthorize> authorArrayList) {
        super(mContext, resource, authorArrayList);
        this.mContext = mContext;
        this.resource = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(mContext);
            v = vi.inflate(resource, null);
        }

        SewerAuthorize p = getItem(position);

        if (p != null) {
            TextView sewer_name = (TextView) v.findViewById(R.id.sewer_name);
            TextView sewer_add = (TextView) v.findViewById(R.id.sewer_add);
            TextView sewer_modify = (TextView) v.findViewById(R.id.sewer_modify);
            TextView sewer_remove = (TextView) v.findViewById(R.id.sewer_remove);
            if (sewer_remove != null) {
                sewer_remove.setText(p.getSewerModify());
            }

            if (sewer_modify != null) {
                sewer_modify.setText(p.getSewerRemove());
            }
            if (sewer_name != null) {
                sewer_name.setText(getNamefromId(p.getSewerId()));
            }

            if (sewer_add != null) {
                sewer_add.setText(p.getSewerAdd());
            }

        }

        return v;
    }

    public String getNamefromId(Long id){
        return "Sewer " + id.toString();
    }
}
