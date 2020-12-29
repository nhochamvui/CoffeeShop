package com.example.coffeeshop;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import android.text.Layout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
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

import java.util.ArrayList;
import java.util.UUID;

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
    
    private DatabaseReference mDatabase;
    private ValueEventListener valueEventListenerFetchUser;
    private ArrayList<Sewer> sewerArrayList;
    private SewerAdapter sewerAdapter;
    private RecyclerView recyclerView;
    private FloatingActionButton floatingActionButtonAddDrink;
    private EditText editTextDrinkName;
    private EditText editTextDrinkPrice;
    private EditText editTextDrinkDescription;
    private EditText editTextDrinkCategory;
    private Button buttonDrinkChooseImg;
    private ImageView imageViewDrinkThumbnail;
    private final int PICK_IMAGE_REQUEST = 233;
    private Uri imgUri;
    private TextView textViewNumberOfProduct;

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
        View v = inflater.inflate(R.layout.fragment_drink_management, container, false);
        initialComponent(v);

        //set up layout for RecyclerView
        sewerAdapter = new SewerAdapter(SewerManagementFragment.this.getContext(), sewerArrayList, SewerManagementFragment.this);
        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        recyclerView.setLayoutManager(new GridLayoutManager(SewerManagementFragment.this.getContext(),2));
        recyclerView.setAdapter(sewerAdapter);
        recyclerView.setHasFixedSize(true);
        final int spacing = getResources().getDimensionPixelSize(R.dimen.spacing)/2;
        recyclerView.setPadding(spacing, spacing, spacing, spacing);
        recyclerView.setClipToPadding(false);
        recyclerView.setClipChildren(false);
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                int position = parent.getChildAdapterPosition(view);
                if(position == 0 || position ==1)
                    outRect.set(spacing+20, spacing+17, 0, spacing);
                else
                    outRect.set(spacing+20, spacing+24, 0, spacing);
            }
        });
        return v;
    }

    private void initialComponent(View v) {
        recyclerView = v.findViewById(R.id.recyclerViewDrinkManagement);
        sewerArrayList = new ArrayList<Sewer>();
        textViewNumberOfProduct = v.findViewById(R.id.textViewNumberOfProduct);
        floatingActionButtonAddDrink = v.findViewById(R.id.floatingActionButtonAddDrink);
        floatingActionButtonAddDrink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), SewerCreateActivity.class);
                SewerManagementFragment.this.startActivity(intent);
            }
        });
    }
    private void addNewSewer() {
        final AppCompatDialog dialog = new AppCompatDialog(this.getContext());
        dialog.setContentView(R.layout.dialog_drink_add);
        dialog.show();
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
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
//        dialog.getWindow().setAttributes(layoutParams);
        editTextDrinkName = dialog.findViewById(R.id.editTextDrinkName);
        editTextDrinkPrice = dialog.findViewById(R.id.editTextDrinkPrice);
        editTextDrinkDescription = dialog.findViewById(R.id.editTextDrinkDescription);
        editTextDrinkCategory = dialog.findViewById(R.id.editTextDrinkCategory);
        editTextDrinkCategory.setText("Food");
        Button buttonConfirm = dialog.findViewById(R.id.buttonDrinkConfirm);
        buttonConfirm.setText("Create");
        imageViewDrinkThumbnail = dialog.findViewById(R.id.imageViewDrinkThumbnail);
        imageViewDrinkThumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosePicture();
            }
        });

        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editTextDrinkName.getText().toString().equals("")
                        || editTextDrinkPrice.getText().toString().equals("")
                        || editTextDrinkDescription.getText().toString().equals("")
                        || editTextDrinkCategory.getText().toString().equals("")) {
                    Toast.makeText(getContext(), "Please fill out the form!", Toast.LENGTH_SHORT).show();
                }
                else {
                    uploadPicture();
                    dialog.dismiss();
                }
            }
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data!=null && data.getData() != null)
        {
            imgUri = data.getData();
            Log.e("onactivity",""+imgUri.toString());
            imageViewDrinkThumbnail.setImageURI(imgUri);
        }
    }

    private void uploadPicture() {
        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("items");
        final DatabaseReference newDrinkRef = mDatabase.push();
        final Drink drink = new Drink("", "", "", "","","");
        drink.setName(editTextDrinkName.getText().toString());
        drink.setPrice(editTextDrinkPrice.getText().toString());
        drink.setCategory(editTextDrinkCategory.getText().toString());
        drink.setDesc(editTextDrinkDescription.getText().toString());

        if(imgUri == null) {
            drink.setImg("https://firebasestorage.googleapis.com/v0/b/tictactoe-c6001.appspot.com/o/drinks%2Fly-tra.png?alt=media&token=6d1d4b9a-a31f-47ab-996f-e9afc2274aa9");
            drink.setId(newDrinkRef.getKey());
            newDrinkRef.setValue(drink);
        }
        else {
            final ProgressDialog progressDialog = new ProgressDialog(getContext());
            progressDialog.show();
            progressDialog.setTitle("Creating new product");

            final FirebaseStorage storage = FirebaseStorage.getInstance();
            final StorageReference storageReference = storage.getReference().child("drinks/" + UUID.randomUUID().toString());
            final UploadTask uploadTask = storageReference.putFile(imgUri);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.dismiss();
                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Log.e("download url", "got: " + uri.toString());
                            drink.setId(newDrinkRef.getKey());
                            drink.setImg(uri.toString());
                            newDrinkRef.setValue(drink);
                            removeTheLastestImgURI();
                            Toast.makeText(getContext(), "Insert new product successfully!", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    removeTheLastestImgURI();
                    Toast.makeText(getContext(), "Insert new product fail!", Toast.LENGTH_LONG).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                }
            });
        }
    }

    private void choosePicture() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Choose Picture"), PICK_IMAGE_REQUEST);
    }

    private void uploadPicture(final Drink drink) {
        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("items");

        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.show();
        progressDialog.setTitle("Edit product");

        drink.setName(editTextDrinkName.getText().toString());
        drink.setPrice(editTextDrinkPrice.getText().toString());
        drink.setCategory(editTextDrinkCategory.getText().toString());
        drink.setDesc(editTextDrinkDescription.getText().toString());
        Log.e("drink img","at "+drink.getImg());
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean isExist = false;
                for(final DataSnapshot id : dataSnapshot.getChildren())
                {
                    if(id.child("id").getValue().equals(drink.getId())) {
                        //if user change the picture
                        if (imgUri != null) {
                            Log.e("imgUri", "imgUri at " + imgUri.toString());
                            final FirebaseStorage storage = FirebaseStorage.getInstance();
                            final StorageReference storageReference = storage.getReference().child("drinks/" + UUID.randomUUID().toString());
                            final UploadTask uploadTask = storageReference.putFile(imgUri);
                            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                                    progressDialog.dismiss();
                                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            Log.e("download url", "Upload product image successfully at : " + uri.toString());
                                            drink.setImg(uri.toString());
//                                            Toast.makeText(getContext(), "Upload product image successfully!", Toast.LENGTH_LONG).show();
                                            mDatabase.child(id.getKey()).setValue(drink);
                                            removeTheLastestImgURI();
                                        }
                                    });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.dismiss();
                                    Toast.makeText(getContext(), "Upload product image fail!", Toast.LENGTH_LONG).show();
                                    mDatabase.child(id.getKey()).setValue(drink);
                                    removeTheLastestImgURI();
                                }
                            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                                }
                            });
                        }
                        else{
                            progressDialog.dismiss();
                            mDatabase.child(id.getKey()).setValue(drink);
                        }
                        Log.e("item is exist","1. item is exist in database");
                        isExist = true;
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    // load data from firebase and fetch to recycler view
    private void fetchDataIntoRecyclerView() {
        mDatabase = FirebaseDatabase.getInstance().getReference().child("sewer");
        valueEventListenerFetchUser = mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.e("ON CHANGING SEWER","detected changed!");
                sewerArrayList.clear();
                Sewer sewer = new Sewer("", "", "","","", "");
                for (DataSnapshot sewerObject : dataSnapshot.getChildren()) {
                        Log.e("Fetch Sewer","Fetching Sewer");
                        sewer = sewerObject.getValue(Sewer.class);
                        Log.e("Fetch Sewer","getId: "+sewer.getId());
                        Log.e("Fetch Sewer","getName: "+sewer.getName());
                        Log.e("Fetch Sewer","getDesc: "+sewer.getDesc());
                        Log.e("Fetch Sewer","getCategory: "+sewer.getCategory());
                        Log.e("Fetch Sewer","getLocation: "+sewer.getLocation());
                        Log.e("Fetch Sewer","getChannel: "+sewer.getChannel());
                        sewerArrayList.add(sewer);
                }
                textViewNumberOfProduct.setText("Sewer list: "+sewerArrayList.size()+" sewers");
                sewerAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void removeTheLastestImgURI(){
        imgUri = null;
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
        mDatabase.removeEventListener(valueEventListenerFetchUser);
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

        bottomSheetView.findViewById(R.id.bottomSheetEditOption).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    bottomSheetView.findViewById(R.id.bottomSheetEditOption).setBackground(getResources().getDrawable(R.drawable.background_gradient_color));
                    bottomSheetDialog.dismiss();
                    Intent intent = new Intent(getContext(), SewerEditActivity.class);
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
        bottomSheetView.findViewById(R.id.bottomSSheetDeleteOption).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    bottomSheetView.findViewById(R.id.bottomSSheetDeleteOption).setBackground(getResources().getDrawable(R.drawable.background_gradient_color));
                    bottomSheetDialog.dismiss();
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
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();

//        SewerDetailFragment sewerDetailFragment= new SewerDetailFragment();
//        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, sewerDetailFragment)
//                .addToBackStack("SewerManagementFragment")
//                .commit();
//        Log.e("done", "done");
    }
    public void deleteSewer(final Sewer sewer){
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.show();
        progressDialog.setTitle("Đang xóa");
        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("sewer");
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot id : dataSnapshot.getChildren())
                {
                    if(id.child("id").getValue().equals(sewer.getId())) {
                        mDatabase.child(id.getKey()).setValue(null).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getContext(), "Đã xóa", Toast.LENGTH_SHORT).show();
                            }
                        })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getContext(), "Đã xảy ra lỗi: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        progressDialog.dismiss();
    }
    @Override
    public void OnDeleteClick(int position) {
        final Sewer sewer = sewerArrayList.get(position);
        new AlertDialog.Builder(getContext()).setTitle("Are you sure?").setMessage("This product will be deleted!")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("items");
                        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for(DataSnapshot id : dataSnapshot.getChildren())
                                {
                                    if(id.child("id").getValue().equals(sewer.getId())) {
                                        mDatabase.child(id.getKey()).setValue(null);
                                        Log.e("ok", "ok");
                                        Toast.makeText(getContext(), "Deleted!",Toast.LENGTH_SHORT);
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
}