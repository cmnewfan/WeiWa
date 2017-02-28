package com.weiwa.ljl.weiwa;


import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;


/**
 * A simple {@link Fragment} subclass.
 */
public class EditFragment extends Fragment {

    private MainActivity.onPost onpost;
    private View mView;
    private EditText editText;
    private TextView hint;
    private ImageButton addImage;
    private ImageButton addCamera;
    private byte[] pic;
    private Uri imageUri;


    public EditFragment() {
        // Required empty public constructor
    }

    public void setOnPost(MainActivity.onPost post){
        onpost = post;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_edit, container, false);
        editText = (EditText) mView.findViewById(R.id.editor);
        hint = (TextView) mView.findViewById(R.id.hint);
        addImage = (ImageButton) mView.findViewById(R.id.upload_confirm);
        addCamera = (ImageButton) mView.findViewById(R.id.camera);
        addCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 设定action和miniType
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File photo = new File(WeiwaApplication.CacheCategory, "Pic.jpg");
                intent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photo));
                imageUri = Uri.fromFile(photo);
                startActivityForResult(intent, getActivity().RESULT_FIRST_USER);
            }
        });
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                hint.setText("还可输入"+(140-editText.getText().length())+"字");
            }
        });
        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 设定action和miniType
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_PICK);
                intent.setType("image/*");
                intent.putExtra("return-data",true);
                // 以需要返回值的模式开启一个Activity
                startActivityForResult(intent, getActivity().RESULT_FIRST_USER);
            }
        });
        ((MainActivity)getActivity()).setOnAddButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pic==null) {
                    onpost.post(editText.getText().toString());
                }else{
                    onpost.post(editText.getText().toString(),pic);
                }
                ((MainActivity)getActivity()).popBack();
            }
        });
        return mView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 获取原图的Uri，它是一个内容提供者
        Uri uri;
        if(data!=null&&data.getData()!=null ){
            uri = data.getData();
        }else {
            uri = imageUri;
        }
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
            //thumbnail.setImageBitmap(bitmap);
            InputStream fis = getActivity().getContentResolver().openInputStream(uri);
            pic = new byte[8194];
            fis.read(pic);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
