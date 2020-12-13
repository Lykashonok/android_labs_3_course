package com.example.marinepunk.cell;

import java.util.ArrayList;

public class Checker {
    private Cell.State field[][];
    private boolean checkedCells[][];
    public int one, two, three, four;
    int size;

    public Checker(Cell.State[][] field, boolean[][] checkedCells) {
        this.field = field;
        this.checkedCells = checkedCells;
        size = field.length;
    }

    public Checker(Cell.State[][] field) {
        this.field = field;
        size = field.length;
        checkedCells = new boolean[size][size];
    }

    public boolean Check(){
//        if (!CheckShipCellsNum()){
//            return false;
//        }
        return CheckShipNum();
    }

    private boolean CheckShipCellsNum(){
        int shipCellsNum = 0;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (field[i][j] == Cell.State.ALIVE){
                    shipCellsNum += 1;
                }
            }
        }
        return shipCellsNum == 20;
    }

    public boolean CheckShipCellsInAngles(int i, int j) {
        return !IsNotExistOrNotShip(i - 1, j - 1) || !IsNotExistOrNotShip(i - 1, j + 1)
                || !IsNotExistOrNotShip(i + 1, j - 1) || !IsNotExistOrNotShip(i + 1, j + 1);
    }

    public boolean isDefeat() {
        int total = 0;
        for (int i = 0; i < 10; i++)
            for (int j = 0; j < 10; j++)
                if (field[i][j] == Cell.State.DESTROYED)
                    total++;
        return total == 20;
    }

    private boolean CheckShipNum(){
        one = two = three = four = 0;
        int shipLen = 0;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (field[i][j] == Cell.State.ALIVE){
                    if (CheckShipCellsInAngles(i,j)){
                        return false;
                    }
                    if (checkedCells[i][j]){
                        continue;
                    }
                    if (!IsNotExistOrNotShip(i, j - 1) || !IsNotExistOrNotShip(i, j + 1)) {
                        int x = j, y = i;
                        while (x <= 9){
                            if (field[i][x] == Cell.State.ALIVE){
                                checkedCells[i][x] = true;
                                shipLen += 1;
                            }
                            else{
                                break;
                            }
                            x++;
                        }
                    }
                    else {
                        int x = j, y = i;
                        while (y <= 9){
                            if (field[y][j] == Cell.State.ALIVE){
                                checkedCells[y][j] = true;
                                shipLen += 1;
                            }
                            else{
                                break;
                            }
                            y++;
                        }
                    }
                    switch (shipLen){
                        case 1:
                            one++;
                            break;
                        case 2:
                            two++;
                            break;
                        case 3:
                            three++;
                            break;
                        case 4:
                            four++;
                            break;
                        default:
                            return false;
                    }
                    shipLen = 0;
                }
            }
        }
        return one == 4 && two == 3 && three == 2 && four == 1;
    }

    public int[] getShips() {
        return new int[]{one, two, three, four};
    }

    private boolean IsNotExistOrNotShip(int i, int j){
        boolean result;
        try {
            result = field[i][j] != Cell.State.ALIVE;
        }
        catch (Exception e) {
            result = true;
        }
        return result;
    }

}