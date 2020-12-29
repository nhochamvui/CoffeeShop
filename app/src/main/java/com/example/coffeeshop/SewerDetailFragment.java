package com.example.coffeeshop;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.github.nkzawa.emitter.Emitter;

import org.json.JSONException;

import java.net.URISyntaxException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SewerDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SewerDetailFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ImageView videoContainer;
    private Socket mySocket;
    int i = 0;
    public SewerDetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SewerDetailFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SewerDetailFragment newInstance(String param1, String param2) {
        SewerDetailFragment fragment = new SewerDetailFragment();
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
        Log.e("Sewer Detail Fragment", "we are here");
        View v = inflater.inflate(R.layout.fragment_sewer_detail, container, true);
        videoContainer = v.findViewById(R.id.imageView_videoContainer);
        videoContainer.setImageDrawable(null);
        v.invalidate();
        videoContainer.setImageDrawable(getResources().getDrawable(R.drawable.drink_background));
        // setting up the socket
        if(setUpSocket()){
            mySocket.on("imageSendAndroid", onNewMessage);
            boolean isConnected = mySocket.connect().connected();
        }
        else{
            Log.e("Socket", "connection failed");
        }
        return inflater.inflate(R.layout.fragment_sewer_detail, container, false);
    }

    private boolean setUpSocket(){
        try{
            this.mySocket = IO.socket("http://104.155.233.176:3000/");
            return true;
        }
        catch (URISyntaxException e){
            Log.e("Socket","URI Syntax Exception: "+e.getMessage());
            return false;
        }
    }

    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.e("SocketIO", "connected: "+mySocket.connected());
//                    byte[] data = (byte[]) args[0];
                    byte[] data = Base64.decode((byte[]) args[0], Base64.DEFAULT | Base64.NO_WRAP);
                    Bitmap image;

                    try{
                        if(i==0){
                            image = BitmapFactory.decodeByteArray(data, 0, data.length);
//                            videoContainer.setImageBitmap(image);
                            image = Bitmap.createScaledBitmap(image, 100, 100, false);
                            videoContainer.setImageDrawable(getResources().getDrawable(R.drawable.drink_background));
//                            videoContainer.setImageBitmap(image);
//                            videoContainer.setVisibility(View.GONE);
//                            videoContainer.setVisibility(View.VISIBLE);
                            i++;
                            Log.e("fuck", "duck");
                        }

                    }
                    catch (Exception e){
                        Log.e("decode", "err: "+e.getMessage());
                    }
//                    image.setImageBitmap(Bitmap.createScaledBitmap(bmp, image.getWidth(), image.getHeight(), false));

                }
            });
        }
    };
}