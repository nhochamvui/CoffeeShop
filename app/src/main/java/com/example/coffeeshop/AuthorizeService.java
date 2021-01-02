package com.example.coffeeshop;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AuthorizeService {
    private User user;
    private DatabaseReference mDatabase;

    public AuthorizeService(String userName){
        this.mDatabase = FirebaseDatabase.getInstance().getReference().child("users");
        retrieveUser(userName);

    }

    public void setUser(User user){
        this.user = user;
    }

    public User getUser(){
        return this.user;
    }

    public void retrieveUser(final String userName) {
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot id : dataSnapshot.getChildren()) {
                    if (id.child("username").getValue().equals(userName))
                    {
                        setUser(id.getValue(User.class));
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public boolean isAdmin(){
        return this.user.getRole().equals("Admin") ? true : false;
    }
    public boolean canCreate(){
        return this.user.getAdd();
    }
    public boolean canModify(){
        return this.user.getModify();
    }
    public boolean canRemove(){
        return this.user.getRemove();
    }
}
