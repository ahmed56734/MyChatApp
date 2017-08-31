package com.example.ahmed.mychatapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.ahmed.mychatapp.widget.UpdateWidgetService;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.ahmed.mychatapp.Utils.setActive;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;


    @BindView(R.id.toolbar)
    Toolbar toolbar;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);







        mAuth = FirebaseAuth.getInstance();




    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sign_out_item:

                setActive(mAuth.getCurrentUser().getUid(), false);
                AuthUI.getInstance().signOut(this);
                UpdateWidgetService.startActionUpdateFriendsWidget(this);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }









}
