package com.example.marinepunk.ui.game;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;

import com.example.marinepunk.R;
import com.example.marinepunk.help.TableManager;
import com.example.marinepunk.viewmodel.ApplicationViewModel;

public class GameStatFragment extends Fragment {
    private ApplicationViewModel applicationViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        applicationViewModel = new ViewModelProvider(requireActivity()).get(ApplicationViewModel.class);
        View view = inflater.inflate(R.layout.fragment_game, container, false);

        applicationViewModel.getCurrentGameStateInfo().observe(getViewLifecycleOwner(), stat -> {
            if (stat != null && stat.gs.hostId != null && stat.gs.userId != null) {
                TableLayout fieldHostStat =
                        TableManager.createField(requireActivity(), view, R.id.fieldHost, stat.gs.hostState);
                TableLayout fieldUserStat =
                        TableManager.createField(requireActivity(), view, R.id.fieldUser, stat.gs.userState);

                TableManager.fillField(requireActivity(), fieldHostStat, stat.gs.userState, false);
                TableManager.fillField(requireActivity(), fieldUserStat, stat.gs.hostState, false);
            }
        });

        return view;
    }
}