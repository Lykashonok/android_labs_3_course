package com.example.tabatimer.View;

import android.app.Activity;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.tabatimer.Model.DB;
import com.example.tabatimer.Model.Tables.TabataItem;
import com.example.tabatimer.Model.Tables.TabataItemInSet;
import com.example.tabatimer.Model.Tables.TabataSet;
import com.example.tabatimer.ModelView.ApplicationViewModel;
import com.example.tabatimer.R;
import com.example.tabatimer.View.Adapters.AdapterBuilder;
import com.example.tabatimer.View.Adapters.BaseAdapter;
import com.example.tabatimer.View.Adapters.BaseItemMoveCallback;
import com.example.tabatimer.View.Adapters.SetListAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TabataSetListFragment extends Fragment {
    private ApplicationViewModel applicationViewModel;
    private DB database;

    private SetListAdapter setListAdapter;
    private LinearLayoutManager linearLayoutManager;
    private RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tabata_set_list, container, false);
        applicationViewModel = new ViewModelProvider(requireActivity()).get(ApplicationViewModel.class);

        database = DB.getInstance(requireActivity());

        AdapterBuilder.createDraggableSetAdapter(
                this, setListAdapter, linearLayoutManager,
                recyclerView, database.tabataSetDao().getAll(), view);

        Button addSetButton = view.findViewById(R.id.add_set_button);
        addSetButton.setOnClickListener(v -> {
            Navigation.findNavController(view).navigate(R.id.action_tabataSetListFragment_to_tabataSetEditFragment);
        });

        return view;
    }
}