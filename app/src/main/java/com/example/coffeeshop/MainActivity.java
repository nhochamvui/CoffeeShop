package com.example.coffeeshop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.*;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends AppCompatActivity{
    private DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users");
        User user = new User("thao", "1234", "admin");
//        createNewUser(user);

        /*retrieveUser(user, new MyCallBack()
        {
            @Override
            public void onCallBack(User user1) {
                if(!user1.isNull())
                    Log.e("RETRIEVE USER", "onCallBack(): " +user1.getUsername());
                else Log.e("RETRIEVE USER", "Sai tên tài khoản hoặc mật khẩu");
            }

            @Override
            public boolean isSuccess(boolean isSuccess) {
                return false;
            }
        });
        */
        User userTest = new User("tho", "1234", "admin", "1");
        updateUser(userTest, new MyCallBack() {
            @Override
            public void onCallBack(User user) {

            }
            @Override
            public boolean isSuccess(boolean isSuccess) {
                Log.e("updateUser", "onCallBack(): " +isSuccess);
                return isSuccess;
            }
        });

        /*
        deleteUser(user, new MyCallBack() {
            @Override
            public void onCallBack(User user) {

            }
            @Override
            public boolean isSuccess(boolean isSuccess) {
                Log.e("DeleteUser", "Delete user: "+isSuccess);
                return isSuccess;
            }
        });
         */
        Item item = new Item("Bánh Phồng Tôm","Cùng với hương vị BBQ tôm hùm nướng, buổi tiệc trà của bạn sẽ thêm đậm hương vị","15000","hinhanh","food");
//        createNewItem(item);
//        updateItem(item, new MyCallBack() {
//            @Override
//            public void onCallBack(User user) {
//
//            }
//
//            @Override
//            public boolean isSuccess(boolean isSuccess) {
//
//                Log.e("update item", "update item: "+isSuccess);
//                return isSuccess;
//            }
//        });
//        deleteItem(item2, new MyCallBack() {
//            @Override
//            public void onCallBack(User user) {
//
//            }
//
//            @Override
//            public boolean isSuccess(boolean isSuccess) {
//                Log.e("Delete item", "Delete item: "+isSuccess);
//                return isSuccess;
//            }
//        });
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
    private void createNewUser(final User user) {
        try {
            final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("users");
            Query query = mDatabase.orderByKey().limitToLast(1);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    int lastID = 0;
                    for (DataSnapshot data : dataSnapshot.getChildren())
                        lastID = Integer.parseInt(data.getKey()) + 1;
                    mDatabase.child("" + lastID).child("username").setValue(user.getUsername());
                    mDatabase.child("" + lastID).child("pwd").setValue(hash(user.getPwd()));
                    mDatabase.child("" + lastID).child("role").setValue(user.getRole());
                    mDatabase.child("" + lastID).child("id").setValue("" + lastID);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } catch (Exception err) {
            Log.d("create user", "" + err);
        }
    }
    private void updateUser(final User user, final MyCallBack myCallBack) {
        try {
            final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("users");
            mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    boolean isSuccess = false;
                    for (DataSnapshot id : dataSnapshot.getChildren()) {
                        Log.e("UPDATE inner class", "liet ke ID: " + id.child("id").getValue() + " user id = "+user.getId());
                        if (id.child("id").getValue().toString().equals(user.getId()))
                        {
                            Log.e("UPDATE inner class", "found ID: " + id.getKey());
                            mDatabase.child(id.getKey()).child("username").setValue(user.getUsername());
                            mDatabase.child(id.getKey()).child("pwd").setValue(hash(user.getPwd()));
                            mDatabase.child(id.getKey()).child("role").setValue(user.getRole());

                            Log.e("UPDATE USER", "inner class: " +user.getUsername());
                            isSuccess = true;
                            break;
                        }
                    }
                    myCallBack.isSuccess(isSuccess);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } catch (Exception err) {
            Log.d("update user", "" + err);
        }
    }
    private void deleteUser(final User user, final MyCallBack myCallBack) {
        try {
            final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("users");
            mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    boolean isSuccess = false;
                    for (DataSnapshot id : dataSnapshot.getChildren()) {
                        if (id.child("username").getValue().equals(user.getUsername()))
                        {
                            Log.e("DELETE inner class", "found ID: " + id.getKey());
                            mDatabase.child(id.getKey()).setValue(null);
                            isSuccess = true;
                            break;
                        }
                    }
                    myCallBack.isSuccess(isSuccess);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } catch (Exception err) {
            Log.d("update user", "" + err);
        }
    }
    //Kiểm tra mật khẩu và trả về đối tượng user
    private void retrieveUser(final User user, final MyCallBack myCallBack) {
        String pwd = new String("");
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users");
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean flag = false;
                User user1 = new User("", "", "");
                String pwd = hash(user.getPwd());
                Log.e("RETRIEVE USER", "HASH: " +pwd);
                for (DataSnapshot id : dataSnapshot.getChildren()) {
                    if (id.child("username").getValue().equals(user.getUsername()))
                    {
                        user1 = id.getValue(User.class);
                        Log.e("RETRIEVE USER", "inner class: " +user1.getUsername());
                        break;
                    }
                }
                myCallBack.onCallBack(user1);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void createNewItem(final Item item) {
        try {
            final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("items");
            mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    int lastID = 0;
                    for (DataSnapshot data : dataSnapshot.getChildren())
                        lastID = Integer.parseInt(data.getKey()) + 1;
                    mDatabase.child("" + lastID).child("name").setValue(item.getName());
                    mDatabase.child("" + lastID).child("desc").setValue(item.getDesc());
                    mDatabase.child("" + lastID).child("price").setValue(item.getPrice());
                    mDatabase.child("" + lastID).child("img").setValue(item.getImg());
                    mDatabase.child("" + lastID).child("category").setValue(item.getCategory());
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } catch (Exception err) {
            Log.d("create user", "" + err);
        }
    }
    private void updateItem(final Item item, final MyCallBack myCallBack) {
        try {
            final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("items");
            mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    boolean isSuccess = false;
                    for (DataSnapshot id : dataSnapshot.getChildren()) {
                        if (id.child("name").getValue().equals(item.getName()))
                        {
                            Log.e("UPDATE inner class", "found ID: " + id.getKey());
                            mDatabase.child(id.getKey()).child("name").setValue(item.getName());
                            mDatabase.child(id.getKey()).child("desc").setValue(item.getDesc());
                            mDatabase.child(id.getKey()).child("img").setValue(item.getPrice());
                            mDatabase.child(id.getKey()).child("price").setValue(item.getImg());
                            mDatabase.child(id.getKey()).child("category").setValue(item.getCategory());
                            Log.e("UPDATE item", "inner class: " +item.getName());
                            isSuccess = true;
                            break;
                        }
                    }
                    myCallBack.isSuccess(isSuccess);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } catch (Exception err) {
            Log.d("update user", "" + err);
        }
    }
    private void deleteItem(final Item item, final MyCallBack myCallBack) {
        try {
            final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("items");
            mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    boolean isSuccess = false;
                    for (DataSnapshot id : dataSnapshot.getChildren()) {
                        if (id.child("name").getValue().equals(item.getName()))
                        {
                            Log.e("DELETE inner class", "found ID: " + id.getKey());
                            mDatabase.child(id.getKey()).setValue(null);
                            isSuccess = true;
                            break;
                        }
                    }
                    myCallBack.isSuccess(isSuccess);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } catch (Exception err) {
            Log.d("update user", "" + err);
        }
    }
}
