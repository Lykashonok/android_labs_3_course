package com.example.tabatimer.ModelView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.CountDownTimer;
import android.provider.Settings;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.room.Database;

import com.example.tabatimer.Model.DB;
import com.example.tabatimer.Model.Tables.TabataItem;
import com.example.tabatimer.Model.Tables.TabataItemInSet;
import com.example.tabatimer.Model.Tables.TabataSet;
import com.example.tabatimer.R;
import com.example.tabatimer.Service.TimerService;
import com.example.tabatimer.View.Adapters.ItemInSetListAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class ApplicationViewModel extends ViewModel {
    private MutableLiveData<TabataItem> currentItem;
    private MutableLiveData<TabataSet> currentSet;
    private MutableLiveData<ItemInSetListAdapter> currentItemInSetListAdapter;

    private MutableLiveData<TabataItemInSet> currentTimerItemInSet;
    private MutableLiveData<List<TabataItemInSet>> currentTimerItemsInSet;
    private MutableLiveData<MediaPlayer> sound;
    private DB database;
    private TabataTimerItem timer;
    private Intent timerIntent;
    static public final String timerStateAction = "com.example.tabatimer.timer";

    private MutableLiveData<Integer> fontSizeSetting;

    public LiveData<TabataItem> getCurrentItem() {
        if (currentItem == null) {
            currentItem = new MutableLiveData<TabataItem>(new TabataItem("","", (long) 0));
        }
        return currentItem;
    }

    public void setCurrentItem(TabataItem value) {
        if (currentItem == null) {
            currentItem = new MutableLiveData<TabataItem>(new TabataItem("","", (long) 0));
        }
        currentItem.setValue(value);
    }

    public LiveData<Integer> getFontSizeSetting() {
        if (fontSizeSetting == null) {
            fontSizeSetting = new MutableLiveData<Integer>(0);

        }
        return fontSizeSetting;
    }

    public void setFontSizeSetting(Integer value) {
        if (fontSizeSetting == null) {
            fontSizeSetting = new MutableLiveData<Integer>(0);
        }
        fontSizeSetting.setValue(value);
    }

    public void saveCurrentItem() {
        TabataItem i = currentItem.getValue();
        if (i.id != null && i.id != 0) {
            database.tabataItemDao().update(i);
        } else {
            database.tabataItemDao().insert(i);
        }

    }

    public LiveData<TabataSet> getCurrentSet() {
        if (currentSet == null) {
            currentSet = new MutableLiveData<TabataSet>(new TabataSet("","", 0));
        }
        return currentSet;
    }

    public void setCurrentSet(TabataSet value) {
        if (currentSet == null) {
            currentSet = new MutableLiveData<TabataSet>(new TabataSet("","", 0));
        }
        currentSet.setValue(value);
    }

    public void saveCurrentSet() {
        TabataSet s = currentSet.getValue();
        if (s.id != null && s.id != 0 ) {
            database.tabataSetDao().update(currentSet.getValue());
        } else {
            database.tabataSetDao().insert(currentSet.getValue());
        }
    }

    public LiveData<ItemInSetListAdapter> getCurrentItemInSetListAdapter() {
        if (currentItemInSetListAdapter == null) {
            currentItemInSetListAdapter = new MutableLiveData<ItemInSetListAdapter>(new ItemInSetListAdapter(null,null,0,null));
        }
        return currentItemInSetListAdapter;
    }

    public void setCurrentItemInSetListAdapter(ItemInSetListAdapter value) {
        if (currentItemInSetListAdapter == null) {
            currentItemInSetListAdapter = new MutableLiveData<ItemInSetListAdapter>(new ItemInSetListAdapter(null,null,0,null));
        }
        currentItemInSetListAdapter.setValue(value);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void reorderItemsInSet(List<TabataItemInSet> iiss) {
        iiss.forEach(iis -> iis.index = iiss.indexOf(iis));
        database.tabataItemInSetDao().update(iiss);
    }

    public LiveData<List<TabataItemInSet>> getCurrentTimerItemsInSet() {
        if (currentTimerItemsInSet == null) {
            currentTimerItemsInSet = new MutableLiveData<List<TabataItemInSet>>(new LinkedList());
        }
        return currentTimerItemsInSet;
    }

    public void setCurrentTimerItemsInSet(List<TabataItemInSet> value) {
        if (currentTimerItemsInSet == null) {
            currentTimerItemsInSet = new MutableLiveData<List<TabataItemInSet>>(new LinkedList());
        }
        currentTimerItemsInSet.setValue(value);
    }

    public LiveData<TabataItemInSet> getCurrentTimerItemInSet() {
        if (currentTimerItemInSet == null) {

            currentTimerItemInSet = new MutableLiveData<TabataItemInSet>(new TabataItemInSet(0,0,0));
        }
        return currentTimerItemInSet;
    }

    public void setCurrentTimerItemInSet(TabataItemInSet value) {
        if (currentTimerItemInSet == null) {
            currentTimerItemInSet = new MutableLiveData<TabataItemInSet>(new TabataItemInSet(0,0,0));
        }
        currentTimerItemInSet.setValue(value);
    }

    private BroadcastReceiver timerReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(timerStateAction)) {
                stopTimerService(context, intent);
            }
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void startTimerService(Context context) {
        timerItemStop(true);

        List<String> itemListDuration = new ArrayList<>();

        TabataItem item = database.tabataItemDao().getById(getCurrentTimerItemInSet().getValue().id_tabata_item);
        if (item != null) {
            List<TabataItemInSet> items = getCurrentTimerItemsInSet().getValue();

            for (int i = 0; i < items.size(); i++) {
                itemListDuration.add(database.tabataItemDao().getById(items.get(i).id_tabata_item).duration.toString());
            }
            Integer itemIndex = items.indexOf(getCurrentTimerItemInSet().getValue());
            String itemDuration = currentTimerItemInSet.getValue().duration.toString();

            timerIntent = new Intent(context, TimerService.class);
            timerIntent.setAction(timerStateAction);
            timerIntent.putExtra("itemIndex", itemIndex.toString());
            timerIntent.putExtra("itemDuration", itemDuration);
            timerIntent.putExtra("itemListDuration", (ArrayList<String>) itemListDuration);


            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(timerStateAction);
            context.registerReceiver(timerReciever, intentFilter);

            context.startService(timerIntent);
        }
    }

    public void stopTimerService(Context context, Intent intent) {
        Long itemDuration = (Long) intent.getExtras().get("itemDuration");
        Integer itemIndex = (Integer) intent.getExtras().get("itemIndex");

        List<TabataItemInSet> items = getCurrentTimerItemsInSet().getValue();

        if (itemIndex != 0) {
            TabataItemInSet iis = items.get(itemIndex);
            iis.duration = itemDuration;
            items.set(itemIndex, iis);
            setCurrentTimerItemInSet(iis);
            timerItemPlay(itemIndex);
        }
        context.unregisterReceiver(timerReciever);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void timerSetPlay() {
        TabataSet set = currentSet.getValue();
        List<TabataItemInSet> iiss = database.tabataItemInSetDao().getAll(set.id);
        iiss.sort((o1, o2) -> o1.index.compareTo(o2.index));
        for (TabataItemInSet iis: iiss ) {
            iis.duration = database.tabataItemDao().getById(iis.id_tabata_item).duration;
        }
        if (getCurrentTimerItemInSet().getValue().id_tabata_item == 0) {
            setCurrentTimerItemsInSet(iiss);
            setCurrentTimerItemInSet(iiss.get(0));
        }
        timerItemPlay(getCurrentTimerItemInSet().getValue().index);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void timerItemStop(boolean pause) {
        if (timer != null) {
            timer.cancel();
            if (!pause) {
                setCurrentTimerItemInSet(new TabataItemInSet(0, 0, 0));
            }
            timer = null;
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    public void timerItemSkip() {
        TabataItemInSet iis = getCurrentTimerItemInSet().getValue();
        List<TabataItemInSet> iiss = getCurrentTimerItemsInSet().getValue();
        if (iis.index < iiss.size() - 1) {
            setCurrentTimerItemInSet(iiss.get(iiss.indexOf(iis) + 1));
            timerItemStop(true);
            iiss.sort((o1, o2) -> o1.index.compareTo(o2.index));
            timerSetPlay();
        }
    }

    public void timerItemPlay(int index) {
        timer = new TabataTimerItem(
getCurrentTimerItemInSet().getValue().duration*1000, (long) 1000, this, index
        );
        timer.start();
    }

    public void createSound(MediaPlayer value) {
        sound = new MutableLiveData<MediaPlayer>(value);
    }

    public void startSound() {
        sound.getValue().start();
    }

    public void setDatabase(DB database) {
        this.database = database;
    }

    static public void setThemeInContext(Context context, Integer value) {
        switch (value) {
            case 10: context.setTheme(R.style.Theme_Tabatimer_font10); break;
            case 11: context.setTheme(R.style.Theme_Tabatimer_font11); break;
            case 12: context.setTheme(R.style.Theme_Tabatimer_font12); break;
            case 13: context.setTheme(R.style.Theme_Tabatimer_font13); break;
            case 14: context.setTheme(R.style.Theme_Tabatimer_font14); break;
            case 15: context.setTheme(R.style.Theme_Tabatimer_font15); break;
            case 16: context.setTheme(R.style.Theme_Tabatimer_font16); break;
        }
    }
}