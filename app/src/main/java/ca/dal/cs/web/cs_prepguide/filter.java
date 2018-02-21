//package ca.dal.cs.web.cs_prepguide;
//
//import android.support.v7.app.AppCompatActivity;
//import android.os.Bundle;
//import android.widget.ArrayAdapter;
//import android.widget.Button;
//import android.widget.Spinner;
//
//public class filter extends AppCompatActivity {
//
//    public static final String STREAM="STREAM";
//    public static final String COMPANY="COMPANY";
//    public static final String TYPE="TYPE";
//
//    private Button btnApplyFilter;
//    private Spinner spinnerCompany, spinnerType, spinnerStream;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_filter);
//
//        btnApplyFilter= findViewById(R.id.btnApplyFilter);
//        spinnerStream= findViewById(R.id.spinner_stream);
//        spinnerCompany= findViewById(R.id.spinner_company);
//        spinnerType= findViewById(R.id.spinner_type);
//
//        ArrayAdapter<CharSequence> streamAdapter= ArrayAdapter.createFromResource(this,
//                R.array.stream_array, R.layout.spinner_dropdown_item);
//        spinnerStream.setAdapter(streamAdapter);
//
//        ArrayAdapter<CharSequence> companyAdapter= ArrayAdapter.createFromResource(this,
//                R.array.company_array, R.layout.spinner_dropdown_item);
//        spinnerCompany.setAdapter(companyAdapter);
//
//        ArrayAdapter<CharSequence> typeAdapter= ArrayAdapter.createFromResource(this,
//                R.array.type_array, R.layout.spinner_dropdown_item);
//        spinnerType.setAdapter(typeAdapter);
//    }
//}
