package com.example.tabatimer.View.Adapters;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public class BaseItemMoveCallback<ViewHolder extends RecyclerView.ViewHolder>
        extends ItemTouchHelper.Callback {

    private final ItemTouchHelperContract mAdapter;
    private final Boolean allowDrag;

    public BaseItemMoveCallback(ItemTouchHelperContract adapter, Boolean allowDrag) {
        this.allowDrag = allowDrag;
        mAdapter = adapter;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return allowDrag;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        Log.println(Log.WARN, "swiped", "swiped");
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT;
        return makeMovementFlags(dragFlags, 0);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                          RecyclerView.ViewHolder target) {
        mAdapter.onRowMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {

        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            ViewHolder myViewHolder = (ViewHolder) viewHolder;
            mAdapter.onRowSelected(myViewHolder);
        }

        super.onSelectedChanged(viewHolder, actionState);
    }
    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        ViewHolder myViewHolder = (ViewHolder) viewHolder;
        mAdapter.onRowClear(myViewHolder);
    }

    public interface ItemTouchHelperContract<ViewHolder> {

        void onRowMoved(int fromPosition, int toPosition);
        void onRowSelected(ViewHolder myViewHolder);
        void onRowClear(ViewHolder myViewHolder);

    }

}
