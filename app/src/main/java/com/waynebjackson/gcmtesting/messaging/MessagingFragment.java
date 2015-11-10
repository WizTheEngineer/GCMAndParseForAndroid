package com.waynebjackson.gcmtesting.messaging;

import android.graphics.Paint;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.waynebjackson.gcmtesting.R;
import com.waynebjackson.gcmtesting.log.Logger;
import com.waynebjackson.gcmtesting.models.GCMUser;
import com.waynebjackson.gcmtesting.string.StringValidator;
import com.waynebjackson.gcmtesting.system.Toaster;

import java.util.HashMap;

public class MessagingFragment extends Fragment implements View.OnClickListener {
    private static final Logger LOGGER = new Logger(MessagingFragment.class.getSimpleName());

    private static final int MINIMUM_MESSAGE_LENGTH = 2;

    private GCMUser mCurrentUser;
    private Toaster mToaster;
    private TextView mSignedInAsTextView, mSignOutTextView;
    private EditText mEmailField, mMessageField;
    private Button mSendButton;

    public MessagingFragment() {
    }

    public static MessagingFragment newInstance() {
        return new MessagingFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCurrentUser = (GCMUser) ParseUser.getCurrentUser();
        mToaster = new Toaster(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_messaging, container, false);
        mSignedInAsTextView = (TextView) rootView.findViewById(R.id.signed_in_as_text_view);
        mSignedInAsTextView.setText(String.format("%s %s.", getString(R.string.signed_in_as),
                mCurrentUser.getEmail()));
        mEmailField = (EditText) rootView.findViewById(R.id.email_field);
        mMessageField = (EditText) rootView.findViewById(R.id.message_field);
        mSendButton = (Button) rootView.findViewById(R.id.send_button);
        mSignOutTextView = (TextView) rootView.findViewById(R.id.sign_out_text_view);

        // Underline the sign out text view
        mSignOutTextView.setPaintFlags(mSignOutTextView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        mSendButton.setOnClickListener(this);
        mSignOutTextView.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.send_button:
                attemptToSendMessage();
                break;
            case R.id.sign_out_text_view:
                signOut();
                break;
        }
    }

    private void attemptToSendMessage() {
        String email = mEmailField.getText().toString().trim();
        String message = mMessageField.getText().toString().trim();

        if (verifyFields(email, message)) {
            sendMessage(email, message);
        }
    }

    private boolean verifyFields(String email, String message) {
        if (StringValidator.isNullOrEmpty(email) || StringValidator.isNullOrEmpty(message)) {
            mToaster.showShortMessage(R.string.empty_fields);
            return false;
        } else if (!StringValidator.isValidEmail(email)) {
            mToaster.showShortMessage(R.string.invalid_email);
            return false;
        } else if (message.length() < MINIMUM_MESSAGE_LENGTH) {
            mToaster.showShortMessage("Message must be at least " + MINIMUM_MESSAGE_LENGTH + " characters.");
            return false;
        } else if (email.equals(mCurrentUser.getEmail())) {
            mToaster.showShortMessage(R.string.cannot_message_self);
            return false;
        }
        return true;
    }

    private void sendMessage(String receiverEmail, String message) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("receiverEmail", receiverEmail);
        params.put("message", message);
        ParseCloud.callFunctionInBackground("sendMessage", params, new FunctionCallback<String>() {

            @Override
            public void done(String successMessage, ParseException e) {
                if (e == null) {
                    mToaster.showShortMessage(R.string.message_success);
                    LOGGER.d("Success calling cloud message function: ", successMessage);
                } else {
                    mToaster.showShortMessage(R.string.message_error);
                    LOGGER.e(e, "Error sending message via parse.");
                }
            }
        });
    }

    private void signOut() {
        ((MessagingActivity) getActivity()).signOut();
    }
}
