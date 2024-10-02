package dev.dev7.example;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.widget.PopupMenu;

public class ResetConfigActivity extends HomeActivity
{
    private ImageButton btnPopup1;
    private Button connection;
    private EditText v2ray_config;
    private SharedPreferences sharedPreferences;

    @SuppressLint({"SetTextI18n", "UnspecifiedRegisterReceiverFlag"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.reset_activity);

        if (savedInstanceState == null) {
            btnPopup1 = findViewById(R.id.imageButton);
            connection = findViewById(R.id.btn_connection);
            v2ray_config = findViewById(R.id.v2ray_config);
        }


        btnPopup1.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(ResetConfigActivity.this, v);
            popup.setOnMenuItemClickListener(ResetConfigActivity.this);
            popup.inflate(R.menu.popup_menu);
            popup.show();
        });


        // initialize shared preferences for save or reload default config
        sharedPreferences = getSharedPreferences("conf", MODE_PRIVATE);
        // reload previous config to edit text
        v2ray_config.setText(sharedPreferences.getString("v2ray_config", getDefaultConfig()));

        connection.setOnClickListener(view ->
        {
            String v2rayConfig = v2ray_config.getText().toString().trim(); // Получаем текст из EditText
            if (v2rayConfig.isEmpty()) { // Проверяем, пустое ли поле
                // Можно показать сообщение об ошибке, если поле пустое
                Toast.makeText(ResetConfigActivity.this, "Введите ссылку!", Toast.LENGTH_SHORT).show();
            } else {
                // Сохраняем конфигурацию и открываем вторую активность
                sharedPreferences.edit().putString("v2ray_config", v2rayConfig).apply();
                startSecondActivity();
            }
        });
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if(item.getItemId() == R.id.tg){
            openTelegramBot();
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Проверяем, существует ли сохраненная конфигурация
        String v2rayConfig = sharedPreferences.getString("v2ray_config", null);
        if (v2rayConfig != null) {
            // Если конфигурация существует, открываем вторую активность
            startSecondActivity();
        }
    }

    public void startSecondActivity(){
        Intent intent = new Intent(ResetConfigActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    public static String getDefaultConfig() {
        return "";
    }
}