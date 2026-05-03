package com.example.project;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import java.util.ArrayList;

public class AdminUsersActivity extends AppCompatActivity {

    private Database db;
    private ListView listViewUsers;
    private Button btnBack;
    private ArrayList<Person> personsList;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> displayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_list);

        db = new Database(this);
        db.open();

        listViewUsers = findViewById(R.id.listView);
        btnBack = findViewById(R.id.btnBack);
        TextView titleText = findViewById(R.id.titleText);
        titleText.setText("Manage Users");

        displayList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, displayList);
        listViewUsers.setAdapter(adapter);

        loadUsers();

        listViewUsers.setOnItemClickListener((parent, view, position, id) -> {
            showUserOptions(position);
        });

        btnBack.setOnClickListener(v -> finish());
    }

    private void loadUsers() {
        personsList = db.getAllPersons(); // Now includes role
        displayList.clear();
        for (Person person : personsList) {
            String role = person.getRole() != null ? person.getRole() : "user";
            displayList.add(person.getName() + " (" + person.getEmail() + ") - " + role);
        }
        adapter.notifyDataSetChanged();
    }

    private void showUserOptions(int position) {
        Person person = personsList.get(position);
        String[] options = {"Edit User", "Delete User", "Cancel"};

        new AlertDialog.Builder(this)
                .setTitle("User: " + person.getName())
                .setItems(options, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            showEditUserDialog(person);
                            break;
                        case 1:
                            showDeleteUserDialog(person);
                            break;
                    }
                })
                .show();
    }

    private void showEditUserDialog(Person person) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_edit_user, null);

        EditText etName = view.findViewById(R.id.etName);
        EditText etEmail = view.findViewById(R.id.etEmail);
        EditText etPassword = view.findViewById(R.id.etPassword);

        etName.setText(person.getName());
        etEmail.setText(person.getEmail());
        etPassword.setText(person.getPassword());

        builder.setView(view)
                .setTitle("Edit User")
                .setPositiveButton("Update", (dialog, which) -> {
                    String name = etName.getText().toString().trim();
                    String email = etEmail.getText().toString().trim();
                    String password = etPassword.getText().toString().trim();

                    if (name.isEmpty() || email.isEmpty()) {
                        Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    person.setName(name);
                    person.setEmail(email);
                    person.setPassword(password);

                    int rows = db.update(person);
                    if (rows > 0) {
                        Toast.makeText(this, "User updated", Toast.LENGTH_SHORT).show();
                        loadUsers();
                    } else {
                        Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showDeleteUserDialog(Person person) {
        new AlertDialog.Builder(this)
                .setTitle("Delete User")
                .setMessage("Are you sure you want to delete " + person.getName() + "?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    int count = db.deletePerson(person.getId());
                    if (count > 0) {
                        Toast.makeText(this, "User deleted", Toast.LENGTH_SHORT).show();
                        loadUsers();
                    } else {
                        Toast.makeText(this, "Delete failed", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (db != null) {
            db.close();
        }
    }

    private void showChangeRoleDialog(Person person) {
        String[] roles = {"user", "admin"};
        String currentRole = person.getRole() != null ? person.getRole() : "user";
        int checkedItem = currentRole.equals("admin") ? 1 : 0;

        new AlertDialog.Builder(this)
                .setTitle("Change Role for " + person.getName())
                .setSingleChoiceItems(roles, checkedItem, (dialog, which) -> {
                    String newRole = roles[which];
                    if (db.updateUserRole(person.getId(), newRole)) {
                        Toast.makeText(this, "Role updated to " + newRole, Toast.LENGTH_SHORT).show();
                        loadUsers();
                    }
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}