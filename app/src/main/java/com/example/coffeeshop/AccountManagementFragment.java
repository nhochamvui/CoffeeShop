package com.example.coffeeshop;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AccountManagementFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AccountManagementFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private User user;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private EditText userId;
    private ImageView imageViewChooseAvatar;
    private EditText userRole;
    private EditText userName;
    private TextView changeInfo;
    private ImageView avatar;
    private EditText editTextUserName;
    private Uri imgUri;
    private EditText editTextPassword;
    private Spinner editTextRole;
    private RadioGroup radioGroupAdd;
    private final int PICK_IMAGE_REQUEST = 322;
    private RadioGroup radioGroupRemove;
    private RadioGroup radioGroupModify;
    private RadioButton radioButtonAdd;
    private RadioButton radioButtonRemove;
    private TextView userDisplayName;
    private RadioButton radioButtonModify;
    private Button buttonConfirm;

    public AccountManagementFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AccountManagementFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AccountManagementFragment newInstance(String param1, String param2) {
        AccountManagementFragment fragment = new AccountManagementFragment();
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
        View v = inflater.inflate(R.layout.fragment_store, container, false);
        user = (User)getActivity().getIntent().getSerializableExtra("user");
        initialComponent(v);
        fetchView();
        changeInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editInfo();
            }
        });
        return v;
    }
    public void initialComponent(View v){
        userId = v.findViewById(R.id.user_id);
        userName = v.findViewById(R.id.user_name);
        userRole = v.findViewById(R.id.user_role);
        changeInfo = v.findViewById(R.id.info_change);
        avatar = v.findViewById(R.id.avatar);
    }
    public void fetchView(){
//        userId.setText(user.getUsername());
//        userName.setText(user.getDisplayname());
//        userRole.setText(user.getRole());
//        Glide.with(this)
//                .load(user.getAvatar())
//                .circleCrop()
//                .into(avatar);
    }
    public void editInfo(){
        final Dialog dialog = new Dialog(this.getContext());
        dialog.setContentView(R.layout.dialog_add_user);
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
        radioGroupAdd = dialog.findViewById(R.id.radio_group_add);
        //radioGroupAdd.clearCheck();
        radioGroupRemove = dialog.findViewById(R.id.radio_group_remove);
        //radioGroupRemove.clearCheck();
        radioGroupModify = dialog.findViewById(R.id.radio_group_modify);
        //radioGroupModify.clearCheck();
        editTextUserName = dialog.findViewById(R.id.editTextUserName_Add);
        editTextPassword = dialog.findViewById(R.id.editTextPassword_Add);
        editTextRole = dialog.findViewById(R.id.editTextRole_Add);
//        editTextUserName.setText(user.getDisplayname());
        editTextPassword.setText("");
        String[] Roles = new String[]{"Select Role", "Admin", "User"};
        final List<String> plantsList = new ArrayList<>(Arrays.asList(Roles));
        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_text, plantsList);
        spinnerArrayAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown);
        editTextRole.setAdapter(spinnerArrayAdapter);
        if(user.getRole().equals("Admin"))
            editTextRole.setSelection(1);
        else
            editTextRole.setSelection(2);
        editTextRole.setEnabled(false);
        editTextRole.setClickable(false);
//        if (user.getAdd())
//            radioGroupAdd.check(R.id.radio1);
//        else
//            radioGroupAdd.check(R.id.radio2);
//        if (user.getRemove())
//            radioGroupRemove.check(R.id.radio3);
//        else
//            radioGroupRemove.check(R.id.radio4);
//        if (user.getModify())
//            radioGroupModify.check(R.id.radio5);
//        else
//            radioGroupModify.check(R.id.radio6);
        for (int i = 0; i < radioGroupAdd.getChildCount(); i++) {
            radioGroupAdd.getChildAt(i).setEnabled(false);
        }
        for (int i = 0; i < radioGroupRemove.getChildCount(); i++) {
            radioGroupRemove.getChildAt(i).setEnabled(false);
        }
        for (int i = 0; i < radioGroupModify.getChildCount(); i++) {
            radioGroupModify.getChildAt(i).setEnabled(false);
        }
        imageViewChooseAvatar = dialog.findViewById(R.id.imageViewChooseAvatar);
