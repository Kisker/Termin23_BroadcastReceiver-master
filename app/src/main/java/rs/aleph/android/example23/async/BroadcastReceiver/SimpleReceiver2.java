package rs.aleph.android.example23.async.BroadcastReceiver;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import rs.aleph.android.example23.R;
//Ovo nam je drugi korak, odmah posle Service2. Ovde kreiramo metode za sam prikaz notifikacije, kada posaljemo poruku.
public class SimpleReceiver2 extends BroadcastReceiver {

    private static int notificationID = 1;

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals("SEND_COMMENT")) {
//2. korak - ovde ubacujemo pozive, kako bi nam se notifikacija iscitala.
            //Key Ime i Poruka su pozvani (iz klase SimpleService2), kako bi nasa napisana aktivnost bila vidljiva.
            String name = intent.getExtras().getString("IME");
            String message = intent.getExtras().getString("PORUKA");

            prepareNotification(name, message, context);
        }
    }
//1. korak - kreiramo metodu pripreme notifikacije (izgled, oblik unetih vrednosti, itd.)
    private void prepareNotification(String name, String message, Context context) {
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
        Bitmap bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher);

        mBuilder.setSmallIcon(R.drawable.ic_action_chat);
        mBuilder.setContentTitle(name);
        mBuilder.setContentText(message);

        mBuilder.setLargeIcon(bm);
        mNotificationManager.notify(notificationID, mBuilder.build());
    }
}
