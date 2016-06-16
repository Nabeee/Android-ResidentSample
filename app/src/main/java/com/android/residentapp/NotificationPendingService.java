package com.android.residentapp;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class NotificationPendingService extends Service {
    public NotificationPendingService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Intent newIntent = new Intent(getApplicationContext(), ResidentService.class);
        stopService(newIntent);

        // http://www.atmarkit.co.jp/ait/articles/1204/20/news140.html
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
