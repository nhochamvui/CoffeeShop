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
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ScheduleManagementFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ScheduleManagementFragment extends Fragment implements ScheduleAdapter.ScheduleOnClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ScheduleAdapter scheduleAdapter;
    private ArrayList<Schedule> scheduleArrayList;
    private RecyclerView recyclerView;
    private HttpRequestHelper httpRequestHelper;
    private TextView textViewNumberOfSchedule;
    private FloatingActionButton floatingActionButtonAddSchedule;
    private User user;
    private String date = "2020-12-29";
    private String time = "15:15";
//    action: '1';
//    sewer: 'sewerOnTop2',
    public ScheduleManagementFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SewerScheduling.
     */
    // TODO: Rename and change types and number of parameters
    public static ScheduleManagementFragment newInstance(String param1, String param2) {
        ScheduleManagementFragment fragment = new ScheduleManagementFragment();
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
        View view = inflater.inflate(R.layout.fragment_schedule_management, container, false);
        initialComponent(view);

        //set up layout for RecyclerView
        scheduleAdapter = new ScheduleAdapter(ScheduleManagementFragment.this.getContext(), scheduleArrayList, ScheduleManagementFragment.this);
        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        recyclerView.setLayoutManager(new GridLayoutManager(ScheduleManagementFragment.this.getContext(),1));
        recyclerView.setAdapter(scheduleAdapter);
        recyclerView.setHasFixedSize(true);
        return view;
    }

    private void initialComponent(View v) {
        user = MainActivity.user;
        httpRequestHelper = new HttpRequestHelper(getResources().getString(R.string.server_address));
        recyclerView = v.findViewById(R.id.recyclerViewScheduleManagement);
        scheduleArrayList = new ArrayList<Schedule>();
        textViewNumberOfSchedule = v.findViewById(R.id.textViewNumberOfSchedule);
    }

    // load data from firebase and fetch to recycler view
    private void fetchDataIntoRecyclerView() {
        scheduleArrayList = null;
        final Request getRequest = httpRequestHelper.getGetRequest("/schedules", user.getAccessToken());

        new OkHttpClient().newCall(getRequest).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                if(ScheduleManagementFragment.this.getActivity() != null){
                    ScheduleManagementFragment.this.getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getContext(), "Failed to connect!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull final Response response) throws IOException {
                final String json = response.body().string();
                if(ScheduleManagementFragment.this.getActivity() != null){
                    ScheduleManagementFragment.this.getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if (!response.isSuccessful()) {
                                    JSONObject jsonObject = new JSONObject(json);
                                    Toast.makeText(ScheduleManagementFragment.this.getContext(), "Error: "+ jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                } else {
                                    Type listType = new TypeToken<ArrayList<Schedule>>(){}.getType();
                                    scheduleArrayList = new Gson().fromJson(json, listType);
                                    scheduleAdapter.setItems(scheduleArrayList);
                                    scheduleAdapter.notifyDataSetChanged();
                                }
                            } catch (JSONException e) {
                                Toast.makeText(ScheduleManagementFragment.this.getContext(), "Error", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        Log.e("scheduleManageFragment", "onStart");
    }

    // catch the event when the fragment is focused again
    @Override
    public void onResume() {
        super.onResume();
        Log.e("scheduleManageFragment","onResume");
        fetchDataIntoRecyclerView();
    }

    // catch the even when the fragment is out of focus
    @Override
    public void onPause() {
        super.onPause();
        Log.e("scheduleManageFragment","onPause");
    }

    @Override
    public void OnItemClick(int position) {

    }
    @Override
    public void OnSettingClick(final int position) {
        final Schedule schedule = scheduleArrayList.get(position);
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(ScheduleManagementFragment.this.getContext(), R.style.BottomSheetDialogTheme);
        bottomSheetDialog.setDismissWithAnimation(true);
        final View bottomSheetView = LayoutInflater.from(this.getContext()).inflate(R.layout.bottom_sheet_menu, (LinearLayout)this.getActivity().findViewById(R.id.bottomSheetContainer));
        bottomSheetView.findViewById(R.id.bottomSheetInfoOption).setVisibility(View.GONE);
        bottomSheetView.findViewById(R.id.bottomSheetControlOption).setVisibility(View.GONE);
        bottomSheetView.findViewById(R.id.bottomSheetCreateScheduleOption).setVisibility(View.GONE);
        bottomSheetView.findViewById(R.id.bottomSheetEditOption).setVisibility(View.GONE);
        bottomSheetView.findViewById(R.id.bottomSheetEditOption).setOnTouchListener(new View.OnTouchListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    bottomSheetView.findViewById(R.id.bottomSheetEditOption).setBackground(getResources().getDrawable(R.drawable.background_gradient_color));
                    bottomSheetDialog.dismiss();
                    openEditScheduleDialog(schedule);
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
                            .setTitle("Delete")
                            .setMessage("Delete permanently?")
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Toast.makeText(getContext(), "Canceled", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    deleteSchedule(schedule);
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
                    intent.putExtra("Sewer", schedule);
                    ScheduleManagementFragment.this.startActivity(intent);
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
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void openEditScheduleDialog(final Schedule schedule) {
        final Dialog dialog = new Dialog(ScheduleManagementFragment.this.getContext());
        dialog.setContentView(R.layout.dialog_create_schedule);
//        dialog.setTitle("Create Schedule");
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        List<String> actions = new ArrayList<String>();
        actions.add("Open");actions.add("Close");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(ScheduleManagementFragment.this.getContext(), R.layout.support_simple_spinner_dropdown_item, actions);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        final AutoCompleteTextView autoCompleteTextViewScheduleAction = dialog.findViewById(R.id.autoCompleteTextViewScheduleAction);
        autoCompleteTextViewScheduleAction.setAdapter(dataAdapter);
        autoCompleteTextViewScheduleAction.setText(schedule.parseAction(Integer.parseInt(schedule.getAction())), false);
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
        textViewSetTime.setText(schedule.getTime());
        textViewSetDate.setText(schedule.getDate());
        Button buttonCreateSchedule;
        buttonCreateSchedule = dialog.findViewById(R.id.buttonCreateSchedule);
        buttonCreateSchedule.setText("Update");
        dialog.show();

        textViewSetTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final TimePickerDialog timePickerDialog = new TimePickerDialog(ScheduleManagementFragment.this.getContext(),
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
                DatePickerDialog datePickerDialog = new DatePickerDialog(ScheduleManagementFragment.this.getContext(),
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        month = month+1;
                        textViewSetDate.setText(year+"-"+(month/10<=0?"0"+month:month)+"-"+(dayOfMonth/10<=0?"0"+dayOfMonth:dayOfMonth));
                        date[0] = year; date[1] = month-1; date[2] = dayOfMonth;
                    }
                }, year, month, day);
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.updateDate(date[0], date[1], date[2]);
                datePickerDialog.getDatePicker().setMinDate(new Date().getTime()- 10000);
                datePickerDialog.show();
            }
        });
        buttonCreateSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!textViewSetTime.getText().toString().equals("Set Time") || !textViewSetDate.getText().toString().equals("Set Date")) {
                    schedule.setTime(textViewSetTime.getText().toString());
                    schedule.setTime(textViewSetDate.getText().toString());
                    schedule.setAction(autoCompleteTextViewScheduleAction.getText().toString().equals("Open")?"1":"0");
                    new OkHttpClient().newCall(httpRequestHelper.getEditRequest("/schedules", schedule.getId(), new Gson().toJson(schedule), user.getAccessToken())).enqueue(new Callback() {
                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {
                            Log.e("Create Schedule", ""+e.getMessage());
                        }
                        @Override
                        public void onResponse(@NotNull Call call, @NotNull final Response response) throws IOException {
                            ScheduleManagementFragment.this.getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        JSONObject jsonObject = new JSONObject(response.body().string());
                                        if (!response.isSuccessful()) {
                                            Toast.makeText(ScheduleManagementFragment.this.getContext(), "Error: "+jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                                        } else {
                                            dialog.dismiss();
                                            Toast.makeText(ScheduleManagementFragment.this.getContext(), "Successful: "+jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                                        }
                                    } catch (IOException | JSONException e) {
                                        e.printStackTrace();
                                    }

                                }
                            });
                        }
                    });
                }
            }
        });
    }
    public void deleteSchedule(final Schedule schedule){
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Processing request...");
        progressDialog.show();
        HttpRequestHelper httpRequestHelper = new HttpRequestHelper(getResources().getString(R.string.server_address));
        Request deleteRequest = httpRequestHelper.getDeleteRequest("/schedules", schedule.getId(), user.getAccessToken());
        new OkHttpClient().newCall(deleteRequest).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull final Response response) throws IOException {
                ScheduleManagementFragment.this.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject message = new JSONObject(response.body().string());
                            if (!response.isSuccessful()) {
                                progressDialog.dismiss();
                                Toast.makeText(ScheduleManagementFragment.this.getContext(), "Error: " + message.getString("message"), Toast.LENGTH_SHORT).show();
                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(ScheduleManagementFragment.this.getContext(), "Successful: " + message.getString("message"), Toast.LENGTH_SHORT).show();
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