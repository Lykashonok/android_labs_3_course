package com.example.tabatimer.ModelView;

import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.provider.Settings;

import com.example.tabatimer.Model.Tables.TabataItem;
import com.example.tabatimer.Model.Tables.TabataItemInSet;

import java.util.List;

public class TabataTimerItem extends CountDownTimer {
    ApplicationViewModel applicationViewModel;
    private int index;
    public TabataTimerItem(Long millisInFuture, Long countDownInterval, ApplicationViewModel applicationViewModel, int index) {
        super(millisInFuture, countDownInterval);
        this.applicationViewModel = applicationViewModel;
        this.index = index;
    }

    @Override
    public void onTick(long millisUntilFinished) {
        TabataItemInSet cur = applicationViewModel.getCurrentTimerItemInSet().getValue();
        cur.duration = millisUntilFinished/ 1000;
        applicationViewModel.setCurrentTimerItemInSet(cur);
    }

    @Override
    public void onFinish() {
        List<TabataItemInSet> l = applicationViewModel.getCurrentTimerItemsInSet().getValue();
        index++;
        if (l.size() > index) {
            applicationViewModel.startSound();
            applicationViewModel.setCurrentTimerItemInSet(l.get(index));
            applicationViewModel.timerItemPlay(index);
        } else if (l.size() == index) {
            applicationViewModel.startSound();
        }
    }


}
