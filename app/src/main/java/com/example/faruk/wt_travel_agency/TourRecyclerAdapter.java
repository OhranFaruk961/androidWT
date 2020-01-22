package com.example.faruk.wt_travel_agency;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class TourRecyclerAdapter extends RecyclerView.Adapter<TourRecyclerAdapter.ViewHolder> {

    public List<Tour> tourList;


    public TourRecyclerAdapter(List<Tour> tourList){

        this.tourList = tourList;
}

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tour_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        String destination_data = tourList.get(position).getDestination();
        holder.setDestinationText(destination_data);
    }


    @Override
    public int getItemCount() {
        return tourList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView destinationView;
        private View mView;


        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public  void setDestinationText(String text){

            destinationView = mView.findViewById(R.id.tour_destination);
            destinationView.setText(text);
        }
    }
}
