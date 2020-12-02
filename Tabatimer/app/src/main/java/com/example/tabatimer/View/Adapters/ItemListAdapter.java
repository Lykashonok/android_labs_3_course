package com.example.tabatimer.View.Adapters;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tabatimer.Model.DB;
import com.example.tabatimer.Model.Tables.TabataItem;
import com.example.tabatimer.Model.Tables.TabataItemInSet;
import com.example.tabatimer.Model.Tables.TabataSet;
import com.example.tabatimer.R;
import com.example.tabatimer.View.SettingsFragment;

import java.util.List;


public class ItemListAdapter extends BaseAdapter<ItemListAdapter.ItemListViewHolder, TabataItem>{

    public ItemListAdapter(Fragment context, List<TabataItem> dataList, int listRowViewId, Builder emptyView) {
        super(context, dataList, listRowViewId, emptyView);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemListAdapter.ItemListViewHolder holder, int position) {
        TabataItem item = dataList.get(position);
        holder.textView.setText(item.title);
        holder.setItem(item);

        holder.buttonEdit.setOnClickListener(view -> {
            TabataItem i = dataList.get(holder.getAdapterPosition());
            applicationViewModel.setCurrentItem(i);
            Navigation.findNavController(view).navigate(R.id.action_tabataItemListFragment_to_tabataItemEditFragment);
        });

        holder.buttonChoose.setOnClickListener(v -> {
            TabataSet set = applicationViewModel.getCurrentSet().getValue();
            TabataItemInSet iis = new TabataItemInSet(set.id, item.id, position);
            database.tabataItemInSetDao().insert(iis);
            ItemInSetListAdapter itemInSetListAdapter = applicationViewModel.getCurrentItemInSetListAdapter().getValue();
            itemInSetListAdapter.notifyItemInserted(position+1);
            itemInSetListAdapter.notifyItemRangeChanged(position,position+1);
            Navigation.findNavController(v).navigateUp();
        });

        holder.buttonDelete.setOnClickListener(view -> {
            Dialog dialog = new Dialog(context.getContext(),
                    Boolean.parseBoolean(database.tabataSettingDao().getSetting(SettingsFragment.themeKey)) ?
                            R.style.ThemeOverlay_AppCompat_Dark :
                            R.style.Theme_Tabatimer_alert
            );
            // Context of delete dialog
            dialog.setContentView(R.layout.sure_dialog);
            int width = WindowManager.LayoutParams.MATCH_PARENT;
            int height = WindowManager.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setLayout(width, height);
            // Show dialog
            dialog.show();

            Button button_apply = dialog.findViewById(R.id.dialog_apply);

            // Buttons onclick listeners
            button_apply.setOnClickListener(v -> {
                database.tabataItemDao().delete(item);
                int index = holder.getAdapterPosition();
                dataList.remove(index);
                notifyItemRemoved(index);
                notifyItemRangeChanged(index, dataList.size());

                Toast.makeText(context.getContext(), context.getString(R.string.deleted_successfully), Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            });
        });
    }

    public static class ItemListViewHolder extends RecyclerView.ViewHolder implements BaseAdapter.Builder, BaseAdapter.ViewHolderSelect {
        public TextView textView, color;
        public ImageView buttonEdit, buttonDelete, buttonChoose;
        public View itemView;
        public TabataItem item;
        public ItemListViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            color = itemView.findViewById(R.id.item_color);
            textView = itemView.findViewById(R.id.text_view);
            buttonEdit = itemView.findViewById(R.id.edit_item);
            buttonDelete = itemView.findViewById(R.id.delete_item);
            buttonChoose = itemView.findViewById(R.id.choose_item);
        }

        @Override
        public void select() {
            itemView.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void clear() {
            Drawable mDrawable =  ContextCompat.getDrawable(itemView.getContext(), R.drawable.round_button);
            mDrawable.setColorFilter(Color.parseColor(item.colour.charAt(0) == '#' ? item.colour : "#" +item.colour), PorterDuff.Mode.SRC_IN);
            color.setBackground(mDrawable);
            if (ItemListAdapter.isDarkTheme){
                itemView.setBackgroundColor(itemView.getResources().getColor(R.color.purple_100));
            } else {
                itemView.setBackgroundColor(itemView.getResources().getColor(R.color.white));
            }
        }

        @Override
        public ItemListViewHolder runConstructor(View view) {
            return new ItemListViewHolder(view);
        }
        public void setItem(TabataItem item) {
            this.item = item;
            clear();
        }
    }
}
