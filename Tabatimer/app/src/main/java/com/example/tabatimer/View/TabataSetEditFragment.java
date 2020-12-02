package com.example.tabatimer.View;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
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
import android.widget.EditText;
import android.widget.Toast;

import com.example.tabatimer.Model.DB;
import com.example.tabatimer.Model.Tables.TabataItem;
import com.example.tabatimer.Model.Tables.TabataItemInSet;
import com.example.tabatimer.Model.Tables.TabataSet;
import com.example.tabatimer.ModelView.ApplicationViewModel;
import com.example.tabatimer.R;
import com.example.tabatimer.View.Adapters.AdapterBuilder;
import com.example.tabatimer.View.Adapters.BaseItemMoveCallback;
import com.example.tabatimer.View.Adapters.ItemInSetListAdapter;
import com.example.tabatimer.View.Adapters.ItemListAdapter;
import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;

import java.util.List;

public class TabataSetEditFragment extends Fragment {
    private ApplicationViewModel applicationViewModel;
    private DB database;

    private ItemInSetListAdapter itemInSetListAdapter;
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

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tabata_set_edit, container, false);
        applicationViewModel = new ViewModelProvider(requireActivity()).get(ApplicationViewModel.class);
        database = DB.getInstance(requireActivity());

        TabataSet currentSet = applicationViewModel.getCurrentSet().getValue();
        currentSet.id = currentSet.id == null ? 0 : currentSet.id;
        List<TabataItemInSet> itemsInSet = database.tabataItemInSetDao().getAll(currentSet.id);
        itemsInSet.sort((o1, o2) -> o1.index < o2.index ? -1 : o1.index > o2.index ? 1 : 0);

        // ADAPTER CREATING + RECYCLE VIEW
        itemInSetListAdapter = new ItemInSetListAdapter(this, itemsInSet, R.layout.set_edit_row_view, new ItemInSetListAdapter.ItemInSetListViewHolder(view));
        linearLayoutManager = new LinearLayoutManager(requireActivity());
        recyclerView = view.findViewById(R.id.set_edit_recycler_view);

        ItemTouchHelper.Callback callback = new BaseItemMoveCallback<ItemInSetListAdapter.ItemInSetListViewHolder>(itemInSetListAdapter, true);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerView);

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(itemInSetListAdapter);

        applicationViewModel.setCurrentItemInSetListAdapter(itemInSetListAdapter);
        // END


        Button addItemButton = view.findViewById(R.id.add_item_in_set_button);
        addItemButton.setOnClickListener(v -> {
            if (applicationViewModel.getCurrentSet().getValue().id != 0) {
                Navigation.findNavController(view).navigate(R.id.action_tabataSetEditFragment_to_tabataItemListFragment);
            }
        });

        EditText editSetName = (EditText) view.findViewById(R.id.edit_set_name_input);
        editSetName.setText(applicationViewModel.getCurrentSet().getValue().title);

        Button colorPickDialog = (Button) view.findViewById(R.id.set_color_pick_dialog_button);
        colorPickDialog.setOnClickListener(v -> {
            TabataSet set = applicationViewModel.getCurrentSet().getValue();
            ColorPickerDialogBuilder
                    .with(requireActivity())
                    .setTitle(getString(R.string.choose_color))
                    .initialColor(Color.parseColor(
                            applicationViewModel.getCurrentSet().getValue().colour.charAt(0) == '#' ?
                            applicationViewModel.getCurrentSet().getValue().colour :
                            "#" + applicationViewModel.getCurrentSet().getValue().colour
                    ))
                    .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                    .density(12)
                    .setPositiveButton(getString(R.string.ok), (d, lastSelectedColor, allColors) -> {
                        set.colour = Integer.toHexString(lastSelectedColor);
                        applicationViewModel.setCurrentSet(set);
                    })
                    .setNegativeButton(getString(R.string.cancel), (dialogInterface, i) -> {

                    })
                    .build()
                    .show();
        });
        Button editSetButton = (Button) view.findViewById(R.id.edit_set_button);
        editSetButton.setOnClickListener(v -> {
            TabataSet set = applicationViewModel.getCurrentSet().getValue();
            set.id = set.id == 0 ? null : set.id;
            set.title = editSetName.getText().toString();
            applicationViewModel.setCurrentSet(set);
            applicationViewModel.saveCurrentSet();
            Toast.makeText(requireActivity(), getString(R.string.edited_successfully), Toast.LENGTH_SHORT).show();
            Navigation.findNavController(view).navigateUp();
        });
        return view;
    }
}