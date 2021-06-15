package com.example.financy;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.util.ArrayList;

public class EntryListAdapter extends ArrayAdapter<Entry> {

    private int lastPosition = -1;
    private Context mContext;
    private int mResource;

    static class ViewHolder {
        TextView title;
        TextView amount;
    }

    public EntryListAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Entry> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        String title = getItem(position).getTitle();
        String amount = String.valueOf(getItem(position).getAmount());

        String[] tmp = amount.split("\\.");

        if (tmp[1].length() == 1) {
            amount = tmp[0] + "." + tmp[1] + "0";
        }
        amount = amount + "€";

        final View result;
        ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);

            result = convertView;
            holder = new ViewHolder();
            holder.title = convertView.findViewById(R.id.entry_title);
            holder.amount = convertView.findViewById(R.id.entry_amount);

            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

        Animation animation = AnimationUtils.loadAnimation(mContext,(position > lastPosition) ? R.anim.animation_load_down : R.anim.animation_load_up);
        result.startAnimation(animation);
        lastPosition = position;

        holder.title.setText(title);
        holder.amount.setText(amount);

        return convertView;
    }
}
