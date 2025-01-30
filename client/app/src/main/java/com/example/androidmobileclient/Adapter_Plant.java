package com.example.androidmobileclient;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidmobileclient.databinding.ItemPlantBinding;
import com.example.androidmobileclient.plant.Plant;

import java.util.List;

public class Adapter_Plant extends RecyclerView.Adapter<Adapter_Plant.PlantViewHolder> {

    private List<Plant> plantList;
    private OnItemClickListener listener;

    public Adapter_Plant(List<Plant> plantList) {
        this.plantList = plantList;
    }
    public void updateList(List<Plant> plantList) {
        this.plantList = plantList;
        notifyDataSetChanged();
    }

    public Adapter_Plant setListener(OnItemClickListener listener) {
        this.listener = listener;
        return this;
    }

    @NonNull
    @Override
    public PlantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_plant, parent, false);
        return new PlantViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PlantViewHolder holder, int position) {
        Plant plant = plantList.get(position);
        holder.bind(plant);

        holder.itemView.setOnClickListener(view -> {
            if(listener != null){
                listener.onItemClick(plant, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return plantList == null ? 0 : plantList.size();
    }

    // Interface for item click listener
    public interface OnItemClickListener {
        void onItemClick(Plant plant, int position);
    }

    class PlantViewHolder extends RecyclerView.ViewHolder {

        ItemPlantBinding binding;

        public PlantViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemPlantBinding.bind(itemView);
        }

        public void bind(Plant plant) {
            binding.LBLTitle.setText(plant.getAlias());
            //binding.IMGImage.setImageResource(plant.getImage());
        }
    }
}
