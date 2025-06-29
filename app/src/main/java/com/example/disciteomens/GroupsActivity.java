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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GroupsActivity extends AppCompatActivity {

    private ListView listView;
    private EditText editGroupNameToCreate;
    private Button btnCreateGroup;
    private TextView textJoinHint;

    private ArrayAdapter<String> adapter;
    private List<Group> groupList = new ArrayList<>();
    private Set<String> joinedGroupIds = new HashSet<>();

    private DisciteOmnesApi apiService;
    private final String currentUserId = "user123"; // Feste User-ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups);

        // Statusbar
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

        listView = findViewById(R.id.list_groups);
        editGroupNameToCreate = findViewById(R.id.editGroupNameToCreate);
        btnCreateGroup = findViewById(R.id.btnCreateGroup);
        textJoinHint = findViewById(R.id.textJoinHint);

        apiService = RetrofitClient.getApi();

        loadGroups();

        btnCreateGroup.setOnClickListener(v -> {
            String name = editGroupNameToCreate.getText().toString().trim();
            if (name.isEmpty()) {
                Toast.makeText(this, "Bitte einen Gruppennamen eingeben", Toast.LENGTH_SHORT).show();
                return;
            }

            Group newGroup = new Group();
            newGroup.setName(name);
            newGroup.setMembers(new ArrayList<>());

            //KI-unterstützt
            apiService.createGroup(newGroup).enqueue(new Callback<>() {
                @Override
                public void onResponse(Call<Group> call, Response<Group> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        groupList.add(response.body());
                        updateListView();
                        Toast.makeText(GroupsActivity.this, "Gruppe erstellt!", Toast.LENGTH_SHORT).show();
                        editGroupNameToCreate.setText("");
                    } else {
                        Toast.makeText(GroupsActivity.this, "Erstellen fehlgeschlagen", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Group> call, Throwable t) {
                    Toast.makeText(GroupsActivity.this, "Fehler: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        listView.setOnItemClickListener((adapterView, view, position, id) -> {
            Group clickedGroup = groupList.get(position);

            if (clickedGroup.getMembers() != null && clickedGroup.getMembers().contains(currentUserId)) {
                Toast.makeText(this, "Du bist dieser Gruppe bereits beigetreten", Toast.LENGTH_SHORT).show();
                return;
            }

            if (clickedGroup.getMembers() == null) {
                clickedGroup.setMembers(new ArrayList<>());
            }
            clickedGroup.getMembers().add(currentUserId);

            apiService.updateGroup(clickedGroup.getId(), clickedGroup).enqueue(new Callback<>() {
                @Override
                public void onResponse(Call<Group> call, Response<Group> response) {
                    if (response.isSuccessful()) {
                        joinedGroupIds.add(clickedGroup.getId());
                        Toast.makeText(GroupsActivity.this, "Beigetreten: " + clickedGroup.getName(), Toast.LENGTH_SHORT).show();
                        loadGroups();
                    } else {
                        Toast.makeText(GroupsActivity.this, "Beitreten fehlgeschlagen", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Group> call, Throwable t) {
                    Toast.makeText(GroupsActivity.this, "Verbindungsfehler: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    //KI-unterstützt
    private void loadGroups() {
        apiService.getGroups().enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<List<Group>> call, Response<List<Group>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    groupList = response.body();
                    joinedGroupIds.clear();
                    for (Group g : groupList) {
                        if (g.getMembers() != null && g.getMembers().contains(currentUserId)) {
                            joinedGroupIds.add(g.getId());
                        }
                    }
                    updateListView();
                } else {
                    Toast.makeText(GroupsActivity.this, "Fehler beim Laden der Gruppen", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Group>> call, Throwable t) {
                Toast.makeText(GroupsActivity.this, "Verbindungsfehler: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateListView() {
        List<String> display = new ArrayList<>();
        for (Group g : groupList) {
            if (joinedGroupIds.contains(g.getId())) {
                display.add(g.getName() + " (Beigetreten)");
            } else {
                display.add(g.getName() + " (ID: " + g.getId() + ")");
            }
        }
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, display);
        listView.setAdapter(adapter);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
