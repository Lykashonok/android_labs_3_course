package com.example.marinepunk.ui.game;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.marinepunk.R;
import com.example.marinepunk.cell.GameState;
import com.example.marinepunk.help.TableManager;
import com.example.marinepunk.viewmodel.ApplicationViewModel;

public class GameFragment extends Fragment {

    private ApplicationViewModel applicationViewModel;

    @SuppressLint("UseCompatLoadingForDrawables")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        applicationViewModel = new ViewModelProvider(requireActivity()).get(ApplicationViewModel.class);
        View view = inflater.inflate(R.layout.fragment_game, container, false);

        GameState gs = applicationViewModel.getGameState().getValue();
        TableLayout fieldHost =
            TableManager.createField(requireActivity(), view, R.id.fieldHost, gs.hostState);
        TableLayout fieldUser =
            TableManager.createField(requireActivity(), view, R.id.fieldUser, gs.userState);

        if(applicationViewModel.auth.getCurrentUser() != null) {
            String currentUid = applicationViewModel.auth.getCurrentUser().getUid();
            if (gs.hostId != null && gs.hostId.equals(currentUid)){
                applicationViewModel.setFieldClickListenerAttack(fieldUser, ApplicationViewModel.USER_FIELD);
            } else if (gs.userId != null && gs.userId.equals(currentUid)){
                applicationViewModel.setFieldClickListenerAttack(fieldHost, ApplicationViewModel.HOST_FIELD);
            }
        }

        applicationViewModel.getGameState().observe(getViewLifecycleOwner(), state -> {
            GameState gstate = applicationViewModel.getGameState().getValue();
            String cuid = applicationViewModel.auth.getCurrentUser().getUid();
            boolean isCurrentHost = gstate.hostId != null && gs.hostId.equals(cuid);
            boolean isCurrentUser = gstate.userId != null && gs.userId.equals(cuid);
            if (applicationViewModel.auth.getCurrentUser() != null) {
                if (isCurrentHost) {
                    TableManager.fillField(requireActivity(), fieldHost, gstate.hostState, false);
                } else {
                    TableManager.fillField(requireActivity(), fieldHost, gstate.hostState, true);
                }
                if (isCurrentUser) {
                    TableManager.fillField(requireActivity(), fieldUser, gstate.userState, false);
                } else {
                    TableManager.fillField(requireActivity(), fieldUser, gstate.userState, true);
                }
                if (applicationViewModel.isCurrentUserTurn()) {
                    Drawable ubg = getResources().getDrawable(R.drawable.empty_drawable);
                    Drawable hbg = getResources().getDrawable(R.drawable.empty_drawable);
                    if (isCurrentHost) {
                        ubg = getResources().getDrawable(R.drawable.field_wait_border);
                        hbg = getResources().getDrawable(R.drawable.field_turn_border);
                    } else if (isCurrentUser) {
                        ubg = getResources().getDrawable(R.drawable.field_turn_border);
                        hbg = getResources().getDrawable(R.drawable.field_wait_border);
                    }
                    fieldHost.setBackground(ubg);
                    fieldUser.setBackground(hbg);
                }
            }
        });

        return view;
    }
}