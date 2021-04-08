package ac.id.ubm.justin.sentimentanalysis;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.text.DecimalFormat;

public class ChartFragment extends Fragment {
    private WebView webView;
    private String po,ne,bulan,tahun;
    private TextView tanggal,persenpo,persenne;
    private Button back;
    private double nPo,nNe;
    private static DecimalFormat df = new DecimalFormat("0.00");
    private ProgressDialog pDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chart, container, false);
        try{
            po = getArguments().getString("po");
            ne = getArguments().getString("ne");
            bulan = getArguments().getString("bulan");
            tahun = getArguments().getString("tahun");
        } catch (Exception ex){
            ex.printStackTrace();
        }
        tanggal = (TextView) view.findViewById(R.id.fragment_tanggal);
        persenpo = (TextView) view.findViewById(R.id.fragment_po);
        persenne = (TextView) view.findViewById(R.id.fragment_ne);
        back = (Button) view.findViewById(R.id.fragment_back);
        tanggal.setText(bulan+" "+tahun);

        nPo = 100 * Float.parseFloat(po) / (Float.parseFloat(po) + Float.parseFloat(ne));
        nPo = Double.parseDouble(df.format(nPo));
        nNe = 100 - nPo;
        nNe = Double.parseDouble(df.format(nNe));

        persenpo.setText("Positive\n"+nPo+"%");
        persenne.setText("Negative\n"+nNe+"%");

        webView = (WebView) view.findViewById(R.id.webview_chart);
        webView.setWebViewClient(new webviewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("https://anokataa.pythonanywhere.com/chart?po="+po+"&ne="+ne);


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new MainFragment()).commit();
            }
        });

        return view;
    }

    private class webviewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Loading...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if(pDialog != null){
                pDialog.dismiss();
            }
        }

    }

}
