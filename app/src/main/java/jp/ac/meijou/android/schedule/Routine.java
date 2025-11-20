package jp.ac.meijou.android.schedule;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast; // Toastを正しくインポートする
import androidx.appcompat.app.AlertDialog; // AlertDialogをインポート
import androidx.appcompat.app.AppCompatActivity;

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

        loadRoutineData();

        // ViewCompat.setOnApplyWindowInsetsListener の第1引数を binding.main に修正
        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding.sleepStart.setOnClickListener(v -> showTimePickerDialog(binding.sleepStart));
        binding.breakfastStart.setOnClickListener(v -> showTimePickerDialog(binding.breakfastStart));
        binding.lunchStart.setOnClickListener(v -> showTimePickerDialog(binding.lunchStart));
        binding.dinnerStart.setOnClickListener(v -> showTimePickerDialog(binding.dinnerStart));
        binding.bathStart.setOnClickListener(v -> showTimePickerDialog(binding.bathStart));
        binding.others1Start.setOnClickListener(v -> showTimePickerDialog(binding.others1Start));


        // Duration入力欄の設定（キーボード入力を無効化し、ダイアログ選択にする）
        setupDurationInput(binding.sleepDuration);
        setupDurationInput(binding.breakfastDuration);
        setupDurationInput(binding.lunchDuration);
        setupDurationInput(binding.dinnerDuration);
        setupDurationInput(binding.bathDuration);
        setupDurationInput(binding.others1Duration);


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

    private void setupDurationInput(TextView textView) {
        textView.setFocusable(false);
        textView.setClickable(true);
        textView.setOnClickListener(v -> showDurationPickerDialog(textView));
    }

    private void showDurationPickerDialog(final TextView targetTextView) {
        // 30分刻みの選択肢を作成 (30分〜720分(12時間)まで)
        final String[] durationValues = new String[24];
        for (int i = 0; i < durationValues.length; i++) {
            durationValues[i] = String.valueOf((i + 1) * 30);
        }

        NumberPicker picker = new NumberPicker(this);
        picker.setMinValue(0);
        picker.setMaxValue(durationValues.length - 1);
        picker.setDisplayedValues(durationValues);
        picker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS); // キーボード入力を防ぐ

        // 現在の値があれば初期値に設定
        String currentVal = targetTextView.getText().toString();
        for (int i = 0; i < durationValues.length; i++) {
            if (durationValues[i].equals(currentVal)) {
                picker.setValue(i);
                break;
            }
        }

        new AlertDialog.Builder(this)
                .setTitle("継続時間(分)を選択")
                .setView(picker)
                .setPositiveButton("OK", (dialog, which) -> {
                    targetTextView.setText(durationValues[picker.getValue()]);
                })
                .setNegativeButton("キャンセル", null)
                .show();
    }

    /**
     * 30分刻みの時刻選択ダイアログを表示するメソッド
     * @param targetTextView 結果をセットするTextView
     */
    private void showTimePickerDialog(final TextView targetTextView) {
        // ダイアログ用のカスタムビューを生成
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_time_picker, null);
        final NumberPicker hourPicker = dialogView.findViewById(R.id.hourPicker);
        final NumberPicker minutePicker = dialogView.findViewById(R.id.minutePicker);

        // 時間の選択肢 (0-23)
        hourPicker.setMinValue(0);
        hourPicker.setMaxValue(23);

        // 分の選択肢 (00, 30)
        final String[] minuteValues = {"00", "30"};
        minutePicker.setMinValue(0);
        minutePicker.setMaxValue(minuteValues.length - 1);
        minutePicker.setDisplayedValues(minuteValues);


        // 既存の値があれば初期値に設定
        String currentTime = targetTextView.getText().toString();
        if (!currentTime.isEmpty() && currentTime.contains(":")) {
            String[] timeParts = currentTime.split(":");
            hourPicker.setValue(Integer.parseInt(timeParts[0]));
            if (timeParts[1].equals("30")) {
                minutePicker.setValue(1);
            } else {
                minutePicker.setValue(0);
            }
        }


        // AlertDialogを構築
        new AlertDialog.Builder(this)
                .setTitle("時刻を選択")
                .setView(dialogView)
                .setPositiveButton("OK", (dialog, which) -> {
                    int hour = hourPicker.getValue();
                    String minute = minuteValues[minutePicker.getValue()];

                    String selectedTime = String.format("%d:%s", hour, minute);
                    targetTextView.setText(selectedTime);
                })
                .setNegativeButton("キャンセル", null)
                .show();
    }

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

    // クラスの内側に追加
    private void loadRoutineData() {
        // 保存時と同じ名前 "RoutinePrefs" を指定してインスタンスを取得
        SharedPreferences sharedPreferences = getSharedPreferences("RoutinePrefs", Context.MODE_PRIVATE);

        // 各Viewに保存されたデータを読み込んでセットする
        // 第2引数は、まだデータが保存されていない場合に表示される「デフォルト値」

        // 睡眠
        binding.Sleep.setChecked(sharedPreferences.getBoolean("sleep_is_checked", false));
        binding.sleepStart.setText(sharedPreferences.getString("sleep_start_time", ""));
        binding.sleepDuration.setText(sharedPreferences.getString("sleep_duration", ""));

        // 朝食
        binding.breakfast.setChecked(sharedPreferences.getBoolean("breakfast_is_checked", false));
        binding.breakfastStart.setText(sharedPreferences.getString("breakfast_start_time", ""));
        binding.breakfastDuration.setText(sharedPreferences.getString("breakfast_duration", ""));

        // 昼食
        binding.lunch.setChecked(sharedPreferences.getBoolean("lunch_is_checked", false));
        binding.lunchStart.setText(sharedPreferences.getString("lunch_start_time", ""));
        binding.lunchDuration.setText(sharedPreferences.getString("lunch_duration", ""));

        // 夕食
        binding.dinner.setChecked(sharedPreferences.getBoolean("dinner_is_checked", false));
        binding.dinnerStart.setText(sharedPreferences.getString("dinner_start_time", ""));
        binding.dinnerDuration.setText(sharedPreferences.getString("dinner_duration", ""));

        // 風呂
        binding.bath.setChecked(sharedPreferences.getBoolean("bath_is_checked", false));
        binding.bathStart.setText(sharedPreferences.getString("bath_start_time", ""));
        binding.bathDuration.setText(sharedPreferences.getString("bath_duration", ""));

        // その他
        binding.others1.setChecked(sharedPreferences.getBoolean("others1_is_checked", false));
        binding.others1Start.setText(sharedPreferences.getString("others1_start_time", ""));
        binding.others1Duration.setText(sharedPreferences.getString("others1_duration", ""));
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