//        Glide.with(imageViewChooseAvatar.getContext())
//                .load(user.getAvatar())
//                .centerCrop()
//                .error(R.drawable.ic_round_broken_image_24)
//                .placeholder(R.drawable.ic_baseline_image_24)
//                .transform(new RoundedCorners(10))
//                .into(imageViewChooseAvatar);
//        final User userOriginal = new User(user.getUsername()
//                ,user.getDisplayname()
//                ,user.getRole()
//                ,user.getPassword()
//                ,user.getAvatar()
//                ,user.getAdd()
//                ,user.getModify()
//                ,user.getRemove()
//        );
        Button buttonConfirm = dialog.findViewById(R.id.buttonConfirm_Add);
        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editTextUserName.getText().equals("") || editTextRole.getSelectedItem().equals("Select Role"))
                {
                    Toast.makeText(getContext(), "User Name and Role are required!", Toast.LENGTH_SHORT).show();
                }
                else {
                    radioButtonAdd = dialog.findViewById(radioGroupAdd.getCheckedRadioButtonId());
                    radioButtonRemove = dialog.findViewById(radioGroupRemove.getCheckedRadioButtonId());
                    radioButtonModify = dialog.findViewById(radioGroupModify.getCheckedRadioButtonId());
//                    uploadPicture(userOriginal);
                    dialog.dismiss();
                }
            }
        });
        imageViewChooseAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosePicture();
            }
        });
    }
    /*public void uploadPicture(final User userOriginal)
    {
        final User user1 = new User("", "", "", "", "", false, false, false);
        user1.setUsername(userOriginal.getUsername());
        user1.setDisplayname(editTextUserName.getText().toString());
        user1.setRole(editTextRole.getSelectedItem().toString());
        if(editTextPassword.getText().toString().equals(""))// password field is blank
            user1.setPassword(userOriginal.getPassword());
        else
            user1.setPassword(hash(editTextPassword.getText().toString()));
        //set default avatar
        user1.setAvatar(userOriginal.getAvatar());
        user1.setAdd(Boolean.parseBoolean(radioButtonAdd.getText().toString()));
        user1.setRemove(Boolean.parseBoolean(radioButtonRemove.getText().toString()));
        user1.setModify(Boolean.parseBoolean(radioButtonModify.getText().toString()));
        user = user1;
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("chat room", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("display_name", user.getDisplayname());
        editor.commit();
        ((MainActivity) getActivity()).reloadName();
        fetchView();
        if(imgUri!=null)//save user when uploading new avatar
        {
            final ProgressDialog progressDialog= new ProgressDialog(getContext());
            progressDialog.show();
            final FirebaseStorage storage = FirebaseStorage.getInstance();
            final StorageReference storageReference = storage.getReference().child("avatars/"+ UUID.randomUUID().toString());
            final UploadTask uploadTask = storageReference.putFile(imgUri);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.dismiss();
                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(final Uri uri) {
                            Log.e("download url","got: "+uri.toString());
                            final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("users");
                            mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot id : dataSnapshot.getChildren()) {
                                        if (id.child("username").getValue().equals(userOriginal.getUsername()))
                                        {
                                            user1.setAvatar(uri.toString());
                                            Log.e("edit user when uri", "userOriginal: "+userOriginal.getUsername()+" -> "+editTextUserName.getText().toString());
                                            mDatabase.child(id.getKey()).setValue(user1);
                                            removeTheLastestImgURI();
                                            break;
                                        }
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    removeTheLastestImgURI();
                                }
                            });
                        }
                    });

                    Toast.makeText(getContext(),"Uploaded",Toast.LENGTH_LONG).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    removeTheLastestImgURI();
                    Toast.makeText(getContext(), "Upload Failed!", Toast.LENGTH_LONG).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                }
            });
        }
        else// save user when no uploading avatar
        {
            final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("users");
            mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot id : dataSnapshot.getChildren()) {
                        if (id.child("username").getValue().equals(userOriginal.getUsername()))
                        {
                            Log.e("edit user when uri null", "userOriginal: "+userOriginal.getUsername()+" -> "+editTextUserName.getText().toString() +" | "+user.getAvatar());
                            mDatabase.child(id.getKey()).setValue(user);
                            break;
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }
    }*/
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
            Log.e("onactivity",""+imgUri.toString());
            imageViewChooseAvatar.setImageURI(imgUri);
        }
    }
    public void removeTheLastestImgURI()
    {
        imgUri = null;
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
}