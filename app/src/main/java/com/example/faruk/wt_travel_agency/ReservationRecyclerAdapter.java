package com.example.faruk.wt_travel_agency;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReservationRecyclerAdapter extends RecyclerView.Adapter<ReservationRecyclerAdapter.ViewHolder> {

    //region Privatne varijable
    private Activity mActivity;
    private List<Reservation> reservationList;
    private Button removeReservationBtn;
    private StorageReference storageReference;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private  String current_user_id;
    private Button deleteReservationBtn;

    //endregion

    //region Logika
    public ReservationRecyclerAdapter(Activity activity, List<Reservation> reservationList) {

        this.mActivity = activity;
        this.reservationList = reservationList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reservation_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final Reservation model = reservationList.get(position);
        holder.bind(mActivity, model);
        deleteReservationBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(v.getContext(),"rezervacija obrisana  "+position,Toast.LENGTH_LONG).show();
                return false;
            }
        });
    }


    @Override
    public int getItemCount() {
        return reservationList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView destinationView;
        private TextView departureView;
        private TextView returnDateView;
        private TextView priceView;
        private View mView;



        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            destinationView = mView.findViewById(R.id.reservation_destination);
            departureView = mView.findViewById(R.id.reservation_date_departure);
            returnDateView = mView.findViewById(R.id.reservation_date_return);
            priceView = mView.findViewById(R.id.reservation_price);
            deleteReservationBtn = mView.findViewById(R.id.deleteReservationBtn);


        }

        public void bind(Context mContext, Reservation model) {

            destinationView.setText(model.getDestination());
            departureView.setText(model.getDepartureDate());
            returnDateView.setText((model.getReturnDate() != null && !model.getReturnDate().isEmpty()) ? " - " + model.getReturnDate(): "");
            priceView.setText(model.getPrice()+ ",00 KM");
        }
    }

    //endregion
}
