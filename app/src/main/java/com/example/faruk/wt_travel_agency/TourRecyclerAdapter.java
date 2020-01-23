package com.example.faruk.wt_travel_agency;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

public class TourRecyclerAdapter extends RecyclerView.Adapter<TourRecyclerAdapter.ViewHolder> {

    private Activity mActivity;
    private List<Tour> tourList;

    public TourRecyclerAdapter(Activity activity, List<Tour> tourList) {

        this.mActivity = activity;
        this.tourList = tourList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tour_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        Tour model = tourList.get(position);
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
            priceView.setText(model.getPrice());

            if (model.getImage() != null && !model.getImage().isEmpty())
                Glide.with(mContext).load(model.getImage()).into(imageView);
        }
    }
}
