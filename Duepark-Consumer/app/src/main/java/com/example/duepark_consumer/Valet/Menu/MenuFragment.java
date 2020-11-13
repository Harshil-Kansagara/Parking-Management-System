package com.example.duepark_consumer.Valet.Menu;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.duepark_consumer.Employee.DetailEmployeeActivity;
import com.example.duepark_consumer.Help.HelpActivity;
import com.example.duepark_consumer.Helper.SessionManagerHelper;
import com.example.duepark_consumer.HomeActivity;
import com.example.duepark_consumer.R;
import com.example.duepark_consumer.Valet.ValetHomeActivity;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class MenuFragment extends Fragment {

    private static final String TAG = "MenuFragment";
    private String base_url;
    private TextView logoutBtn;
    private SessionManagerHelper sessionManagerHelper;
    private HashMap<String, String> employeeDetails;
    private CircleImageView profilePic;
    private TextView loginUserName, loginUserDesignation;
    private ImageView detailBtn;
    private TextView incomeBtn, helpBtn;
    private static int RESULT_LOAD_IMAGE = 1;
    private Uri pickImageUri = null;
    private Bitmap bitmap;

    public MenuFragment(){}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container, false);

        base_url = getResources().getString(R.string.base_url);
        sessionManagerHelper  = new SessionManagerHelper(getContext());
        employeeDetails = sessionManagerHelper.getEmployeeDetails();

        loginUserName = view.findViewById(R.id.loginUserName);
        loginUserDesignation = view.findViewById(R.id.loginUserDesignation);
        profilePic = view.findViewById(R.id.profile_photo);
        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Build.VERSION.SDK_INT >= 23)
                {
                    checkAndRequestForPermission();
                }
                else
                {
                    pickImage();
                }
            }
        });

        detailBtn = view.findViewById(R.id.detailBtn);
        detailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(view.getContext(), DetailEmployeeActivity.class);
                i.putExtra("EmployeeId", employeeDetails.get(SessionManagerHelper.EmployeeId));
                i.putExtra("IsValet",true);
                i.putExtra("IsEmployeeActive","1");
                startActivity(i);
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                getActivity().finish();
            }
        });

        incomeBtn = view.findViewById(R.id.incomeBtn);
        incomeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(view.getContext(), ValetIncomeActivity.class);
                startActivity(i);
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                getActivity().finish();
            }
        });

        helpBtn = view.findViewById(R.id.helpBtn);
        helpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(view.getContext(), HelpActivity.class);
                i.putExtra("IsValet", true);
                startActivity(i);
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                getActivity().finish();
            }
        });

        setUserDetail();

        logoutBtn = view.findViewById(R.id.logoutBtn);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BackgroundTask backgroundTask = new BackgroundTask(view.getContext());
                backgroundTask.execute("logout");
                //sessionManagerHelper.logoutEmployee();
            }
        });

        return view;
    }

    private void setUserDetail(){
        //String baseUrl = "https://duepark.000webhostapp.com/profilePic/";
        loginUserName.setText(employeeDetails.get(SessionManagerHelper.EmployeeName));
        loginUserDesignation.setText(employeeDetails.get(SessionManagerHelper.EmployeeRole));
        if(!Objects.equals(employeeDetails.get(SessionManagerHelper.EmployeeProfilePic), "null")){
            Picasso.get().load(base_url+"profilePic/"+employeeDetails.get(SessionManagerHelper.EmployeeProfilePic)+".png").into(profilePic);
        }
        else{
            Picasso.get().load(R.drawable.userphoto).into(profilePic);
            //profilePic.setImageResource(R.drawable.userphoto);
        }
    }

    // For image
    public void pickImage() {
        Intent i = new Intent(
                Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, RESULT_LOAD_IMAGE);
    }

    private void cropRequest(Uri imageUri) {
        CropImage.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMultiTouchEnabled(true)
                .start(Objects.requireNonNull(getActivity()));
    }

    private void checkAndRequestForPermission( ) {
        if(ContextCompat.checkSelfPermission(Objects.requireNonNull(getContext()), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            if(ActivityCompat.shouldShowRequestPermissionRationale(Objects.requireNonNull(getActivity()), Manifest.permission.READ_EXTERNAL_STORAGE))
            {
                Toast.makeText(getContext(), "Please accept for required permission", Toast.LENGTH_SHORT).show();
            }
            else
            {
                int PReqCode = 1;
                ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PReqCode);
            }
        }
        else
        {
            pickImage();
        }
    }

    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data){
            Uri selectedImage = data.getData();
            CropImage.activity(selectedImage)
                    .start(Objects.requireNonNull(getActivity()));
        }
        else if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == RESULT_OK && requestCode==RESULT_LOAD_IMAGE) {
            Uri imageUri = CropImage.getPickImageResultUri(Objects.requireNonNull(getContext()), data);
            cropRequest(imageUri);
        }
        else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                pickImageUri = result.getUri();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), pickImageUri);
                    profilePic.setImageBitmap(bitmap);
                    BackgroundTask backgroundTask = new BackgroundTask(getContext());
                    backgroundTask.execute("update");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 123) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("Duepark", "Permission has been granted");

            } else {
                Log.d("Duepark", "Permission has been denied or request cancelled");
            }
        }
    }

    class BackgroundTask extends AsyncTask<String, Void, String>{

        private Context context;
        private ProgressDialog progressDialog;

        BackgroundTask(Context ctx){
            context = ctx;
            progressDialog = new ProgressDialog(context);
        }

        @Override
        protected void onPreExecute() {
            progressDialog.show();
            progressDialog.setCanceledOnTouchOutside(false);
        }

        @Override
        protected String doInBackground(String... params) {
            if(params[0].equals("update")) {
                progressDialog.setMessage("Updating profile pic...");
                String update_profilePicUrl = base_url + "update_employeeProfilePic.php";
                try {
                    URL url = new URL(update_profilePicUrl);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    OutputStream ops = httpURLConnection.getOutputStream();
                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(ops, "UTF-8"));
                    String data = URLEncoder.encode("EmployeeId", "UTF-8") + "=" + URLEncoder.encode(employeeDetails.get(SessionManagerHelper.EmployeeId), "UTF-8") + "&" +
                            URLEncoder.encode("EmployeeProfilePic", "UTF-8") + "=" + URLEncoder.encode(getStringImage(bitmap), "UTF-8");
                    bw.write(data);
                    bw.flush();
                    bw.close();
                    ops.close();

                    InputStream ips = httpURLConnection.getInputStream();
                    BufferedReader br = new BufferedReader(new InputStreamReader(ips, "iso-8859-1"));
                    StringBuilder result = new StringBuilder();
                    String line = "";
                    while ((line = br.readLine()) != null) {
                        result.append(line);
                    }
                    br.close();
                    ips.close();
                    httpURLConnection.disconnect();
                    Log.d(TAG, "doInBackground: " + result.toString());
                    return result.toString();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else{
                progressDialog.setMessage("Logging out...");
                String logout_url = base_url+"logout.php?EmployeeId="+employeeDetails.get(SessionManagerHelper.EmployeeId);
                try{
                    URL url = new URL(logout_url);
                    HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                    httpURLConnection.setRequestMethod("GET");
                    httpURLConnection.setDoOutput(true);
                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                    StringBuilder stringBuilder = new StringBuilder();
                    String line;

                    while((line = bufferedReader.readLine())!=null){
                        stringBuilder.append(line+"\n");
                    }

                    httpURLConnection.disconnect();

                    String result = stringBuilder.toString().trim();
                    Log.d(TAG, "doInBackground: "+result);
                    inputStream.close();
                    bufferedReader.close();
                    return result;
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if(result.equals("LogoutSuccessfully")){
                sessionManagerHelper.logoutEmployee();
            }
            progressDialog.dismiss();
        }
    }
}
