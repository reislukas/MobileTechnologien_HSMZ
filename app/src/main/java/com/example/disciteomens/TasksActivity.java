package com.example.disciteomens;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowInsetsController;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TasksActivity extends AppCompatActivity {

    private ListView listView;
    private List<Task> taskList = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private EditText inputTask;
    private Button btnAddTask;
    private String groupId = "1234"; // feste Beispiel-ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);

        // Statusbar hell
        Window window = getWindow();
        window.setStatusBarColor(Color.WHITE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowInsetsController insetsController = window.getInsetsController();
            if (insetsController != null) {
                insetsController.setSystemBarsAppearance(
                        WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                        WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
                );
            }
        } else {
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        listView = findViewById(R.id.list_tasks);
        inputTask = findViewById(R.id.input_task);
        btnAddTask = findViewById(R.id.btn_add_task);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<>());
        listView.setAdapter(adapter);

        loadTasks();

        //KI-unterstützt
        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            Task task = taskList.get(i);
            task.setCompleted(!task.isCompleted());

            RetrofitClient.getApi().updateTask(task.getId(), task).enqueue(new Callback<>() {
                @Override
                public void onResponse(Call<Task> call, Response<Task> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(TasksActivity.this, "Aufgabe aktualisiert", Toast.LENGTH_SHORT).show();
                        updateDisplay();
                    } else {
                        Toast.makeText(TasksActivity.this, "Fehler beim Aktualisieren", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Task> call, Throwable t) {
                    Toast.makeText(TasksActivity.this, "Netzwerkfehler", Toast.LENGTH_SHORT).show();
                }
            });
        });

        listView.setOnItemLongClickListener((adapterView, view, i, l) -> {
            Task taskToDelete = taskList.get(i);

            RetrofitClient.getApi().deleteTask(taskToDelete.getId()).enqueue(new Callback<>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        taskList.remove(i);
                        updateDisplay();
                        Toast.makeText(TasksActivity.this, "Aufgabe gelöscht", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(TasksActivity.this, "Fehler beim Löschen", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(TasksActivity.this, "Netzwerkfehler: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

            return true;
        });

        btnAddTask.setOnClickListener(v -> {
            String text = inputTask.getText().toString().trim();
            if (text.isEmpty()) {
                Toast.makeText(this, "Bitte Text eingeben", Toast.LENGTH_SHORT).show();
                return;
            }

            Task newTask = new Task(text, false, groupId);

            RetrofitClient.getApi().addTask(newTask).enqueue(new Callback<>() {
                @Override
                public void onResponse(Call<Task> call, Response<Task> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        taskList.add(response.body());
                        updateDisplay();
                        inputTask.setText("");
                        Toast.makeText(TasksActivity.this, "Aufgabe hinzugefügt", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(TasksActivity.this, "Fehler beim Hinzufügen", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Task> call, Throwable t) {
                    Toast.makeText(TasksActivity.this, "Netzwerkfehler", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void loadTasks() {
        RetrofitClient.getApi().getTasks(groupId).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<List<Task>> call, Response<List<Task>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    taskList = response.body();
                    updateDisplay();
                } else {
                    Toast.makeText(TasksActivity.this, "Fehler beim Laden der Aufgaben", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Task>> call, Throwable t) {
                Toast.makeText(TasksActivity.this, "Netzwerkfehler", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateDisplay() {
        List<String> displayList = new ArrayList<>();
        for (Task t : taskList) {
            if (t.isCompleted()) {
                displayList.add("✔ " + t.getTitle());
            } else {
                displayList.add(t.getTitle());
            }
        }
        adapter.clear();
        adapter.addAll(displayList);
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}