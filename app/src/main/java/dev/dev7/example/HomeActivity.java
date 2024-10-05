package dev.dev7.example;

import static dev.dev7.lib.v2ray.utils.V2rayConstants.SERVICE_CONNECTION_STATE_BROADCAST_EXTRA;
import static dev.dev7.lib.v2ray.utils.V2rayConstants.V2RAY_SERVICE_STATICS_BROADCAST_INTENT;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.Uri;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageButton;
import android.content.SharedPreferences;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Objects;
import dev.dev7.lib.v2ray.V2rayController;
import dev.dev7.lib.v2ray.utils.V2rayConstants;

import android.annotation.SuppressLint;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;


public class HomeActivity extends AppCompatActivity
{

    private ImageButton connect;
    private Button connection;
    private BroadcastReceiver v2rayBroadCastReceiver;
    private String tgLink = "https://t.me/SUPERhit_vpn_bot";

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @SuppressLint({"SetTextI18n", "WrongViewCast", "UseCompatLoadingForDrawables"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        SharedPreferences sharedPreferences = getSharedPreferences("conf", MODE_PRIVATE);
        String v2ray_config = sharedPreferences.getString("v2ray_config", "");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);

        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(getResources().getDrawable(R.color.gray));



        if (savedInstanceState == null)
        {
            connect = findViewById(R.id.imageButton4);
            V2rayController.init(this, R.drawable.ic_launcher, "V2ray Android");
            V2rayController.init(this, R.drawable.ic_launcher, "V2ray Android");
            connection = findViewById(R.id.button15);
        }

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
            public void onReceive(Context context, Intent intent) {runOnUiThread(() -> {

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.popup_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if(id == R.id.reset){
            resetTheConfiguration();
        }
        if(id == R.id.tgBot){
            openTelegramBot();
        }
        if(id == R.id.share){
            shareApp();
        }

        if(id == R.id.about){
            aboutUs();
        }

        return super.onOptionsItemSelected(item);

    }

    public void shareApp(){
        String botLink = tgLink; // Ссылка на вашего Telegram-бота
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Привет! Попробуй этот VPN: " + botLink);
        shareIntent.setType("text/plain");
        startActivity(Intent.createChooser(shareIntent, "Поделиться ботом через"));
    }

    public void aboutUs(){
        Intent intent = new Intent(HomeActivity.this, AboutActivity.class);
        startActivity(intent);
    }

    public void openTelegramBot(){
        Intent telegram = new Intent(Intent.ACTION_VIEW , Uri.parse(tgLink));
        startActivity(telegram);
    }

    public void resetTheConfiguration() {
        // Создаем диалог для подтверждения сброса конфигурации
        new AlertDialog.Builder(HomeActivity.this)
                .setTitle("Сброс конфигурации")
                .setMessage("Вы уверены, что хотите сбросить конфигурацию?")
                .setPositiveButton("Подтвердить", (dialog, which) -> {
                    // Действие при подтверждении сброса
                    SharedPreferences sharedPreferences = getSharedPreferences("conf", MODE_PRIVATE);
                    String v2ray_config = sharedPreferences.getString("v2ray_config", "");

                    if (!v2ray_config.isEmpty()) {
                        // Удаляем сохраненную конфигурацию
                        sharedPreferences.edit().remove("v2ray_config").apply();
                        // Останавливаем V2ray
                        V2rayController.stopV2ray(this);
                    }

                    // Запускаем активность ResetConfigActivity
                    Intent intent = new Intent(HomeActivity.this, ResetConfigActivity.class);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Отменить", (dialog, which) -> {
                    // Действие при отмене (просто закрываем диалог)
                    dialog.dismiss();
                })
                .show();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (v2rayBroadCastReceiver != null){
            unregisterReceiver(v2rayBroadCastReceiver);
        }
    }
}