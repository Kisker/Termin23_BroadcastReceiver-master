package rs.aleph.android.example23.activities;

import android.app.AlarmManager;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;

import rs.aleph.android.example23.R;
import rs.aleph.android.example23.adapters.DrawerListAdapter;
import rs.aleph.android.example23.async.BroadcastReceiver.SimpleReceiver;
import rs.aleph.android.example23.async.BroadcastReceiver.SimpleReceiver2;
import rs.aleph.android.example23.async.Service.SimpleService;
import rs.aleph.android.example23.dialogs.AboutDialog;
import rs.aleph.android.example23.fragments.DetailFragment;
import rs.aleph.android.example23.fragments.ImeKomentarFragment;
import rs.aleph.android.example23.fragments.ListFragment;
import rs.aleph.android.example23.fragments.ListFragment.OnProductSelectedListener;
import rs.aleph.android.example23.model.NavigationItem;
import rs.aleph.android.example23.tools.ReviewerTools;

public class MainActivity extends AppCompatActivity implements OnProductSelectedListener {

    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItemFromDrawer(position);
        }
    }

    private DrawerLayout drawerLayout;
    private ListView drawerList;
    private ActionBarDrawerToggle drawerToggle;
    private RelativeLayout drawerPane;
    private CharSequence drawerTitle;
    private CharSequence title;

    private ArrayList<NavigationItem> navigationItems = new ArrayList<NavigationItem>();

    private AlertDialog dialog;

    private boolean landscapeMode = false;
    private boolean listShown = false;
    private boolean detailShown = false;

    private int productId = 0;

    private SimpleReceiver sync;
    private SimpleReceiver2 komentar;
    private PendingIntent pendingIntent;
    private AlarmManager manager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);

        // Draws navigation items
        navigationItems.add(new NavigationItem(getString(R.string.drawer_home), getString(R.string.drawer_home_long), R.drawable.ic_action_product));
        navigationItems.add(new NavigationItem(getString(R.string.drawer_settings), getString(R.string.drawer_Settings_long), R.drawable.ic_action_settings));
        navigationItems.add(new NavigationItem(getString(R.string.drawer_about), getString(R.string.drawer_about_long), R.drawable.ic_action_about));

        title = drawerTitle = getTitle();
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        drawerList = (ListView) findViewById(R.id.navList);

        // Populate the Navigtion Drawer with options
        drawerPane = (RelativeLayout) findViewById(R.id.drawerPane);
        DrawerListAdapter adapter = new DrawerListAdapter(this, navigationItems);

        // set a custom shadow that overlays the main content when the drawer opens
        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        drawerList.setOnItemClickListener(new DrawerItemClickListener());
        drawerList.setAdapter(adapter);

        // Enable ActionBar app icon to behave as action to toggle nav drawer
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final android.support.v7.app.ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_drawer);
            actionBar.setHomeButtonEnabled(true);
            actionBar.show();
        }

        drawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                drawerLayout,         /* DrawerLayout object */
                toolbar,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        ) {
            public void onDrawerClosed(View view) {
                getSupportActionBar().setTitle(title);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setTitle(drawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        if (savedInstanceState == null) {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ListFragment listFragment = new ListFragment();
            ft.add(R.id.displayList, listFragment, "List_Fragment");
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.commit();
            selectItemFromDrawer(0);
        }

        if (findViewById(R.id.displayDetail) != null) {
            landscapeMode = true;
            getFragmentManager().popBackStack();

            DetailFragment detailFragment = (DetailFragment) getFragmentManager().findFragmentById(R.id.displayDetail);
            if (detailFragment == null) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                detailFragment = new DetailFragment();
                ft.replace(R.id.displayDetail, detailFragment, "Detail_Fragment1");
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.commit();
                detailShown = true;
            }
        }

        listShown = true;
        detailShown = false;
        productId = 0;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_item_detail, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Metoda koja je izmenjena da reflektuje rad sa Asinhronim zadacima
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int status = ReviewerTools.getConnectivityStatus(getApplicationContext());

        switch (item.getItemId()) {
            case R.id.action_refresh:
                Toast.makeText(MainActivity.this, "Sinhronizacija dugo traje koristiti servis. dobro :)", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(MainActivity.this, SimpleService.class);
                intent.putExtra("STATUS", status);

                startService(intent);
                break;
            case R.id.action_add:
//Kada odredimo sve prethodne klase, na kraju se vracamo u Main i odredjujemo metode pozivanja
                ImeKomentarFragment detailFragment = new ImeKomentarFragment();
                //pozivom na ovu metodu (iz klase ImeKomentar), nama notifikacija uopste ne bi bila vidljiva
                detailFragment.setStatus(status);
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.displayList, detailFragment, "ImeKomentarFragment");
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.addToBackStack("ImeKomentarFragment");
                ft.commit();
                listShown = false;
                detailShown = false;

                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setTitle(CharSequence title) {
        getSupportActionBar().setTitle(title);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        drawerToggle.onConfigurationChanged(newConfig);
    }

    private void selectItemFromDrawer(int position) {
        if (position == 0) {

        } else if (position == 1) {
            Intent settings = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(settings);
        } else if (position == 2) {
            if (dialog == null) {
                dialog = new AboutDialog(MainActivity.this).prepareDialog();
            } else {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }

            dialog.show();
        }

        drawerList.setItemChecked(position, true);
        setTitle(navigationItems.get(position).getTitle());
        drawerLayout.closeDrawer(drawerPane);
    }

    @Override
    public void onProductSelected(int id) {

        productId = id;

        if (landscapeMode) {
            DetailFragment detailFragment = (DetailFragment) getFragmentManager().findFragmentById(R.id.displayDetail);
            detailFragment.updateProduct(id);
        } else {
            DetailFragment detailFragment = new DetailFragment();
            detailFragment.setProduct(id);
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.displayList, detailFragment, "Detail_Fragment");
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.addToBackStack("Detail_Fragment");
            ft.commit();
            listShown = false;
            detailShown = true;
        }
    }

    @Override
    public void onBackPressed() {

        if (landscapeMode) {
            finish();
        } else if (listShown == true) {
            finish();
        } else if (detailShown == true) {
            getFragmentManager().popBackStack();
            ListFragment listFragment = new ListFragment();
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.displayList, listFragment, "List_Fragment");
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.commit();
            listShown = true;
            detailShown = false;
        }

    }

    /**
     * Prilikom startovanja aplikacije potrebno je registrovati
     * elemente sa kojimaa radimo. Kada aplikaciaj nije aktivna
     * te elemente moramo da uklnimo.
     */
    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();

        setUpReceiver();
        //treci korak, kako bi ta metoda bila pozvana i vidljiva nasa poslata notifikacija
        setUpReceiver2();
        setUpManager();
    }

    /**
     * Registrujemo nas BroadcastReceiver i dodajemo mu 'filter'.
     * Filter koristimo prilikom prispeca poruka. Jedan receiver
     * moze da reaguje na vise tipova poruka. One nam kazu
     * sta tacno treba da se desi kada poruka odredjenog tipa (filera)
     * stigne.
     */
    private void setUpReceiver() {
        sync = new SimpleReceiver();

        //registracija jednog filtera
        IntentFilter filter = new IntentFilter();
        filter.addAction("SYNC_DATA");
        registerReceiver(sync, filter);
    }
