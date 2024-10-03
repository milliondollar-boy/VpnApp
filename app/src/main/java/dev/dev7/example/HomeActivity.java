package dev.dev7.example;

import static dev.dev7.lib.v2ray.utils.V2rayConstants.SERVICE_CONNECTION_STATE_BROADCAST_EXTRA;
import static dev.dev7.lib.v2ray.utils.V2rayConstants.V2RAY_SERVICE_STATICS_BROADCAST_INTENT;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.Uri;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.content.SharedPreferences;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;

import java.util.Objects;
import dev.dev7.lib.v2ray.V2rayController;
import dev.dev7.lib.v2ray.utils.V2rayConstants;

public class HomeActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    private ImageButton connect, tgBot2;
    private Button connection;
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
            tgBot2 = findViewById(R.id.tgBot2);
            connect = findViewById(R.id.imageButton4);
            V2rayController.init(this, R.drawable.ic_launcher, "V2ray Android");
            V2rayController.init(this, R.drawable.ic_launcher, "V2ray Android");
            connection = findViewById(R.id.button15);
        }


        tgBot2.setOnClickListener(v -> openTelegramBot());


        connect.setOnClickListener(view -> {
            if (V2rayController.getConnectionState() == V2rayConstants.CONNECTION_STATES.DISCONNECTED) {
                V2rayController.startV2ray(this, "Test Server", v2ray_config, null);
            } else {
                V2rayController.stopV2ray(this);
            }
        });

        v2rayBroadCastReceiver = new BroadcastReceiver() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onReceive(Context context, Intent intent) {
                runOnUiThread(() -> {

                    switch ((V2rayConstants.CONNECTION_STATES) Objects.requireNonNull(Objects.requireNonNull(intent.getExtras()).getSerializable(SERVICE_CONNECTION_STATE_BROADCAST_EXTRA))) {
                        case CONNECTED:
                            connect.setImageResource(R.drawable.disconnection);
                            connection.setText("Подключено");
                            break;
                        case DISCONNECTED:
                            connect.setImageResource(R.drawable.connection);
                            connection.setText("Отключено");
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

    public void showPopupMenu(View view){
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.inflate(R.menu.popup_menu);
        popupMenu.show();
    }


    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.reset){
            resetTheConfiguration();
        }
        return true;
    }

    public void openTelegramBot(){
        String botUsername = "SUPERhit_vpn_bot"; // Замените на имя вашего бота
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