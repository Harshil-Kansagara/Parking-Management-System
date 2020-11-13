package com.example.duepark_admin.Menu;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.duepark_admin.HomeActivity;
import com.example.duepark_admin.Menu.Employee.DetailEmployeeActivity;
import com.example.duepark_admin.Menu.Employee.EmployeeActivity;
import com.example.duepark_admin.R;
import com.example.duepark_admin.Service.SessionManager;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class MenuFragment extends Fragment {

    private View view;
    private SessionManager sessionManager;
    private TextView loginUserDesignation, loginUserName;
    private HashMap<String, String> user;
    private CircleImageView profilePic;

    public MenuFragment() { }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_menu, container, false);

        sessionManager = new SessionManager(getContext());
        user = sessionManager.getUserDetails();

        loginUserName = view.findViewById(R.id.loginUserName);
        loginUserDesignation = view.findViewById(R.id.loginUserDesignation);
        profilePic = view.findViewById(R.id.profile_photo);

        setUserDetail();

        ImageView backBtn = view.findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backToHome();
            }
        });

        ImageView detailBtn = view.findViewById(R.id.detailBtn);
        detailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent detailActivity = new Intent(getContext(), DetailEmployeeActivity.class);
                detailActivity.putExtra("userid", user.get(sessionManager.EmployeeId));
                detailActivity.putExtra("username", user.get(sessionManager.EmployeeName));
                detailActivity.putExtra("generatedemployeeid", user.get(sessionManager.GeneratedEmployeeId));
                detailActivity.putExtra("back", "back");
                getContext().startActivity(detailActivity);
            }
        });

        TextView employeeList = view.findViewById(R.id.employeeList);

        if(Objects.equals(user.get(sessionManager.EmployeeRole), "Sale")){
            employeeList.setVisibility(View.GONE);
        }else {
            employeeList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent employeeActivity = new Intent(getContext(), EmployeeActivity.class);
                    getContext().startActivity(employeeActivity);
                }
            });
        }

        TextView logoutBtn = view.findViewById(R.id.logOutBtn);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sessionManager.logoutUser();
            }
        });

        return view;
    }

    private void setUserDetail(){
        String baseUrl = "https://duepark.000webhostapp.com/profilePic/";
        loginUserName.setText(user.get(sessionManager.EmployeeName));
        loginUserDesignation.setText(user.get(sessionManager.EmployeeRole));
        /*if(!Objects.equals(user.get(sessionManager.EmployeeProfilePic), "null")){
            Picasso.get().load(baseUrl+user.get(sessionManager.EmployeeProfilePic)+".png").into(profilePic);
        }
        else{
            Picasso.get().load(R.drawable.userphoto).into(profilePic);
            //profilePic.setImageResource(R.drawable.userphoto);
        }*/
    }

    private void backToHome() {
        startActivity(new Intent(getContext(), HomeActivity.class));
        getActivity().overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        Objects.requireNonNull(getActivity()).finish();
    }


}
