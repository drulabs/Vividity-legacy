package org.drulabs.vividvidhi.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import org.drulabs.vividvidhi.PresenterCreator;
import org.drulabs.vividvidhi.R;
import org.drulabs.vividvidhi.dto.User;
import org.drulabs.vividvidhi.utils.Store;

public class ReqAccessActivity extends AppCompatActivity implements ReqAccessContract.View,
        View.OnClickListener {

    // UI References
    EditText etName;
    EditText etUsername;
    EditText etPassword;
    EditText etRelationship;
    View btnReqAccess;
    ImageView imgProfile;
    View reqAccessHolder;

    ProgressDialog dialog;

    // Request access presenter
    ReqAccessContract.Presenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_access);
        setToolBarTitle(getString(R.string.txt_request_access));

        initializeUI();

        PresenterCreator.createReqAccessPresenter(this, this);

        presenter.start();
    }

    void initializeUI() {
        etName = (EditText) findViewById(R.id.req_access_name);
        etUsername = (EditText) findViewById(R.id.req_access_username);
        etPassword = (EditText) findViewById(R.id.req_access_password);
        etRelationship = (EditText) findViewById(R.id.req_access_relationship);

        imgProfile = (ImageView) findViewById(R.id.pic_profile);
        imgProfile.setOnClickListener(this);

        reqAccessHolder = findViewById(R.id.activity_request_access);

        btnReqAccess = findViewById(R.id.req_access_action);
        btnReqAccess.setOnClickListener(this);

        dialog = new ProgressDialog(ReqAccessActivity.this);
        dialog.setMessage(getString(R.string.loading_please_wait));
        dialog.setCanceledOnTouchOutside(false);

        String savedName = Store.getInstance(this).getMyName();
        if (savedName != null) {
            etName.setText(savedName);
        }
    }

    @Override
    public void setPresenter(ReqAccessContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        presenter.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        presenter.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.destroy();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.pic_profile:
                if (etUsername.getText().toString().trim().length() > 0) {
                    presenter.launchImagePicker();
                } else {
                    onEmptyUserNameError();
                }
                break;
            case R.id.req_access_action:
                User user = new User();
                user.setName(etName.getText().toString());
                user.setAdmin(false);
                user.setPassword(etPassword.getText().toString());
                user.setRelationShip(etRelationship.getText().toString());
                user.setUsername(etUsername.getText().toString());
                user.setPicName(Store.getInstance(this).getPicName());
                presenter.handleAccessRequest(user);
                break;
            default:
                break;
        }
    }

    @Override
    public void onNoProfilePicError() {
        showSnackBar(getString(R.string.no_profile_pic));
    }

    @Override
    public void onEmptyUserNameError() {
        showSnackBar(getString(R.string.empty_username_msg));
    }

    @Override
    public void onNameError(String message) {
        etName.setError(message);
    }

    @Override
    public void onUsernameError(String message) {
        etUsername.setError(message);
    }

    @Override
    public void onPasswordError(String message) {
        etPassword.setError(message);
    }

    @Override
    public void onRelationshipEmptyError() {
        etRelationship.setError(getString(R.string.field_cannot_be_empty));
    }

    @Override
    public void onRequestSentSuccessfully() {
        showSnackBar(getString(R.string.user_registration_request_successful));
    }

    @Override
    public void onAlreadyRegistered() {
        showSnackBar(getString(R.string.user_already_requested_msg));
    }

    @Override
    public void onImageAvailable(Bitmap image) {
        imgProfile.setImageBitmap(image);
    }

    @Override
    public void onAlreadyGranted() {
        showSnackBar(getString(R.string.already_registered_msg));
    }

    @Override
    public void onError(String displayMessage) {
        showSnackBar(displayMessage);
    }

    @Override
    public void showLoadingIndicator() {
        dialog.show();
    }

    @Override
    public void hideLoadingIndicator() {
        dialog.dismiss();
    }

    void setToolBarTitle(String title) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }
    }

    void showSnackBar(String message) {
        Snackbar.make(reqAccessHolder, message, Snackbar.LENGTH_SHORT).show();
    }

}
