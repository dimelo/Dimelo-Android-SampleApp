package com.dimelo.sampleapp.chats;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ViewFlipper;

import com.dimelo.dimelosdk.main.Chat;
import com.dimelo.dimelosdk.main.Dimelo;
import com.dimelo.sampleapp.R;

public class TabTelecom extends Fragment implements SampleDimeloTab {
    static private String CHAT_STATE_KEY = "tab_telecom_chat_state";
    static private int CLOSED = 0;
    static private int OPEN = 1;

    private ViewFlipper mViewFlipper;
    private Chat mDimeloChat;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(CHAT_STATE_KEY, mViewFlipper.getDisplayedChild() == 0 ? CLOSED : OPEN);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tab_telecom, container, false);
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FragmentManager childFragmentManager = getChildFragmentManager();
        mDimeloChat = (Chat)childFragmentManager.findFragmentByTag("dimelo_telecom_chat");
        if (mDimeloChat == null) {
            mDimeloChat = Dimelo.getInstance().newChatFragment();
            FragmentTransaction fragmentTransaction = childFragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.chat_telecom_container, mDimeloChat, "dimelo_telecom_chat");
            fragmentTransaction.commit();
        }
        mDimeloChat.setUserVisibleHint(false);

        mViewFlipper = (ViewFlipper) view.findViewById(R.id.viewflipper);
        mViewFlipper.setOutAnimation(view.getContext(), android.support.v7.appcompat.R.anim.abc_shrink_fade_out_from_bottom);
        view.findViewById(R.id.live_support).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mViewFlipper.setInAnimation(view.getContext(), android.support.v7.appcompat.R.anim.abc_grow_fade_in_from_bottom);
                openChat();
            }
        });

        if (savedInstanceState != null){
            int chatState = savedInstanceState.getInt(CHAT_STATE_KEY, CLOSED);
            if (chatState == OPEN){
                openChat();
            }
        }
        customize();
    }

    private void openChat(){
        Dimelo.getInstance().setUserIdentifier("Tab Telecom");
        Dimelo.getInstance().setUserName("Tab Telecom");
        mViewFlipper.setDisplayedChild(1);
        mDimeloChat.setUserVisibleHint(true);
    }


    private void customize(){
        Chat.Customization customisation = mDimeloChat.getCustomization();

        customisation.backgroundColor = getResources().getColor(R.color.purple_500);
        customisation.inputbarBackgroundColor = getResources().getColor(R.color.purple_50);

        customisation.setUserMessageBubbleDrawable(R.drawable.row_user_message_bubble, getResources().getColor(R.color.purple_800));
        customisation.setAgentMessageBubbleDrawable(R.drawable.row_agent_message_bubble, Color.WHITE);
        customisation.setSystemMessageBubbleDrawable(R.drawable.row_system_message_bubble, Color.rgb(0xd0, 0xd3, 0xd9));

        customisation.userMessageTextColor = Color.WHITE;

        customisation.agentMessageTextColor = Color.BLACK;
        customisation.agentNameColor = Color.BLACK;

        customisation.systemMessageTextColor = Color.BLACK;
        customisation.dateTextColor = Color.WHITE;

        customisation.apply();
    }

    @Override
    public boolean isHandlingBack(){
        if (isVisible() && (mViewFlipper.getDisplayedChild() == 1)) {
            mViewFlipper.setDisplayedChild(0);
            mDimeloChat.setUserVisibleHint(false);
            return true;
        }
        return false;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mDimeloChat = null;
        mViewFlipper = null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // DimeloChat is a nested Fragment, we must notify it about onActivityResult
        mDimeloChat.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // DimeloChat is a nested Fragment, we must notify it about onRequestPermissionsResult
        mDimeloChat.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

}