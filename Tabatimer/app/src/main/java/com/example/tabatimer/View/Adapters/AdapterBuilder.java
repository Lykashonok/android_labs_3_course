package com.example.tabatimer.View.Adapters;

import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tabatimer.Model.Tables.TabataItem;
import com.example.tabatimer.Model.Tables.TabataItemInSet;
import com.example.tabatimer.Model.Tables.TabataSet;
import com.example.tabatimer.R;

import java.util.List;

public class AdapterBuilder {
    public static void createDraggableSetAdapter(
            Fragment context,
            SetListAdapter adapter,
            LinearLayoutManager linearLayoutManager,
            RecyclerView recyclerView,
            List<TabataSet> list,
            View view) {
        adapter = new SetListAdapter(context, list, R.layout.set_list_row_view, new SetListAdapter.SetListViewHolder(view));
        linearLayoutManager = new LinearLayoutManager(context.requireActivity());
        recyclerView = view.findViewById(R.id.set_list_recycler_view);

        // Drag n drop callback attaching
        ItemTouchHelper.Callback callback = new BaseItemMoveCallback<SetListAdapter.SetListViewHolder>(adapter, true);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerView);

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
    }

    public static void createDraggableItemInSetAdapter(
            Fragment context,
            ItemInSetListAdapter adapter,
            LinearLayoutManager linearLayoutManager,
            RecyclerView recyclerView,
            List<TabataItemInSet> list,
            View view) {
        adapter = new ItemInSetListAdapter(context, list, R.layout.set_edit_row_view, new ItemInSetListAdapter.ItemInSetListViewHolder(view));
        linearLayoutManager = new LinearLayoutManager(context.requireActivity());
        recyclerView = view.findViewById(R.id.set_edit_recycler_view);

        // Drag n drop callback attaching
        ItemTouchHelper.Callback callback = new BaseItemMoveCallback<ItemInSetListAdapter.ItemInSetListViewHolder>(adapter, true);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerView);

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
    }

    public static void createDraggableItemAdapter(
            Fragment context,
            ItemListAdapter adapter,
            LinearLayoutManager linearLayoutManager,
            RecyclerView recyclerView,
            List<TabataItem> list,
            View view) {
        adapter = new ItemListAdapter(context, list, R.layout.item_list_row_view, new ItemListAdapter.ItemListViewHolder(view));
        linearLayoutManager = new LinearLayoutManager(context.requireActivity());
        recyclerView = view.findViewById(R.id.item_list_recycler_view);

        // Drag n drop callback attaching
        ItemTouchHelper.Callback callback = new BaseItemMoveCallback<ItemListAdapter.ItemListViewHolder>(adapter, true);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerView);

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
    }
}