// drugi korak u Main-u. Odredjujemo metodu setUpReceiver2
    private void setUpReceiver2() {
        komentar = new SimpleReceiver2();

        //registracija jednog filtera
        IntentFilter filter = new IntentFilter();
        filter.addAction("SEND_COMMENT");
        registerReceiver(komentar, filter);
    }

    /**
     * Kada zelimo da se odredjeni zadaci ponavljaju, potrebno je
     * da registrujemo manager koji ce motriti kada je vreme da se
     * taj posao obavi. Kada registruje vreme za pokretanje zadatka
     * on emituje Intent operativnom sistemu sta je potrebno da se
     * desi.
     * Takodje potrebno je da definisemo ponavljanja tj. na koliko
     * vremena zelimo da se posao ponovo obavi
     */
    private void setUpManager() {
        //Intent koji ce manager emitovati operativnom sistemu
        //Startujemo jedan servis
        Intent intent = new Intent(this, SimpleService.class);
        int status = ReviewerTools.getConnectivityStatus(getApplicationContext());
        intent.putExtra("STATUS", status);

        //definisemo manager i kazemo kada je potrebno da se ponavlja
        /*
        parametri:
            context: this - u komkontekstu zelimo da se intent izvrsava
            requestCode: 0 - nas jedinstev kod
            intent: intent koji zelimo da se izvrsi kada dodje vreme
            flags: 0 - flag koji opisuje sta da se radi sa intent-om kada se poziv desi
            detaljnije:https://developer.android.com/reference/android/app/PendingIntent.html#getService(android.content.Context, int, android.content.Intent, int)
        */
        pendingIntent = PendingIntent.getService(this, 0, intent, 0);

        //koristicemo sistemski AlarmManager pa je potrebno da dobijemo
        //njegovu instancu.
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        //definisemo kako ce alarm manager da reaguje.
        //prvi parametar kaze da ce reagovati u rezimu ponavljanja
        //drugi parametar od kada krece da meri vreme
        //treci parametar na koliko jedinica vremena ce ragovati (minimalno 1min)
        //poslednji parametar nam govori koju akciju treba da preduzmemo kada se alarm iskljuci
        manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), ReviewerTools.calculateTimeTillNextSync(1), pendingIntent);

        Toast.makeText(this, "Alarm Set", Toast.LENGTH_SHORT).show();
    }

    /**
     * Moramo voditi racuna o komponentama koje je potrebno osloboditi
     * kada aplikacija nije aktivna.
     */
    @Override
    protected void onPause() {
        //ako je manager kreiran potrebno je da ga uklonimo
        if (manager != null) {
            manager.cancel(pendingIntent);
            manager = null;
        }

        //osloboditi resurse koje koristi receiver
        if (sync != null) {
            unregisterReceiver(sync);
            sync = null;
        }

        super.onPause();

    }

}


