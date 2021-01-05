package com.example.coffeeshop;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.timepicker.TimeFormat;
import com.google.firebase.database.DatabaseReference;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SewerManagementFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SewerManagementFragment extends Fragment implements SewerAdapter.SewerOnClickListener{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ArrayList<Sewer> sewerArrayList;
    private SewerAdapter sewerAdapter;
    private RecyclerView recyclerView;
    private FloatingActionButton floatingActionButtonAddDrink;
    private TextView textViewNumberOfProduct;
    private User2 user;
    private HttpRequestHelper httpRequestHelper;

    public SewerManagementFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FoodManagementFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SewerManagementFragment newInstance(String param1, String param2) {
        SewerManagementFragment fragment = new SewerManagementFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_sewer_management, container, false);
        initialComponent(v);

        //set up layout for RecyclerView
        sewerAdapter = new SewerAdapter(SewerManagementFragment.this.getContext(), sewerArrayList, SewerManagementFragment.this);
        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        recyclerView.setLayoutManager(new GridLayoutManager(SewerManagementFragment.this.getContext(),1));
        recyclerView.setAdapter(sewerAdapter);
        recyclerView.setHasFixedSize(true);
        final int spacing = getResources().getDimensionPixelSize(R.dimen.spacing)/2;

//        recyclerView.setPadding(spacing, spacing, spacing, spacing);
//        recyclerView.setClipToPadding(true);
//        recyclerView.setClipChildren(true);
        /*recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                int position = parent.getChildAdapterPosition(view);
                int left = 0, right = 0, top = 0, bottom = spacing+15;
                if(position == 0 || position == 1){
                    top = spacing + 15;
                }
                if(position %2 == 0) {
//                    left = 5;
                    right = 5;
                }
                outRect.set(left, top, right, bottom);
            }
        });*/
        return v;
    }

    private void initialComponent(View v) {
        user = MainActivity.user;
        httpRequestHelper = new HttpRequestHelper(getResources().getString(R.string.server_address));
        recyclerView = v.findViewById(R.id.recyclerViewDrinkManagement);
//        sewerArrayList = new ArrayList<Sewer>();
        sewerArrayList = new ArrayList<Sewer>();
        textViewNumberOfProduct = v.findViewById(R.id.textViewNumberOfProduct);
        floatingActionButtonAddDrink = v.findViewById(R.id.floatingActionButtonAddDrink);
        floatingActionButtonAddDrink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if(MainActivity.authorizeService.canCreate()){
                    Intent intent = new Intent(getContext(), SewerCreateActivity.class);
                    intent.putExtra("User", user);
                    SewerManagementFragment.this.startActivity(intent);
//                }
//                else{
//                    Toast.makeText(getContext(), "You don't have permission!", Toast.LENGTH_LONG).show();
//                }
            }
        });
    }

    // load data from firebase and fetch to recycler view
    private void fetchDataIntoRecyclerView() {
        sewerArrayList = null;
        final Request getRequest = httpRequestHelper.getGetRequest("/sewers", user.getAccessToken());
            new OkHttpClient().newCall(getRequest).enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                }
                @Override
                public void onResponse(@NotNull Call call, @NotNull final Response response) throws IOException {
                    String json = null;
                    try {
                        json = response.body().string();
                        int i = 0;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (!response.isSuccessful()) {
                        Toast.makeText(SewerManagementFragment.this.getContext(), "Error: "+json, Toast.LENGTH_SHORT).show();
                    } else {
                        Gson gson = new Gson();
                        Type listType = new TypeToken<ArrayList<Sewer>>(){}.getType();
                        sewerArrayList = gson.fromJson(json, listType);
                        sewerAdapter.setItems(sewerArrayList);
                        updateSewerAdapter();
                    }
                }

            });
    }
    public void updateSewerAdapter(){
        SewerManagementFragment.this.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textViewNumberOfProduct.setText("Sewer list: "+sewerArrayList.size()+" sewers");
                sewerAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.e("on Start", "on Start Sewer Management");
    }

    // catch the event when the fragment is focused again
    @Override
    public void onResume() {
        super.onResume();
        Log.e("food","onResume");
        fetchDataIntoRecyclerView();
    }

    // catch the even when the fragment is out of focus
    @Override
    public void onPause() {
        super.onPause();
        Log.e("food","onPause");
//        mDatabase.removeEventListener(valueEventListenerFetchUser);
        int i = 0;
    }

    @Override
    public void OnItemClick(int position) {

    }
    @Override
    public void OnSettingClick(final int position) {
        final Sewer sewer = sewerArrayList.get(position);
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(SewerManagementFragment.this.getContext(), R.style.BottomSheetDialogTheme);
        bottomSheetDialog.setDismissWithAnimation(true);
        final View bottomSheetView = LayoutInflater.from(this.getContext()).inflate(R.layout.bottom_sheet_menu, (LinearLayout)this.getActivity().findViewById(R.id.bottomSheetContainer));

        bottomSheetView.findViewById(R.id.bottomSheetInfoOption).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    bottomSheetView.findViewById(R.id.bottomSheetEditOption).setBackground(getResources().getDrawable(R.drawable.background_gradient_color));
                    bottomSheetDialog.dismiss();
                    Intent intent = new Intent(getContext(), SewerInfoActivity.class);
                    intent.putExtra("Sewer", sewer);
                    SewerManagementFragment.this.startActivity(intent);
                    return true;
                }
                if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                    //finger was lifted
                    bottomSheetView.findViewById(R.id.bottomSheetEditOption).setBackground(getResources().getDrawable(R.color.colorWhite));
                    return true;
                }
                return false;
            }
        });
        bottomSheetView.findViewById(R.id.bottomSheetEditOption).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    bottomSheetView.findViewById(R.id.bottomSheetEditOption).setBackground(getResources().getDrawable(R.drawable.background_gradient_color));
                    bottomSheetDialog.dismiss();
                    if(MainActivity.authorizeService.canModify()){
                        Intent intent = new Intent(getContext(), SewerEditActivity.class);
                        intent.putExtra("Sewer", sewer);
                        SewerManagementFragment.this.startActivity(intent);
                    }
                    else{
                        Toast.makeText(getContext(), "You don't have permission!", Toast.LENGTH_LONG).show();
                    }
                    return true;
                }
                if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                    //finger was lifted
                    bottomSheetView.findViewById(R.id.bottomSheetEditOption).setBackground(getResources().getDrawable(R.color.colorWhite));
                    return true;
                }
                return false;
            }
        });
        bottomSheetView.findViewById(R.id.bottomSSheetDeleteOption).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    bottomSheetView.findViewById(R.id.bottomSSheetDeleteOption).setBackground(getResources().getDrawable(R.drawable.background_gradient_color));
                    bottomSheetDialog.dismiss();
