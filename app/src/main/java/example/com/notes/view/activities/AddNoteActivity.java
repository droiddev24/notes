package example.com.notes.view.activities;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

import example.com.notes.R;
import example.com.notes.common.NConstants;
import example.com.notes.common.Utils;
import example.com.notes.model.NdbAdapter;
import example.com.notes.model.NotesInfo;

/**
 * Created by VISHU on 06-Aug-2016.
 */

public class AddNoteActivity extends AppCompatActivity {

    private Context context = this;
    private NotesInfo notesInfo;

    private EditText titleEt;
    private EditText contentEt;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_note);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        titleEt = (EditText) findViewById(R.id.et_title);
        contentEt = (EditText) findViewById(R.id.et_note);
        if (getIntent().getExtras() != null) {
            notesInfo = (NotesInfo) getIntent().getExtras().getSerializable(NConstants.NOTES_INFO);
            titleEt.setText(notesInfo.getTitle());
            contentEt.setText(notesInfo.getContent());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_add_note, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_done:
                validateEmptyFields();
                break;
            case android.R.id.home:
                this.finish();
                break;
        }
        return true;
    }

    private void validateEmptyFields() {
        String title = titleEt.getText().toString();
        String content = contentEt.getText().toString();
        StringBuilder errorSb = new StringBuilder();
        if (title.isEmpty()) {
            errorSb.append("Please enter a title. ");
        }
        if (content.isEmpty()) {
            errorSb.append("Please enter a note. ");
        }
        if (errorSb.length() > 0) {
            showError(errorSb.toString());
        } else {
            saveNote(title, content);
        }
    }

    private void showError(String error) {
        AlertDialog.Builder dialogBuilder = Utils.getAlertDialog(context, "Error", error);
        dialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialogObject = dialogBuilder.create();
        alertDialogObject.show();
    }

    private void saveNote(String title, String content) {
        boolean hasToSave = false;
        if (notesInfo == null) {
            notesInfo = new NotesInfo();
            hasToSave = true;
        } else {
            hasToSave = whetherToSave(title, content);
        }
        if(hasToSave) {
            NdbAdapter ndbAdapter = new NdbAdapter(this);
            if (!ndbAdapter.isOpen())
                ndbAdapter.open();
            notesInfo.setContent(content);
            notesInfo.setTitle(title);
            ndbAdapter.addUpdateNote(notesInfo);
            ndbAdapter.close();
            goBack(notesInfo);
        }
        finish();
    }

    private void goBack(NotesInfo notesInfo){
        Intent resultIntent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable(NConstants.NOTES_INFO, notesInfo);
        resultIntent.putExtras(bundle);
        setResult(Activity.RESULT_OK, resultIntent);
    }

    private boolean whetherToSave(String title, String content){
        return notesInfo.getTitle().equals(title) || notesInfo.getContent().equals(content);
    }
}
