package com.example.xkfeng.mycat.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.xkfeng.mycat.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;

public class VoiceFragment extends Fragment {

    @BindView(R.id.inVoiceImg)
    CircleImageView inVoiceImg;
    Unbinder unbinder;
    private View view;
    private Context mContext;
    private boolean isClicked = false ;
    private static final String TAG = "VoiceFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.voice_fragment_layout, container, false);
        mContext = getContext();
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.inVoiceImg)
    public void onViewClicked(View view) {

        if (!isClicked){
            isClicked = true ;
            Glide.with(mContext)
                    .load(R.drawable.ic_invoice_blue)
                    .into(inVoiceImg) ;
        }else {
            isClicked = false ;

            Glide.with(mContext)
                    .load(R.drawable.ic_invoice_black)
                    .into(inVoiceImg) ;


        }
    }
}
