package com.example.xkfeng.mycat.Fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.xkfeng.mycat.R;

public class SendInvitationFragment extends Fragment {


    private static final String TAG = "SendInvitationFragment";

    private View view ;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.friend_send_invitation_layout , container ,false) ;

        return view ;
    }
}
