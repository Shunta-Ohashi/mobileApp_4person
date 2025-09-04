package jp.ac.meijou.android.schedule;

import android.app.Activity;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import org.jetbrains.annotations.Async;

import java.util.ArrayList;

import jp.ac.meijou.android.schedule.databinding.ActivityMain2Binding;

public class MainActivity2 extends AppCompatActivity {

    private ActivityMain2Binding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMain2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ArrayList<Schedule> scheduleData = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            scheduleData.add(new Schedule(i + ":00", "予定はありません"));;
        }

        ScheduleAdapter adapter = new ScheduleAdapter(scheduleData);
        binding.recyclerView.setAdapter(adapter);

    }
}