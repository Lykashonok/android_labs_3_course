package com.example.tabatimer.View.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tabatimer.Model.DB;
import com.example.tabatimer.Model.Tables.TabataSet;
import com.example.tabatimer.ModelView.ApplicationViewModel;
import com.example.tabatimer.R;
import com.example.tabatimer.View.SettingsFragment;

import java.util.Collections;
import java.util.List;

public abstract class BaseAdapter<BaseViewHolder extends RecyclerView.ViewHolder
        & BaseAdapter.Builder & BaseAdapter.ViewHolderSelect, T>
        extends RecyclerView.Adapter<BaseViewHolder>
        implements BaseItemMoveCallback.ItemTouchHelperContract<BaseViewHolder> {

    public interface Builder<BaseViewHolder> {
        BaseViewHolder runConstructor(View view);
    }

    public interface ViewHolderSelect{
        void select();
        void clear();
    }

    protected List<T> dataList;
    protected Fragment context;
    protected DB database;
    protected ApplicationViewModel applicationViewModel;
    private Builder<BaseViewHolder> builder;
    // R.layout.set_list_row_view
    protected int listRowViewId;
    public static Boolean isDarkTheme;

    public BaseAdapter(Fragment context, List<T> dataList, int listRowViewId, Builder<BaseViewHolder> builder) {
        this.builder = builder;
        this.context = context;
        this.dataList = dataList;
        this.listRowViewId = listRowViewId;
        notifyDataSetChanged();
        if (context != null) {
            applicationViewModel = new ViewModelProvider(context.requireActivity()).get(ApplicationViewModel.class);
            database = DB.getInstance(context.requireActivity());
            setupDarkTheme();
        }
    }

    private void setupDarkTheme() {
        isDarkTheme = Boolean.parseBoolean(
            database.tabataSettingDao().getSetting(SettingsFragment.themeKey)
        );
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(listRowViewId, parent, false);
        return builder.runConstructor(view);
    }

    // onBindViewHolder implemented in every next class

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    @Override
    public void onRowMoved(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(dataList, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(dataList, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onRowSelected(BaseViewHolder myViewHolder) {
        myViewHolder.select();
    }

    @Override
    public void onRowClear(BaseViewHolder myViewHolder) {
        myViewHolder.clear();
    }
}
