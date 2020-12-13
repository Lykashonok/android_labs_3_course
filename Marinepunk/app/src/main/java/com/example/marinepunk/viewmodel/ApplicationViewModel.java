package com.example.marinepunk.viewmodel;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.marinepunk.MainActivity;
import com.example.marinepunk.R;
import com.example.marinepunk.cell.Cell;
import com.example.marinepunk.cell.Cells;
import com.example.marinepunk.cell.Checker;
import com.example.marinepunk.cell.GameState;
import com.example.marinepunk.cell.GameStateStat;
import com.example.marinepunk.help.AccountInfo;
import com.example.marinepunk.help.TableManager;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ApplicationViewModel extends ViewModel {
    final static public String OWN_FIELD = "OWN_FIELD";
    final static public String USER_FIELD = "USER_FIELD";
    final static public String HOST_FIELD = "HOST_FIELD";

    private MutableLiveData<Cell.State[][]> ownField;
    private MutableLiveData<GameState> gameState;
    private MutableLiveData<GameStateStat> currentGameStateInfo;
    private MutableLiveData<DatabaseReference> gameNode;
    private MutableLiveData<int[]> checkField;
    public FirebaseDatabase database;
    public FirebaseAuth auth;

    private MutableLiveData<AccountInfo> accountInfo;


    private Context activityContext;

    public ApplicationViewModel() {
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
    }

    public void saveOrCreateGameState(String code) {
        setGameNode(database.getReference("processes").child(code));
        saveOrCreateGameState();
    }

    public void initiateGameState(String code, Activity activity) {
        setGameNode(database.getReference("processes").child(code));
        setGameState(new GameState(
            getOwnField().getValue(), Cells.getEmptyRawCells(), auth.getCurrentUser().getUid(),
            null, GameState.State.Waiting
        ));
        saveOrCreateGameState();
        getGameNode().getValue().child(code).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                GameState gs = new GameState();
                gs.hostState = getOwnField().getValue();
                gs.userState = Cells.getEmptyRawCells();
                gs.hostId = auth.getCurrentUser().getUid();
                gs.state = GameState.State.hostTurn;
                setGameState(gs);
                saveOrCreateGameState(code);
                setGameStateListener();

                toGameScreen(activity);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                toast("Something went wrong while initiating the game. Error - " + error.getCode());
            }
        });
    }

    public void connectToGame(String code, Activity activity) {
        setGameNode(database.getReference("processes").child(code));
        DatabaseReference dbr = getGameNode().getValue();
        String s = dbr.getKey();
        dbr.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String userId = snapshot.child("userId").getValue(String.class);
                String hostId = snapshot.child("hostId").getValue(String.class);
                if (hostId == null) {
                    toast("There's no any game with such code: " + code);
                    return;
                }
                GameState.State state = snapshot.child("state").getValue(GameState.State.class);
                String cuid = auth.getCurrentUser().getUid();
                if (state != GameState.State.Ended) {
                    if (hostId.equals(cuid)) {
                        setGameStateFromSnapshot(snapshot);
                        toast("Successfully reconnected to game as host");
                        toGameScreen(activity);
                    } else if (userId != null) {
                        if (userId.equals(cuid)) {
                            setGameStateFromSnapshot(snapshot);
                            toast("Successfully reconnected to game as user");
                            toGameScreen(activity);
                        } else {
                            toast("Someone already entered this game.");
                        }
                    } else {
                        Map<String, Object> connectUpdate = new HashMap<>();
                        if (state == GameState.State.Waiting)
                            connectUpdate.put("/state", GameState.State.hostTurn.toString());
                        connectUpdate.put("/userId", auth.getCurrentUser().getUid());
                        connectUpdate.put("/userState", TableManager.fieldToString(getOwnField().getValue()));
                        getGameNode().getValue().updateChildren(connectUpdate).addOnCompleteListener(command -> {
                            if (command.isSuccessful()) {
                                toast("Successfully connected to game");
                                toGameScreen(activity);
                            } else {
                                toast("Something went wrong.");
                            }
                        });
                    }
                } else if (state == GameState.State.Ended) {
                    toast("Game was already ended");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                toast("Something went wrong while connecting. Error - " + error.getCode());
            }
        });
        setGameStateListener();
    }

    private void toGameScreen(Activity activity) {
        NavController navController = Navigation.findNavController(activity, R.id.nav_host_fragment);
        navController.navigate(R.id.action_nav_home_to_nav_gallery);
    }

    // send current gameState to database by current code
    public void saveOrCreateGameState() {
        GameState gs = getGameState().getValue();
        GameStateForSend gfs = new GameStateForSend(
            gs.state.name(), gs.hostId, gs.userId,
            TableManager.fieldToString(gs.hostState),
            TableManager.fieldToString(gs.userState)
        );
        getGameNode().getValue().setValue(gfs).addOnCompleteListener(command -> {
            if (command.isSuccessful()) {
                switch (gs.state) {
                    case Waiting:
                        toast("Game created");
                        break;
                    case Ended:
                        toast("Game ended!");
                        break;
                    case hostTurn:
                    case userTurn:
                        break;
                }
            } else {
                toast("Something went wrong, reconnect to the game, please");
            }
        });
    }

    class GameStateForSend {
        public String state = "";
        public String hostId = "";
        public String userId = "";
        public String hostState = "";
        public String userState = "";
        public GameStateForSend(String state, String hostId, String userId, String hostState, String userState) {
            this.state = state; this.hostId = hostId; this.userId = userId; this.hostState = hostState; this.userState = userState;
        }
    }

    // set listener by current code
    public void setGameStateListener() {
        ValueEventListener vel = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                setGameStateFromSnapshot(snapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                toast("Something went wrong while getting game state. Error - " + error.getCode());
            }
        };
        getGameNode().getValue().addValueEventListener(vel);
    }

    private static Cell.State[][] getState(DataSnapshot snapshot, String child) throws JsonProcessingException {
        DataSnapshot stateRaw = snapshot.child(child);
        ObjectMapper mapper = new ObjectMapper();
        String content = (String)stateRaw.getValue(String.class);
        return content == null ? Cells.getEmptyRawCells() : mapper.readValue(content, Cell.State[][].class);
    }

    public static GameState parseGSSnapshot(DataSnapshot snapshot) {
        GameState gs = new GameState();
        gs.state = snapshot.child("state").getValue(GameState.State.class);
        gs.hostId = snapshot.child("hostId").getValue(String.class);
        gs.userId = snapshot.child("userId").getValue(String.class);
        try {
            gs.hostState = getState(snapshot, "hostState");
            gs.userState = getState(snapshot, "userState");
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return gs;
    }


    public void setGameStateFromSnapshot(DataSnapshot snapshot) {
        GameState gs = parseGSSnapshot(snapshot);

        if(gs.userState != null & gs.hostState != null) {
            setGameState(gs);
            if (isCurrentUserTurn()) {
                toast("Your turn!");
            }
        }
    }

    public boolean isCurrentUserTurn() {
        GameState gs = getGameState().getValue();
        if (gs.state == GameState.State.Ended) {
            return true;
        } else if (
            gs.userId != null && gs.hostId != null &&
            (gs.userId.equals(auth.getCurrentUser().getUid()) && gs.state == GameState.State.userTurn ||
            gs.hostId.equals(auth.getCurrentUser().getUid()) && gs.state == GameState.State.hostTurn)
        ) {
            return true;
        } else {
            return false;
        }
    }

    public void setGameState(GameState gameState) {
        if (this.gameState == null) {
            this.gameState = new MutableLiveData<>(new GameState(
                Cells.getEmptyRawCells(), Cells.getEmptyRawCells(),
                    null, null, GameState.State.Waiting
            ));
        }
        this.gameState.setValue(gameState);
    }

    public LiveData<GameState> getGameState() {
        if (gameState == null) {
            gameState = new MutableLiveData<>(new GameState(
                Cells.getEmptyRawCells(), Cells.getEmptyRawCells(),
                null, null, GameState.State.Waiting
            ));
        }
        return gameState;
    }

    public void setAccountInfo(AccountInfo value) {
        if (this.accountInfo == null) {
            this.accountInfo = new MutableLiveData<>(new AccountInfo());
        }
        this.accountInfo.setValue(value);
    }

    public LiveData<AccountInfo> getAccountInfo() {
        if (this.accountInfo == null) {
            this.accountInfo = new MutableLiveData<>(new AccountInfo());
        }
        return this.accountInfo;
    }

    public void setCurrentGameStateInfo(GameStateStat gameState) {
        if (this.currentGameStateInfo == null) {
            this.currentGameStateInfo = new MutableLiveData<>(new GameStateStat());
        }
        this.currentGameStateInfo.setValue(gameState);
    }

    public LiveData<GameStateStat> getCurrentGameStateInfo() {
        if (currentGameStateInfo == null) {
            currentGameStateInfo = new MutableLiveData<>(new GameStateStat());
        }
        return currentGameStateInfo;
    }

    public void setOwnField(Cell.State[][] field) {
        if (ownField == null) { ownField = new MutableLiveData<>(Cells.getEmptyRawCells()); }
        ownField.setValue(field);
    }

    public LiveData<Cell.State[][]> getOwnField() {
        if (ownField == null) { ownField = new MutableLiveData<>(Cells.getEmptyRawCells()); }
        return ownField;
    }
    public void setCheckField(int[] value) {
        if (checkField == null) { checkField = new MutableLiveData<>(new int[] {0,0,0,0}); }
        checkField.setValue(value);
    }

    public LiveData<int[]> getCheckField() {
        if (checkField == null) { checkField = new MutableLiveData<>(new int[] {0,0,0,0}); }
        return checkField;
    }

    public void setGameNode(DatabaseReference ref) {
        if (gameNode == null) { gameNode = new MutableLiveData<>(database.getReference("processes/test")); }
        gameNode.setValue(ref);
    }

    public LiveData<DatabaseReference> getGameNode() {
        if (gameNode == null) { gameNode = new MutableLiveData<>(database.getReference("processes/test")); }
        return gameNode;
    }

    public void setFieldClickListenerCreate(TableLayout field) {
        int size = 10;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                int i_c = i, j_c = j;
                Button cell = (Button)((TableRow)field.getChildAt(i)).getChildAt(j);
                cell.setOnClickListener(v -> {
                    Cell.State[][] states = getOwnField().getValue();
                    Checker checker = new Checker(states);
                    if (!checker.CheckShipCellsInAngles(i_c, j_c) &&
                        states[i_c][j_c] != Cell.State.ALIVE
                    ) {
                        states[i_c][j_c] = Cell.State.ALIVE;
                    } else {
                        states[i_c][j_c] = Cell.State.EMPTY;
                    }
                    checker = new Checker(states);
                    checker.Check();
                    setCheckField(checker.getShips());
                    setOwnField(states);
                });
            }
        }
    }

    public void setFieldClickListenerAttack(TableLayout field, String fieldName) {
        int size = 10;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                int i_c = i, j_c = j;
                Button cell = (Button)((TableRow)field.getChildAt(i)).getChildAt(j);
                cell.setOnClickListener(v -> {
                    String cuid = auth.getCurrentUser().getUid();
                    GameState gs = gameState.getValue();
                    Cell.State[][] states = Cells.getEmptyRawCells();
                    switch (fieldName) {
                        case USER_FIELD:
                            states = gs.userState;
                            break;
                        case HOST_FIELD:
                            states = gs.hostState;
                            break;
                    }
                    if(gs.state != GameState.State.Waiting) {
                        if (gs.userId.equals(cuid) && gs.state == GameState.State.userTurn) {
                            gs.state = states[i_c][j_c] == Cell.State.ALIVE ?
                                GameState.State.userTurn : GameState.State.hostTurn;
                        } else if (gs.hostId.equals(cuid) && gs.state == GameState.State.hostTurn) {
                            gs.state = states[i_c][j_c] == Cell.State.ALIVE ?
                                GameState.State.hostTurn : GameState.State.userTurn;
                        } else {
                            if (gs.state != GameState.State.Ended) {
                                toast("Not your turn, wait please!");
                            } else if (gs.state == GameState.State.Ended) {
                                toast("Game Already ended!");
                            }
                            return;
                        }
                    } else if (gs.state == GameState.State.Waiting) {
                        toast("There's no any user yet. Wait, please");
                    }
                    switch (states[i_c][j_c]) {
                        case EMPTY:
                            // Miss
                            states[i_c][j_c] = Cell.State.MISSED;
                            break;
                        case ALIVE:
                            // Attack
                            states[i_c][j_c] = Cell.State.DESTROYED;
                            break;
                    }
                    Checker hostChecker = new Checker(gs.hostState);
                    Checker userChecker = new Checker(gs.userState);
                    if (hostChecker.isDefeat()) {
                        gs.state = GameState.State.Ended;
                        gs.userId = "_" + gs.userId;
                        toast("Game Ended! User won!");
                    } else if (userChecker.isDefeat()) {
                        gs.state = GameState.State.Ended;
                        gs.hostId = "_" + gs.hostId;
                        toast("Game Ended! Host won!");
                    }
                    setGameState(gs);
                    saveOrCreateGameState();
                });
            }
        }
    }

    public void setActivityContext(Context context) {
        this.activityContext = context;
    }

    public void toast(String text) {
        Toast.makeText(activityContext, text, Toast.LENGTH_SHORT).show();
    }
}