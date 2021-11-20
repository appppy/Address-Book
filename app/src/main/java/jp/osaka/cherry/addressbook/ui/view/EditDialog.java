/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jp.osaka.cherry.addressbook.ui.view;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;

import jp.osaka.cherry.addressbook.R;
import jp.osaka.cherry.addressbook.android.IDialog;

import static jp.osaka.cherry.addressbook.constants.EXTRA.EXTRA_FILE_NAME;


/**
 * 作成ダイアログ
 */
public class EditDialog extends AppCompatDialogFragment implements IDialog {

    /**
     * @serial 自身
     */
    private EditDialog mSelf;

    /**
     * @serial コールバック
     */
    private Callbacks<String> mCallbacks;

    /**
     * @serial 項目
     */
    private String mItem;

    /**
     * インスタンスの取得
     *
     * @param   item 項目
     */
    public static EditDialog newInstance(String item) {

        EditDialog f = new EditDialog();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putString(EXTRA_FILE_NAME, item);
        f.setArguments(args);

        return f;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mItem = getArguments().getString(EXTRA_FILE_NAME);
        }
        mSelf = this;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        mCallbacks = (Callbacks<String>) getActivity();

        // カスタム表示を設定
        LayoutInflater inflater = (LayoutInflater) requireActivity().getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        final View layout = inflater.inflate(R.layout.dialog_edit, null);

        @SuppressLint("CutPasteId") final DialogInterface.OnClickListener positive = (dialog, which) -> {
            EditText id;
            id = layout.findViewById(R.id.edit1);
            mCallbacks.onPositiveButtonClicked(mSelf, id.getText().toString());
        };
        final DialogInterface.OnClickListener negative = (dialog, which) -> mCallbacks.onNegativeButtonClicked(mSelf, "");

        // Setup EditText
        @SuppressLint("CutPasteId") EditText editText1 = layout.findViewById(R.id.edit1);
        if (editText1 != null) {
            editText1.setText(mItem);
            //editText1.setInputType(InputType.TYPE_CLASS_TEXT);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        AlertDialog dialog;
        builder.setIcon(R.drawable.ic_create_black_24dp);
        builder.setView(layout);
        builder.setPositiveButton(android.R.string.ok, positive);
        builder.setNegativeButton(android.R.string.cancel, negative);
        dialog = builder.create();

        return dialog;
    }

}
