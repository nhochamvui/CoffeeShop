package com.example.coffeeshop;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserManagementFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserManagementFragment extends Fragment implements UserAdapter.OnClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ArrayList<User> userList;
    private ArrayList<Long> sewerList;
    private DatabaseReference mDatabase;
    private RecyclerView recyclerView;
    private ListView listviewAuthor;
    private UserAdapter userAdapter;
    private FloatingActionButton floatingActionButtonAddUser;
    private ImageView imageViewChooseAvatar;
    private ValueEventListener valueEventListenerFetchUser;
    private final int PICK_IMAGE_REQUEST = 322;
    private Uri imgUri;
    private String downloadImageUrl = new String("");
    private EditText editTextUserName;
    private EditText editTextPassword;
    private Spinner editTextRole;
    private RadioGroup radioGroupAdd;
    private RadioGroup radioGroupRemove;
    private RadioGroup radioGroupModify;
    private RadioButton radioButtonAdd;
    private RadioButton radioButtonRemove;
    private RadioButton radioButtonModify;
    private Button buttonSewerManagement;
    private Button buttonConfirm;
    private TextView textViewCurrentUser;
    public UserManagementFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UserManagementFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UserManagementFragment newInstance(String param1, String param2) {
        UserManagementFragment fragment = new UserManagementFragment();
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
        View v = inflater.inflate(R.layout.fragment_user_management, container, false);
        initialComponent(v);
        Log.e("from begin","userlist size: "+userList.size());
        //set up layout for RecyclerView
        userAdapter = new UserAdapter(UserManagementFragment.this.getContext(), userList, UserManagementFragment.this, this.getActivity());
        recyclerView.setLayoutManager(new GridLayoutManager(UserManagementFragment.this.getContext(),1));
        recyclerView.setAdapter(userAdapter);
        return v;
    }

    private void fetchDataIntoRecyclerView() {
        userList = null;
        final Request getRequest = MainActivity.httpRequestHelper.getGetRequest("/users", MainActivity.user.getAccessToken());
        new OkHttpClient().newCall(getRequest).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                UserManagementFragment.this.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(UserManagementFragment.this.getContext(), "Failed to connect!", Toast.LENGTH_LONG);
                    }});
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull final Response response) throws IOException {
                final String json = response.body().string();
                UserManagementFragment.this.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!response.isSuccessful()) {
                            try {
                                JSONObject jsonObject = new JSONObject(json);
                                Toast.makeText(UserManagementFragment.this.getContext(), "Error: "+ jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                Toast.makeText(UserManagementFragment.this.getContext(), "Error", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Type listType = new TypeToken<ArrayList<User>>(){}.getType();
                            userList = new Gson().fromJson(json, listType);
                            userAdapter.setItems(userList);
                            textViewCurrentUser.setText("Users: "+userList.size()+" users");
                            userAdapter.notifyDataSetChanged();
                        }
                    }
                });

            }

        });
    }

    public void initialComponent(View v)
    {
        recyclerView = v.findViewById(R.id.recyclerViewUserManagement);
        userList = new ArrayList<User>();
        floatingActionButtonAddUser = v.findViewById(R.id.floatingActionButtonAddUser);
        textViewCurrentUser = v.findViewById(R.id.textViewCurrentUser);
        floatingActionButtonAddUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MainActivity.authorizeService.isAdmin()){
                    addUser();
                }
                else{
                    Toast.makeText(getContext(), "Permission is required!", Toast.LENGTH_LONG);
                }
            }
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data!=null && data.getData() != null)
        {
            imgUri = data.getData();
            Log.e("onactivity",""+imgUri.toString());
            imageViewChooseAvatar.setImageURI(imgUri);
        }
    }

    private String hash(String s)
    {
        StringBuilder pwdHex = new StringBuilder();
        try
        {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(s.getBytes());
            byte[] messageDigests = messageDigest.digest();
            for(byte tempMess : messageDigests)
            {
                String h = Integer.toHexString(0xFF & tempMess);
                while(h.length()<2)
                {
                    h = "0" +h;
                }
                pwdHex.append(h);
            }
            return pwdHex.toString();
        }
        catch(NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        return "";
    }
    @Override
    public void OnItemClick(int position) {
        Toast.makeText(this.getContext(), ""+userList.get(position).getName(), Toast.LENGTH_LONG).show();
    }

    public void addUser() {
        final Dialog dialog = new Dialog(this.getContext());
        dialog.setContentView(R.layout.dialog_add_user);
        dialog.show();
        // xu ly khi nguoi dung nhan phim back -> xoa img da chon
        final EditText editTextEmail_Add, editTextUserCity, editTextUserDistrict;
        editTextUserName = dialog.findViewById(R.id.editTextUserName_Add);
        editTextPassword = dialog.findViewById(R.id.editTextUserPassword);
        editTextRole = dialog.findViewById(R.id.editTextRole_Add);
        editTextEmail_Add = dialog.findViewById(R.id.editTextEmail_Add);
        editTextUserCity = dialog.findViewById(R.id.editTextUserCity);
        editTextUserDistrict = dialog.findViewById(R.id.editTextUserDistrict);

        String[] Roles = new String[]{"Select Role", "Admin", "User"};
        final List<String> plantsList = new ArrayList<>(Arrays.asList(Roles));
        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_text, plantsList);
        spinnerArrayAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown);
        editTextRole.setAdapter(spinnerArrayAdapter);
        editTextRole.setSelection(0);
        buttonConfirm = dialog.findViewById(R.id.buttonConfirm_Add);
        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editTextPassword.getText().toString().equals("")
                        || editTextRole.getSelectedItem().toString().equals("Select Role")
                        || editTextUserName.getText().toString().equals("")
                        || editTextEmail_Add.getText().toString().equals("")
                        || editTextUserCity.getText().toString().equals("")
                        || editTextUserDistrict.getText().toString().equals("")) {
                    Toast.makeText(getContext(), "Please fill out the form!", Toast.LENGTH_SHORT).show();
                }
                else {
                    Map<String, String> location = new HashMap<>();
                    location.put("city", editTextUserCity.getText().toString());
                    location.put("district", editTextUserDistrict.getText().toString());
                    User user = new User(editTextUserName.getText().toString()
                            ,editTextEmail_Add.getText().toString()
                            ,editTextRole.getSelectedItem().toString()
                            ,editTextPassword.getText().toString()
                            ,location);
                    Request postRequest = MainActivity.httpRequestHelper.getPostRequest("/users", new Gson().toJson(user), MainActivity.user.getAccessToken());
                    new OkHttpClient().newCall(postRequest).enqueue(new Callback() {
                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(UserManagementFragment.this.getContext(), "Failed to connect!", Toast.LENGTH_LONG).show();
                                }
                            });
                        }

                        @Override
                        public void onResponse(@NotNull Call call, @NotNull final Response response) throws IOException {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        if (!response.isSuccessful()) {
                                            JSONObject jsonObject = new JSONObject(response.body().string());
                                            Toast.makeText(UserManagementFragment.this.getContext(), "Error: "+jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                                        } else {
                                            JSONObject jsonObject = new JSONObject(response.body().string());
                                            dialog.dismiss();
                                            Toast.makeText(UserManagementFragment.this.getContext(), "Successful: "+jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                                            fetchDataIntoRecyclerView();
                                        }
                                    } catch (JSONException | IOException e) {
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

    @Override
    public void OnSettingClick(int position) {
        if(MainActivity.authorizeService.isAdmin() || MainActivity.authorizeService.getUser().getEmail().equals(userList.get(position).getEmail())){
            final User user = userList.get(position);
            final Dialog dialog = new Dialog(this.getContext());
            dialog.setContentView(R.layout.dialog_add_user);
            dialog.setTitle("Edit User");
            dialog.show();
            // xu ly khi nguoi dung nhan phim back -> remove img da chon
            dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                        removeTheLastestImgURI();
                        dialog.dismiss();
                        return true;
                    }
                    return false;
                }
            });
            final EditText editTextEmail_Add, editTextUserCity, editTextUserDistrict;
            radioGroupAdd = dialog.findViewById(R.id.radio_group_add);
            radioGroupRemove = dialog.findViewById(R.id.radio_group_remove);
            radioGroupModify = dialog.findViewById(R.id.radio_group_modify);
            editTextUserName = dialog.findViewById(R.id.editTextUserName_Add);
            editTextPassword = dialog.findViewById(R.id.editTextUserPassword);
            editTextRole = dialog.findViewById(R.id.editTextRole_Add);
            editTextUserCity = dialog.findViewById(R.id.editTextUserCity);
            editTextUserDistrict = dialog.findViewById(R.id.editTextUserDistrict);
            imageViewChooseAvatar = dialog.findViewById(R.id.imageViewChooseAvatar);
            editTextEmail_Add = dialog.findViewById(R.id.editTextEmail_Add);

            editTextEmail_Add.setText(user.getEmail());
            editTextEmail_Add.setEnabled(false);

            String[] roles = new String[]{"Admin", "User", "Guest"};
//        final List<String> plantsList = new ArrayList<>(Arrays.asList(Roles));
            final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_text, roles);
            spinnerArrayAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown);
            editTextRole.setAdapter(spinnerArrayAdapter);
            //set default value
            editTextUserName.setText(user.getName());
            editTextUserCity.setText(user.getLocation().get("city"));
            editTextUserDistrict.setText(user.getLocation().get("district"));
            editTextPassword.setText("");
            editTextRole.setSelection(user.getRole().equals(roles[0]) ? 0 : (user.getRole().equals(roles[1]) ? 1 : 2));

            Button buttonConfirm = dialog.findViewById(R.id.buttonConfirm_Add);
            buttonConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(editTextUserName.getText().toString().equals("")
                            || editTextUserCity.getText().toString().equals("")
                            || editTextUserDistrict.getText().toString().equals("")){
                        Toast.makeText(getContext(), "Please fill out the form!", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Map<String, String> location = new HashMap<>();
                        location.put("city", editTextUserCity.getText().toString());
                        location.put("district", editTextUserDistrict.getText().toString());
                        User editUser = new User(editTextUserName.getText().toString()
                                ,editTextEmail_Add.getText().toString()
                                ,editTextRole.getSelectedItem().toString()
                                ,location);
                        if(!editTextPassword.getText().toString().equals("")){
                            editUser.setPassword(editTextPassword.getText().toString());
                        }
                        Request editRequest = MainActivity.httpRequestHelper.getEditRequest("/users", editUser.getEmail(), new Gson().toJson(editUser), MainActivity.user.getAccessToken());
                        new OkHttpClient().newCall(editRequest).enqueue(new Callback() {
                            @Override
                            public void onFailure(@NotNull Call call, @NotNull IOException e) {

                            }

                            @Override
                            public void onResponse(@NotNull Call call, @NotNull final Response response) throws IOException {
                                final String json = response.body().string();
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            if (!response.isSuccessful()) {
                                                JSONObject jsonObject = new JSONObject(json);
                                                Toast.makeText(UserManagementFragment.this.getContext(), "Error: "+jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                                            } else {
                                                JSONObject jsonObject = new JSONObject(json);
                                                dialog.dismiss();
                                                Toast.makeText(UserManagementFragment.this.getContext(), "Successful: "+jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                                                fetchDataIntoRecyclerView();
                                            }
                                        } catch (JSONException e) {
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
        else{
            Toast.makeText(getContext(), "Permission is required!", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void OnDeleteClick(final int position) {
        new MaterialAlertDialogBuilder(getContext())
                .setTitle("Delete")
                .setMessage("Delete this user permanently?")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(getContext(), "Cancelled!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteUser(userList.get(position));
                    }
                })
                .show();
    }

    private void deleteUser(User user){
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Processing request...");
        progressDialog.show();
        HttpRequestHelper httpRequestHelper = new HttpRequestHelper(getResources().getString(R.string.server_address));
        Request deleteRequest = httpRequestHelper.getDeleteRequest("/users", user.getEmail(), MainActivity.user.getAccessToken());
        new OkHttpClient().newCall(deleteRequest).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                UserManagementFragment.this.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(UserManagementFragment.this.getContext(), "Failed to connect!", Toast.LENGTH_LONG);
                    }});
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull final Response response) throws IOException {
                UserManagementFragment.this.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject message = new JSONObject(response.body().string());
                            if (!response.isSuccessful()) {
                                progressDialog.dismiss();
                                Toast.makeText(UserManagementFragment.this.getContext(), "Error: " + message.getString("message"), Toast.LENGTH_SHORT).show();
                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(UserManagementFragment.this.getContext(), "Successful: " + message.getString("message"), Toast.LENGTH_SHORT).show();
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
    public void onPause() {
        super.onPause();
//        mDatabase.removeEventListener(valueEventListenerFetchUser);
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchDataIntoRecyclerView();
    }
    public void removeTheLastestImgURI()
    {
        imgUri = null;
    }

    public void sewerSelectDialog(){
        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Choose sewers to manage");

        // add a radio button list
        mDatabase = FirebaseDatabase.getInstance().getReference().child("sewer");
        valueEventListenerFetchUser = mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                sewerList.clear();
                SewerAuthorize sewerAuthorize = new SewerAuthorize();
                for (DataSnapshot id : dataSnapshot.getChildren()) {
                    sewerAuthorize = id.getValue(SewerAuthorize.class);
                    sewerList.add(sewerAuthorize.getSewerId());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        boolean[] checkeds = { false, true, false, false, false };
        builder.setMultiChoiceItems(sewerList.toArray(new String[0]), checkeds,  new DialogInterface.OnMultiChoiceClickListener() {
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {

            }
        });

        // add OK and Cancel buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int position) {
                Toast.makeText(getContext(), "clicked", Toast.LENGTH_LONG).show();
            }
        });
        builder.setNegativeButton("Cancel", null);

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}