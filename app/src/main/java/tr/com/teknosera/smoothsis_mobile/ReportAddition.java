package tr.com.teknosera.smoothsis_mobile;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class ReportAddition extends AppCompatActivity {

    private EditText txtDate = null, txtUretilen, txtBeslenen, txtFireMiktari, txtIskartaMiktari, txtIskartaNedeni, txtFireNedeni, txtAciklama;
    private Spinner vardiya;
    private Toolbar toolbar;
    private Button btndate, btnOperatorSec;
    private TextView makineAdi, islemAdi, viewSiparisKodu, viewUretilenMiktar, viewPLMIKTAR, viewStokAdi;
    private ProgressDialog mProgress;


    SharedPreferences sharedUser;
    Connection connectdb;


    private int year = 0;
    private int month = 0;
    private int day = 0;

    static final int DATE_DIALOG_ID = 999;

    final ArrayList<String> tranformElement = new ArrayList<String>();
    final HashMap<String, Boolean> listOperatorChecked = new HashMap<>();
    final HashMap<Integer, String> operators = new HashMap<Integer, String>();
    final List<Integer> endOfSelectedValues = new ArrayList<>();

    final ArrayList<String> listviewView = new ArrayList<String>();

    boolean rapor_state = false;
    boolean operator_state = false;
    String username = null;
    int kullaniciId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_addition);

        ConnectionClass connect = new ConnectionClass();

        connectdb = connect.ConnectionHelper();

        toolbar = (Toolbar) findViewById(R.id.additionToolbar);
        txtDate = (EditText) findViewById(R.id.txtDate);
        txtUretilen = (EditText) findViewById(R.id.txtUretilen);
        txtBeslenen = (EditText) findViewById(R.id.txtBeslenen);
        txtFireMiktari = (EditText) findViewById(R.id.txtFireMiktari);
        txtFireNedeni = (EditText) findViewById(R.id.txtFireNedeni);
        txtIskartaMiktari = (EditText) findViewById(R.id.txtIskartaMiktari);
        txtIskartaNedeni = (EditText) findViewById(R.id.txtIskartaNedeni);

        txtAciklama = (EditText) findViewById(R.id.txtAciklama);

        makineAdi = (TextView) findViewById(R.id.makineAdi);
        islemAdi = (TextView) findViewById(R.id.islemAdi);
        viewSiparisKodu = (TextView) findViewById(R.id.viewSiparisKodu);
        viewUretilenMiktar = (TextView) findViewById(R.id.viewUretilenMiktar);
        viewPLMIKTAR = (TextView) findViewById(R.id.viewPLMIKTAR);
        viewStokAdi = (TextView) findViewById(R.id.viewStokAdi);

        vardiya = (Spinner) findViewById(R.id.vardiya);
        btndate = (Button) findViewById(R.id.dateBtn);
        btnOperatorSec = (Button) findViewById(R.id.btnOperator);

        // MAKİNE DETAYLARI
        final HashMap<String, String> getInfos = (HashMap<String, String>) getIntent().getSerializableExtra("machineDetail");

        sharedUser = this.getSharedPreferences("UserInfo", MODE_PRIVATE);

        username = sharedUser.getString("UserId", "none");
        kullaniciId = sharedUser.getInt("kulInckey", -1);

        toolbar.setTitle("VARDIYA RAPORU GİRİŞİ [ MAKİNE: " + getInfos.get("MAKINE") + " - İŞLEM: " + getInfos.get("ISLEM") + " ]");

        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }


        /* ----------------------------------------------------------------------- */

        makineAdi.setText(getInfos.get("MAKINE"));
        islemAdi.setText(getInfos.get("ISLEM"));
        viewSiparisKodu.setText(getInfos.get("SIPARIS_KOD"));
        viewStokAdi.setText(getInfos.get("STOK_ADI"));

        try {

            String getUretimDatas = "SELECT CONCAT(PLAN_URET_MIK, ' ', BIRIM) AS PLANLANAN, CONCAT(ISNULL(SUM(URETILEN_MIK),0), ' ', BIRIM) AS URETILEN_MIK FROM URETIM UR LEFT JOIN RAPOR RP ON RP.UR_INCKEY = UR.UR_INCKEY WHERE UR.UR_INCKEY = " + getInfos.get("URET_INCKEY") + "GROUP BY UR.PLAN_URET_MIK, UR.BIRIM";
            PreparedStatement ps = connectdb.prepareStatement(getUretimDatas);
            ResultSet resultOfInfot = ps.executeQuery();

            if (resultOfInfot.next()) {
                viewPLMIKTAR.setText(resultOfInfot.getString("PLANLANAN"));
                viewUretilenMiktar.setText(String.valueOf(resultOfInfot.getInt("URETILEN_MIK")));
            }


        } catch (SQLException ex) {
            Log.e("VERİ HATASI", ex.getMessage());
        }

        /* ---------------------------------------------------------------------- */

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH) + 1;
        day = c.get(Calendar.DAY_OF_MONTH);
        txtDate.setText(new StringBuilder()
                .append(padding_str(day))
                .append("-").append(padding_str(month))
                .append("-").append(padding_str(year))
        );

        btndate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar mcurrentTime = Calendar.getInstance();
                int year = mcurrentTime.get(Calendar.YEAR);
                int month = mcurrentTime.get(Calendar.MONTH);
                int day = mcurrentTime.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePicker;
                datePicker = new DatePickerDialog(ReportAddition.this, new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        txtDate.setText(padding_str(dayOfMonth) + "-" + padding_str(monthOfYear + 1) + "-" + year);

                    }
                }, year, month, day);
                datePicker.setTitle("Tarih Seçiniz");
                datePicker.setButton(DatePickerDialog.BUTTON_POSITIVE, "Ayarla", datePicker);
                datePicker.setButton(DatePickerDialog.BUTTON_NEGATIVE, "İptal", datePicker);

                datePicker.show();
            }
        });


        /* ---------------------------------------------------------------------------------------------------------- */


        btnOperatorSec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (endOfSelectedValues.size() > 0)
                    endOfSelectedValues.clear();

                final AlertDialog.Builder builder = new AlertDialog.Builder(ReportAddition.this);


                try {
                    String query = "SELECT * FROM OPERATOR WHERE OP_DURUMU=1";
                    PreparedStatement ps = connectdb.prepareStatement(query);
                    ResultSet resultOfOperators = ps.executeQuery();

                    while (resultOfOperators.next()) {

                        operators.put(resultOfOperators.getInt("OP_INCKEY"), resultOfOperators.getString("ADSOYAD"));
                        listOperatorChecked.put(resultOfOperators.getString("ADSOYAD"), false);
                        tranformElement.add(resultOfOperators.getString("ADSOYAD"));

                    }

                } catch (Exception ex) {
                    Log.e("Bağlantı Hatası !", ex.getMessage());
                }

                String[] listOperator = new String[tranformElement.size()];
                boolean[] listOperator_checked = new boolean[tranformElement.size()];
                listOperator = tranformElement.toArray(listOperator);

                for (int k = 0; k < listOperatorChecked.size(); k++) {
                    listOperatorChecked.put(tranformElement.get(k), false);
                }


                builder.setMultiChoiceItems(listOperator, listOperator_checked, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {

                        listOperatorChecked.put(tranformElement.get(which), isChecked);

                    }
                });

                builder.setCancelable(false);

                builder.setTitle("OPERATÖR SEÇİNİZ");

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        ListView secilenOperatorler = (ListView) findViewById(R.id.secilenOperatorler);
                        ArrayAdapter<String> itemsAdapter = null;

                        if (listviewView.size() > 0)
                            listviewView.clear();


                        for (String value : operators.values()) {

                            if (listOperatorChecked.get(value)) {
                                endOfSelectedValues.add(getKey(operators, value));
                                listviewView.add(value);

                            }

                        }

                        itemsAdapter = new ArrayAdapter<String>(getBaseContext(), R.layout.listview_item, listviewView);
                        secilenOperatorler.setAdapter(itemsAdapter);
                        itemsAdapter.notifyDataSetChanged();
                        setListViewHeightBasedOnChildren(secilenOperatorler);

                    }
                });


                builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

                AlertDialog dialog = builder.create();

                dialog.show();

            }
        });

        /* ---------------------------------------------------------------------------------------------------------- */

        Button btnKaydet = (Button) findViewById(R.id.btnKaydet);

        btnKaydet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String raporTarih = txtDate.getText().toString().trim();

                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
                java.util.Date reportDate = null;
                try {
                    reportDate = dateFormat.parse(raporTarih);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                String raporVardiya = vardiya.getSelectedItem().toString().trim();
                double raporBeslenen = (txtBeslenen.getText().toString().trim() != null && txtBeslenen.getText().toString().trim().length() != 0) ? Double.valueOf(txtBeslenen.getText().toString().trim()) : 0;
                double raporUretilen = (txtUretilen.getText().toString().trim() != null && txtUretilen.getText().toString().trim().length() != 0) ? Double.valueOf(txtUretilen.getText().toString().trim()) : 0;
                double raporFireMiktari = (txtFireMiktari.getText().toString().trim() != null && txtFireMiktari.getText().toString().trim().length() != 0) ? Double.valueOf(txtFireMiktari.getText().toString().trim()) : 0;
                String raporFireNedeni = txtFireNedeni.getText().toString().trim();
                double raporIskartaMiktari = (txtIskartaMiktari.getText().toString().trim() != null && txtIskartaMiktari.getText().toString().trim().length() != 0) ? Double.valueOf(txtIskartaMiktari.getText().toString().trim()) : 0;
                String raporIskartaNedeni = txtIskartaNedeni.getText().toString().trim();
                String raporAciklama = txtAciklama.getText().toString().trim();

                if (raporUretilen > 0 && raporBeslenen > 0) {

                    mProgress = ProgressDialog.show(ReportAddition.this, "Lütfen Bekleyin", "Rapor Kaydediliyor...", true, false);

                    try {
                        PreparedStatement pst = connectdb.prepareStatement("INSERT INTO RAPOR(UR_INCKEY, RAPOR_TARIH, RAPOR_VARDIYA, BESLENEN_MIK, URETILEN_MIK, FIRE_MIK, FIRE_NEDENI, ISKARTA_MIK, ISKARTA_NEDENI, KAYIT_YAPAN_KUL, ACIKLAMA) VALUES (?,?,?,?,?,?,?,?,?,?,?)");
                        pst.setInt(1, Integer.parseInt(getInfos.get("URET_INCKEY")));
                        pst.setDate(2, new java.sql.Date(reportDate.getTime()));
                        pst.setString(3, raporVardiya);
                        pst.setDouble(4, raporBeslenen);
                        pst.setDouble(5, raporUretilen);
                        pst.setDouble(6, raporFireMiktari);
                        pst.setString(7, raporFireNedeni);
                        pst.setDouble(8, raporIskartaMiktari);
                        pst.setString(9, raporIskartaNedeni);
                        pst.setInt(10, kullaniciId);
                        pst.setString(11, raporAciklama);
                        pst.executeUpdate();

                        rapor_state = true;

                        String query = "SELECT MAX(RAPOR_INCKEY) AS RAPOR_INCKEY FROM RAPOR";
                        PreparedStatement ps = connectdb.prepareStatement(query);
                        ResultSet resultOfRapor = ps.executeQuery();

                        int rapor_id = 0;

                        if (resultOfRapor.next()) {
                            rapor_id = Integer.valueOf(resultOfRapor.getString("RAPOR_INCKEY"));

                            if (rapor_id > 0 && endOfSelectedValues.size() > 0) {

                                for (int i = 0; i < endOfSelectedValues.size(); i++) {

                                    PreparedStatement preState = connectdb.prepareStatement("INSERT INTO OPERATOR_TO_RAPOR(OP_INCKEY,RAPOR_INCKEY) VALUES (?,?)");
                                    preState.setInt(1, endOfSelectedValues.get(i));
                                    preState.setInt(2, rapor_id);
                                    preState.executeUpdate();
                                }

                                operator_state = true;

                            }

                        }

                    } catch (Exception ex) {
                        mProgress.dismiss();
                        Toast.makeText(ReportAddition.this, ex.toString(), Toast.LENGTH_LONG).show();
                    }


                    if (rapor_state) {
                        mProgress.dismiss();
                        Toast.makeText(ReportAddition.this, "Rapor Başarıyla Kaydedildi", Toast.LENGTH_LONG).show();
                        //new SendMail().execute("");
                    } else {
                        Toast.makeText(ReportAddition.this, "HATA! Rapor Kaydedilemedi", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(ReportAddition.this, "ÜRETİLEN VE BESLENEN MİKTAR 0'DAN BÜYÜK OLMALI", Toast.LENGTH_LONG).show();
                }


            }
        });


    }

    private class SendMail extends AsyncTask<String, Integer, Void> {

        String raporVardiya = vardiya.getSelectedItem().toString().trim();
        String raporTarih = txtDate.getText().toString().trim();
        double raporBeslenen = (txtBeslenen.getText().toString().trim() != null && txtBeslenen.getText().toString().trim().length() != 0) ? Double.valueOf(txtBeslenen.getText().toString().trim()) : 0;
        double raporUretilen = (txtUretilen.getText().toString().trim() != null && txtUretilen.getText().toString().trim().length() != 0) ? Double.valueOf(txtUretilen.getText().toString().trim()) : 0;
        double raporFireMiktari = (txtFireMiktari.getText().toString().trim() != null && txtFireMiktari.getText().toString().trim().length() != 0) ? Double.valueOf(txtFireMiktari.getText().toString().trim()) : 0;
        String raporFireNedeni = txtFireNedeni.getText().toString().trim();
        double raporIskartaMiktari = (txtIskartaMiktari.getText().toString().trim() != null && txtIskartaMiktari.getText().toString().trim().length() != 0) ? Double.valueOf(txtIskartaMiktari.getText().toString().trim()) : 0;
        String raporIskartaNedeni = txtIskartaNedeni.getText().toString().trim();
        String raporAciklama = txtAciklama.getText().toString().trim();

        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(ReportAddition.this, "Lütfen Bekleyin", "Rapor Email'i Gönderiliyor...", true, false);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();

        }

        protected Void doInBackground(String... params) {

            List<String> toList = new ArrayList<>();

            String host = "";
            String port = "";
            String fromAddress = "";
            String fromPassword = "";
            String fromDisplayName = "";
            String toEmails = "";

            try {
                String query = "SELECT TOP 1 * FROM EMAIL";
                PreparedStatement ps = connectdb.prepareStatement(query);
                ResultSet resultOfEmailSetting = ps.executeQuery();

                while (resultOfEmailSetting.next()) {
                    host = resultOfEmailSetting.getString("HOST");
                    port = resultOfEmailSetting.getString("PORT");
                    fromAddress = resultOfEmailSetting.getString("FROM_ADDRESS");
                    fromPassword = resultOfEmailSetting.getString("FROM_PASSWORD");
                    fromDisplayName = resultOfEmailSetting.getString("FROM_DISPLAYNAME");
                    toEmails = resultOfEmailSetting.getString("TO_EMAIL");

                }
            } catch (Exception ex) {
                Log.e("Bağlantı Hatası !", ex.getMessage());
            }

            Mail m = new Mail(host, port, fromAddress, fromPassword);

            String mailOperator = "";
            if (listviewView.size() > 0) {

                for (int i = 0; i < listviewView.size(); i++) {

                    mailOperator += listviewView.get(i) + ", ";

                }

                mailOperator = mailOperator.substring(0, mailOperator.length() - 2);

            }

            String[] emailList = toEmails.split(";");

            m.setTo(emailList);

            DateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy, dddd", new Locale("tr"));
            Date date = new Date();

            String EmailSubject = dateFormat.format(date) + " TARİHLİ ÜRETİM RAPORU";
            String EmailBody = "BESLENEN MİKTAR: " + raporBeslenen + "<br>" +
                    "ÜRETİLEN MİKTAR: " + raporUretilen + "<br>" +
                    "FİRE MİKTARI: " + raporFireMiktari + "<br>" +
                    "FİRE NEDENİ: " + raporFireNedeni + "<br>" +
                    "ISKARTA MİKTARI: " + raporIskartaMiktari + "<br>" +
                    "ISKARTA NEDENİ: " + raporIskartaNedeni + "<br>" +
                    "KAYIT YAPAN KULLANICI: " + username + "<br>" +
                    "RAPOR TARİH: " + raporTarih + "<br>" +
                    "OPERATÖRLER: " + mailOperator + "<br>" +
                    "AÇIKLAMA: " + raporAciklama;

            m.setSubject(EmailSubject);
            m.setBody(EmailBody);

            try {
                if (m.send()) {
                    progressDialog.dismiss();
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), "EMail Başarıyla Gönderildi", Toast.LENGTH_LONG).show();
                        }
                    });

                } else {
                    progressDialog.dismiss();
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), "HATA! Email(ler) Gönderilemedi", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            } catch (Exception e) {
                Log.e("MailApp", "Could not send email", e);
            }
            return null;
        }


    }

    private static String padding_str(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + String.valueOf(c);
    }

    private Integer getKey(HashMap<Integer, String> search, String searchingValue) {

        for (Integer key : search.keySet()) {
            if (search.get(key).equals(searchingValue)) {
                return key;
            }
        }

        return null;
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {

        android.widget.ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));

        if (params.height > 400)
            params.height = 400;

        listView.setLayoutParams(params);
    }

}
