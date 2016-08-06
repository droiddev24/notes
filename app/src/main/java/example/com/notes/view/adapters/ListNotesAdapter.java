package example.com.notes.view.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;

import example.com.notes.R;
import example.com.notes.common.NConstants;
import example.com.notes.model.NdbAdapter;
import example.com.notes.model.NotesInfo;
import example.com.notes.view.activities.ListNotesActivity;
import example.com.notes.view.activities.NoteDetailsActivity;

/**
 * Created by VISHU on 06-Aug-2016.
 */

public class ListNotesAdapter extends RecyclerView.Adapter<ListNotesAdapter.ViewHolder> {

    private Context context;

    private boolean selectionModeOn = false;
    private ArrayList<NotesInfo> notesList = new ArrayList<>();

    public ListNotesAdapter(Context context, ArrayList<NotesInfo> notesList) {
        this.context = context;
        this.notesList = notesList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_list_notes, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final NotesInfo notesInfo = notesList.get(position);

        if(selectionModeOn){
            holder.noteCb.setVisibility(View.VISIBLE);
        }else {
            holder.noteCb.setVisibility(View.GONE);
        }

        holder.noteCb.setChecked(notesInfo.isSelected());

        holder.titleTv.setText(notesInfo.getTitle());
        holder.noteTv.setText(notesInfo.getContent());

        holder.noteCb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notesInfo.setSelected(holder.noteCb.isChecked());
            }
        });

        holder.parentCv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putSerializable(NConstants.NOTES_INFO, notesInfo);
                Intent addNoteIntent = new Intent(context, NoteDetailsActivity.class);
                addNoteIntent.putExtra(NConstants.POSITION, position);
                addNoteIntent.putExtras(bundle);
                ((ListNotesActivity) context).startActivityForResult(addNoteIntent, NConstants.NOTE_DETAILS_REQUEST);
            }
        });

        holder.parentCv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                selectionModeOn = !selectionModeOn;
                notifyDataSetChanged();
                ((ListNotesActivity)context).toggleActions();
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return notesList.size();
    }

    public void deleteSelected(){
        ArrayList<NotesInfo> notesToDelete = new ArrayList<>();
        NdbAdapter ndbAdapter = ((ListNotesActivity)context).ndbAdapter;
        if(!ndbAdapter.isOpen())
            ndbAdapter.open();
        for(NotesInfo notesInfo : notesList){
            if(notesInfo.isSelected()) {
                notesToDelete.add(notesInfo);
                ndbAdapter.deleteNoteById(notesInfo.getId());
            }
        }
        notesList.removeAll(notesToDelete);
        ndbAdapter.close();
        notifyDataSetChanged();
    }

    public void unselectItems(){
        for(NotesInfo notesInfo : notesList){
            notesInfo.setSelected(false);
        }
    }

    public void addNewNote (NotesInfo notesInfo){
        notesList.add(notesInfo);
        notifyDataSetChanged();
    }

    public ArrayList<NotesInfo> getNotesList() {
        return notesList;
    }

    public void setNotesList(ArrayList<NotesInfo> notesList) {
        this.notesList = notesList;
    }

    public void setAtPosition(int atPosition, NotesInfo notesInfo){
        notesList.set(atPosition, notesInfo);
        notifyDataSetChanged();
    }

    public void deleteAt(int deleteAt){
        notesList.remove(deleteAt);
        notifyDataSetChanged();
    }

    public boolean isSelectionModeOn() {
        return selectionModeOn;
    }

    public void setSelectionModeOn(boolean selectionModeOn) {
        this.selectionModeOn = selectionModeOn;
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {

        protected TextView titleTv;
        protected TextView noteTv;
        protected CardView parentCv;
        protected CheckBox noteCb;

        protected ViewHolder(View itemView) {
            super(itemView);

            titleTv = (TextView) itemView.findViewById(R.id.tv_title);
            noteTv = (TextView) itemView.findViewById(R.id.tv_note);
            parentCv = (CardView) itemView.findViewById(R.id.cv_parent);
            noteCb = (CheckBox) itemView.findViewById(R.id.cb_note);
        }
    }
}
