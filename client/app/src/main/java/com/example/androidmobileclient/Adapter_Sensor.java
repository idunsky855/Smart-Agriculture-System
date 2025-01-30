package com.example.androidmobileclient;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidmobileclient.databinding.ItemSensorBinding;

import java.util.List;

public class Adapter_Sensor extends RecyclerView.Adapter<Adapter_Sensor.SensorViewHolder> {

    private List<Sensor> sensorList;
    private OnItemClickListener listener;

    public Adapter_Sensor(List<Sensor> sensorList) {
        this.sensorList = sensorList;
    }
    public void updateList(List<Sensor> sensorList) {
        this.sensorList = sensorList;
        notifyDataSetChanged();
    }

    public Adapter_Sensor setListener(OnItemClickListener listener) {
        this.listener = listener;
        return this;
    }

    @NonNull
    @Override
    public SensorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sensor, parent, false);
        return new SensorViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SensorViewHolder holder, int position) {
        Sensor sensor = sensorList.get(position);
        holder.bind(sensor);

        holder.itemView.setOnClickListener(view -> {
            if(listener != null){
                listener.onItemClick(sensor, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return sensorList == null ? 0 : sensorList.size();
    }

    // Interface for item click listener
    public interface OnItemClickListener {
        void onItemClick(Sensor sensor, int position);
    }

    class SensorViewHolder extends RecyclerView.ViewHolder {

        ItemSensorBinding binding;

        public SensorViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemSensorBinding.bind(itemView);
        }

        public void bind(Sensor sensor) {
            binding.LBLTitle.setText(sensor.getName());
            //binding.IMGImage.setImageResource(sensor.getImage());
        }
    }
}
