package tr.com.teknosera.smoothsis_mobile;

import android.annotation.SuppressLint;
import android.os.StrictMode;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class ConnectionClass {

    private String ipaddress = "192.168.1.122";
    private String database = "smoothsis";
    private String username = "sa";
    private String password = "64726123";

    private Connection conn = null;
    private String ConnURL = null;

    @SuppressLint("NewApi")
    public Connection ConnectionHelper() {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {

            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            ConnURL = "jdbc:jtds:sqlserver://" + ipaddress + ";"+ "databaseName=" + database + ";user=" + username + ";password=" + password + ";";
            conn = DriverManager.getConnection(ConnURL);

        } catch (SQLException se) {
            Log.e("ERRO", se.getMessage());
        } catch (ClassNotFoundException e) {
            Log.e("ERRO", e.getMessage());
        } catch (Exception e) {
            Log.e("ERRO", e.getMessage());
        }

        return conn;
    }
}
