package com.example.tabatimer.View;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.tabatimer.Model.DB;
import com.example.tabatimer.Model.Tables.TabataItem;
import com.example.tabatimer.Model.Tables.TabataItemInSet;
import com.example.tabatimer.Model.Tables.TabataSet;
import com.example.tabatimer.ModelView.ApplicationViewModel;
import com.example.tabatimer.R;
import com.example.tabatimer.Service.TimerService;
import com.example.tabatimer.View.Adapters.BaseItemMoveCallback;
import com.example.tabatimer.View.Adapters.ItemInSetListAdapter;
import com.example.tabatimer.View.Adapters.SetListAdapter;
import com.example.tabatimer.View.Adapters.TimerListAdapter;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class TabatimerFragment extends Fragment {
    private ApplicationViewModel applicationViewModel;
    private DB database;

    private TimerListAdapter itemInSetListAdapter;
    private LinearLayoutManager linearLayoutManager;
    private RecyclerView recyclerView;

    private Button buttonStart, buttonStop, buttonPause;
    private TextView itemName, itemDuration;

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

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tabatimer, container, false);
        applicationViewModel = new ViewModelProvider(requireActivity()).get(ApplicationViewModel.class);
        database = DB.getInstance(requireActivity());

        TabataSet currentSet = applicationViewModel.getCurrentSet().getValue();
        currentSet.id = currentSet.id == null ? 0 : currentSet.id;
        List<TabataItemInSet> itemsInSet = database.tabataItemInSetDao().getAll(currentSet.id);
        itemsInSet.sort((o1, o2) -> o1.index.compareTo(o2.index));
//        List<TabataItem> items = new LinkedList<>();
//        itemsInSet.forEach(iis -> items.add(database.tabataItemDao().getById(iis.id_tabata_item)));

        // ADAPTER CREATING + RECYCLE VIEW
        itemInSetListAdapter = new TimerListAdapter(this, itemsInSet, R.layout.item_in_set_row_view, new TimerListAdapter.TimerListViewHolder(view));
        linearLayoutManager = new LinearLayoutManager(requireActivity());
        recyclerView = view.findViewById(R.id.timer_recycler_view);

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(itemInSetListAdapter);
        // END

        buttonStart = (Button) view.findViewById(R.id.timer_start_button);
        buttonStop = (Button) view.findViewById(R.id.timer_stop_button);
        buttonPause = (Button) view.findViewById(R.id.timer_pause_button);

        buttonStart.setOnClickListener(v -> {
            applicationViewModel.timerSetPlay();
        });

        itemDuration = (TextView) view.findViewById(R.id.timer_current_time);
        itemName = (TextView) view.findViewById(R.id.timer_set_name);
        applicationViewModel.getCurrentTimerItemInSet().observe(getViewLifecycleOwner(), value -> {
            itemDuration.setText((new Long(value.duration)).toString());
            TabataItem item = database.tabataItemDao().getById(value.id_tabata_item);
            if (value.id != null && value.id != 0) {
                itemName.setText(item.title);
                itemName.setTextColor(Color.parseColor(
                        item.colour.charAt(0) == '#' ? item.colour : '#' + item.colour
                ));
            } else {
                itemName.setText("Start?");
            }

        });

        Button pauseButton = view.findViewById(R.id.timer_pause_button);
        pauseButton.setOnClickListener(v -> {
            applicationViewModel.timerItemStop(true);
        });

        Button skipButton = view.findViewById(R.id.timer_skip_button);
        skipButton.setOnClickListener(v -> {
            applicationViewModel.timerItemSkip();
        });

        Button stopButton = view.findViewById(R.id.timer_stop_button);
        stopButton.setOnClickListener(v -> {
            applicationViewModel.timerItemStop(false);
        });

        return view;
    }



    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onPause() {
        applicationViewModel.startTimerService(requireActivity());
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        requireActivity().stopService(new Intent(requireActivity(), TimerService.class));
    }
}