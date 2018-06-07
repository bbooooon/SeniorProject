package com.example.seniorpj100per.FoodList;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.example.seniorpj100per.R;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Smew on 22/9/2560.
 */

public class ListViewAdapter extends BaseAdapter implements Filterable {

    List<KcalTable1> foodName = new ArrayList<>();
    TextView tvNameFood;
    ImageView imgView;
    List<KcalTable1> mStringFilterList = null;
    ValueFilter valueFilter;
    Bitmap bitmap = null;
    String Url = null;
    Context context;

    public ListViewAdapter(List<KcalTable1> foodName, Context context) {
        this.foodName = foodName;
        mStringFilterList = foodName;
        this.context = context;
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
        convertView = mInflater.inflate(R.layout.item_listview_foodlist, parent, false);

        tvNameFood = (TextView) convertView.findViewById(R.id.tv_namefood);
        imgView = (ImageView) convertView.findViewById(R.id.img_foodlist);

        String foodname = foodName.get(position).getFoodname_th();
        String filename = foodName.get(position).getFilename();
        tvNameFood.setText(foodname);

        Context context = imgView.getContext();
        int id = context.getResources().getIdentifier(filename, "mipmap", context.getPackageName());
        Glide.with(context).load(id).asBitmap().override(1080, 600).into(imgView);
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
