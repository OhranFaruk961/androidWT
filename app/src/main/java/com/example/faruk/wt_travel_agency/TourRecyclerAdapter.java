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

public class TourRecyclerAdapter extends RecyclerView.Adapter<TourRecyclerAdapter.ViewHolder> {

    //region Privatne varijable
    private Activity mActivity;
    private List<Tour> tourList;
    private Button addReservationBtn;
    private StorageReference storageReference;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private  String current_user_id;

    //endregion

    //region Logika
    public TourRecyclerAdapter(Activity activity, List<Tour> tourList) {

        this.mActivity = activity;
        this.tourList = tourList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tour_list_item, parent, false);

        addReservationBtn = view.findViewById(R.id.add_reservation_btn);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

       final Tour model = tourList.get(position);


        addReservationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addReservation(v, model);
            }
        });
        holder.bind(mActivity, model);
    }


    @Override
    public int getItemCount() {
        return tourList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView destinationView;
        private TextView departureView;
        private TextView returnDateView;
        private ImageView imageView;
        private TextView priceView;
        private View mView;


        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            destinationView = mView.findViewById(R.id.tour_destination);
            departureView = mView.findViewById(R.id.tour_date_departure);
            returnDateView = mView.findViewById(R.id.tour_date_return);
            priceView = mView.findViewById(R.id.tour_price);
            imageView = mView.findViewById(R.id.tour_image);

        }

        public void bind(Context mContext, Tour model) {

            destinationView.setText(model.getDestination());
            departureView.setText(model.getDepartureDate());
            returnDateView.setText((model.getReturnDate() != null && !model.getReturnDate().isEmpty()) ? "- " + model.getReturnDate(): "");
            priceView.setText(model.getPrice() + " KM");

            if (model.getImage() != null && !model.getImage().isEmpty())
                Glide.with(mContext).load(model.getImage()).into(imageView);
        }
    }

    //endregion

    //region Helper metode

    private void addReservation(View v, Tour tour) {

        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        current_user_id = firebaseAuth.getUid();
       // Reservation reservation = new Reservation(destination.getText().toString(),price.getText().toString(),reservationDeparture.getText().toString(),reservationReturn.getText().toString());

        Map<String, Object> reservationMap = new HashMap<>();
        reservationMap.put("destination",tour.getDestination());
        reservationMap.put("price",tour.getPrice());
        reservationMap.put("departureDate",tour.getDepartureDate());
        reservationMap.put("returnDate",tour.getReturnDate());
        reservationMap.put("user_id",current_user_id);


        firebaseFirestore.collection("Reservations").add(reservationMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {

                if(task.isSuccessful()){

                    Toast.makeText(mActivity,"Putovanje uspješno dodano", Toast.LENGTH_LONG).show();
                    //Intent mainPage = new Intent(AddTourActivity.this,MainActivity.class);
                    //startActivity(mainPage);
                    //finish();//ovo onemogucava back button ?


                }else {

                }
                // addTour_progress.setVisibility(View.INVISIBLE);
            }
        });


    }

    //endregion
}
