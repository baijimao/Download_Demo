package com.download.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.download.entities.FileInfo;
import com.download.services.DownloadService;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView mTvFileName = null;
    private ProgressBar mPbProgress = null;
    private Button mBtStop = null;
    private Button mBtStart = null;
    private FileInfo fileInfo;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE" };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initialize component
        mTvFileName = findViewById(R.id.tvFileName);
        mPbProgress = findViewById(R.id.pbProgress);
        mBtStop = findViewById(R.id.btStop);
        mBtStart = findViewById(R.id.btStart);

        mTvFileName.setText("imooc");
        mPbProgress.setMax(100);

        requestPermission();

        fileInfo = new FileInfo(0, "https://file.mukewang.com/imoocweb/webroot/mobile/imooc7.0.910102001android.apk", "imooc7.0.910102001android.apk", 0, 0);

        //add event listening
        mBtStop.setOnClickListener(this);
        mBtStart.setOnClickListener(this);

        // register the BroadcastReceiver
        IntentFilter filter = new IntentFilter();
        filter.addAction(DownloadService.ACTION_UPDATE);
        registerReceiver(mReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    /**
     * BroadcastReceiver used to update the UI
     */
    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (DownloadService.ACTION_UPDATE.equals(intent.getAction())) {
                int finished = intent.getIntExtra("finished", 0);
                mPbProgress.setProgress(finished);
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btStart:
                Intent startIntent = new Intent(this, DownloadService.class);
                startIntent.setAction(DownloadService.ACTION_START);
                startIntent.putExtra("fileInfo", fileInfo);
                startService(startIntent);
                break;
            case R.id.btStop:
                Intent stopIntent = new Intent(this, DownloadService.class);
                stopIntent.setAction(DownloadService.ACTION_STOP);
                stopIntent.putExtra("fileInfo", fileInfo);
                startService(stopIntent);
                break;
            default:
                break;
        }

    }

    /**
     *
     */
    private void requestPermission() {
        try {
            // check if there is write permission
            int permission = ActivityCompat.checkSelfPermission(this,
                    "android.permission.WRITE_EXTERNAL_STORAGE");
            if (permission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
