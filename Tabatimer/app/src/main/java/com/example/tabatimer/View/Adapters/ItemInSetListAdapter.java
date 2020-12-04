package com.example.tabatimer.View.Adapters;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
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


public class ItemInSetListAdapter extends BaseAdapter<ItemInSetListAdapter.ItemInSetListViewHolder, TabataItemInSet>{

    public ItemInSetListAdapter(Fragment context, List<TabataItemInSet> dataList, int listRowViewId, Builder emptyView) {
        super(context, dataList, listRowViewId, emptyView);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onRowMoved(int fromPosition, int toPosition) {
        super.onRowMoved(fromPosition, toPosition);
        applicationViewModel.reorderItemsInSet(dataList);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(@NonNull ItemInSetListAdapter.ItemInSetListViewHolder holder, int position) {
        TabataItemInSet iis = dataList.get(position);
        TabataItem item = database.tabataItemDao().getById(iis.id_tabata_item);
        holder.textView.setText(item.title);
        holder.setItem(item);

        holder.buttonCopy.setOnClickListener(v -> {
            TabataSet set = applicationViewModel.getCurrentSet().getValue();
            TabataItemInSet n_iis = new TabataItemInSet(set.id, item.id, position+1);
            database.tabataItemInSetDao().insert(n_iis);
            dataList.add(position+1, n_iis);
            notifyItemInserted(position+1);
            notifyItemRangeChanged(position+1, dataList.size()+1);
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
                database.tabataItemInSetDao().delete(iis);
                int index = holder.getAdapterPosition();
                dataList.remove(index);
                notifyItemRemoved(index);
                notifyItemRangeChanged(index, dataList.size());

                applicationViewModel.reorderItemsInSet(dataList);
                Toast.makeText(context.getContext(), context.getString(R.string.deleted_successfully), Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            });
        });
    }

    public static class ItemInSetListViewHolder extends RecyclerView.ViewHolder implements BaseAdapter.Builder, BaseAdapter.ViewHolderSelect {
        public TextView textView, color;
        public ImageView buttonCopy, buttonDelete;
        public View itemView;
        public TabataItem item;
        public ItemInSetListViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            color = itemView.findViewById(R.id.item_in_set_color);
            textView = itemView.findViewById(R.id.text_view);
            buttonCopy = itemView.findViewById(R.id.copy_item_in_set);
            buttonDelete = itemView.findViewById(R.id.delete_item_in_set);
        }

        @Override
        public void select() {
            itemView.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void clear() {
            Drawable mDrawable =  ContextCompat.getDrawable(itemView.getContext(), R.drawable.round_button);
            mDrawable.setColorFilter(Color.parseColor(item.colour.charAt(0) == '#' ? item.colour : "#" +item.colour), PorterDuff.Mode.SRC_IN);
            if (ItemInSetListAdapter.isDarkTheme){
                itemView.setBackgroundColor(itemView.getResources().getColor(R.color.purple_100));
            } else {
                itemView.setBackgroundColor(itemView.getResources().getColor(R.color.white));
            }
            color.setBackground(mDrawable);
        }

        @Override
        public ItemInSetListViewHolder runConstructor(View view) { return new ItemInSetListViewHolder(view);
        }
        public void setItem(TabataItem item) {
            this.item = item;
            clear();
        }
    }
}