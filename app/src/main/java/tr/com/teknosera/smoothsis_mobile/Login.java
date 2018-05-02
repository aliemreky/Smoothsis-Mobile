package tr.com.teknosera.smoothsis_mobile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;

public class Login extends AppCompatActivity {


    private ConnectionClass connectionClass;
    private EditText edtuserid, edtpass;
    private Button btnlogin;
    private ProgressBar pbbar;
    private SharedPreferences shp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        connectionClass = new ConnectionClass();
        edtuserid = (EditText) findViewById(R.id.edtuserid);
        edtpass = (EditText) findViewById(R.id.edtpass);
        btnlogin = (Button) findViewById(R.id.btnlogin);
        pbbar = (ProgressBar) findViewById(R.id.pbbar);
        pbbar.setVisibility(View.GONE);

        shp = this.getSharedPreferences("UserInfo", MODE_PRIVATE);

        String userId = shp.getString("UserId", "none");

        if (userId.equals("none") || userId.trim().equals("")) {

        } else {

            Intent i = new Intent(Login.this, MainActivity.class);
            startActivity(i);
            finish();

        }

        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DoLogin doLogin = new DoLogin();
                doLogin.execute("");

            }
        });

    }


    public class DoLogin extends AsyncTask<String, String, String> {
        String errorString = "";
        Boolean isSuccess = false;
        int kullaniciId;
        String userid = edtuserid.getText().toString();
        String password = edtpass.getText().toString();

        @Override
        protected void onPreExecute() {
            pbbar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String r) {
            pbbar.setVisibility(View.GONE);
            Toast.makeText(Login.this, r, Toast.LENGTH_SHORT).show();

            if (isSuccess) {

                SharedPreferences.Editor edit = shp.edit();
                edit.putString("UserId", userid);
                edit.putInt("kulInckey", kullaniciId);
                edit.commit();
                Intent mainIntent = new Intent(Login.this, MainActivity.class);
                startActivity(mainIntent);
                finish();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            if (userid.trim().equals("") || password.trim().equals(""))
                errorString = "Lütfen Boş Alan Bırakmayınız !";
            else {
                try {
                    Connection con = connectionClass.ConnectionHelper();
                    if (con == null) {
                        errorString = "Bağlantı Hatası !";
                    } else {
                        String userControl = "SELECT * FROM KULLANICI WHERE ADSOYAD='"+ userid + "' AND SIFRE='" + password + "'";
                        Statement stmt = con.createStatement();
                        ResultSet rs = stmt.executeQuery(userControl);

                        if (rs.next()) {
                            errorString = "Giriş Başarılı";
                            kullaniciId = Integer.parseInt(rs.getString("KUL_INCKEY"));
                            isSuccess = true;
                        } else {
                            errorString = "Kullanıcı Bulunamadı !";
                            isSuccess = false;
                        }
                    }
                } catch (Exception ex) {
                    isSuccess = false;
                    errorString = ex.toString();
                }
            }

            return errorString;
        }
    }




}
