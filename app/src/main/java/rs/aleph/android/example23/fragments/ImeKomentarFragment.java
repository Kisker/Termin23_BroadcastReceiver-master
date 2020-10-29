package rs.aleph.android.example23.fragments;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import rs.aleph.android.example23.R;
import rs.aleph.android.example23.async.Service.SimpleService2;
import rs.aleph.android.example23.tools.ReviewerTools;
//Cetvrti i jedan od najvaznijih koraka, Pre toga odrediti fragment_ime_komentar.xml
public class ImeKomentarFragment extends Fragment {

    private EditText etIme, etPoruka;
    private Button button;
    private String ime = "", poruke = "";
    private int status;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            ime = savedInstanceState.getString("ime");
            poruke = savedInstanceState.getString("poruka");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        if (savedInstanceState != null) {
            savedInstanceState.putString("ime", ime);
            savedInstanceState.putString("poruka", poruke);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.v("DetailFragment", "onCreateView()");

        View view = inflater.inflate(R.layout.fragment_ime__komentar, container, false);

        etIme = (EditText) view.findViewById(R.id.editText_Ime);
        etIme.setText(ime);

        etPoruka = (EditText) view.findViewById(R.id.editText_Poruka);
        etPoruka.setText(poruke);

        button = (Button) view.findViewById(R.id.buttonSend);
        button.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("StringFormatInvalid")
            @Override
            public void onClick(View v) {
                if (status == ReviewerTools.TYPE_WIFI || status == ReviewerTools.TYPE_MOBILE) {

                    Toast.makeText(getActivity(), "Zdravo budalo", Toast.LENGTH_SHORT).show();

                    //Obvezno pozivamo intent, kako bi se nasi dopisi/poruke u obliku notifikacije pojavili na levom gornjem uglu app-a
                    Intent intent = new Intent(getActivity(), SimpleService2.class);
                    intent.putExtra("IME", etIme.getText().toString());
                    intent.putExtra("PORUKA", etPoruka.getText().toString());

                    getActivity().startService(intent);
                } else {
                    //U string.xml odredimo poruku koju zelimo da prikazemo, na primer u slucaju da telefon nije konektovan na net.
                    Toast.makeText(getActivity(), getResources().getString(R.string.nije_podesen_receiver_net), Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }
// Bez ove metode, nama notifikacija ne bi ucitala sta smo poslali.
    public void setStatus(int status) {
        this.status = status;
    }
}
