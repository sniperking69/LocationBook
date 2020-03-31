package com.aputech.locationbook;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.PathInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.aputech.locationbook.Model.Note;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class NewNote extends AppCompatActivity {
    private static final String DEBUG_TAG = "hellow";
    EditText title, desc;
    LinearLayout add_location, add_alarm;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference notebookRef = db.collection(Objects.requireNonNull(firebaseAuth.getUid()));
    TextView donetext, loc_disp;
    ImageView doneimage;
    private String geoCode;
    private String latLng;
    private FloatingActionButton  floatingActionButton;
    Animation bounce,fabclose;
    private BottomSheetBehavior<LinearLayout> bottomSheetBehavior;
    boolean isOpen =false;
    private View editlayout;
    private ImageView floatingActionButtonclose;
    View bottomView,tableview;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_note);
        Toolbar toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayShowTitleEnabled(false);
        editlayout = findViewById(R.id.editlayout);
        ab.setDisplayHomeAsUpEnabled(true);
        bottomView = findViewById(R.id.bottom_sheet);
        tableview = findViewById(R.id.action_menu);
        toolbar.setBackgroundColor(Color.WHITE);
        loc_disp = findViewById(R.id.loc_disp);
        desc = findViewById(R.id.description);
        title = findViewById(R.id.Title);
        bounce = AnimationUtils.loadAnimation(this,R.anim.bounce);
        floatingActionButton = findViewById(R.id.floatingActionButton);
        floatingActionButtonclose = findViewById(R.id.image_close);
        floatingActionButtonclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideFAB(bottomView);

            }
        });
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    revealFAB(bottomView);
                    tableview.startAnimation(bounce);

                }

        });

    }

    @Override
    public boolean onPrepareOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.newnote_menu, menu);
        MenuItem colorpicker = menu.findItem(R.id.palette);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.done:
                SAVE();
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void SAVE() {
        Note note = new Note();
        note.setTitle(title.getText().toString());
        note.setType(0);
        note.setDesc(desc.getText().toString());
        notebookRef.add(note);
        Toast.makeText(NewNote.this, "Note Added Successfully", Toast.LENGTH_LONG).show();

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                latLng = data.getStringExtra("LatLng");
                geoCode = data.getStringExtra("geocode");
                loc_disp.setText(latLng);
                loc_disp.setVisibility(View.VISIBLE);


            }
        }
    }
    private void revealFAB(final View view) {

        int cx = view.getWidth() ;
        int cy = view.getHeight() ;

        float finalRadius = (float) Math.hypot(cx, cy);

        Animator anim = ViewAnimationUtils.createCircularReveal(view, cx, cy, 0, finalRadius);
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                view.setVisibility(View.VISIBLE);
                floatingActionButton.setVisibility(View.INVISIBLE);
            }
        });
        anim.start();

    }
    private void hideFAB(final View view) {

        int cx = view.getWidth() ;
        int cy = view.getHeight() ;

        float initialRadius = (float) Math.hypot(cx, cy);

        Animator anim = ViewAnimationUtils.createCircularReveal(view, cx, cy, initialRadius, 0);

        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                view.setVisibility(View.INVISIBLE);
                floatingActionButton.setVisibility(View.VISIBLE);
            }
        });

        anim.start();
    }




}
