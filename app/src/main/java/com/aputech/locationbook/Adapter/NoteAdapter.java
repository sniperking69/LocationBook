package com.aputech.locationbook.Adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.aputech.locationbook.Model.Note;
import com.aputech.locationbook.R;
import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.util.ArrayList;


public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.MyViewHolder> {

    private ArrayList<Note> NotesList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, year, genre;
        public CardView card;
        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            card = (CardView) view.findViewById(R.id.card);
        }
    }


    public NoteAdapter(ArrayList<Note> NotesList) {
        this.NotesList = NotesList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cards, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Note Note = NotesList.get(position);
        holder.title.setText(Note.getTitle());
        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        holder.card.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return NotesList.size();
    }
}