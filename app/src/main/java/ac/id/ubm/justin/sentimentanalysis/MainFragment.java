package ac.id.ubm.justin.sentimentanalysis;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainFragment extends Fragment {

    private String linkYear = "https://anokataa.pythonanywhere.com/year";
    private String linkMonth = "https://anokataa.pythonanywhere.com/month";
    private String linkCalculate = "https://anokataa.pythonanywhere.com/skripsi";
    private Button calculate;
    private Spinner sYear,sMonth;
    private ArrayList<String> mYear,mMonth;
    private String nYear,nMonth,bulan,tahun;
    private ProgressDialog pDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        sYear = (Spinner) view.findViewById(R.id.spin_year);
        sMonth = (Spinner) view.findViewById(R.id.spin_month);
        calculate = (Button) view.findViewById(R.id.btn_calculate);
        loadingDialog();

        calculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingDialog();
                bulan = sMonth.getSelectedItem().toString();
                tahun = sYear.getSelectedItem().toString();
                JsonArrayRequest arrayRequest = new JsonArrayRequest(Request.Method.GET, linkCalculate+"?bulan="+
                        bulan+"&tahun="+tahun, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try{
                            pDialog.dismiss();
                            JSONObject data = response.getJSONObject(0);
                            JSONArray arr = data.getJSONArray("cal");
                            String job = data.toString();
                            Bundle bundle = new Bundle();
                            bundle.putString("po", arr.getString(0));
                            bundle.putString("ne", arr.getString(1));
                            bundle.putString("bulan", bulan);
                            bundle.putString("tahun", tahun);
                            ChartFragment chartFragment = new ChartFragment();
                            chartFragment.setArguments(bundle);
                            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                    chartFragment).commit();
                        } catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("proinfo","error onClick : "+error.getMessage());
                    }
                });
                arrayRequest.setRetryPolicy(new DefaultRetryPolicy(
                        60000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                Controller.getInstance().addToRequestQueue(arrayRequest);
            }
        });

        loadYear();
        sYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
                ((TextView) parent.getChildAt(0)).setTextSize(30);
                ((TextView) parent.getChildAt(0)).setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                nYear = mYear.get(position);
                loadMonth();
                sMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
                        ((TextView) parent.getChildAt(0)).setTextSize(30);
                        ((TextView) parent.getChildAt(0)).setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                        nMonth = mMonth.get(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        return view;
    }

    private void loadYear() {
        mYear = new ArrayList<>();
        JsonArrayRequest arrayRequest = new JsonArrayRequest(Request.Method.GET, linkYear, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.d("proinfo","response : "+response.toString());
                for(int i = 0; i < response.length(); i++){
                    try{
                        JSONObject data = response.getJSONObject(i);
                        JSONArray arr = data.getJSONArray("year");
                        for(int j = 0; j < arr.length(); j++){
                            mYear.add(arr.getString(j));
                        }
                        Log.d("proinfo", "onResponse: "+data.getString("year"));
                        sYear.setAdapter(new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_dropdown_item,mYear));
                    } catch (JSONException e){
                        e.printStackTrace();
                    }
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("proinfo","error : "+error.getMessage());
            }
        });
        Controller.getInstance().addToRequestQueue(arrayRequest);
    }

    private void loadMonth() {
        mMonth = new ArrayList<>();
        JsonArrayRequest arrayRequest = new JsonArrayRequest(Request.Method.GET, linkMonth+"?year="+nYear, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.d("proinfo","response : "+response.toString());
                for(int i = 0; i < response.length(); i++){
                    try{
                        JSONObject data = response.getJSONObject(i);
                        JSONArray arr = data.getJSONArray("month");
                        for(int j = 0; j < arr.length(); j++){
                            mMonth.add(arr.getString(j));
                        }
                        Log.d("proinfo", "onResponse: "+data.getString("month"));
                        sMonth.setAdapter(new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_dropdown_item,mMonth));
                    } catch (JSONException e){
                        e.printStackTrace();
                    }
                }
                pDialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("proinfo","error : "+error.getMessage());
            }
        });
        Controller.getInstance().addToRequestQueue(arrayRequest);
    }

    private void loadingDialog(){
        pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Loading...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();
    }
}
