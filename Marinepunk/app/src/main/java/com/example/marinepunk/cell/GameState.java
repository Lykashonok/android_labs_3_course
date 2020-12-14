package com.example.marinepunk.cell;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import java.util.LinkedList;
import java.util.List;

public class GameState {
    // game ends. winnerId starts with '_' underscore symbol.
    public Cell.State[][] hostState;
    public Cell.State[][] userState;
    public String hostId = null;
    public String userId = null;
    public State state = State.Waiting;

    public enum State {
        Waiting, Ended, hostTurn, userTurn
    }

    public GameState(Cell.State[][] hostState, Cell.State[][] userState, String hostId, String userId, GameState.State state) {
        this.hostState = hostState; this.userState = userState;
        this.hostId = hostId; this.userId = userId;
        this.state = state;
    }

    public GameState() {}

    public static String setStateToJson(GameState gs) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(gs);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "error";
        }
    }

    public static GameState getStateFromJson(String jsonString) {
        Gson gson = new Gson();
        GameState gs = gson.fromJson(jsonString, GameState.class);
        return gs;
    }
}
