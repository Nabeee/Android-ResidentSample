package com.android.residentapp;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.android.residentapp.model.Feed;

import java.util.ArrayList;
import java.util.List;

public class ResidentService extends Service {
    private View overlapView;
    private WindowManager windowManager;
    private ListView listView;
    private List<Feed> feedsList;
    private FeedListAdapter feedListAdapter;
    // Window Manager Layout Params
    private WindowManager.LayoutParams focusParams;
    private WindowManager.LayoutParams noFocusParams;
    private WindowManager.LayoutParams minimumParams;

    public ResidentService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        initMainOverLapViewParam();
        initMainOverLapView();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // display the notification
        startForeground();

        // 	When stop forced, it will not reboot.
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        removeOverlapView();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    // Run service in foreground so it is less likely to be killed by system
    private void startForeground() {
        Intent intent = new Intent(getApplicationContext(), NotificationPendingService.class);
        PendingIntent contentIntent = PendingIntent.getService(getApplicationContext(), 0, intent, 0);

        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle(getResources().getString(R.string.app_name))
                .setTicker(getResources().getString(R.string.app_name))
                .setContentText(getResources().getString(R.string.app_name))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(contentIntent)
                .setOngoing(true)
                .build();
        startForeground(9999, notification);
    }

    private void setMainOverLapView() {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        overlapView = layoutInflater.inflate(R.layout.main_overlap, null);

        windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        windowManager.addView(overlapView, noFocusParams);

        listView = (ListView) overlapView.findViewById(R.id.list_view);

        // Display Data
        ((Button) overlapView.findViewById(R.id.display_data_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetListView();
                setFeedList();
            }
        });

        // Small
        ((Button) overlapView.findViewById(R.id.small_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeOverlapView();
                setMinimumOverLapView();

                ((Button) overlapView.findViewById(R.id.minimum_button)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        removeOverlapView();
                        initMainOverLapView();
                    }
                });
            }
        });

        // Remove
        ((Button) overlapView.findViewById(R.id.remove_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopSelf();
                stopForeground(true);
            }
        });
    }

    // Whet tap the small button, will display the minimum view.
    private void setMinimumOverLapView() {
        LayoutInflater layoutInflater = LayoutInflater.from(ResidentService.this);
        overlapView = layoutInflater.inflate(R.layout.minimum_overlap, null);

        windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        windowManager.addView(overlapView, minimumParams);
    }

    // Remove the view
    private void removeOverlapView() {
        if (windowManager != null && overlapView != null) {
            windowManager.removeView(overlapView);
        }
    }

    private void setFeedList() {
        feedsList = new ArrayList<Feed>();
        int dataCount = 20;
        for (int i = 0; i < dataCount; i++) {
            Feed feed = new Feed();
            feed.title = "data" + i;
            feedsList.add(feed);
        }

        feedListAdapter = new FeedListAdapter(getApplicationContext(), feedsList);
        listView.setAdapter(feedListAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.v("tappd position", "" + position);
                resetListView();
            }
        });

        // WindowManager.LayoutParams Update
        updateMainOverLapViewLayout();
    }

    // MainView表示の一連の処理
    private void initMainOverLapView() {
        // 最前部に表示するViewの設定。
        setMainOverLapView();
    }

    // Clear the listView data
    private void resetListView() {
        if (feedsList != null && feedListAdapter != null) {
            feedsList.clear();
            feedListAdapter.notifyDataSetChanged();
        }

        // WindowManager.LayoutParams Update
        updateMainOverLapViewLayout();
    }

    private void initMainOverLapViewParam() {
        // Window Manager Layout Params
        focusParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
//                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                WindowManager.LayoutParams.FLAG_FULLSCREEN |
                        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT);
        focusParams.gravity = Gravity.TOP | Gravity.LEFT;

        noFocusParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                        WindowManager.LayoutParams.FLAG_FULLSCREEN |
                        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT);
        noFocusParams.gravity = Gravity.TOP | Gravity.LEFT;

        minimumParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                        WindowManager.LayoutParams.FLAG_FULLSCREEN |
                        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT);
        minimumParams.gravity = Gravity.TOP | Gravity.LEFT;
    }

    private void updateMainOverLapViewLayout() {
        windowManager.updateViewLayout(overlapView, (listView.getCount() > 0) ? focusParams : noFocusParams);
    }
}
