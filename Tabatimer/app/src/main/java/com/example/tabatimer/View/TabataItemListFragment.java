package com.example.tabatimer.View;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.tabatimer.Model.DB;
import com.example.tabatimer.Model.Tables.TabataItem;
import com.example.tabatimer.Model.Tables.TabataSet;
import com.example.tabatimer.ModelView.ApplicationViewModel;
import com.example.tabatimer.R;
import com.example.tabatimer.View.Adapters.AdapterBuilder;
import com.example.tabatimer.View.Adapters.BaseItemMoveCallback;
import com.example.tabatimer.View.Adapters.ItemListAdapter;

import java.util.List;


public class TabataItemListFragment extends Fragment {
    private ApplicationViewModel applicationViewModel;
    private DB database;

    private ItemListAdapter itemListAdapter;
    private LinearLayoutManager linearLayoutManager;
    private RecyclerView recyclerView;

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.clear();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tabata_item_list, container, false);
        applicationViewModel = new ViewModelProvider(requireActivity()).get(ApplicationViewModel.class);

        database = DB.getInstance(requireActivity());
        List<TabataItem> items = database.tabataItemDao().getAll();

        // ADAPTER CREATING
        itemListAdapter = new ItemListAdapter(this, database.tabataItemDao().getAll(), R.layout.item_list_row_view, new ItemListAdapter.ItemListViewHolder(view));
        linearLayoutManager = new LinearLayoutManager(requireActivity());
        recyclerView = view.findViewById(R.id.item_list_recycler_view);

        ItemTouchHelper.Callback callback = new BaseItemMoveCallback<ItemListAdapter.ItemListViewHolder>(itemListAdapter, true);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerView);

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(itemListAdapter);
        // END


        Button addItemButton = view.findViewById(R.id.add_item_button);
        addItemButton.setOnClickListener(v -> {
            applicationViewModel.setCurrentItem(new TabataItem("", "#FFFFFF", (long)0));
            Navigation.findNavController(view).navigate(R.id.action_tabataItemListFragment_to_tabataItemEditFragment);
        });

        return view;
    }
}