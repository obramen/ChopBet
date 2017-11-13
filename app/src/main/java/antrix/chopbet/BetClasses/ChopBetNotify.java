package antrix.chopbet.BetClasses;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.widget.RemoteViews;

import antrix.chopbet.Activities.activityChopBet;
import antrix.chopbet.R;

public class ChopBetNotify extends BaseActivity{


    private NotificationCompat.Builder builder;
    private NotificationManager notificationManager;
    private int notification_id;
    private RemoteViews remoteViews;
    private Context context;
    String SourceActivity;
    String ExtraInfo;
    String NotifyTitle;
    String NotifyMessage;
    View button;


/*
    public void ChopBetNotify (String SourceActivity, String NotifyTitle, String NotifyMessage, String ExtraInfo){
        context = this;
        this.SourceActivity = SourceActivity;
        this.NotifyTitle = NotifyTitle;
        this.NotifyMessage = NotifyMessage;
        this.ExtraInfo = ExtraInfo;





        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        remoteViews = new RemoteViews(getPackageName(), R.layout.custom_notification);

        remoteViews.setImageViewResource(R.id.notificationIcon, R.mipmap.ic_launcher);
        remoteViews.setTextViewText(R.id.notificationTitle, NotifyTitle);
        remoteViews.setTextViewText(R.id.notificationMessage, NotifyMessage);


        notification_id = (int) System.currentTimeMillis();
        Intent openNotification = new Intent("Open_Notification");
        openNotification.putExtra("id", notification_id);
        openNotification.putExtra("SourceActivity", SourceActivity);
        openNotification.putExtra("ExtraInfo", ExtraInfo);


        PendingIntent p_openNotification = PendingIntent.getBroadcast(context, 123, openNotification, 0);
        remoteViews.setOnClickPendingIntent(R.id.notificationButton, p_openNotification);




        Intent notification_intent = new Intent(context, activityChopBet.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notification_intent, 0);

        builder = new NotificationCompat.Builder(context);
        builder.setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true)
                .setCustomBigContentView(remoteViews)
                .setContentIntent(pendingIntent);

        notificationManager.notify(notification_id, builder.build());





    }

  */


public ChopBetNotify(){

    }


    public void notifyClick(View button){

        this.button = button;

        button.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent notification_intent = new Intent(context, activityChopBet.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notification_intent, 0);

                builder = new NotificationCompat.Builder(context);
                builder.setSmallIcon(R.mipmap.ic_launcher)
                        .setAutoCancel(true)
                        .setCustomBigContentView(remoteViews)
                        .setContentIntent(pendingIntent);

                notificationManager.notify(notification_id, builder.build());
            }
        });
    }





}
