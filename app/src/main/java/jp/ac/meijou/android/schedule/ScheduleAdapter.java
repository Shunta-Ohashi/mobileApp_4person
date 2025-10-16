package jp.ac.meijou.android.schedule;

import android.graphics.Color;
import android.telephony.CellIdentity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import java.util.Random;

import jp.ac.meijou.android.schedule.databinding.ItemlistScheduleBinding;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder> {

    private final List<Schedule> localDataSet;
    private final Random random = new Random();

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

        //習慣のところは水色に設定
        if("睡眠".equals(currentItem.getSchedule()) ||
           "朝食".equals(currentItem.getSchedule()) ||
           "昼食".equals(currentItem.getSchedule()) ||
           "夕食".equals(currentItem.getSchedule()) ||
           "お風呂".equals(currentItem.getSchedule()) ||
           "その他".equals(currentItem.getSchedule())) {
            int lightBlue = Color.argb(128, 173, 216, 230);
            viewHolder.getScheduleTextView().setBackgroundColor(lightBlue);
        } else if (!"予定はありません".equals(currentItem.getSchedule())) {
            int red = Color.argb(128, 173, 30, 30);
            viewHolder.getScheduleTextView().setBackgroundColor(red);
        }
        //用事がないなら背景色を元に戻す
        else {
            viewHolder.getScheduleTextView().setBackgroundColor(Color.TRANSPARENT);
        }
    }

    @Override
    public int getItemCount() {
        return localDataSet.size();
    }
    /*
    private int getRandomColor() {
        // Alpha(透明度)は128で固定し、RGB(赤緑青)を0-255の範囲でランダムに生成
        int r = random.nextInt(256);
        int g = random.nextInt(256);
        int b = random.nextInt(256);
        return Color.argb(128, r, g, b);
    }
     */
}