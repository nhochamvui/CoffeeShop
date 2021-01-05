package com.example.coffeeshop;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.DrmInitData;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.util.JSONPObject;
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
    private User2 user;
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
        floatingActionButtonAddSchedule = v.findViewById(R.id.floatingActionButtonAddSchedule);
        floatingActionButtonAddSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), SewerCreateActivity.class);
                intent.putExtra("User", user);
                ScheduleManagementFragment.this.startActivity(intent);
            }
        });
    }

    // load data from firebase and fetch to recycler view
    private void fetchDataIntoRecyclerView() {
        scheduleArrayList = null;
        final Request getRequest = httpRequestHelper.getGetRequest("/schedules", user.getAccessToken());

        new OkHttpClient().newCall(getRequest).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e("onFailure", "err: "+e.getMessage());
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull final Response response) throws IOException {
                ScheduleManagementFragment.this.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (!response.isSuccessful()) {
                                JSONObject message = new JSONObject(response.body().string());
                                Toast.makeText(ScheduleManagementFragment.this.getContext(), "Error: "+message.getString("message"), Toast.LENGTH_SHORT).show();
                            } else {
                                Gson gson = new Gson();
                                Type listType = new TypeToken<ArrayList<Schedule>>(){}.getType();
                                scheduleArrayList = gson.fromJson(response.body().string(), listType);
                                scheduleAdapter.setItems(scheduleArrayList);
                                scheduleAdapter.notifyDataSetChanged();
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
        bottomSheetView.findViewById(R.id.bottomSheetEditOption).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    bottomSheetView.findViewById(R.id.bottomSheetEditOption).setBackground(getResources().getDrawable(R.drawable.background_gradient_color));
                    bottomSheetDialog.dismiss();
                    if(MainActivity.authorizeService.canModify()){
                        Intent intent = new Intent(getContext(), SewerEditActivity.class);
                        intent.putExtra("Schedule", schedule);
                        ScheduleManagementFragment.this.startActivity(intent);
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