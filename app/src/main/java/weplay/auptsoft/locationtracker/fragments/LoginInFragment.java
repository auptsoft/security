package weplay.auptsoft.locationtracker.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.json.JSONException;

import java.net.HttpURLConnection;

import weplay.auptsoft.locationtracker.HomeActivity;
import weplay.auptsoft.locationtracker.R;
import weplay.auptsoft.locationtracker.StartupActivity;
import weplay.auptsoft.locationtracker.controllers.ServerUtil;
import weplay.auptsoft.locationtracker.controllers.Utility;
import weplay.auptsoft.locationtracker.databinding.FragmentLoginBinding;
import weplay.auptsoft.locationtracker.models.AppState;
import weplay.auptsoft.locationtracker.models.User;
import weplay.auptsoft.locationtracker.view_models.UserViewModel;

/**
 * Created by Andrew on 5.2.19.
 */

public class LoginInFragment extends Fragment{

    FragmentLoginBinding fragmentLoginBinding;
    UserViewModel userViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userViewModel = new UserViewModel();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        fragmentLoginBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false);
        fragmentLoginBinding.setUserViewModel(userViewModel);
        View view = fragmentLoginBinding.getRoot();

       // AppCompatActivity appCompatActivity = (AppCompatActivity)getActivity();
        //appCompatActivity.getSupportActionBar().setTitle("Login");

        fragmentLoginBinding.emailEdit.addTextChangedListener(textWatcher);

        fragmentLoginBinding.registerTextView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                StartupActivity.viewPager.setCurrentItem(0, true);
            }
        });

        fragmentLoginBinding.loginBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //debug start
                /*AppState.skipStartup = true;
                User user = new User();
                user.setFirstName("Andrew");
                user.setLastName("Oshodin");
                user.setEmail("andrewoshodin@gmail.com");
                user.setPhoneNumber("08143914876");
                AppState.currentUser = user;
                try {
                    User.saveToPreference(AppState.sharedPreferences, AppState.currentUser);
                } catch (JSONException je) {

                }
                Intent intent = new Intent(getContext(), HomeActivity.class);
                startActivity(intent);
                getActivity().finish(); */
                //debug end;

                login();   //release
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
            fragmentLoginBinding.emailInputLayout.setError(userViewModel.getEmailErrorMsg());
            //fragmentLoginBinding.passwordInputLayout.setError(userViewModel.getPasswordMsg());
        }
    };

    public void login() {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Logging in...");
        progressDialog.show();
        /*try {
            Toast.makeText(getContext(), UserViewModel.toJson(userViewModel), Toast.LENGTH_LONG).show(); //debug
        } catch (JSONException je) {

        } */
        Utility.login(userViewModel, AppState.sharedPreferences, new ServerUtil.OnResultListener() {
            @Override
            public void onResult(String data, HttpURLConnection httpURLConnection) {
                //Toast.makeText(getContext(), data, Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getContext(), HomeActivity.class);
                progressDialog.hide();
                startActivity(intent);
                getActivity().finish();
            }

            @Override
            public void onError(String errorString) {
                progressDialog.hide();
                Toast.makeText(getContext(), errorString, Toast.LENGTH_LONG).show();
            }
        });
    }
}
