package com.example.marinepunk.help;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.example.marinepunk.MainActivity;
import com.example.marinepunk.R;
import com.example.marinepunk.cell.Cell;

public class TableManager {
    // main activity context, any view
    @SuppressLint("UseCompatLoadingForDrawables")
    public static TableLayout createField(Context context, View view, int fieldId, Cell.State[][] states) {
        TableLayout cellsView = (TableLayout) view.findViewById(fieldId);

        // it may be ownField, userField, hostField

        int[] screenSize = getScreenSize(context);
        int cellSizeX = screenSize[0] / states.length - 20;
        int cellSizeY = screenSize[1] / (states.length * 2) - 20;
        for (int i = 0; i < states.length; i++) {
            // Row creating
            TableRow row = new TableRow(view.getContext());
            row.setLayoutParams(new TableRow.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT
            ));

            // Row filling
            for (int j = 0; j < states[i].length; j++) {
                Button cellView = new Button(view.getContext());
                @SuppressLint("UseCompatLoadingForDrawables")
                Drawable cellBg = context.getResources().getDrawable(R.drawable.cell_empty, view.getContext().getTheme());
                cellView.setLayoutParams(new TableRow.LayoutParams( cellSizeX, cellSizeY));
                cellView.setBackground(cellBg);
                row.addView(cellView);
            }
            TableLayout.LayoutParams params = new TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT
            );
            cellsView.addView(row, params);
        }
        cellsView.setBackground(context.getResources().getDrawable(
                R.drawable.field_turn_border, context.getTheme()));
        return cellsView;
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public static void setCellBackgroundState(Context context, View v, Cell.State state, boolean hide) {
        if (hide) {
            switch (state){
                case ALIVE:
                case EMPTY:
                    v.setBackground(context.getResources().getDrawable(
                        R.drawable.cell_hidden,     context.getTheme())); break;
                case MISSED: v.setBackground(context.getResources().getDrawable(
                        R.drawable.cell_missed,    context.getTheme())); break;
                case DESTROYED: v.setBackground(context.getResources().getDrawable(
                        R.drawable.cell_destroyed, context.getTheme())); break;
            }
        } else {
            switch (state){
                case ALIVE: v.setBackground(context.getResources().getDrawable(
                        R.drawable.cell_alive,     context.getTheme())); break;
                case MISSED: v.setBackground(context.getResources().getDrawable(
                        R.drawable.cell_missed,    context.getTheme())); break;
                case EMPTY: v.setBackground(context.getResources().getDrawable(
                        R.drawable.cell_empty,     context.getTheme())); break;
                case DESTROYED: v.setBackground(context.getResources().getDrawable(
                        R.drawable.cell_destroyed, context.getTheme())); break;
            }
        }
    }

    // context must be main activity
    public static int[] getScreenSize(Context context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((MainActivity)(context)).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        return new int[] {width, height};
    }

    public static String fieldToString(Cell.State[][] field) {
        StringBuilder jsonString = new StringBuilder("[");
        for (int i = 0; i < field.length; i++) {
            jsonString.append("[");
            for (int j = 0; j < field.length; j++) {
                jsonString.append(field[i][j].ordinal());
                if (j != field.length - 1) jsonString.append(",");
            }
            jsonString.append("]");
            if (i != field.length - 1) jsonString.append(",");
        }
        jsonString.append("]");
        return jsonString.toString();
    }

    public static void fillField(Context context, TableLayout field, Cell.State[][] values, boolean hide) {
        int size = values.length;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                TableManager.setCellBackgroundState(context,
                        ((TableRow)field.getChildAt(i)).getChildAt(j), values[i][j], hide);
            }
        }
    }
}
