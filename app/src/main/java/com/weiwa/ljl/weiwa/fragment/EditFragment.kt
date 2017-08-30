package com.weiwa.ljl.weiwa.fragment


import android.app.Activity
import android.app.Fragment
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import com.weiwa.ljl.weiwa.R
import com.weiwa.ljl.weiwa.activity.MainActivity
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import java.io.IOException


/**
 * A simple [Fragment] subclass.
 */
class EditFragment : Fragment() {

    private var onpost: MainActivity.onPost? = null
    private var mView: View? = null
    private val editText: EditText by lazy { mView!!.findViewById(R.id.editor) as EditText }
    private val hint: TextView by lazy { mView!!.findViewById(R.id.hint) as TextView }
    internal val addImage: ImageButton by lazy { mView!!.findViewById(R.id.edit_fragment_image) as ImageButton }
    internal val addCamera: ImageButton by lazy { mView!!.findViewById(R.id.edit_fragment_camera) as ImageButton }
    private val image: com.weiwa.ljl.weiwa.view.TouchImageView by lazy { mView!!.findViewById(R.id.selectImage) as com.weiwa.ljl.weiwa.view.TouchImageView }
    private var pic: ByteArray? = null
    private var imageUri: Uri? = null

    fun setOnPost(post: MainActivity.onPost) {
        onpost = post
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_edit, container, false)
        addCamera.setOnClickListener {
            // 设定action和miniType
            val intent = Intent()
            intent.action = Intent.ACTION_PICK
            intent.type = "image/*"
            intent.putExtra("return-data", true)
            // 以需要返回值的模式开启一个Activity
            startActivityForResult(intent, Activity.RESULT_FIRST_USER)
        }
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable) {
                hint.text = "还可输入" + (140 - editText!!.text.length) + "字"
            }
        })
        (activity as MainActivity).setOnAddButtonClickListener(View.OnClickListener {
            if (pic == null) {
                onpost!!.post(editText.text.toString())
            } else {
                onpost!!.post(editText.text.toString(), pic!!)
            }
            (activity as MainActivity).popBack()
        })
        return mView
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // 获取原图的Uri，它是一个内容提供者
        val uri: Uri
        if (data != null && data.data != null) {
            uri = data.data
        } else if (imageUri != null) {
            uri = imageUri!!
        } else {
            return
        }
        try {
            image.setImageBitmap(MediaStore.Images.Media.getBitmap(activity.contentResolver, uri))
            val fis = activity.contentResolver.openInputStream(uri)
            val byteBuffer = ByteArrayOutputStream()
            val bufferSize = 1024 * 8
            val buffer = ByteArray(bufferSize)
            var len = 0
            while ({ len = fis.read(buffer);len }() != -1) {
                byteBuffer.write(buffer, 0, len)
            }
            pic = byteBuffer.toByteArray()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}// Required empty public constructor
