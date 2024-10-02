package dev.dev7.example;

import static dev.dev7.lib.v2ray.utils.V2rayConstants.SERVICE_CONNECTION_STATE_BROADCAST_EXTRA;
import static dev.dev7.lib.v2ray.utils.V2rayConstants.SERVICE_DOWNLOAD_SPEED_BROADCAST_EXTRA;
import static dev.dev7.lib.v2ray.utils.V2rayConstants.SERVICE_DOWNLOAD_TRAFFIC_BROADCAST_EXTRA;
import static dev.dev7.lib.v2ray.utils.V2rayConstants.SERVICE_DURATION_BROADCAST_EXTRA;
import static dev.dev7.lib.v2ray.utils.V2rayConstants.SERVICE_UPLOAD_SPEED_BROADCAST_EXTRA;
import static dev.dev7.lib.v2ray.utils.V2rayConstants.SERVICE_UPLOAD_TRAFFIC_BROADCAST_EXTRA;
import static dev.dev7.lib.v2ray.utils.V2rayConstants.V2RAY_SERVICE_STATICS_BROADCAST_INTENT;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.Uri;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageButton;
import android.content.SharedPreferences;
import android.widget.TextView;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import java.util.Objects;
import dev.dev7.lib.v2ray.V2rayController;
import dev.dev7.lib.v2ray.utils.V2rayConfigs;
import dev.dev7.lib.v2ray.utils.V2rayConstants;

public class HomeActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    private ImageButton connect, btnPopup2;
    private Button connection;
    private TextView connection_speed, connection_traffic, connection_time, server_delay, connected_server_delay, connection_mode;
    private BroadcastReceiver v2rayBroadCastReceiver;

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @SuppressLint({"SetTextI18n", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        SharedPreferences sharedPreferences = getSharedPreferences("conf", MODE_PRIVATE);
        String v2ray_config = sharedPreferences.getString("v2ray_config", "");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);

        if (savedInstanceState == null)
        {

            btnPopup2 = findViewById(R.id.setting_id);
            connect = findViewById(R.id.imageButton4);
            V2rayController.init(this, R.drawable.ic_launcher, "V2ray Android");
            V2rayController.init(this, R.drawable.ic_launcher, "V2ray Android");
            connection_speed = findViewById(R.id.connection_speed);
            connection_time = findViewById(R.id.connection_duration);
            connection_traffic = findViewById(R.id.connection_traffic);
            server_delay = findViewById(R.id.server_delay);
            connection_mode = findViewById(R.id.connection_mode);
            connected_server_delay = findViewById(R.id.connected_server_delay);
            connection = findViewById(R.id.button15);
        }

        btnPopup2.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(HomeActivity.this, v);
            popup.setOnMenuItemClickListener(HomeActivity.this);
            popup.inflate(R.menu.popup_menu);
            popup.show();
        });


        connect.setOnClickListener(view -> {
            if (V2rayController.getConnectionState() == V2rayConstants.CONNECTION_STATES.DISCONNECTED) {
                V2rayController.startV2ray(this, "Test Server", v2ray_config, null);
            } else {
                V2rayController.stopV2ray(this);
            }
        });


        connected_server_delay.setOnClickListener(view -> {
            connected_server_delay.setText("connected server delay : measuring...");
            // Don`t forget to do ui jobs in ui thread!
            V2rayController.getConnectedV2rayServerDelay(this, delayResult -> runOnUiThread(() -> connected_server_delay.setText("connected server delay : " + delayResult + "ms")));
        });

        // Another way to check the connection delay of a config without connecting to it.
        server_delay.setOnClickListener(view -> {
            server_delay.setText("server delay : measuring...");
            new Handler().postDelayed(() -> server_delay.setText("server delay : " + V2rayController.getV2rayServerDelay(v2ray_config) + "ms"), 200);
        });

        connection_mode.setOnClickListener(view -> {
            V2rayController.toggleConnectionMode();
            connection_mode.setText("connection mode : " + V2rayConfigs.serviceMode.toString());
        });

        v2rayBroadCastReceiver = new BroadcastReceiver() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onReceive(Context context, Intent intent) {
                runOnUiThread(() -> {
                    connection_time.setText("connection time : " + Objects.requireNonNull(intent.getExtras()).getString(SERVICE_DURATION_BROADCAST_EXTRA));
                    connection_speed.setText("connection speed : " + intent.getExtras().getString(SERVICE_UPLOAD_SPEED_BROADCAST_EXTRA) + " | " + intent.getExtras().getString(SERVICE_DOWNLOAD_SPEED_BROADCAST_EXTRA));
                    connection_traffic.setText("connection traffic : " + intent.getExtras().getString(SERVICE_UPLOAD_TRAFFIC_BROADCAST_EXTRA) + " | " + intent.getExtras().getString(SERVICE_DOWNLOAD_TRAFFIC_BROADCAST_EXTRA));
                    connection_mode.setText("connection mode : " + V2rayConfigs.serviceMode.toString());
                    switch ((V2rayConstants.CONNECTION_STATES) Objects.requireNonNull(intent.getExtras().getSerializable(SERVICE_CONNECTION_STATE_BROADCAST_EXTRA))) {
                        case CONNECTED:
                            connect.setImageResource(R.drawable.disconnection);
                            connection.setText("Подключено");
                            break;
                        case DISCONNECTED:
                            connect.setImageResource(R.drawable.connection);
                            connection.setText("Отключено");
                            connected_server_delay.setText("connected server delay : wait for connection");
                            break;
                        case CONNECTING:
                            connection.setText("CONNECTING");
                            break;
                        default:
                            break;
                    }
                });
            }
        };

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(v2rayBroadCastReceiver, new IntentFilter(V2RAY_SERVICE_STATICS_BROADCAST_INTENT), RECEIVER_EXPORTED);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                registerReceiver(v2rayBroadCastReceiver, new IntentFilter(V2RAY_SERVICE_STATICS_BROADCAST_INTENT), Context.RECEIVER_NOT_EXPORTED);
            }
        }
    }


    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if(item.getItemId() == R.id.reset){
            resetTheConfiguration();
        }
        return true;
    }

    public void openTelegramBot(){
        String botUsername = "My First Bot"; // Замените на имя вашего бота
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("https://t.me/crackeppp_bot" + botUsername));
        startActivity(intent);
    }

    public void resetTheConfiguration(){

        SharedPreferences sharedPreferences = getSharedPreferences("conf", MODE_PRIVATE);
        String v2ray_config = sharedPreferences.getString("v2ray_config", "");

        if (!v2ray_config.isEmpty()) {

            sharedPreferences.edit().remove("v2ray_config").apply();
            V2rayController.stopV2ray(this);

        }

        Intent intent = new Intent(HomeActivity.this, ResetConfigActivity.class);
        startActivity(intent);
        finish();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (v2rayBroadCastReceiver != null){
            unregisterReceiver(v2rayBroadCastReceiver);
        }
    }
}