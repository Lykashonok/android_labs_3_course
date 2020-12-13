package com.example.marinepunk.cell;

import java.util.ArrayList;
import java.util.List;

public class Cells {
    public static Cell.State[][] getEmptyRawCells() {
        Cell.State[][] cells = new Cell.State[10][10];
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                cells[i][j] = Cell.State.EMPTY;
            }
        }
        return cells;
    }
}
