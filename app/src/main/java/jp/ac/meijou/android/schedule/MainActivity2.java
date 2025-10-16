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

        Intent ResultIntent = getIntent();// 習慣からhabitTime[][]データを受け取る
        function.ToDo[][] result = (function.ToDo[][]) ResultIntent.getSerializableExtra("time");

        if (result != null) {
            System.out.println("activity1画面からresultを受け取りました");
        }

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

        for (int h = 0; h < 24; h++) {
            for (int m = 0; m < 2; m++) {
                if (result[h][m] != null) {
                    String title = result[h][m].getTitle();
                    int index = h * 2 + m; // 0〜47に変換

                    // indexが範囲内なら更新
                    if (index >= 0 && index < scheduleData.size()) {
                        Schedule old = scheduleData.get(index);
                        Schedule updated = new Schedule(old.getTime(), title);
                        scheduleData.set(index, updated);
                    }

                    // デバッグ出力
                    System.out.println(h + "時" + (m == 0 ? "00" : "30") + " → " + title);
                }
            }
        }

/*
        //予定変更テスト用↓↓↓
        int targetIndex1 = 20;   //何番目の配列に予定を追加するか
                                 //例）10:00なら20番目
        Schedule currentSchedule = scheduleData.get(targetIndex1);
        Schedule newSchedule = new Schedule(currentSchedule.getTime(),"会議");    //scheduleの欄のみ変更する
        scheduleData.set(targetIndex1, newSchedule);

        int targetIndex2 = 27;   //何番目の配列に予定を追加するか
                                 //例）13:30なら27番目
        Schedule currentSchedule2 = scheduleData.get(targetIndex2);
        Schedule newSchedule2 = new Schedule(currentSchedule2.getTime(),"歯医者");    //scheduleの欄のみ変更する
        scheduleData.set(targetIndex2, newSchedule2);
        //↑↑↑
 */

        ScheduleAdapter adapter = new ScheduleAdapter(scheduleData);
        binding.recyclerView.setAdapter(adapter);

    // 戻るボタン
        binding.buttonBack.setOnClickListener(view -> {
            var intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        });
    }
}