package example.com.notes.view.activities;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;

import example.com.notes.R;
import example.com.notes.common.NConstants;
import example.com.notes.common.Utils;
import example.com.notes.model.NdbAdapter;
import example.com.notes.model.NotesInfo;
import example.com.notes.view.adapters.ListNotesAdapter;

/**
 * Created by VISHU on 06-Aug-2016.
 */

public class ListNotesActivity extends AppCompatActivity {

    private Context context = this;
    private ListNotesAdapter listNotesAdapter;
    public NdbAdapter ndbAdapter;

    private FloatingActionButton addNoteFab;
    private RecyclerView notesRv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_notes);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tb_action_bar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);

        notesRv = (RecyclerView) findViewById(R.id.rv_notes);
        addNoteFab = (FloatingActionButton) findViewById(R.id.fab_add_notes);

        addNoteFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addNoteIntent = new Intent(context, AddNoteActivity.class);
                startActivityForResult(addNoteIntent, NConstants.ADD_NOTE_REQUEST);
            }
        });

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        notesRv.setLayoutManager(mLayoutManager);
        notesRv.setItemAnimator(new DefaultItemAnimator());

        ndbAdapter = new NdbAdapter(context);
        if(!ndbAdapter.isOpen())
            ndbAdapter.open();
        listNotesAdapter = new ListNotesAdapter(context, ndbAdapter.getAllNotes());
        notesRv.setAdapter(listNotesAdapter);
        ndbAdapter.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_list_notes, menu);
        for(int i = 0; i<menu.size(); i++){
            menu.getItem(i).setVisible(listNotesAdapter.isSelectionModeOn());
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_cancel:
                toggleActions();
                listNotesAdapter.setSelectionModeOn(false);
                listNotesAdapter.unselectItems();
                listNotesAdapter.notifyDataSetChanged();
                break;
            case R.id.action_delete:
                boolean anySelected = false;
                for(NotesInfo notesInfo : listNotesAdapter.getNotesList()){
                    if(notesInfo.isSelected()) {
                        anySelected = true;
                        break;
                    }
                }
                if(anySelected) {
                    confirmDeletion();
                }else{
                    showError();
                }
                break;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode== NConstants.NOTE_DETAILS_REQUEST && resultCode == Activity.RESULT_OK){
            if(data.getExtras() != null) {
                String action = data.getStringExtra(NConstants.ACTION);
                if(action.equals(NConstants.ACTION_EDIT)){
                    NotesInfo notesInfoToPut = (NotesInfo) data.getExtras().getSerializable(NConstants.NOTES_INFO);
                    int position = data.getIntExtra(NConstants.POSITION, -1);
                    listNotesAdapter.setAtPosition(position, notesInfoToPut);
                } else if (action.equals(NConstants.ACTION_DELETE)){
                    int position = data.getIntExtra(NConstants.POSITION, -1);
                    listNotesAdapter.deleteAt(position);
                }
            }
        } else if(requestCode== NConstants.ADD_NOTE_REQUEST && resultCode == Activity.RESULT_OK){
            if(data.getExtras() != null){
                NotesInfo notesInfo = (NotesInfo) data.getExtras().getSerializable(NConstants.NOTES_INFO);
                listNotesAdapter.addNewNote(notesInfo);
            }
        }
    }

    public void toggleActions(){
        invalidateOptionsMenu();
    }

    private void showError(){
        AlertDialog.Builder dialogBuilder = Utils.getAlertDialog(context, "Alert!", "Please selecte at-least one note.");
        dialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog alertDialogObject = dialogBuilder.create();
        alertDialogObject.show();
    }

    private void confirmDeletion(){
        AlertDialog.Builder dialogBuilder = Utils.getAlertDialog(context, "Alert!", "Are you sure you want to delete this note?");
        dialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                toggleActions();
                deleteNotes();
            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                toggleActions();
                dialogInterface.dismiss();
            }
        });
        AlertDialog alertDialogObject = dialogBuilder.create();
        alertDialogObject.show();
    }

    private void deleteNotes(){
        listNotesAdapter.setSelectionModeOn(false);
        listNotesAdapter.deleteSelected();
    }
}
