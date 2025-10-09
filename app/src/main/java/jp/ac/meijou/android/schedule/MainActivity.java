package jp.ac.meijou.android.schedule;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton; // FABのインポート

import java.util.ArrayList;
import java.util.List;

import jp.ac.meijou.android.schedule.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    // 表示するデータのリスト (より構造化されたデータクラスを使うのが理想)
    private final List<Pair<String, String>> scheduleDataList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this); // エッジトゥエッジ表示を有効化
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Window Insets のリスナー設定 (元のコードから)
        // ここでの findViewById は binding.main を使う方が一貫性があります
        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(insets.left, insets.top, insets.right, insets.bottom);
            return windowInsets;
        });



        binding.floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddScheduleDialog();
            }
        });

        // スケジュール作成ボタンを押したらActivity2へ遷移する
        binding.button.setOnClickListener(view -> {
            var intent = new Intent(this, MainActivity2.class);






            startActivity(intent);
        });

        // 歯車ボタンを押したら Routine アクティビティへ遷移する
        binding.buttonSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Routine.class);
                startActivity(intent);
            }
        });
    }

    private void showAddScheduleDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        // ダイアログ用のレイアウトを inflate する
        View dialogView = inflater.inflate(R.layout.dialog_add_schedule, null);
        final EditText editTextScheduleName = dialogView.findViewById(R.id.editTextScheduleName);
        final EditText editTextScheduleDuration = dialogView.findViewById(R.id.editTextScheduleDuration);

        new AlertDialog.Builder(this)
                .setTitle("新しい予定を追加")
                .setView(dialogView)
                .setPositiveButton("追加", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String scheduleName = editTextScheduleName.getText().toString().trim();
                        String durationStr = editTextScheduleDuration.getText().toString().trim();

                        if (!scheduleName.isEmpty() && !durationStr.isEmpty()) {
                            try {
                                int durationMinutes = Integer.parseInt(durationStr);
                                if (durationMinutes > 0) {
                                    // データリストに追加
                                    scheduleDataList.add(new Pair<>(scheduleName, durationMinutes + "分"));
                                    // UIを更新
                                    addScheduleItemView(binding.schedulesLinearLayoutContainer, scheduleName, durationMinutes + "分");
                                    dialog.dismiss();
                                } else {
                                    // 0以下の時間は無効
                                    editTextScheduleDuration.setError("0より大きい値を入力してください");
                                    // ダイアログを閉じないようにするには、onClick内で何もしないか、
                                    // AlertDialogのButtonを上書きするなどの高度なテクニックが必要
                                    // ここでは簡潔さのため、エラー表示のみ
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
                        // Positive Button のデフォルトの動作では、onClickが終了するとダイアログが閉じる。
                        // エラー時に閉じないようにするには、一手間必要（例：Buttonを取得してカスタムリスナーを設定）
                        // 今回はシンプルに、エラーがあっても一旦閉じる動作のままとしています。
                        // もし閉じたくない場合は、AlertDialogの表示後にButtonを取得してsetOnClickListenerを上書きします。
                    }
                })
                .setNegativeButton("キャンセル", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .create()
                .show();
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
}

