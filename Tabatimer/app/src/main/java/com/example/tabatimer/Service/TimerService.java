package com.example.tabatimer.Service;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.example.tabatimer.MainActivity;
import com.example.tabatimer.Model.Tables.TabataItem;
import com.example.tabatimer.Model.Tables.TabataItemInSet;
import com.example.tabatimer.ModelView.ApplicationViewModel;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class TimerService extends Service {
    private List<Long> list = new LinkedList<>();
    private MediaPlayer sound;
    private Long itemDuration;
    private Integer itemIndex;
    private CountDownTimer timer;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        ArrayList<String> itemListDuration = intent.getStringArrayListExtra("itemListDuration");
        itemDuration = Long.parseLong(intent.getStringExtra("itemDuration"));
        itemIndex = Integer.parseInt(intent.getStringExtra("itemIndex"));
        sound = MediaPlayer.create(getApplicationContext(), Settings.System.DEFAULT_NOTIFICATION_URI);

        for (int i = 0; i < itemListDuration.size(); i++) {
            list.add(Long.parseLong(itemListDuration.get(i)));
        }
        nextTimerItem();
        return START_STICKY;
    }

    private void nextTimerItem() {
        timer = new CountDownTimer(itemDuration * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                itemDuration = millisUntilFinished;
            }

            @Override
            public void onFinish() {
                sound.start();
                itemIndex++;
                if (list.size() > itemIndex) {
                    itemDuration = list.get(itemIndex);
                    nextTimerItem();
                }
            }
        };
        timer.start();
    }

    @Override
    public void onDestroy() {

        timer.cancel();
        Intent intent = new Intent();
        intent.setAction(ApplicationViewModel.timerStateAction);
        intent.putExtra("itemIndex", itemIndex);
        intent.putExtra("itemDuration", itemDuration / 1000);
        sendBroadcast(intent);
        super.onDestroy();
    }



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
