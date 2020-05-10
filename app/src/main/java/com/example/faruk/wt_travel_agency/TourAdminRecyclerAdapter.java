package com.example.faruk.wt_travel_agency;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TourAdminRecyclerAdapter extends RecyclerView.Adapter<TourAdminRecyclerAdapter.ViewHolder> {

    //region Privatne varijable
    private Activity mActivity;
    private List<Tour> tourList;
    private Button addReservationBtn;
    private StorageReference storageReference;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private  String current_user_id;
    FirebaseUser currentUser;

    //endregion

    //region Logika
    public TourAdminRecyclerAdapter(Activity activity, List<Tour> tourList) {

        this.mActivity = activity;
        this.tourList = tourList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tour_admin_list_item, parent, false);


        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        final Tour model = tourList.get(position);
        holder.bind(mActivity, model);
        holder.removeTour(model, position);
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
            destinationView = mView.findViewById(R.id.tour_destination_admin);
            departureView = mView.findViewById(R.id.tour_date_departure_admin);
            returnDateView = mView.findViewById(R.id.tour_date_return_admin);
            priceView = mView.findViewById(R.id.tour_price_admin);
            imageView = mView.findViewById(R.id.tour_image_admin);

        }

        public void removeTour(final Tour model, final int position) {
            mView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (FirebaseAuth.getInstance().getCurrentUser() != null)
                        currentUser = FirebaseAuth.getInstance().getCurrentUser();
                    FirebaseFirestore.getInstance().collection("Users").document(currentUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @SuppressLint("RestrictedApi")
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (!task.isSuccessful() || !task.getResult().exists()) return;

                            boolean isAdmin = task.getResult().getBoolean("admin");

                            if (isAdmin) {
                                AlertDialog dialog = new AlertDialog.Builder(mActivity)
                                        .setMessage("Da li želite obrisati")
                                        .setNegativeButton("NE", null)
                                        .setPositiveButton("DA", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                CollectionReference reference = FirebaseFirestore.getInstance().collection("Tours");
                                                reference.document(model.getId()).delete();
                                                Toast.makeText(mActivity,"Tour obrisan",Toast.LENGTH_LONG).show();
                                                tourList.remove(position);
                                                notifyDataSetChanged();
                                            }
                                        }).create();
                                dialog.show();
                            }
                        }
                    });
                    return true;
                }
            });
        }

        public void bind(Context mContext, Tour model) {

            destinationView.setText(model.getDestination());
            departureView.setText(model.getDepartureDate());
            returnDateView.setText((model.getReturnDate() != null && !model.getReturnDate().isEmpty()) ? " - " + model.getReturnDate(): "");
            priceView.setText(model.getPrice()+ ",00 KM");

            if (model.getImage() != null && !model.getImage().isEmpty())
                Glide.with(mContext).load(model.getImage()).into(imageView);
        }
    }

    //endregion
}
