package com.example.duepark_admin.Service;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;

import com.example.duepark_admin.R;

public class addListenerOnTextChange implements TextWatcher {

    private Context context;
    private EditText mEditText;

    public addListenerOnTextChange(Context context, EditText mEditText) {
        this.context = context;
        this.mEditText = mEditText;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        mEditText.setCompoundDrawablesWithIntrinsicBounds(0,0, R.drawable.ic_edit,0);
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        Toast.makeText(context, "Text is changing", Toast.LENGTH_SHORT).show();
        //mEditText.setCompoundDrawablesWithIntrinsicBounds(0,0, R.drawable.error, 0);
    }

    @Override
    public void afterTextChanged(Editable editable) {
        Toast.makeText(context, "Text Changed successfully", Toast.LENGTH_SHORT).show();
    }
}
