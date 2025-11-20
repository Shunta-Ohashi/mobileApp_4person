package jp.ac.meijou.android.schedule;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.NumberPicker;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton; // FABのインポート
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.HashSet;
import java.util.Set;

import jp.ac.meijou.android.schedule.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    // 表示するデータのリスト (より構造化されたデータクラスを使うのが理想)
    private final List<Pair<String, Integer>> scheduleDataList = new ArrayList<>();
    // 保存済みテンプレート (名前, 分)
    private final List<Pair<String, Integer>> savedTemplates = new ArrayList<>();
    private static final String PREFS_NAME = "schedule_prefs";
    private static final String KEY_TEMPLATES = "templates";
    private static final String KEY_HABIT_TIME = "habit_time";
    private static function.habit[][] habitTime = null;
    private final Gson gson = new Gson();
    //習慣からのデータを受け取る配列

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this); // エッジトゥエッジ表示を有効化
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // データを読み込む
        loadHabitTime();

        Intent habitIntent = getIntent();// 習慣からhabitTime[][]データを受け取る
        function.habit[][] received = (function.habit[][]) habitIntent.getSerializableExtra("HabitTime");

        if (received!= null) {
            habitTime = received;
            saveHabitTime(); // 受け取ったデータを保存
            System.out.println("Routine画面からhabitTimeを受け取りました,またはデータが既に入っています．");
        }

        // Window Insets のリスナー設定 (元のコードから)
        // ここでの findViewById は binding.main を使う方が一貫性があります
        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(insets.left, insets.top, insets.right, insets.bottom);
            return windowInsets;
        });


        //予定を追加ボタン -> 新規作成 or 保存済みテンプレート選択のオプションを表示
        loadSavedTemplates();
        binding.floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFabOptionsDialog();
            }
        });

        // スケジュール作成ボタンを押したらActivity2へ遷移する
        binding.button.setOnClickListener(view -> {
            Intent intent = new Intent(this, MainActivity2.class);


        //データ読み取り
            function.ToDo[][] time = new function.ToDo[24][2];

        //受け取ったhabitTimeをtimeにコピー
            if (habitTime != null) {
                for (int h = 0; h < 24; h++) {
                    for (int m = 0; m < 2; m++) {
                        if (habitTime[h][m] != null) {
                            // habit の内容を ToDo にコピー
                            time[h][m] = new function.ToDo(
                                    habitTime[h][m].getTitle(),
                                    habitTime[h][m].getDuration()
                            );
                        }
                    }
                }
            }

            // scheduleDataList の内容を ToDo オブジェクトに変換して配置
            for (Pair<String, Integer> data : scheduleDataList) {
                String name = data.first;
                int durationMinutes = data.second;
                int durationUnits = Math.max(1, durationMinutes / 30); // 30分単位に換算

                function.ToDo task = new function.ToDo(name, durationUnits);

                try {
                    function.decideStartSlotRandom(time, task);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            // * 配置結果をログに出力（確認用）
            for (int h = 0; h < 24; h++) {
                for (int m = 0; m < 2; m++) {
                    if (time[h][m] != null) {
                        System.out.println(h + "時" + (m == 0 ? "00" : "30") + " → " + time[h][m].getTitle());
                    }
                }
            }
            //完成した予定表timeをmainactivity2へ送信
            Intent ResultIntent = new Intent(this, MainActivity2.class);
            intent.putExtra("time", (Serializable)time);

            startActivity(intent);
            });




        // 歯車ボタンを押したら Routine アクティビティへ遷移する
        binding.buttonSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] options = new String[]{"設定画面 (Routine)", "テンプレート管理"};
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("メニュー")
                        .setItems(options, (dialog, which) -> {
                            if (which == 0) {
                                Intent intent = new Intent(MainActivity.this, Routine.class);
                                startActivity(intent);
                            } else {
                                Intent intent = new Intent(MainActivity.this, TemplateManagerActivity.class);
                                startActivity(intent);
                            }
                        })
                        .show();
            }
        });
    }

    private void saveHabitTime() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        String json = gson.toJson(habitTime);
        editor.putString(KEY_HABIT_TIME, json);
        editor.apply();
    }

    private void loadHabitTime() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String json = prefs.getString(KEY_HABIT_TIME, null);
        if (json != null) {
            Type type = new TypeToken<function.habit[][]>() {}.getType();
            habitTime = gson.fromJson(json, type);
        }
    }


    private void showAddScheduleDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        // ダイアログ用のレイアウトを inflate する
        View dialogView = inflater.inflate(R.layout.dialog_add_schedule, null);
        final EditText editTextScheduleName = dialogView.findViewById(R.id.editTextScheduleName);
        final EditText editTextScheduleDuration = dialogView.findViewById(R.id.editTextScheduleDuration);

        editTextScheduleDuration.setFocusable(false);
        editTextScheduleDuration.setClickable(true);
        editTextScheduleDuration.setOnClickListener(v -> showDurationPickerDialog(editTextScheduleDuration));

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("新しい予定を追加")
                .setView(dialogView)
                .setPositiveButton("追加", null) // リスナーは後で設定して、バリデーションエラー時に閉じないようにする
                .setNegativeButton("キャンセル", (d, which) -> d.cancel())
                .create();

        dialog.show();

        // PositiveButtonのクリックリスナーを上書き
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String scheduleName = editTextScheduleName.getText().toString().trim();
            String durationStr = editTextScheduleDuration.getText().toString().trim();

            if (!scheduleName.isEmpty() && !durationStr.isEmpty()) {
                try {
                    int durationMinutes = Integer.parseInt(durationStr);
                    if (durationMinutes > 0 && durationMinutes % 30 == 0) {
                        // データリストに追加
                        scheduleDataList.add(new Pair<>(scheduleName, durationMinutes));
                        // テンプレートとして保存（存在しなければ）
                        addTemplateIfNotExists(scheduleName, durationMinutes);
                        // UIを更新
                        addScheduleItemView(binding.schedulesLinearLayoutContainer, scheduleName, durationMinutes + "分");
                        dialog.dismiss();
                    } else {
                        if (durationMinutes <= 0) {
                            editTextScheduleDuration.setError("0より大きい値を入力してください");
                        } else {
                            editTextScheduleDuration.setError("30分単位(30, 60, 90...)で入力してください");
                        }
                    }
                } catch (NumberFormatException e) {
                    // 数値変換エラー
                    editTextScheduleDuration.setError("数値を入力してください");
                }
            } else {
                if (scheduleName.isEmpty()) {
                    editTextScheduleName.setError("予定名を入力してください");
                }
                if (durationStr.isEmpty()) {
                    editTextScheduleDuration.setError("所要時間を入力してください");
                }
            }
        });
    }

    private void addScheduleItemView(LinearLayout container, String label, String value) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View itemView = inflater.inflate(R.layout.item_schedule, container, false);

        TextView textLabel = itemView.findViewById(R.id.itemTextLabel);
        TextView textValue = itemView.findViewById(R.id.itemTextValue);

        textLabel.setText(label);
        textValue.setText(value);

        container.addView(itemView);
    }

    // (オプション) リスト全体を再描画するメソッド
    // private void updateScheduleListView() {
    //     binding.schedulesLinearLayoutContainer.removeAllViews();
    //     for (Pair<String, String> data : scheduleDataList) {
    //         addScheduleItemView(binding.schedulesLinearLayoutContainer, data.first, data.second);
    //     }
    // }

    // Java用のPairクラス (AndroidXに androidx.core.util.Pair があるのでそれを使っても良い)
    // ここでは簡略化のため内部クラスとして定義。実際のプロジェクトでは androidx.core.util.Pair を推奨
    private static class Pair<F, S> {
        public final F first;
        public final S second;

        public Pair(F first, S second) {
            this.first = first;
            this.second = second;
        }
    }

    // --- Saved templates handling ---
    private void loadSavedTemplates() {
        var prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        Set<String> set = prefs.getStringSet(KEY_TEMPLATES, new HashSet<>());
        savedTemplates.clear();
        for (String s : set) {
            // stored as "name::minutes"
            String[] parts = s.split("::", 2);
            if (parts.length == 2) {
                try {
                    int minutes = Integer.parseInt(parts[1]);
                    savedTemplates.add(new Pair<>(parts[0], minutes));
                } catch (NumberFormatException ignored) {
                }
            }
        }
    }

    private void saveTemplates() {
        var prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        Set<String> set = new HashSet<>();
        for (Pair<String, Integer> p : savedTemplates) {
            set.add(p.first + "::" + p.second);
        }
        prefs.edit().putStringSet(KEY_TEMPLATES, set).apply();
    }

    private void addTemplateIfNotExists(String name, int minutes) {
        for (Pair<String, Integer> p : savedTemplates) {
            if (p.first.equals(name) && p.second == minutes) return;
        }
        savedTemplates.add(new Pair<>(name, minutes));
        saveTemplates();
    }

    private void showSavedTemplatesDialog() {
        loadSavedTemplates();
        if (savedTemplates.isEmpty()) {
            // まだテンプレートがない場合は追加ダイアログを開く
            showAddScheduleDialog();
            return;
        }
        // ListView を使ってタップと長押しを分ける
        ListView listView = new ListView(this);
        List<String> labels = new ArrayList<>();
        for (Pair<String, Integer> p : savedTemplates) {
            labels.add(p.first + " (" + p.second + "分)");
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, labels);
        listView.setAdapter(adapter);

        // タップ: そのまま追加
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Pair<String, Integer> sel = savedTemplates.get(position);
                scheduleDataList.add(new Pair<>(sel.first, sel.second));
                addScheduleItemView(binding.schedulesLinearLayoutContainer, sel.first, sel.second + "分");
            }
        });

        // 長押し: 編集/削除メニューを表示
        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            showTemplateActionDialog(position);
            return true;
        });

        new AlertDialog.Builder(this)
                .setTitle("保存済み予定を選択")
                .setView(listView)
                .setNegativeButton("キャンセル", null)
                .show();
    }

    private void showTemplateActionDialog(int index) {
        String[] actions = new String[]{"編集", "削除"};
        new AlertDialog.Builder(this)
                .setTitle(savedTemplates.get(index).first + " の操作")
                .setItems(actions, (dialog, which) -> {
                    if (which == 0) {
                        // 編集
                        showEditTemplateDialog(index);
                    } else if (which == 1) {
                        // 削除
                        savedTemplates.remove(index);
                        saveTemplates();
                    }
                })
                .setNegativeButton("キャンセル", null)
                .show();
    }

    private void showEditTemplateDialog(int index) {
        Pair<String, Integer> p = savedTemplates.get(index);
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_add_schedule, null);
        final EditText editTextScheduleName = dialogView.findViewById(R.id.editTextScheduleName);
        final EditText editTextScheduleDuration = dialogView.findViewById(R.id.editTextScheduleDuration);
        editTextScheduleName.setText(p.first);
        editTextScheduleDuration.setText(String.valueOf(p.second));

        editTextScheduleDuration.setFocusable(false);
        editTextScheduleDuration.setClickable(true);
        editTextScheduleDuration.setOnClickListener(v -> showDurationPickerDialog(editTextScheduleDuration));

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("テンプレートを編集")
                .setView(dialogView)
                .setPositiveButton("保存", null) // リスナーは後で設定
                .setNegativeButton("キャンセル", null)
                .create();

        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String newName = editTextScheduleName.getText().toString().trim();
            String durationStr = editTextScheduleDuration.getText().toString().trim();
            if (!newName.isEmpty() && !durationStr.isEmpty()) {
                try {
                    int newMinutes = Integer.parseInt(durationStr);
                    if (newMinutes > 0 && newMinutes % 30 == 0) {
                        savedTemplates.set(index, new Pair<>(newName, newMinutes));
                        saveTemplates();
                        dialog.dismiss();
                    } else {
                        if (newMinutes <= 0) {
                            editTextScheduleDuration.setError("0より大きい値を入力してください");
                        } else {
                            editTextScheduleDuration.setError("30分単位(30, 60, 90...)で入力してください");
                        }
                    }
                } catch (NumberFormatException e) {
                    editTextScheduleDuration.setError("数値を入力してください");
                }
            } else {
                if (newName.isEmpty()) {
                    editTextScheduleName.setError("予定名を入力してください");
                }
                if (durationStr.isEmpty()) {
                    editTextScheduleDuration.setError("所要時間を入力してください");
                }
            }
        });
    }

    private void showFabOptionsDialog() {
        String[] options = new String[]{"新規で予定を作成", "保存済み予定から選択"};
        new AlertDialog.Builder(this)
                .setTitle("予定を追加")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            showAddScheduleDialog();
                        } else if (which == 1) {
                            showSavedTemplatesDialog();
                        }
                    }
                })
                .setNegativeButton("キャンセル", null)
                .show();
    }

    private void showDurationPickerDialog(final TextView targetTextView) {
        final String[] durationValues = new String[48]; // 30分〜24時間
        for (int i = 0; i < durationValues.length; i++) {
            durationValues[i] = String.valueOf((i + 1) * 30);
        }

        NumberPicker picker = new NumberPicker(this);
        picker.setMinValue(0);
        picker.setMaxValue(durationValues.length - 1);
        picker.setDisplayedValues(durationValues);
        picker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        String currentVal = targetTextView.getText().toString();
        for (int i = 0; i < durationValues.length; i++) {
            if (durationValues[i].equals(currentVal)) {
                picker.setValue(i);
                break;
            }
        }

        new AlertDialog.Builder(this)
                .setTitle("所要時間(分)を選択")
                .setView(picker)
                .setPositiveButton("OK", (dialog, which) -> {
                    targetTextView.setText(durationValues[picker.getValue()]);
                })
                .setNegativeButton("キャンセル", null)
                .show();
    }
}
