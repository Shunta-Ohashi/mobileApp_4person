package jp.ac.meijou.android.schedule;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast; // Toastを正しくインポートする

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import jp.ac.meijou.android.schedule.databinding.ActivityRoutineBinding;

public class Routine extends AppCompatActivity {

    private ActivityRoutineBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityRoutineBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // ViewCompat.setOnApplyWindowInsetsListener の第1引数を binding.main に修正
        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding.SettingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveRoutineData(); // "保存しました" というメッセージをToastで表示

                function.habit[][] habitTime = saveHabitData();//habitTimeを宣言,値を取得

                Toast.makeText(Routine.this, "設定を保存しました", Toast.LENGTH_SHORT).show();
                // 現在の画面を閉じる
                Intent intent = new Intent(Routine.this, MainActivity.class);
                intent.putExtra("HabitTime",(Serializable)habitTime);
                startActivity(intent);
                finish();
            }
        });
    } // onCreateメソッドの閉じカッコ

    //入力値durationを30分に変換するメソッド
    private int parseDuration(String text) {
        try {
            int minutes = Integer.parseInt(text.trim());
            return Math.max(1, minutes / 30); // 30分単位に変換（最低1単位）
        } catch (NumberFormatException e) {
            return 1;
        }
    }

    // 時刻入力（"7:00"）から hour と halfIndex を求める
    private int[] parseStartTime(String timeText) {
        try {
            String[] parts = timeText.split(":");
            int hour = Integer.parseInt(parts[0]);
            int minute = Integer.parseInt(parts[1]);
            int half = (minute >= 30) ? 1 : 0;
            return new int[]{hour, half};
        } catch (Exception e) {
            return new int[]{0, 0};
        }
    }

    // saveRoutineDataメソッドをクラスの内側に移動
    private void saveRoutineData() {
        // "RoutinePrefs"という名前でSharedPreferencesのインスタンスを取得
        SharedPreferences sharedPreferences = getSharedPreferences("RoutinePrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // ビューバインディングを使って各Viewにアクセスし、データを保存
        // binding.<id> の形式で記述

        // 睡眠
        editor.putBoolean("sleep_is_checked", binding.Sleep.isChecked());
        editor.putString("sleep_start_time", binding.sleepStart.getText().toString());
        editor.putString("sleep_duration", binding.sleepDuration.getText().toString());

        // 朝食
        editor.putBoolean("breakfast_is_checked", binding.breakfast.isChecked());
        editor.putString("breakfast_start_time", binding.breakfastStart.getText().toString());
        editor.putString("breakfast_duration", binding.breakfastDuration.getText().toString());

        // 昼食
        editor.putBoolean("lunch_is_checked", binding.lunch.isChecked());
        editor.putString("lunch_start_time", binding.lunchStart.getText().toString());
        editor.putString("lunch_duration", binding.lunchDuration.getText().toString());

        // 夕食
        editor.putBoolean("dinner_is_checked", binding.dinner.isChecked());
        editor.putString("dinner_start_time", binding.dinnerStart.getText().toString());
        editor.putString("dinner_duration", binding.dinnerDuration.getText().toString());

        // 風呂
        editor.putBoolean("bath_is_checked", binding.bath.isChecked());
        editor.putString("bath_start_time", binding.bathStart.getText().toString());
        editor.putString("bath_duration", binding.bathDuration.getText().toString());

        // その他
        editor.putBoolean("others1_is_checked", binding.others1.isChecked());
        editor.putString("others1_start_time", binding.others1Start.getText().toString());
        editor.putString("others1_duration", binding.others1Duration.getText().toString());

        // apply() を呼び出して非同期で保存を確定
        editor.apply();
    }

private function.habit[][] saveHabitData() {

// --- SharedPreferences 保存後に Habit オブジェクトを作る ---
        List<function.habit> habitList = new ArrayList<>();

        function.habit[][] habitTime = new function.habit[24][2]; // 時間割

// 睡眠
        if (binding.Sleep.isChecked()) {
            int duration = parseDuration(binding.sleepDuration.getText().toString());
            int[] time = parseStartTime(binding.sleepStart.getText().toString());
            function.habit h = new function.habit("睡眠", duration);
            function f = new function();
            f.occupy(habitTime, time[0], time[1], duration, h);
            habitList.add(h);
        }

// 朝食
        if (binding.breakfast.isChecked()) {
            int duration = parseDuration(binding.breakfastDuration.getText().toString());
            int[] time = parseStartTime(binding.breakfastStart.getText().toString());
            function.habit h = new function.habit("朝食", duration);
            function f = new function();
            f.occupy(habitTime, time[0], time[1], duration, h);
            habitList.add(h);
        }

// 昼食
        if (binding.lunch.isChecked()) {
            int duration = parseDuration(binding.lunchDuration.getText().toString());
            int[] time = parseStartTime(binding.lunchStart.getText().toString());
            function.habit h = new function.habit("昼食", duration);
            function f = new function();
            f.occupy(habitTime, time[0], time[1], duration, h);
            habitList.add(h);
        }

// 夕食
        if (binding.dinner.isChecked()) {
            int duration = parseDuration(binding.dinnerDuration.getText().toString());
            int[] time = parseStartTime(binding.dinnerStart.getText().toString());
            function.habit h = new function.habit("夕食", duration);
            function f = new function();
            f.occupy(habitTime, time[0], time[1], duration, h);
            habitList.add(h);
        }

// 風呂
        if (binding.bath.isChecked()) {
            int duration = parseDuration(binding.bathDuration.getText().toString());
            int[] time = parseStartTime(binding.bathStart.getText().toString());
            function.habit h = new function.habit("お風呂", duration);
            function f = new function();
            f.occupy(habitTime, time[0], time[1], duration, h);
            habitList.add(h);
        }

// その他
        if (binding.others1.isChecked()) {
            int duration = parseDuration(binding.others1Duration.getText().toString());
            int[] time = parseStartTime(binding.others1Start.getText().toString());
            function.habit h = new function.habit("その他", duration);
            function f = new function();
            f.occupy(habitTime, time[0], time[1], duration, h);
            habitList.add(h);
        }

// Habitリストの内容を確認（デバッグ用）
// 後にhabitlistを消す（デバック用）
        for (function.habit h : habitList) {
            System.out.println("登録済みHabit: " + h.getTitle() + "（" + h.getDuration() + "単位）");
        }

        System.out.println("Habit登録が完了しました。");
        return habitTime;
    }

} // Routineクラスの閉じカッコ
