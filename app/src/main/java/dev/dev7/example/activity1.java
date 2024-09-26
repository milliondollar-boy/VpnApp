package dev.dev7.example;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class activity1 extends AppCompatActivity
{
    private Button connection;
    private EditText v2ray_config;
    private SharedPreferences sharedPreferences;

    @SuppressLint({"SetTextI18n", "UnspecifiedRegisterReceiverFlag"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_1);

        if (savedInstanceState == null) {
            connection = findViewById(R.id.btn_connection);
            v2ray_config = findViewById(R.id.v2ray_config);
        }

        // initialize shared preferences for save or reload default config
        sharedPreferences = getSharedPreferences("conf", MODE_PRIVATE);
        // reload previous config to edit text
        v2ray_config.setText(sharedPreferences.getString("v2ray_config", getDefaultConfig()));

        connection.setOnClickListener(view ->
        {
            String v2rayConfig = v2ray_config.getText().toString().trim(); // Получаем текст из EditText
            if (v2rayConfig.isEmpty()) { // Проверяем, пустое ли поле
                // Можно показать сообщение об ошибке, если поле пустое
                Toast.makeText(activity1.this, "Введите ссылку!", Toast.LENGTH_SHORT).show();
            } else {
                // Сохраняем конфигурацию и открываем вторую активность
                sharedPreferences.edit().putString("v2ray_config", v2rayConfig).apply();
                startSecondActivity();
            }
        });
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
        Intent intent = new Intent(activity1.this, activity2.class);
        startActivity(intent);
        finish();
    }

    public static String getDefaultConfig() {
        return "";
    }
}