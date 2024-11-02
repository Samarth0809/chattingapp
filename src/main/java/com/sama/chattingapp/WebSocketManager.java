package com.sama.chattingapp;

import androidx.annotation.NonNull;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class WebSocketManager {
    private WebSocket webSocket;
    private static final String SERVER_URL = "ws://10.0.2.2:8080/chat"; // Replace with your server's IP if testing on a physical device
    private MessageListener messageListener;
    private final OkHttpClient client; // OkHttpClient instance for managing connections

    // Constructor to initialize the OkHttpClient
    public WebSocketManager() {
        client = new OkHttpClient(); // Initialize OkHttpClient
    }

    // Method to start WebSocket connection
    public void startWebSocket() {
        Request request = new Request.Builder().url(SERVER_URL).build();
        webSocket = client.newWebSocket(request, new ChatWebSocketListener());
    }

    // Method to send messages
    public void sendMessage(String message) {
        if (webSocket != null) {
            webSocket.send(message);
        }
    }

    // Method to set the message listener
    public void setMessageListener(MessageListener listener) {
        this.messageListener = listener;
    }

    // Method to close WebSocket connection
    public void closeWebSocket() {
        if (webSocket != null) {
            webSocket.close(1000, "Closing connection"); // 1000 is a normal closure code
            webSocket = null; // Nullify the reference after closing
        }
    }

    // WebSocket listener to handle events
    private final class ChatWebSocketListener extends WebSocketListener {
        @Override
        public void onMessage(@NonNull WebSocket webSocket, @NonNull String text) {
            // Notify the listener when a message is received
            if (messageListener != null) {
                messageListener.onMessage(text);
            }
        }

        @Override
        public void onFailure(@NonNull WebSocket webSocket, @NonNull Throwable t, Response response) {
            // Handle errors
            t.printStackTrace(); // Log the error for debugging
            // Optionally, notify the listener about the failure
        }
    }

    // Interface for message listener
    public interface MessageListener {
        void onMessage(String message); // Callback method for incoming messages
    }
}
