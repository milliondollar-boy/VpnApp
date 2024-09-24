package dev.dev7.example;

import android.net.Uri;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatActivity;
import dev.dev7.lib.v2ray.V2rayController;
import dev.dev7.lib.v2ray.utils.V2rayConstants;

public class activity2 extends AppCompatActivity {

    private ImageButton connect;
    private Button reset, tg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        SharedPreferences sharedPreferences;
        sharedPreferences = getSharedPreferences("conf", MODE_PRIVATE);



        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_2);


        if (savedInstanceState == null)
        {
            connect = findViewById(R.id.btn_connect_image);
            reset = findViewById(R.id.button6);
            tg = findViewById(R.id.button7);
        }

        reset.setOnClickListener(v -> resetTheConfiguration());

        tg.setOnClickListener(v -> openTelegramBot());

        connect.setOnClickListener(view ->
        {
            // Чтение сохранённой конфигурации
            String v2ray_config = sharedPreferences.getString("v2ray_config", "");
            if (V2rayController.getConnectionState() == V2rayConstants.CONNECTION_STATES.DISCONNECTED) {
                V2rayController.startV2ray(this, "Test Server", v2ray_config, null);
                connect.setImageResource(R.drawable.new_photo);
            } else {
                V2rayController.stopV2ray(this);
                connect.setImageResource(R.drawable.photo);
            }
        });
    }

    public void openTelegramBot(){
        String botUsername = "My First Bot"; // Замените на имя вашего бота
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("https://t.me/crackeppp_bot" + botUsername));
        startActivity(intent);
    }

    public void resetTheConfiguration(){
        // v2ray_config.getDefaultConfig();
        Intent intent = new Intent(activity2.this, activity1.class);
        startActivity(intent);
    }
}