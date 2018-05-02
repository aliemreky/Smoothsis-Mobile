package tr.com.teknosera.smoothsis_mobile;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class ListAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private List<Map<String, String>> rows;

    private Context activity;

    public ListAdapter(Context activity , List<Map<String, String>> rows) {

        this.mInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.activity = activity;
        this.rows = rows;

    }

    public int getCount() {

        return rows.size();
    }

    public Map<String, String>  getItem(int position) {
        return rows.get(position);
    }


    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View satirView = convertView;

        if (satirView == null) {
            satirView = mInflater.inflate(R.layout.tablelay, null);
        }

        Map<String, String> p = getItem(position);

        if (p != null) {
            TextView URETIM_INCKEY = (TextView) satirView.findViewById(R.id.URETIM_INCKEY);
            TextView SIPARIS_KODU = (TextView) satirView.findViewById(R.id.SIPARIS_KODU);
            TextView STOK_KODU = (TextView) satirView.findViewById(R.id.STOK_KODU);
            TextView ISLEM_NO = (TextView) satirView.findViewById(R.id.ISLEM_NO);
            TextView ISLEM = (TextView) satirView.findViewById(R.id.ISLEM);
            TextView MAKINE = (TextView) satirView.findViewById(R.id.MAKINE);
            TextView YUZDE = (TextView) satirView.findViewById(R.id.YUZDE);

            URETIM_INCKEY.setText(p.get("URET_INCKEY"));
            SIPARIS_KODU.setText(p.get("SIPARIS_KOD"));
            STOK_KODU.setText(p.get("STOK_KOD"));
            ISLEM_NO.setText(p.get("ISLEM_NO"));
            ISLEM.setText(p.get("ISLEM"));
            MAKINE.setText(p.get("MAKINE"));
            YUZDE.setText(p.get("YUZDE"));
        }

        return satirView;
    }
}
