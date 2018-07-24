package com.test.testh264sender.ui;

import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.TextView;

import com.test.testh264sender.R;
import com.test.testh264sender.Utils;

/**
 * Created by xu.wang
 * Date on  2018/5/28 09:41:00.
 *
 * @Desc
 */

public class StartActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView mWifiInfoView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        initialView();

        mWifiInfoView = findViewById(R.id.tv_wifi_info);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getWifiInfo();
    }

    private void getWifiInfo() {
        if (Utils.isWifiConnected(this)) {
            WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
//            Log.d("wifiInfo", wifiInfo.toString());
//            Log.d("SSID",wifiInfo.getSSID());
            mWifiInfoView.setText("当前Wifi：" + wifiInfo.getSSID());
        } else {
            mWifiInfoView.setText("当前未连接wifi");
        }
    }

    private void initialView() {
        AppCompatButton btn_living = findViewById(R.id.btn_test_living);
        AppCompatButton btn_record = findViewById(R.id.btn_test_record);
        btn_living.setOnClickListener(this);
        btn_record.setOnClickListener(this);
        findViewById(R.id.btn_wifi_setting)
                .setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_test_living:
                Intent livingIntent = new Intent(this, LaifengLivingActivity.class);
                startActivity(livingIntent);
                break;
            case R.id.btn_test_record:
                Intent intent = new Intent(this, LaifengScreenRecordActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_wifi_setting:
                startActivity(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS));
                break;
        }
    }
}
