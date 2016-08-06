package example.com.notes.view.activities;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import example.com.notes.R;
import example.com.notes.common.NConstants;
import example.com.notes.common.Utils;
import example.com.notes.model.NdbAdapter;
import example.com.notes.model.NotesInfo;

/**
 * Created by VISHU on 06-Aug-2016.
 */

public class NoteDetailsActivity extends AppCompatActivity {

    private Context context = this;
    private NotesInfo notesInfo;

    private TextView titleTv;
    private TextView contentTv;

    private int position = -1;
    private boolean noteWasChanged = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_note);
        initToolbar();

        titleTv = (TextView) findViewById(R.id.tv_title);
        contentTv = (TextView) findViewById(R.id.tv_note);
        if (getIntent().getExtras() != null) {
            notesInfo = (NotesInfo) getIntent().getExtras().getSerializable(NConstants.NOTES_INFO);
            titleTv.setText(notesInfo.getTitle());
            contentTv.setText(notesInfo.getContent());
            position = getIntent().getIntExtra(NConstants.POSITION, -1);
        } else {
            Toast.makeText(context, "Something went wrong.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_view_note, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit:
                Bundle bundle = new Bundle();
                bundle.putSerializable(NConstants.NOTES_INFO, notesInfo);
                Intent addNoteIntent = new Intent(context, AddNoteActivity.class);
                addNoteIntent.putExtras(bundle);
                startActivityForResult(addNoteIntent, NConstants.EDIT_NOTE_REQUEST);
                break;
            case R.id.action_delete:
                confirmDeletion();
                break;
            case android.R.id.home:
                if (noteWasChanged) {
                    Bundle resultBundle = new Bundle();
                    resultBundle.putSerializable(NConstants.NOTES_INFO, notesInfo);
                    goBack(resultBundle);
                }
                this.finish();
                break;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == NConstants.EDIT_NOTE_REQUEST && resultCode == Activity.RESULT_OK) {
            if (data.getExtras() != null) {
                notesInfo = (NotesInfo) data.getExtras().getSerializable(NConstants.NOTES_INFO);
                titleTv.setText(notesInfo.getTitle());
                contentTv.setText(notesInfo.getContent());
                noteWasChanged = true;
            }
        }
    }

    private void initToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.tb_action_bar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        upArrow.setColorFilter(0xffffffff, PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
    }

    private void confirmDeletion() {
        AlertDialog.Builder dialogBuilder = Utils.getAlertDialog(context, "Alert!", "Are you sure you want to delete this note?");
        dialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteNote();
            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog alertDialogObject = dialogBuilder.create();
        alertDialogObject.show();
    }

    private void deleteNote() {
        NdbAdapter ndbAdapter = new NdbAdapter(context);
        if (!ndbAdapter.isOpen())
            ndbAdapter.open();
        ndbAdapter.deleteNoteById(notesInfo.getId());
        ndbAdapter.close();
        goBack(null);
    }

    private void goBack(Bundle bundle) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra(NConstants.POSITION, position);
        resultIntent.putExtra(NConstants.ACTION, NConstants.ACTION_DELETE);
        if (bundle != null) {
            resultIntent.putExtras(bundle);
        }
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }
}
