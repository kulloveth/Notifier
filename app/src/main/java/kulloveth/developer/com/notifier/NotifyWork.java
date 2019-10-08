package kulloveth.developer.com.notifier;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import static android.graphics.Color.RED;
import static android.media.AudioAttributes.CONTENT_TYPE_SONIFICATION;
import static android.media.AudioAttributes.USAGE_NOTIFICATION_RINGTONE;
import static android.media.RingtoneManager.TYPE_NOTIFICATION;
import static android.media.RingtoneManager.getDefaultUri;
import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.O;
import static androidx.core.app.NotificationManagerCompat.IMPORTANCE_DEFAULT;
import static androidx.core.app.NotificationManagerCompat.IMPORTANCE_MAX;

public class NotifyWork extends Worker {
    public static final String NOTIFICATION_ID = "notification_id";
    public static final String NOTIFICATION_NAME = "Remember";
    public static final String NOTIFICATION_CHANNEL = "Reminder_Channel";
    public static final String NOTIFICATION_WORK = "Notification_Work";

    public NotifyWork(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        int id = getInputData().getInt(NOTIFICATION_ID, 0);
        sendNotification(id);
        return Result.success();
    }

    private void sendNotification(int id) {
        NotificationManager notificationManager = (NotificationManager) getApplicationContext()
                .getSystemService(Context.NOTIFICATION_SERVICE);
        Bitmap bitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.ic_done_white_24dp);
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra(NOTIFICATION_ID, id);
        String titleNotification = "Reminder";
        String subtitleNotification = "Time To WakeUp";
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder notification = new NotificationCompat.Builder(getApplicationContext(), NOTIFICATION_CHANNEL)
                .setSmallIcon(R.drawable.ic_done_white_24dp).setContentTitle(titleNotification)
                .setContentText(subtitleNotification).setDefaults(IMPORTANCE_DEFAULT).setSound(getDefaultUri(TYPE_NOTIFICATION))
                .setContentIntent(pendingIntent).setPriority(NotificationCompat.PRIORITY_DEFAULT).setAutoCancel(true);

        notification.setPriority(IMPORTANCE_MAX);
        notificationManager.notify(id, notification.build());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notification.setChannelId(NOTIFICATION_CHANNEL);

            Uri ringtoneManager = getDefaultUri(TYPE_NOTIFICATION);
            AudioAttributes audioAttributes = new AudioAttributes.Builder().setUsage(USAGE_NOTIFICATION_RINGTONE)
                    .setContentType(CONTENT_TYPE_SONIFICATION).build();


            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL, NOTIFICATION_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            channel.enableLights(true);
            channel.setLightColor(RED);
            channel.enableVibration(true);
            channel.setSound(ringtoneManager, audioAttributes);
          //  NotificationManager notificationManager2 = getApplicationContext().getSystemService(NotificationManager.class);

                notificationManager.createNotificationChannel(channel);

        }


    }
}
