package com.example.coffeeshop;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
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
    private EditText editTextRole;
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
//        fetchDataIntoRecyclerView();

        return v;
    }

    private void fetchDataIntoRecyclerView() {
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users");
        valueEventListenerFetchUser = mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                User user = new User("", "", "", "", false, false, false);
                Log.e("on DataChange","userlist size: "+userList.size());
                for (DataSnapshot id : dataSnapshot.getChildren()) {
                    user = id.getValue(User.class);
                    userList.add(user);
                }
                textViewCurrentUser.setText("User list: "+userList.size()+" users");
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
        floatingActionButtonAddUser = v.findViewById(R.id.floatingActionButtonAddUser);
        textViewCurrentUser = v.findViewById(R.id.textViewCurrentUser);
        floatingActionButtonAddUser.setOnClickListener(new View.OnClickListener() {
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
        // xu ly khi nguoi dung nhan phim back -> xoa img da chon
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
        radioGroupRemove = dialog.findViewById(R.id.radio_group_remove);
        radioGroupModify = dialog.findViewById(R.id.radio_group_modify);
        editTextUserName = dialog.findViewById(R.id.editTextUserName_Add);
        editTextPassword = dialog.findViewById(R.id.editTextPassword_Add);
        editTextRole = dialog.findViewById(R.id.editTextRole_Add);
        imageViewChooseAvatar = dialog.findViewById(R.id.imageViewChooseAvatar);
        imageViewChooseAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosePicture();
            }
        });
        buttonConfirm = dialog.findViewById(R.id.buttonConfirm_Add);
        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editTextPassword.getText().toString().equals("")
                        || editTextRole.getText().toString().equals("")
                        || editTextUserName.getText().toString().equals("")) {
                    Toast.makeText(getContext(), "Please fill out the form!", Toast.LENGTH_SHORT).show();
                }
                else {
                    radioButtonAdd = dialog.findViewById(radioGroupAdd.getCheckedRadioButtonId());
                    radioButtonRemove = dialog.findViewById(radioGroupRemove.getCheckedRadioButtonId());
                    radioButtonModify = dialog.findViewById(radioGroupModify.getCheckedRadioButtonId());
                    uploadPicture();
                    dialog.dismiss();
                }
            }
        });
    }
    public void uploadPicture()
    {
        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("users");
        final User user = new User("", "", "", "", false, false, false);
        user.setUsername(editTextUserName.getText().toString());
        user.setRole(editTextRole.getText().toString());
        user.setPassword(hash(editTextPassword.getText().toString()));
        user.setAdd(Boolean.parseBoolean(radioButtonAdd.getText().toString()));
        user.setRemove(Boolean.parseBoolean(radioButtonRemove.getText().toString()));
        user.setModify(Boolean.parseBoolean(radioButtonModify.getText().toString()));
        // set default avatar for user
        user.setAvatar("https://firebasestorage.googleapis.com/v0/b/tictactoe-c6001.appspot.com/o/avatars%2Fuser-default.png?alt=media&token=2b821695-3438-48cf-ad22-c530c75d991d");
        if(imgUri!=null)
        {
            final ProgressDialog progressDialog= new ProgressDialog(getContext());
            progressDialog.show();
            progressDialog.setTitle("Creating new user");

            final FirebaseStorage storage = FirebaseStorage.getInstance();
            final StorageReference storageReference = storage.getReference().child("avatars/"+ UUID.randomUUID().toString());
            final UploadTask uploadTask = storageReference.putFile(imgUri);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.dismiss();
                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Log.e("download url","got: "+uri.toString());

                            //push new
                            user.setAvatar(uri.toString());
                            DatabaseReference newUserRef = mDatabase.push();
                            newUserRef.setValue(user);

                            removeTheLastestImgURI();
                            Toast.makeText(getContext(),"Create new user successfully!",Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    //push new
                    DatabaseReference newUserRef = mDatabase.push();
                    newUserRef.setValue(user);

                    removeTheLastestImgURI();
                    Toast.makeText(getContext(), "Upload avatar fail!", Toast.LENGTH_LONG).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                }
            });
        }
        else {
            DatabaseReference newUserRef = mDatabase.push();
            newUserRef.setValue(user);
        }
    }
    public void uploadPicture(final User userOriginal)
    {
        final User user = new User("", "", "", "", false, false, false);
        user.setUsername(editTextUserName.getText().toString());
        user.setRole(editTextRole.getText().toString());
        if(editTextPassword.getText().toString().equals(""))// password field is blank
            user.setPassword(userOriginal.getPassword());
        else
            user.setPassword(hash(editTextPassword.getText().toString()));
        //set default avatar
        user.setAvatar(userOriginal.getAvatar());
        user.setAdd(Boolean.parseBoolean(radioButtonAdd.getText().toString()));
        user.setRemove(Boolean.parseBoolean(radioButtonRemove.getText().toString()));
        user.setModify(Boolean.parseBoolean(radioButtonModify.getText().toString()));
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
                                            user.setAvatar(uri.toString());
                                            Log.e("edit user when uri", "userOriginal: "+userOriginal.getUsername()+" -> "+editTextUserName.getText().toString());
                                            mDatabase.child(id.getKey()).setValue(user);
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
        Toast.makeText(this.getContext(), ""+userList.get(position).getUsername(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void OnSettingClick(int position) {
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
        editTextUserName.setText(userList.get(position).getUsername());
        editTextPassword.setText("");
        editTextRole.setText(userList.get(position).getRole());
        if (userList.get(position).getAdd())
            radioGroupAdd.check(R.id.radio1);
        else
            radioGroupAdd.check(R.id.radio2);
        if (userList.get(position).getRemove())
            radioGroupRemove.check(R.id.radio3);
        else
            radioGroupRemove.check(R.id.radio4);
        if (userList.get(position).getModify())
            radioGroupModify.check(R.id.radio5);
        else
            radioGroupModify.check(R.id.radio6);

        imageViewChooseAvatar = dialog.findViewById(R.id.imageViewChooseAvatar);
        Glide.with(imageViewChooseAvatar.getContext())
                .load(userList.get(position).getAvatar())
                .centerCrop()
                .error(R.drawable.ic_round_broken_image_24)
                .placeholder(R.drawable.ic_baseline_image_24)
                .transform(new RoundedCorners(10))
                .into(imageViewChooseAvatar);
        final User userOriginal = new User(userList.get(position).getUsername()
                                    ,userList.get(position).getRole()
                                    ,userList.get(position).getPassword()
                                    ,userList.get(position).getAvatar()
                                    ,userList.get(position).getAdd()
                                    ,userList.get(position).getModify()
                                    ,userList.get(position).getRemove()
                                    );
        Button buttonConfirm = dialog.findViewById(R.id.buttonConfirm_Add);
        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editTextUserName.getText().equals("") || editTextRole.getText().equals(""))
                {
                    Toast.makeText(getContext(), "User Name and Role are required!", Toast.LENGTH_SHORT).show();
                }
                else {
                    radioButtonAdd = dialog.findViewById(radioGroupAdd.getCheckedRadioButtonId());
                    radioButtonRemove = dialog.findViewById(radioGroupRemove.getCheckedRadioButtonId());
                    radioButtonModify = dialog.findViewById(radioGroupModify.getCheckedRadioButtonId());
                    uploadPicture(userOriginal);
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

    @Override
    public void OnDeleteClick(int position) {
        final String userNameCompare = userAdapter.getItem(position).getUsername();

        new AlertDialog.Builder(getContext()).setTitle("Are you sure?").setMessage("This user will be deleted!")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mDatabase = FirebaseDatabase.getInstance().getReference().child("users");
                        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                User user = new User("", "", "", "", false, false, false);
                                for (DataSnapshot id : dataSnapshot.getChildren()) {
                                    user = id.getValue(User.class);
                                    if(user.getUsername().equals(userNameCompare)) {
                                        mDatabase.child(id.getKey()).setValue(null);
                                        Toast.makeText(getContext(), "User has been deleted!",Toast.LENGTH_SHORT);
                                        break;
                                    }
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }
                        });
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getContext(), "Canceled!",Toast.LENGTH_SHORT);
                        Log.e("cancel", "cancel");
                    }
                }).show();
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