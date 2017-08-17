package com.devfill.privatapi;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class PayPhoneAdapter extends RecyclerView.Adapter<PayPhoneAdapter.MyViewHolder>  {

    private static final String LOG_TAG = "ReminderTag";

    public static Context mContext;
    private Activity myActivity;
    public static List<PayPhone> mPayPhoneList;

    boolean undoOn; // is undo on, you can turn it on from the toolbar menu
    List<PayPhone> itemsPendingRemoval;
    int lastInsertedIndex; // so we can add some more items for testing purposes
    private static final int PENDING_REMOVAL_TIMEOUT = 3000; // 3sec

    private Handler handler = new Handler(); // hanlder for running delayed runnables
    HashMap<PayPhone, Runnable> pendingRunnables = new HashMap<>(); // map of items to pending runnables, so we can cancel a removal if need be

    IPayPhoneListener mIPayPhoneListener;

    public interface IPayPhoneListener {
        public void onClickPay(int position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        public TextView name,amt,number;
        public Button pay;
        Button undoButton;

        public MyViewHolder(View v) {
            super(v);
            this.name = (TextView) v.findViewById(R.id.name);
            this.number = (TextView) v.findViewById(R.id.number);
            this.amt = (TextView) v.findViewById(R.id.amt);
            this.pay = (Button) v.findViewById(R.id.pay);
            this.undoButton = (Button) itemView.findViewById(R.id.undo_button);
        }
    }


    public PayPhoneAdapter(Context context, Activity activity,List<PayPhone> list,IPayPhoneListener iPayPhoneListener) {

        mPayPhoneList = list;
        mContext = context;
        myActivity = activity;
        mIPayPhoneListener = iPayPhoneListener;

        itemsPendingRemoval = new ArrayList<>();
        // let's generate some items
        lastInsertedIndex = 15;
        // this should give us a couple of screens worth
      /*  for (int i=1; i<= lastInsertedIndex; i++) {
            mPayPhoneList.add("Item " + i);
        }*/
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v;
        v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.pay_phone_card, viewGroup, false);
        return new MyViewHolder(v);

    }
    @Override
    public void onBindViewHolder(final MyViewHolder viewHolder, final int position) {

        final PayPhone payPhone = mPayPhoneList.get(position);


        viewHolder.name.setText(payPhone.getName());
        viewHolder.amt.setText(payPhone.getAmt());
        viewHolder.number.setText(payPhone.getNumber());

        viewHolder.pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mIPayPhoneListener.onClickPay(position);

            }
        });

    }

    @Override
    public int getItemCount() {
        return mPayPhoneList.size();
    }
    public void setUndoOn(boolean undoOn) {
        this.undoOn = undoOn;
    }

    public boolean isUndoOn() {
        return undoOn;
    }

    public void pendingRemoval(int position) {
        final PayPhone item = mPayPhoneList.get(position);
        if (!itemsPendingRemoval.contains(item)) {
            itemsPendingRemoval.add(item);
            // this will redraw row in "undo" state
            notifyItemChanged(position);
            // let's create, store and post a runnable to remove the item
            Runnable pendingRemovalRunnable = new Runnable() {
                @Override
                public void run() {
                    remove(mPayPhoneList.indexOf(item));
                }
            };
            handler.postDelayed(pendingRemovalRunnable, PENDING_REMOVAL_TIMEOUT);
            pendingRunnables.put(item, pendingRemovalRunnable);
        }
    }

    public void remove(int position) {
        PayPhone payPhone = mPayPhoneList.get(position);
        if (itemsPendingRemoval.contains(payPhone)) {
            itemsPendingRemoval.remove(payPhone);
        }
        if (mPayPhoneList.contains(payPhone)) {
            mPayPhoneList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public boolean isPendingRemoval(int position) {
        PayPhone payPhone = mPayPhoneList.get(position);
        return itemsPendingRemoval.contains(payPhone);
    }
    /**
     * ViewHolder capable of presenting two states: "normal" and "undo" state.
     */
    static class TestViewHolder extends RecyclerView.ViewHolder {

        TextView titleTextView;
        Button undoButton;

        public TestViewHolder(ViewGroup parent) {
            super(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_view, parent, false));
            titleTextView = (TextView) itemView.findViewById(R.id.title_text_view);
            undoButton = (Button) itemView.findViewById(R.id.undo_button);
        }

    }
}
