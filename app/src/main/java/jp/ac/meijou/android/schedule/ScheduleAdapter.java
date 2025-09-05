package jp.ac.meijou.android.schedule;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

import jp.ac.meijou.android.schedule.databinding.ItemlistScheduleBinding;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder> {

    private final List<Schedule> localDataSet;

    public static class ScheduleViewHolder extends RecyclerView.ViewHolder {

        private final ItemlistScheduleBinding binding;

        public ScheduleViewHolder(@NonNull ItemlistScheduleBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public TextView getTimeTextView() { return binding.textViewTime; }
        public TextView getScheduleTextView() { return binding.textViewSchedule; }
    }

    public ScheduleAdapter(List<Schedule> dataSet) {
        this.localDataSet = dataSet;
    }

    @NonNull
    @Override
    public ScheduleViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        ItemlistScheduleBinding binding = ItemlistScheduleBinding.inflate(
                LayoutInflater.from(viewGroup.getContext()),
                viewGroup,
                false
        );
        return new ScheduleViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ScheduleViewHolder viewHolder, final int position) {
        Schedule currentItem = localDataSet.get(position);
        viewHolder.getTimeTextView().setText(currentItem.getTime());
        viewHolder.getScheduleTextView().setText(currentItem.getSchedule());
    }

    @Override
    public int getItemCount() {
        return localDataSet.size();
    }
}