package dev.dev7.example;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;


public class ResetConfigActivity extends HomeActivity
{
    private EditText v2ray_config;
    private SharedPreferences sharedPreferences;

    @SuppressLint({"SetTextI18n", "UnspecifiedRegisterReceiverFlag"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.reset_activity);

        if (savedInstanceState == null) {
            v2ray_config = findViewById(R.id.v2ray_config);
        }

        // initialize shared preferences for save or reload default config
        sharedPreferences = getSharedPreferences("conf", MODE_PRIVATE);
        // reload previous config to edit text
        v2ray_config.setText(sharedPreferences.getString("v2ray_config", getDefaultConfig()));




        // Добавляем слушатель для отслеживания изменений текста
        v2ray_config.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Не нужно ничего делать
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String input = s.toString().trim();


                // Проверяем, если введенное значение соответствует шаблону ссылки
                if (input.startsWith("vless://")) {
                    // Сохраняем конфигурацию и открываем вторую активность
                    sharedPreferences.edit().putString("v2ray_config", input).apply();
                    startHomeActivity();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Не нужно ничего делать
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
            startHomeActivity();
        }
    }

    public void startHomeActivity(){
        Intent intent = new Intent(ResetConfigActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    public static String getDefaultConfig() {
        return "";
    }
}