//                    if(MainActivity.authorizeService.canRemove()){
                    new MaterialAlertDialogBuilder(getContext())
                            .setTitle("Xóa")
                            .setMessage("Xóa vĩnh viễn: "+sewer.getName()+"?")
                            .setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Toast.makeText(getContext(), "Đã hủy", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    deleteSewer(sewer);
                                }
                            })
                            .show();
//                    }
//                    else{
//                        Toast.makeText(getContext(), "You don't have permission!", Toast.LENGTH_LONG).show();
//                    }
                    return true;
                }
                if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                    //finger was lifted
                    bottomSheetView.findViewById(R.id.bottomSSheetDeleteOption).setBackground(getResources().getDrawable(R.color.colorWhite));
                    return true;
                }
                return false;
            }
        });
        bottomSheetView.findViewById(R.id.bottomSheetControlOption).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    bottomSheetView.findViewById(R.id.bottomSheetControlOption).setBackground(getResources().getDrawable(R.drawable.background_gradient_color));
                    bottomSheetDialog.dismiss();
                    Intent intent = new Intent(getContext(), SewerDetailActivity.class);
                    intent.putExtra("Sewer", sewer);
                    SewerManagementFragment.this.startActivity(intent);
                    return true;
                }
                if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                    //finger was lifted
                    bottomSheetView.findViewById(R.id.bottomSheetControlOption).setBackground(getResources().getDrawable(R.color.colorWhite));
                    return true;
                }
                return false;
            }
        });
        bottomSheetView.findViewById(R.id.bottomSheetCreateScheduleOption).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    bottomSheetView.findViewById(R.id.bottomSheetEditOption).setBackground(getResources().getDrawable(R.drawable.background_gradient_color));
                    bottomSheetDialog.dismiss();
                    openCreateScheduleDialog();
                    return true;
                }
                if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                    //finger was lifted
                    bottomSheetView.findViewById(R.id.bottomSheetEditOption).setBackground(getResources().getDrawable(R.color.colorWhite));
                    return true;
                }
                return false;
            }
        });
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }

    private void openCreateScheduleDialog() {
        final Dialog dialog = new Dialog(SewerManagementFragment.this.getContext());
        dialog.setContentView(R.layout.dialog_create_schedule);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        final EditText editTextMqttAddress,editTextMqttPort, editTextSocketAddress, editTextSocketPort;
        final TextView textViewSetTime,textViewSetDate;
        final int[] time = new int[2];
        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);
        final int date[] = new int[3];
        date[0] = year; date[1] = month; date[2] = day;
        textViewSetTime = dialog.findViewById(R.id.textViewSetTime);
        textViewSetDate = dialog.findViewById(R.id.textViewSetDate);
        Button buttonCreateSchedule;
        buttonCreateSchedule = dialog.findViewById(R.id.buttonCreateSchedule);
        editTextMqttAddress = dialog.findViewById(R.id.editTextMqttAddress);
        editTextMqttPort = dialog.findViewById(R.id.editTextMqttPort);
        editTextSocketAddress = dialog.findViewById(R.id.editTextSocketAddress);
        editTextSocketPort = dialog.findViewById(R.id.editTextSocketPort);
        dialog.show();

        textViewSetTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final TimePickerDialog timePickerDialog = new TimePickerDialog(SewerManagementFragment.this.getContext(),
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        textViewSetTime.setText((hourOfDay/10<=0?"0"+hourOfDay:hourOfDay)+":"+( minute/10 <= 0 ? "0"+minute: minute));
                        time[0] = hourOfDay;
                        time[1] = minute;
                    }
                }, 12, 00, true);
                timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                timePickerDialog.updateTime(time[0], time[1]);
                timePickerDialog.show();
            }
        });
        textViewSetDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(SewerManagementFragment.this.getContext(),
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        month = month+1;
                        textViewSetDate.setText(year+"-"+(month/10<=0?"0"+month:month)+"-"+(dayOfMonth/10<=0?"0"+dayOfMonth:dayOfMonth));
                        date[0] = year; date[1] = month; date[2] = day;
                    }
                }, year, month, day);
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.updateDate(date[0], date[1], date[2]);
                datePickerDialog.show();
            }
        });
        buttonCreateSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editTextMqttAddress.getText().toString().equals("")
                        || editTextMqttPort.getText().toString().equals("")
                        || editTextSocketAddress.getText().toString().equals("")
                        || editTextSocketPort.getText().toString().equals(""))
                {
                    dialog.dismiss();
                }
            }
        });
    }

    public void deleteSewer(final Sewer sewer){
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Processing request...");
        progressDialog.show();
        HttpRequestHelper httpRequestHelper = new HttpRequestHelper(getResources().getString(R.string.server_address));
        Request deleteRequest = httpRequestHelper.getDeleteRequest("/sewers", sewer.getId(), user.getAccessToken());
        new OkHttpClient().newCall(deleteRequest).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull final Response response) throws IOException {
                SewerManagementFragment.this.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject message = new JSONObject(response.body().string());
                            if (!response.isSuccessful()) {
                                progressDialog.dismiss();
                                Toast.makeText(SewerManagementFragment.this.getContext(), "Error: " + message.getString("message"), Toast.LENGTH_SHORT).show();
                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(SewerManagementFragment.this.getContext(), "Successful: " + message.getString("message"), Toast.LENGTH_SHORT).show();
                                fetchDataIntoRecyclerView();
                            }
                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }
    @Override
    public void OnDeleteClick(int position) {
    }
}