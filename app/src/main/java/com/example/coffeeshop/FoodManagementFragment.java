package com.example.coffeeshop;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FoodManagementFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FoodManagementFragment extends Fragment implements DrinkAdapter.DrinkOnClickListener{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    
    private DatabaseReference mDatabase;
    private ValueEventListener valueEventListenerFetchUser;
    private ArrayList<Drink> drinkArrayList;
    private DrinkAdapter drinkAdapter;
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

    public FoodManagementFragment() {
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
    public static FoodManagementFragment newInstance(String param1, String param2) {
        FoodManagementFragment fragment = new FoodManagementFragment();
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
        drinkAdapter= new DrinkAdapter(FoodManagementFragment.this.getContext(), drinkArrayList, FoodManagementFragment.this);
        recyclerView.setLayoutManager(new GridLayoutManager(FoodManagementFragment.this.getContext(),2));
        recyclerView.setAdapter(drinkAdapter);
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
        drinkArrayList = new ArrayList<Drink>();
        textViewNumberOfProduct = v.findViewById(R.id.textViewNumberOfProduct);
        floatingActionButtonAddDrink = v.findViewById(R.id.floatingActionButtonAddDrink);
        floatingActionButtonAddDrink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewDrink();
            }

        });
    }
    private void addNewDrink() {
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

    private void fetchDataIntoRecyclerView() {
        mDatabase = FirebaseDatabase.getInstance().getReference().child("items");
        valueEventListenerFetchUser = mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.e("on changing drink","detected changed!");
                drinkArrayList.clear();
                Drink drink = new Drink("", "", "","","","");
                for (DataSnapshot id : dataSnapshot.getChildren()) {
                    if(id.child("category").getValue().equals("Food")) {
                        Log.e("fetch drink","fetching drink");
                        drink = id.getValue(Drink.class);
                        drinkArrayList.add(drink);
                    }
                }
                textViewNumberOfProduct.setText("Food list: "+drinkArrayList.size()+" products");
                drinkAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
    private void removeTheLastestImgURI(){
        imgUri = null;
    }
    @Override
    public void onResume() {
        super.onResume();
        Log.e("food","onResume");
        fetchDataIntoRecyclerView();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e("food","onPause");
        mDatabase.removeEventListener(valueEventListenerFetchUser);
    }
    @Override
    public void OnItemClick(int position) {

    }

    @Override
    public void OnSettingClick(final int position) {
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

                Log.e("bug","here");
                return false;
            }
        });
        Drink drink = drinkArrayList.get(position);
        editTextDrinkName = dialog.findViewById(R.id.editTextDrinkName);
        editTextDrinkPrice = dialog.findViewById(R.id.editTextDrinkPrice);
        editTextDrinkDescription = dialog.findViewById(R.id.editTextDrinkDescription);
        editTextDrinkCategory = dialog.findViewById(R.id.editTextDrinkCategory);
//        buttonDrinkChooseImg = dialog.findViewById(R.id.buttonDrinkChooseImg);
        editTextDrinkName.setText(drink.getName());
        editTextDrinkPrice.setText(drink.getPrice());
        editTextDrinkDescription.setText(drink.getDesc());
        editTextDrinkCategory.setText(drink.getCategory());

        Button buttonConfirm = dialog.findViewById(R.id.buttonDrinkConfirm);
        buttonConfirm.setText("Update");
        imageViewDrinkThumbnail = dialog.findViewById(R.id.imageViewDrinkThumbnail);
        Glide.with(imageViewDrinkThumbnail.getContext())
                .load(drinkArrayList.get(position).getImg())
                .centerCrop()
                .error(R.drawable.ic_round_broken_image_24)
                .placeholder(R.drawable.ic_baseline_image_24)
                .transform(new RoundedCorners(10))
                .into(imageViewDrinkThumbnail);

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
                    uploadPicture(drinkArrayList.get(position));
                    dialog.dismiss();
                }
            }
        });
    }

    @Override
    public void OnDeleteClick(int position) {
        final Drink drink = drinkArrayList.get(position);
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
                                    if(id.child("id").getValue().equals(drink.getId())) {
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