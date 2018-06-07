package com.example.seniorpj100per.History.Month;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.example.seniorpj100per.Home.HomeResult;
import com.example.seniorpj100per.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Smew on 22/9/2560.
 */

public class ListViewAdapter_History_Month extends BaseAdapter implements Filterable {

    List<HomeResult> foodName = new ArrayList<>();
    TextView tvNameFood;
    TextView tv_energy;
    TextView tv_date;
    List<HomeResult> mStringFilterList = null;
    ValueFilter valueFilter;

    public ListViewAdapter_History_Month(List<HomeResult> foodName) {
        this.foodName = foodName;
        mStringFilterList = foodName;
    }

    @Override
    public int getCount() {
        return foodName.size();
    }

    @Override
    public Object getItem(int position) {
        return foodName.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater mInflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = mInflater.inflate(R.layout.item_listview_history_month, parent, false);

        tvNameFood = (TextView) convertView.findViewById(R.id.tv_name_month);
        tv_energy = (TextView) convertView.findViewById(R.id.tv_energy_month);
        tv_date = (TextView) convertView.findViewById(R.id.tv_date_month);

        tvNameFood.setText(foodName.get(position).getNamefood());
        tv_energy.setText(""+foodName.get(position).getEnergy());
        tv_date.setText(foodName.get(position).getDate());

        return convertView;
    }

    @Override
    public Filter getFilter() {
        if (valueFilter == null) {
            valueFilter = new ValueFilter();
        }
        return valueFilter;
    }

    private class ValueFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            List<String> filterList = new ArrayList<>();
            if (constraint != null && constraint.length() > 0) {
                for (int i = 0; i < mStringFilterList.size(); i++) {
                }
                results.count = filterList.size();
                results.values = filterList;
            } else {
                results.count = mStringFilterList.size();
                results.values = mStringFilterList;
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {
            notifyDataSetChanged();
        }
    }
}
