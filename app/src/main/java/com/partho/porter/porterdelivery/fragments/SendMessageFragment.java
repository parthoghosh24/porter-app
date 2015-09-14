package com.partho.porter.porterdelivery.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.partho.porter.porterdelivery.R;


/**
 * Created by partho on 29/8/15.
 */
public class SendMessageFragment extends DialogFragment {

    private final static String TAG="porter_send_msg";
    private Context mContext=null;
    private EditText number;
    private Button send,cancel;
    private View dialog;
    private String link;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext=getActivity();
        dialog=inflater.inflate(R.layout.message_prompt, container, false);
        initWidgets();
        link=getArguments().getString("link");
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(link))
                {
                    Toast.makeText(mContext,"Please try again!",Toast.LENGTH_SHORT).show();
                    dismiss();
                }
                else
                {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(number.getText().toString(), null, link, null, null);
                    Toast.makeText(mContext,"Message sent successfully",Toast.LENGTH_SHORT).show();
                    dismiss();

                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        return dialog;
    }

    private void initWidgets()
    {
        number=(EditText)dialog.findViewById(R.id.porter_number_input);
        send=(Button)dialog.findViewById(R.id.porter_number_send);
        cancel=(Button)dialog.findViewById(R.id.porter_number_cancel);

    }

}
