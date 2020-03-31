package com.aputech.locationbook;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aputech.locationbook.Adapter.NoteAdapter;
import com.aputech.locationbook.Model.Note;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.ArrayList;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference notebookRef = db.collection(Objects.requireNonNull(auth.getUid()));
    private static  String TAG = "pp";
    boolean search_bool;
    ImageView applogotext;
    private RecyclerView recyclerView;
    private Toolbar myToolbar;
   ImageView progressBar ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressBar = findViewById(R.id.progress_anim);
        progressBar.setBackgroundResource(R.drawable.loading);
        AnimationDrawable frameAnimation = (AnimationDrawable) progressBar.getBackground();
        frameAnimation.start();
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,NewNote.class);
                startActivity(intent);
            }
        });
        applogotext = findViewById(R.id.applogotext);
         myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        ImageView backsearch = findViewById(R.id.backsearch);
        backsearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideFAB();
                search_bool=false;
            }
        });
        final ArrayList<Note> noteArrayList = new ArrayList<>();
      recyclerView = findViewById(R.id.item_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        final NoteAdapter adapter = new NoteAdapter(noteArrayList);
        recyclerView.setAdapter(adapter);
        notebookRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    Note note = documentSnapshot.toObject(Note.class);
                    noteArrayList.add(note);
                }

             //   adapter.notifyDataSetChanged();
              //  progressBar.setVisibility(View.GONE);
            }
        });



    }

    @Override
    public boolean onPrepareOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.profile);
        View view =menuItem.getActionView();
        CircleImageView profileImage = view.findViewById(R.id.toolbar_profile_image);
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,SettingsActivity.class);
                startActivity(intent);
            }
        });
        if (auth.getCurrentUser()!=null){
            Glide
                    .with(this)
                    .load(auth.getCurrentUser().getPhotoUrl())
                    .into(profileImage);
        }
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==R.id.search){
            revealFAB();
            search_bool=true;
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onBackPressed() {
        if (search_bool){
            hideFAB();
        }else {
            super.onBackPressed();
        }

    }

    private void revealFAB() {
        final View view = findViewById(R.id.search_view);
        myToolbar.setVisibility(View.INVISIBLE);
        int cx = view.getWidth() -200;
        int cy = view.getHeight()/2 ;

        float finalRadius = (float) Math.hypot(cx, cy);

        Animator anim = ViewAnimationUtils.createCircularReveal(view, cx, cy, 0, finalRadius);
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);

                view.setVisibility(View.VISIBLE);

            }
        });
        anim.start();

    }
    private void hideFAB() {
        final View view = findViewById(R.id.search_view);

        int cx = view.getWidth()-200;
        int cy = view.getHeight()/2 ;

        float initialRadius = (float) Math.hypot(cx, cy);

        Animator anim = ViewAnimationUtils.createCircularReveal(view, cx, cy, initialRadius, 0);

        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                view.setVisibility(View.INVISIBLE);
                myToolbar.setVisibility(View.VISIBLE);

            }
        });

        anim.start();
    }
    
}
