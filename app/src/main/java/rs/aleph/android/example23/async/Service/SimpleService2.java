package rs.aleph.android.example23.async.Service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import rs.aleph.android.example23.async.SyncTask.SimpleSyncTask2;
//Prvi korak i jedan od najvaznijih. Ovde se pozivamo na Service, pa tek onda idemo na Receiver
public class SimpleService2 extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        new SimpleSyncTask2(intent.getStringExtra("IME"), intent.getStringExtra("PORUKA"), getApplicationContext()).execute(1);

        stopSelf();
        return START_NOT_STICKY;
    }
}
