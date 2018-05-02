package tr.com.teknosera.smoothsis_mobile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    private ConnectionClass connectionClass;
    private ProgressBar progressbar;
    private ListView listproduct;

    private Toolbar toolbar;

    SharedPreferences shp;

    private HashMap<String, String> getItemInfos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.table);

        connectionClass = new ConnectionClass();

        toolbar     = (Toolbar) findViewById(R.id.tool_bar);
        progressbar = (ProgressBar) findViewById(R.id.pbbar);
        listproduct = (ListView) findViewById(R.id.lstproducts);

        toolbar.setTitle("SMOOTHSIS [ ÜRETİM LİSTESİ ]");
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setSupportActionBar(toolbar);

        progressbar.setVisibility(View.GONE);

        fillist();

        shp = this.getSharedPreferences("UserInfo", MODE_PRIVATE);


        String userid = shp.getString("UserId", "none");

        if (userid.equals("none") || userid.trim().equals("")) {
            Intent i = new Intent(MainActivity.this, Login.class);
            startActivity(i);
            finish();
        } else {
            SharedPreferences.Editor edit = shp.edit();
            edit.commit();
        }


        listproduct.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                getItemInfos= (HashMap<String, String>) parent.getItemAtPosition(position);

                registerForContextMenu(listproduct);
                openContextMenu(listproduct);

                return true;
            }
        });

    }

    public void fillist(){
        FillList fillList = new FillList();
        fillList.execute("");
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        super.onCreateContextMenu(menu, v, menuInfo);
        final MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item){

        switch (item.getItemId()) {

            case R.id.raporEkle:

                Intent raporekle = new Intent(MainActivity.this, ReportAddition.class);
                Bundle raporBundle = new Bundle();
                raporBundle.putSerializable("machineDetail", getItemInfos);
                raporekle.putExtras(raporBundle);
                startActivity(raporekle);
                break;

            default:
                break;
        }

        return super.onContextItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.logout) {
            LogOut();
        }
        if (id == R.id.refresh) {
            Refresh();
        }
        return super.onOptionsItemSelected(item);
    }

    private void Refresh() {
        fillist();
        Toast.makeText(MainActivity.this,"YENİLENDİ",Toast.LENGTH_LONG).show();
    }

    public void LogOut() {
        SharedPreferences.Editor edit = shp.edit();
        edit.putString("UserId", "");
        edit.commit();

        Intent intent = new Intent(MainActivity.this, Login.class);
        startActivity(intent);
        this.finish();
    }


    public class FillList extends AsyncTask<String, String, String>  {

        String error = "";

        List<Map<String, String>> prolist  = new ArrayList<Map<String, String>>();

        @Override
        protected void onPreExecute() {

            progressbar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String r) {

            progressbar.setVisibility(View.GONE);

            if(!TextUtils.isEmpty(r))
                Toast.makeText(MainActivity.this, r, Toast.LENGTH_SHORT).show();

            ListAdapter list = new ListAdapter(getApplicationContext(), prolist);
            listproduct.setAdapter(list);

        }

        @Override
        protected String doInBackground(String... params) {

            try {

                Connection con = connectionClass.ConnectionHelper();
                CallableStatement cs = null;

                if (con == null) {
                    error = "BAĞLANTI HATASI ! LÜTFEN BAĞLANTIYI KONTROL EDİNİZ..";

                } else {

                    cs = con.prepareCall("{call dbo.Mobile_Uretim_Listesi}");
                    cs.execute();
                    ResultSet rs = cs.getResultSet();

                    while (rs.next()) {
                        Map<String, String> datanum = new HashMap<String, String>();
                        datanum.put("URET_INCKEY", rs.getString("UR_INCKEY"));
                        datanum.put("SIPARIS_KOD", rs.getString("SIPARIS_KOD"));
                        datanum.put("STOK_KOD", rs.getString("STOK_KOD"));
                        datanum.put("ISLEM_NO", rs.getString("ISLEM_NO"));
                        datanum.put("ISLEM", rs.getString("ISLEM"));
                        datanum.put("MAKINE", rs.getString("MAKINE"));
                        datanum.put("YUZDE", rs.getString("YUZDE"));
                        datanum.put("STOK_ADI", rs.getString("STOK_ADI"));
                        prolist.add(datanum);
                    }

                }
            } catch (SQLException e) {
                error = e.toString();
            }

            return error;
        }
    }


}
