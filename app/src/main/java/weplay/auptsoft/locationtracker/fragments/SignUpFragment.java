package weplay.auptsoft.locationtracker.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.QuickContactBadge;
import android.widget.Toast;

import java.io.Console;
import java.net.HttpURLConnection;

import weplay.auptsoft.locationtracker.HomeActivity;
import weplay.auptsoft.locationtracker.R;
import weplay.auptsoft.locationtracker.StartupActivity;
import weplay.auptsoft.locationtracker.controllers.ServerUtil;
import weplay.auptsoft.locationtracker.controllers.Utility;
import weplay.auptsoft.locationtracker.databinding.FramentSignUpBinding;
import weplay.auptsoft.locationtracker.models.AppState;
import weplay.auptsoft.locationtracker.view_models.UserViewModel;

/**
 * Created by Andrew on 5.2.19.
 */

public class SignUpFragment extends Fragment {

    FramentSignUpBinding framentSignUpBinding;

    UserViewModel userViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userViewModel = new UserViewModel();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        framentSignUpBinding = DataBindingUtil.inflate(inflater, R.layout.frament_sign_up, container, false);
        View view = framentSignUpBinding.getRoot();
        framentSignUpBinding.setUserViewModel(userViewModel);
        //AppCompatActivity appCompatActivity = (AppCompatActivity)getActivity();
        //appCompatActivity.getSupportActionBar().setTitle("Register");

        framentSignUpBinding.firstNameEdit.addTextChangedListener(textWatcher);
        framentSignUpBinding.lastNameEdit.addTextChangedListener(textWatcher);
        framentSignUpBinding.emailEdit.addTextChangedListener(textWatcher);
        framentSignUpBinding.phoneNumberEdit.addTextChangedListener(textWatcher);
        framentSignUpBinding.passwordEdit.addTextChangedListener(textWatcher);
        framentSignUpBinding.confirmPasswordEdit.addTextChangedListener(textWatcher);

        framentSignUpBinding.loginTextView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                StartupActivity.viewPager.setCurrentItem(1, true);
            }
        });

        framentSignUpBinding.registerBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                AppState.skipStartup = true;

                if (userViewModel.getIsValidated()) {
                    //debug start
                    /*Toast.makeText(getContext(), "validated", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getContext(), HomeActivity.class);
                    startActivity(intent);
                    getActivity().finish(); */
                    //debug end

                    submit();   //release()
                } else {
                    Toast.makeText(getContext(), "invalid input", Toast.LENGTH_SHORT).show();
                }

            }
        });

        return view;
    }


    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            framentSignUpBinding.firstNameInputLayout.setError(userViewModel.getFirstNameMsg());
            framentSignUpBinding.lastNameInputLayout.setError(userViewModel.getLastNameMsg());
            framentSignUpBinding.emailInputLayout.setError(userViewModel.getEmailErrorMsg());
            framentSignUpBinding.phoneNumberInputLayout.setError(userViewModel.getPhoneNumberMsg());
            framentSignUpBinding.passwordInputLayout.setError(userViewModel.getPasswordMsg());
            framentSignUpBinding.confirmPasswordInputLayout.setError(userViewModel.getConfirmPasswordMsg());
        }
    };

    public void submit() {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Registering...");
        progressDialog.show();
        Utility.register(userViewModel, new ServerUtil.OnResultListener() {
            @Override
            public void onResult(String data, HttpURLConnection httpURLConnection) {
                progressDialog.hide();
                //Toast.makeText(getContext(), data, Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getContext(), HomeActivity.class);
                startActivity(intent);
                getActivity().finish();
            }

            @Override
            public void onError(String errorString) {
                progressDialog.hide();
                Toast.makeText(getContext(), errorString, Toast.LENGTH_LONG).show();
                //Utility.useWebView(getContext(), AppState.REGISTRATION_URL, "POST", "");
            }
        });
    }
}
