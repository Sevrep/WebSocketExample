package com.sevrep.websocketexample;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.text.MessageFormat;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class MainActivity extends AppCompatActivity {

    private TextView txtOutput;

    private OkHttpClient okHttpClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnStart = findViewById(R.id.btnStart);
        txtOutput = findViewById(R.id.txtOutput);
        okHttpClient = new OkHttpClient();

        btnStart.setOnClickListener(v -> start());
    }

    private final class EchoWebSocketListener extends WebSocketListener {
        private static final int NORMAL_CLOSURE_STATUS = 1000;

        @Override
        public void onOpen(WebSocket webSocket, @NonNull Response response) {
            webSocket.send("Hello, it's Ivan");
            webSocket.send("What's up?");
            webSocket.send(ByteString.decodeHex("Dead hoof beat."));
            webSocket.close(NORMAL_CLOSURE_STATUS, "Goodbye!");
        }

        @Override
        public void onMessage(@NonNull WebSocket webSocket, @NonNull String text) {
            output(String.format("Receiving string: %s", text));
        }

        @Override
        public void onMessage(@NonNull WebSocket webSocket, ByteString bytes) {
            output(String.format("Receiving bytes: %s", bytes.hex()));
        }

        @Override
        public void onClosing(WebSocket webSocket, int code, @NonNull String reason) {
            webSocket.close(NORMAL_CLOSURE_STATUS, null);
            output(MessageFormat.format("Closing : {0} / {1}", code, reason));
        }

        @Override
        public void onFailure(@NonNull WebSocket webSocket, Throwable t, @Nullable Response response) {
            output(String.format("Error : %s", t.getMessage()));
        }
    }

    private void output(final String text) {
        runOnUiThread(() -> txtOutput.setText(String.format("%s\n\n%s", txtOutput.getText().toString(), text)));
    }

    private void start() {
        Request request = new Request.Builder().url("ws://echo.websocket.org").build();
        EchoWebSocketListener listener = new EchoWebSocketListener();

        okHttpClient.newWebSocket(request, listener);
        okHttpClient.dispatcher().executorService().shutdown();
    }

}