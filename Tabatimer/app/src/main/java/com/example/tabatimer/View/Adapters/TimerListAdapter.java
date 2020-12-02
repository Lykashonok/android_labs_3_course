package com.example.tabatimer.View.Adapters;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tabatimer.Model.DB;
import com.example.tabatimer.Model.Tables.TabataItem;
import com.example.tabatimer.Model.Tables.TabataItemInSet;
import com.example.tabatimer.ModelView.ApplicationViewModel;
import com.example.tabatimer.R;

import java.util.List;


public class TimerListAdapter extends BaseAdapter<TimerListAdapter.TimerListViewHolder, TabataItemInSet> {

    public TimerListAdapter(Fragment context, List<TabataItemInSet> dataList, int listRowViewId, Builder emptyView) {
        super(context, dataList, listRowViewId, emptyView);
        applicationViewModel = new ViewModelProvider(context.requireActivity()).get(ApplicationViewModel.class);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(@NonNull TimerListViewHolder holder, int position) {
        TabataItemInSet iis = dataList.get(position);
        TabataItem item = database.tabataItemDao().getById(iis.id_tabata_item);
        holder.textView.setText(item.title + " " + item.duration);
        holder.setItem(item);

        applicationViewModel.getCurrentTimerItemInSet().observe(context.getViewLifecycleOwner(), tabataItemInSet -> {
            if (iis.id == tabataItemInSet.id) {
                holder.itemView.setBackgroundColor(Color.YELLOW);
            } else {
                if (TimerListAdapter.isDarkTheme){
                    holder.itemView.setBackgroundColor(holder.itemView.getResources().getColor(R.color.purple_100));
                } else {
                    holder.itemView.setBackgroundColor(holder.itemView.getResources().getColor(R.color.white));
                }
            }
        });
    }



    public static class TimerListViewHolder extends RecyclerView.ViewHolder implements BaseAdapter.Builder, BaseAdapter.ViewHolderSelect {
        public TextView textView, color;
        public View itemView;
        public TabataItem item;
        public TimerListViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            color = itemView.findViewById(R.id.item_color);
            textView = itemView.findViewById(R.id.text_view);
        }

        @Override
        public void select() {
            itemView.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void clear() {
            Drawable mDrawable =  ContextCompat.getDrawable(itemView.getContext(), R.drawable.round_button);
            mDrawable.setColorFilter(Color.parseColor(item.colour.charAt(0) == '#' ? item.colour : "#" +item.colour), PorterDuff.Mode.SRC_IN);
            if (TimerListAdapter.isDarkTheme){
                itemView.setBackgroundColor(itemView.getResources().getColor(R.color.purple_100));
            } else {
                itemView.setBackgroundColor(itemView.getResources().getColor(R.color.white));
            }
            color.setBackground(mDrawable);
        }

        @Override
        public TimerListViewHolder runConstructor(View view) {
            return new TimerListViewHolder(view);
        }
        public void setItem(TabataItem item) {
            this.item = item;
            clear();
        }
    }
}