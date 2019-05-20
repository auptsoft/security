package weplay.auptsoft.locationtracker.fragments;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import weplay.auptsoft.locationtracker.HomeActivity;
import weplay.auptsoft.locationtracker.R;
import weplay.auptsoft.locationtracker.StartupActivity;
import weplay.auptsoft.locationtracker.databinding.FragmentProfileBinding;
import weplay.auptsoft.locationtracker.models.AppState;
import weplay.auptsoft.locationtracker.models.User;
import weplay.auptsoft.locationtracker.view_models.UserViewModel;

/**
 * Created by Andrew on 5.2.19.
 */

public class ProfileFragment extends Fragment {
    FragmentProfileBinding fragmentProfileBinding;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentProfileBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false);
        View view = fragmentProfileBinding.getRoot();

        //User user = new User(0, "FirstName", "LastName", "firstnamelastname@gmail.com", "08143914876", "");

        User user = AppState.currentUser;
        if (AppState.currentUser == null) {
            user = new User();
        }

        /*AppCompatActivity appCompatActivity = (AppCompatActivity)getActivity();
        appCompatActivity.getSupportActionBar().setTitle("Profile"); */

        ((HomeActivity)getActivity()).toolbar.setTitle("User Profile");

        UserViewModel userViewModel = new UserViewModel(user);
        fragmentProfileBinding.setUserViewModel(userViewModel);

        fragmentProfileBinding.logoutBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                AppState.sharedPreferences.edit().clear();
                Intent intent = new Intent(getContext(), StartupActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        return view;
    }
}
