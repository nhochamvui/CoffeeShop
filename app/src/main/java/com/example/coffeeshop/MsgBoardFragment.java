package com.example.coffeeshop;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.Fragment;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import java.util.Collections;

import java.util.Collections;

import static android.app.Activity.RESULT_OK;

public class MsgBoardFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int SIGN_IN_REQUEST_CODE = 123;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private AppCompatImageButton fab;
    private SharedPreferences sharedPreferences ;
    private FirebaseListAdapter<Message> adapter;
    private EditText input;
    private ListView listOfMessages;
    private DatabaseReference mDatabase;

    public MsgBoardFragment() {
        // Required empty public constructor
    }
    public static MsgBoardFragment newInstance(String param1, String param2) {
        MsgBoardFragment fragment = new MsgBoardFragment();
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
        View v = inflater.inflate(R.layout.fragment_msg_board, container, false);
        initialComponent(v);
        sharedPreferences = this.getActivity().getSharedPreferences("chat room", Context.MODE_PRIVATE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Read the input field and push a new instance
                // of Message to the Firebase database
                FirebaseDatabase.getInstance().getReference().child("chatroom")
                        .push()
                        .setValue(new Message(input.getText().toString(),
                                sharedPreferences.getString("display_name","Anonymous"))
                        );

                // Clear the input
                input.setText("");
            };
        });
        input.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // showMyDialog();
                fab.setVisibility(getView().VISIBLE);
            }
        });
        input.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    // showMyDialog();
                    fab.setVisibility(getView().INVISIBLE);
                }
                else {
                    fab.setVisibility(getView().VISIBLE);
                }
            }
        });
        displayChatMessages();
        //set up layout for RecyclerView
        return v;
    }
    public void initialComponent(View v)
    {
        fab = v.findViewById(R.id.fab);
        input = v.findViewById(R.id.input);
        listOfMessages = v.findViewById(R.id.list_of_messages);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == SIGN_IN_REQUEST_CODE) {
            if(resultCode == RESULT_OK) {
                Toast.makeText(getActivity(),
                        "Successfully signed in. Welcome!",
                        Toast.LENGTH_LONG)
                        .show();
                displayChatMessages();
            } else {
                Toast.makeText(getActivity(),
                        "We couldn't sign you in. Please try again later.",
                        Toast.LENGTH_LONG)
                        .show();
            }
        }

    }
    public void displayChatMessages(){
        Query messages = FirebaseDatabase.getInstance().getReference().child("chatroom");
        FirebaseListOptions<Message> options = new FirebaseListOptions.Builder<Message>()
                .setQuery(messages, Message.class)
                .setLayout(R.layout.message)
                .build();
        adapter = new FirebaseListAdapter<Message>(options) {
            @Override
            protected void populateView(View v, Message model, int position) {
                // Get references to the views of message.xml
                TextView messageText = (TextView)v.findViewById(R.id.message_text);
                TextView messageUser = (TextView)v.findViewById(R.id.message_user);
                TextView messageTime = (TextView)v.findViewById(R.id.message_time);

                // Set their text
                messageText.setText(model.getMessageText());
                messageUser.setText(model.getMessageUser());

                // Format the date before showing it
                messageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)",
                        model.getMessageTime()));
            }
        };
        listOfMessages.setAdapter(adapter);
        adapter.startListening();
    }

}