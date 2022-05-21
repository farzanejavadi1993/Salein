package ir.kitgroup.salein.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;

import dagger.hilt.android.AndroidEntryPoint;
import ir.kitgroup.salein.classes.Util;

import ir.kitgroup.salein.databinding.ActivityLauncherBinding;


@AndroidEntryPoint
public class LauncherActivity extends AppCompatActivity {
    //region Parameter
    private ActivityLauncherBinding binding;
    //endregion Parameter


    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        binding = ActivityLauncherBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Util.ScreenSize(this);
    }





    //region Override Method

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }



    @Override
    protected void onStart() {
        super.onStart();
        startActivity(getIntent());
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();

    }

    //endregion Override Method

}
