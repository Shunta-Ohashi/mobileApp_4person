package jp.ac.meijou.android.schedule;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

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

        //時間とスケジュールをひとまとめにしたArrayListを作成する
        ArrayList<Schedule> scheduleData = new ArrayList<>();
        for (int i = 0; i < 48; i++) {
            if (i % 2 == 0) {
                scheduleData.add(new Schedule(i / 2 + ":00", "予定はありません"));
            }
            else {
                scheduleData.add(new Schedule((i - 1) / 2 + ":30", "予定はありません"));
            }
        }

        //色変更できるかどうかのテスト用↓↓↓
        int targetIndex = 20;
        Schedule newSchedule = new Schedule("10:00","会議");
        scheduleData.set(targetIndex, newSchedule);
        //↑↑↑

        ScheduleAdapter adapter = new ScheduleAdapter(scheduleData);
        binding.recyclerView.setAdapter(adapter);

        binding.buttonBack.setOnClickListener(view -> {
            var intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        });
    }
}