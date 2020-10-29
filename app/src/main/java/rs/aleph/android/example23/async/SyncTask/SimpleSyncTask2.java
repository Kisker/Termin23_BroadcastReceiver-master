package rs.aleph.android.example23.async.SyncTask;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
// Kada smo odredili klase Service i Receiver, prelazino na treci korak. AsyncTask klasa nam pomaze da odredimo
//proces unosenja i ispis vrednosti. Ona zdruzuje na neki nacin Service i Receive i predstavlja jednostavno izvrsavanje
//zadataka u pozadini (asinhroni zadaci)
public class SimpleSyncTask2 extends AsyncTask<Integer, Void, Void> {

    Context context;
    String name, comment;

    public SimpleSyncTask2(String name, String comment, Context context) {
        this.name = name;
        this.comment = comment;
        this.context = context;

    }

    @Override
    protected void onPostExecute(Void aVoid) {
        Intent ints = new Intent("SEND_COMMENT");
        ints.putExtra("IME", name);
        ints.putExtra("PORUKA", comment);
        context.sendBroadcast(ints);
    }


    @Override
    protected Void doInBackground(Integer... integers) {
        int sec = integers[0];

        for (int i = 0; i < sec; i++) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
