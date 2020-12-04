package com.example.tabatimer.View;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.tabatimer.Model.DB;
import com.example.tabatimer.Model.Tables.TabataItem;
import com.example.tabatimer.Model.Tables.TabataSet;
import com.example.tabatimer.ModelView.ApplicationViewModel;
import com.example.tabatimer.R;
import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;

public class TabataItemEditFragment extends Fragment {
    private ApplicationViewModel applicationViewModel;
    private DB database;

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
        View view = inflater.inflate(R.layout.fragment_tabata_item_edit, container, false);
        applicationViewModel = new ViewModelProvider(requireActivity()).get(ApplicationViewModel.class);
        database = DB.getInstance(requireActivity());
        TabataItem item = applicationViewModel.getCurrentItem().getValue();


        EditText editItemName = (EditText) view.findViewById(R.id.edit_item_name_input);
        editItemName.setText(item.title);
        EditText editItemDuration = (EditText) view.findViewById(R.id.edit_item_duration_input);
        editItemDuration.setText(item.duration.toString());

        Button editItemButton = (Button) view.findViewById(R.id.edit_item_button);
        editItemButton.setOnClickListener(v -> {
            item.title = editItemName.getText().toString();
            item.duration = Long.parseLong(editItemDuration.getText().toString());
            applicationViewModel.setCurrentItem(item);
            applicationViewModel.saveCurrentItem();
            Toast.makeText(requireActivity(), "Edited successfully", Toast.LENGTH_SHORT).show();
            Navigation.findNavController(view).navigateUp();
        });
        Button colorPickDialog = (Button) view.findViewById(R.id.color_pick_dialog_button);
        colorPickDialog.setOnClickListener(v -> {
            ColorPickerDialogBuilder
                    .with(requireActivity())
                    .setTitle("Choose color")
                    .initialColor(Color.parseColor(
                            applicationViewModel.getCurrentItem().getValue().colour.charAt(0) == '#' ?
                                    applicationViewModel.getCurrentItem().getValue().colour :
                                    "#" + applicationViewModel.getCurrentItem().getValue().colour
                    ))
                    .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                    .density(12)
                    .setPositiveButton("ok", (d, lastSelectedColor, allColors) -> {
                        item.colour = Integer.toHexString(lastSelectedColor);
                        applicationViewModel.setCurrentItem(item);
                    })
                    .setNegativeButton("cancel", (dialogInterface, i) -> {

                    })
                    .build()
                    .show();
        });
        return view;
    }
}