package com.example.tabatimer.View.Adapters;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
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
import com.example.tabatimer.Model.Tables.TabataSet;
import com.example.tabatimer.R;
import com.example.tabatimer.View.SettingsFragment;

import java.util.List;

public class SetListAdapter extends BaseAdapter<SetListAdapter.SetListViewHolder, TabataSet>{

    public SetListAdapter(Fragment context, List<TabataSet> dataList, int listRowViewId, Builder emptyView) {
        super(context, dataList, listRowViewId, emptyView);
    }

    @Override
    public void onBindViewHolder(@NonNull SetListViewHolder holder, int position) {
        TabataSet set = dataList.get(position);
        holder.textView.setText(set.title);
        holder.setSet(set);
        holder.buttonEdit.setOnClickListener(view -> {
            TabataSet s = dataList.get(holder.getAdapterPosition());
            applicationViewModel.setCurrentSet(s);
            Navigation.findNavController(view).navigate(R.id.action_tabataSetListFragment_to_tabataSetEditFragment);
        });

        holder.buttonStart.setOnClickListener(v -> {

            applicationViewModel.setCurrentSet(set);
            Navigation.findNavController(v).navigate(R.id.action_tabataSetListFragment_to_tabatimerFragment);
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
                database.tabataSetDao().delete(set);
                int index = holder.getAdapterPosition();
                dataList.remove(index);
                notifyItemRemoved(index);
                notifyItemRangeChanged(index, dataList.size());

                Toast.makeText(context.getContext(), view.getResources().getString(R.string.deleted_successfully), Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            });
        });
    }

    public static class SetListViewHolder extends RecyclerView.ViewHolder implements BaseAdapter.Builder, BaseAdapter.ViewHolderSelect {
        public TextView textView, color;
        public ImageView buttonEdit, buttonDelete, buttonStart;
        public View itemView;
        public TabataSet set;
        public SetListViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            color = itemView.findViewById(R.id.set_color);
            textView = itemView.findViewById(R.id.text_view);
            buttonEdit = itemView.findViewById(R.id.edit_set);
            buttonStart = itemView.findViewById(R.id.start_set);
            buttonDelete = itemView.findViewById(R.id.delete_set);
        }

        @Override
        public void select() {
            itemView.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void clear() {
            Drawable mDrawable =  ContextCompat.getDrawable(itemView.getContext(), R.drawable.round_button);
            mDrawable.setColorFilter(Color.parseColor(set.colour.charAt(0) == '#' ? set.colour : "#" +set.colour), PorterDuff.Mode.SRC_IN);
            color.setBackground(mDrawable);
            if (SetListAdapter.isDarkTheme){
                itemView.setBackgroundColor(itemView.getResources().getColor(R.color.purple_100));
            } else {
                itemView.setBackgroundColor(itemView.getResources().getColor(R.color.white));
            }
        }

        @Override
        public SetListViewHolder runConstructor(View view) {
            return new SetListViewHolder(view);
        }
        public void setSet(TabataSet set) {
            this.set = set;
            clear();
        }
    }
}
