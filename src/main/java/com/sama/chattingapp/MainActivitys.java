package com.sama.chattingapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class MainActivitys extends AppCompatActivity {

    private WebSocketManager webSocketManager;
    private EditText editTextMessage;
    private Button buttonSend;
    private List<String> messages = new ArrayList<>();
    private MessageAdapter messageAdapter;
    private DatabaseReference messageDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        editTextMessage = findViewById(R.id.editTextMessage);
        buttonSend = findViewById(R.id.buttonSend);
        RecyclerView recyclerViewMessages = findViewById(R.id.recyclerViewMessages);

        // Set up RecyclerView
        messageAdapter = new MessageAdapter(messages);
        recyclerViewMessages.setAdapter(messageAdapter);
        recyclerViewMessages.setLayoutManager(new LinearLayoutManager(this));

        // Initialize Firebase Database reference
        messageDatabase = FirebaseDatabase.getInstance().getReference("messages");

        // Initialize WebSocket
        webSocketManager = new WebSocketManager();
        webSocketManager.setMessageListener(this::receiveMessage); // Set the message listener
        webSocketManager.startWebSocket();

        // Send button click listener
        buttonSend.setOnClickListener(view -> sendMessage());
    }

    private void sendMessage() {
        String message = editTextMessage.getText().toString().trim(); // Trim whitespace
        if (!message.isEmpty()) {
            webSocketManager.sendMessage(message); // Send message via WebSocket
            saveMessageToFirebase(message); // Save message to Firebase
            editTextMessage.setText(""); // Clear the input field
            messages.add("You: " + message);
            messageAdapter.notifyItemInserted(messages.size() - 1); // Notify adapter about new message
        }
    }

    // Method to receive messages
    public void receiveMessage(String message) {
        runOnUiThread(() -> {
            messages.add("Friend: " + message);
            messageAdapter.notifyItemInserted(messages.size() - 1); // Notify adapter about new message
        });
    }

    // Method to save messages to Firebase
    private void saveMessageToFirebase(String message) {
        messageDatabase.push().setValue(message) // Push the message to the database
                .addOnSuccessListener(aVoid -> {
                    // Optionally, handle success
                })
                .addOnFailureListener(e -> {
                    // Optionally, handle failure
                    e.printStackTrace(); // Log the error
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        webSocketManager.startWebSocket(); // Start WebSocket connection
    }

    @Override
    protected void onPause() {
        super.onPause();
        webSocketManager.closeWebSocket(); // Close WebSocket connection
    }
}
