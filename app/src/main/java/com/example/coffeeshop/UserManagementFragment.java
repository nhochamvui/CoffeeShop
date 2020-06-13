package com.example.coffeeshop;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContentResolverCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
    private DatabaseReference mDatabase;
    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private ImageView imageViewAddUser;
    private ImageView imageViewChooseAvatar;
    private ValueEventListener valueEventListenerFetchUser;
    private final int PICK_IMAGE_REQUEST = 322;
    private Uri imgUri;
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
        userAdapter = new UserAdapter(UserManagementFragment.this.getContext(), userList, UserManagementFragment.this);
        recyclerView.setLayoutManager(new GridLayoutManager(UserManagementFragment.this.getContext(),2));
        recyclerView.setAdapter(userAdapter);
//        fetchDataIntoRecyclerView();

        return v;
    }

    private void fetchDataIntoRecyclerView() {
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users");
        valueEventListenerFetchUser = mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                User user = new User("", "", "");
                Log.e("fetch Data User","userlist size: "+userList.size());
                for (DataSnapshot id : dataSnapshot.getChildren()) {
                    user = id.getValue(User.class);
                    userList.add(user);
                }
                userAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void initialComponent(View v)
    {
        recyclerView = v.findViewById(R.id.recyclerViewUserManagement);
        userList = new ArrayList<User>();
        imageViewAddUser = v.findViewById(R.id.imageViewAddUser);
        imageViewAddUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addUser();
            }
        });
    }
    public void addUser()
    {
        final Dialog dialog = new Dialog(this.getContext());
        dialog.setContentView(R.layout.dialog_add_user);
        dialog.show();
        final EditText editTextUserName = dialog.findViewById(R.id.editTextUserName_Add);
        final EditText editTextPassword = dialog.findViewById(R.id.editTextPassword_Add);
        final EditText editTextRole = dialog.findViewById(R.id.editTextRole_Add);
        imageViewChooseAvatar = dialog.findViewById(R.id.imageViewChooseAvatar);
        Button buttonConfirm = dialog.findViewById(R.id.buttonConfirm_Add);
        imageViewChooseAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosePicture();
            }
        });

        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username = editTextUserName.getText().toString();
                final String password = hash(editTextPassword.getText().toString());
                final String role = editTextRole.getText().toString();

                final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("users");
                DatabaseReference newUserRef = mDatabase.push();
                User user = new User(username, password, role);
                uploadPicture();
                newUserRef.setValue(user);
//                mDatabase.child("" + id).setValue(user);
                dialog.dismiss();
            }
        });
    }
    public void uploadPicture()
    {
        if(imgUri!=null)
        {
            final ProgressDialog progressDialog= new ProgressDialog(getContext());
            progressDialog.show();
            final FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageReference = storage.getReference().child("avatars/"+ UUID.randomUUID().toString());
            storageReference.putFile(imgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.dismiss();
                    storage.getInstance().getReference().child("avatars").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                        }
                    });
                    Toast.makeText(getContext(),"Uploaded",Toast.LENGTH_LONG).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), "Upload Failed!", Toast.LENGTH_LONG).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                }
            });
        }
    }
    public void choosePicture()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Choose Picture"), PICK_IMAGE_REQUEST);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data!=null && data.getData() != null)
        {
            imgUri = data.getData();
            imageViewChooseAvatar.setImageURI(imgUri);
        }
    }

    private String hash(String s)
    {
        StringBuilder pwdHex = new StringBuilder();
        try
        {
            MessageDigest messageDigest = java.security.MessageDigest.getInstance("MD5");
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
        Toast.makeText(this.getContext(), ""+userList.get(position).getUsername(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void OnSettingClick(int position) {
//        Toast.makeText(this.getContext(), "setting", Toast.LENGTH_LONG).show();
    }

    @Override
    public void OnDeleteClick(int position) {
//        Toast.makeText(this.getContext(), "delete", Toast.LENGTH_LONG).show();
        final String userNameCompare = userAdapter.getItem(position).getUsername();
        userList.remove(position);
        userAdapter.notifyItemRemoved(position);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("users");
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = new User("", "", "");
                for (DataSnapshot id : dataSnapshot.getChildren()) {
                    user = id.getValue(User.class);
                    if(user.getUsername().equals(userNameCompare)) {
                        mDatabase.child(id.getKey()).setValue(null);
                        break;
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        mDatabase.removeEventListener(valueEventListenerFetchUser);
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchDataIntoRecyclerView();
    }
}