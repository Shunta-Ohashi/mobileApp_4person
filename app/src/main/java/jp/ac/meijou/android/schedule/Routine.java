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
                saveRoutineData();
                // "保存しました" というメッセージをToastで表示
                Toast.makeText(Routine.this, "設定を保存しました", Toast.LENGTH_SHORT).show();
                // 現在の画面を閉じる
                Intent intent = new Intent(Routine.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    } // onCreateメソッドの閉じカッコ

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

        // --- 他の項目も同様に追加 ---

        // apply() を呼び出して非同期で保存を確定
        editor.apply();
    }

} // Routineクラスの閉じカッコ
