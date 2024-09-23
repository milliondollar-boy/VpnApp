package dev.dev7.example;

import android.os.Bundle;
import android.widget.ImageButton;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatActivity;
import dev.dev7.lib.v2ray.V2rayController;
import dev.dev7.lib.v2ray.utils.V2rayConstants;

public class activity2 extends AppCompatActivity {

    private ImageButton connect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        SharedPreferences sharedPreferences;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_2);


        if (savedInstanceState == null)
        {
            connect = findViewById(R.id.btn_connect_image);
        }

        sharedPreferences = getSharedPreferences("conf", MODE_PRIVATE);

        // Чтение сохранённой конфигурации
        String v2ray_config = sharedPreferences.getString("v2ray_config", "");

        connect.setOnClickListener(view ->
        {
            if (V2rayController.getConnectionState() == V2rayConstants.CONNECTION_STATES.DISCONNECTED) {
                V2rayController.startV2ray(this, "Test Server", v2ray_config, null);
                connect.setImageResource(R.drawable.new_photo);
            } else {
                V2rayController.stopV2ray(this);
                connect.setImageResource(R.drawable.photo);
            }
        });
    }
}