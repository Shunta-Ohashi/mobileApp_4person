package jp.ac.meijou.android.schedule;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TemplateManagerActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "schedule_prefs";
    private static final String KEY_TEMPLATES = "templates";

    private final List<Pair<String, Integer>> templates = new ArrayList<>();
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_template_manager);

        ListView listView = findViewById(R.id.listViewTemplates);
        Button buttonAdd = findViewById(R.id.buttonAddNew);

        loadTemplates();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, labelsFromTemplates());
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            // edit
            showEditDialog(position);
        });

        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            // delete
            templates.remove(position);
            saveTemplates();
            refreshList();
            Toast.makeText(this, "テンプレートを削除しました", Toast.LENGTH_SHORT).show();
            return true;
        });

        buttonAdd.setOnClickListener(v -> showAddDialog());
    }

    private List<String> labelsFromTemplates() {
        List<String> labels = new ArrayList<>();
        for (Pair<String, Integer> p : templates) {
            labels.add(p.first + " (" + p.second + "分)");
        }
        return labels;
    }

    private void refreshList() {
        adapter.clear();
        adapter.addAll(labelsFromTemplates());
        adapter.notifyDataSetChanged();
    }

    private void loadTemplates() {
        var prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        Set<String> set = prefs.getStringSet(KEY_TEMPLATES, new HashSet<>());
        templates.clear();
        for (String s : set) {
            String[] parts = s.split("::", 2);
            if (parts.length == 2) {
                try {
                    int minutes = Integer.parseInt(parts[1]);
                    templates.add(new Pair<>(parts[0], minutes));
                } catch (NumberFormatException ignored) {}
            }
        }
    }

    private void saveTemplates() {
        var prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        Set<String> set = new HashSet<>();
        for (Pair<String, Integer> p : templates) {
            set.add(p.first + "::" + p.second);
        }
        prefs.edit().putStringSet(KEY_TEMPLATES, set).apply();
    }

    private void showAddDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_add_schedule, null);
        final EditText editTextScheduleName = dialogView.findViewById(R.id.editTextScheduleName);
        final EditText editTextScheduleDuration = dialogView.findViewById(R.id.editTextScheduleDuration);

        new AlertDialog.Builder(this)
                .setTitle("新しいテンプレートを追加")
                .setView(dialogView)
                .setPositiveButton("追加", (dialog, which) -> {
                    String name = editTextScheduleName.getText().toString().trim();
                    String durationStr = editTextScheduleDuration.getText().toString().trim();
                    if (!name.isEmpty() && !durationStr.isEmpty()) {
                        try {
                            int minutes = Integer.parseInt(durationStr);
                            templates.add(new Pair<>(name, minutes));
                            saveTemplates();
                            refreshList();
                        } catch (NumberFormatException ignored) {}
                    }
                })
                .setNegativeButton("キャンセル", null)
                .show();
    }

    private void showEditDialog(int index) {
        Pair<String, Integer> p = templates.get(index);
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_add_schedule, null);
        final EditText editTextScheduleName = dialogView.findViewById(R.id.editTextScheduleName);
        final EditText editTextScheduleDuration = dialogView.findViewById(R.id.editTextScheduleDuration);
        editTextScheduleName.setText(p.first);
        editTextScheduleDuration.setText(String.valueOf(p.second));

        new AlertDialog.Builder(this)
                .setTitle("テンプレートを編集")
                .setView(dialogView)
                .setPositiveButton("保存", (dialog, which) -> {
                    String newName = editTextScheduleName.getText().toString().trim();
                    String durationStr = editTextScheduleDuration.getText().toString().trim();
                    if (!newName.isEmpty() && !durationStr.isEmpty()) {
                        try {
                            int newMinutes = Integer.parseInt(durationStr);
                            templates.set(index, new Pair<>(newName, newMinutes));
                            saveTemplates();
                            refreshList();
                        } catch (NumberFormatException ignored) {}
                    }
                })
                .setNegativeButton("キャンセル", null)
                .show();
    }
}
