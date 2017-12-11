package com.example.lenovo.funnygrasshopper

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.text.ClipboardManager
import android.util.Base64
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [EncryptFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [EncryptFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class EncryptFragment : Fragment(), View.OnClickListener{

    // TODO: Rename and change types of parameters
    private var mParam1: String? = null
    private var mParam2: String? = null
    private var ButEnc: Button? = null
    private var EncText: EditText? = null
    private var image: ImageView? = null
    private var AddImg: Button? = null
    private var bitmap: Bitmap? = null
    private var textView: TextView? = null
    private var uri: Uri? = null
    private var mListener: OnFragmentInteractionListener? = null

    //fun ClearGrass() = grassHopper.key.fill("")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mParam1 = arguments.getString(ARG_PARAM1)
            mParam2 = arguments.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view =  inflater!!.inflate(R.layout.fragment_encrypt, container, false)
        ButEnc = view.findViewById(R.id.button_encrypt) as Button
        ButEnc?.setOnClickListener(this)
        EncText = view.findViewById(R.id.edit_text_for_encrypt) as EditText
        image = view.findViewById(R.id.image) as ImageView
        image?.setOnClickListener(this)
        AddImg = view.findViewById(R.id.button_load_image_encrypt) as Button
        AddImg?.setOnClickListener(this)
        textView = view.findViewById(R.id.text_result) as TextView
        textView?.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25.toFloat())
        textView?.setOnClickListener(this)
        return view
    }

    override fun onClick(v: View?): Unit = when (v?.id) {
        R.id.text_result -> CopyText()

        R.id.button_encrypt -> Encrypt()

        R.id.button_load_image_encrypt -> startActivityForResult(Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI), 1)

        R.id.image -> LoadImage()

        else -> MyToast(getString(R.string.element_of_layout_not_found))
    }

    fun onButtonPressed(uri: Uri) {
        if (mListener != null) {
            mListener!!.onFragmentInteraction(uri)
        }
    }

    /**
     * Функция, создающая всплывающий текст
     * @param str Текст, который необходимо вывести на экран[String]
     */
    private fun MyToast(str: String) = Toast.makeText(activity, str, Toast.LENGTH_SHORT).show()

    private fun CopyText(){
        val a = activity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        a.text = textView?.text.toString()
        textView?.setText("")
        MyToast(getString(R.string.copied_cipher))
    }

    private fun Encrypt(){

        val mainActivity = activity as MainActivity
        val grassHopper = mainActivity.grassHopper
        if(grassHopper.key[0].length==0){
            MyToast(getString(R.string.enter_key))
            mainActivity.CallDialog()
            return
        }

        val text = EncText?.text.toString()
        if (text != "") {
            EncText?.setText("")
            grassHopper.Parsing(text)
            grassHopper.encrypt()
            if (bitmap == null) {
                textView?.setText(Base64.encodeToString(grassHopper.real().toByteArray(), Base64.NO_WRAP))
                MyToast(getString(R.string.ecrypt_without_stegano))
            } else {
                image?.setImageBitmap(Stego(bitmap!!).Stegano_encrypt(grassHopper.real()))
                MyToast(getString(R.string.encrypt_and_stegano_done))
            }
        } else MyToast(getString(R.string.enter_text_for_encrypt))
    }

    private fun LoadImage() = if (bitmap != null) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            val filepach = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).path
            val dir = File(filepach + "/FunnyGrassHopper")
            if (!dir.exists()) dir.mkdirs()
            val file = File(dir, "grasshopper${getDate()}.png")

            try {
                val output = FileOutputStream(file)
                bitmap?.compress(Bitmap.CompressFormat.PNG, 100, output)
                output.flush()
                output.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            activity.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)))
            bitmap = null
            image?.visibility = View.INVISIBLE
            MyToast(getString(R.string.saved))
        } else MyToast(getString(R.string.sd_not_found))
    } else MyToast(getString(R.string.image_not_found))

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            mListener = context
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            1 -> if (resultCode == Activity.RESULT_OK) {
                uri = data?.data
                bitmap = MediaStore.Images.Media.getBitmap(activity.contentResolver, uri).copy(Bitmap.Config.ARGB_8888, true)
                image?.visibility = View.VISIBLE
                MyToast(getString(R.string.image_is_load))
            }
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html) for more information.
     */
    interface OnFragmentInteractionListener {
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        private val ARG_PARAM1 = "param1"
        private val ARG_PARAM2 = "param2"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment EncryptFragment.
         */
        fun newInstance(param1: String, param2: String): EncryptFragment {
            val fragment = EncryptFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }
}// Required empty public constructor

/**
 * Функция получания даты и времени
 * @return Значение даты и времени типа [String]
 */
@SuppressLint("SimpleDateFormat")
private fun getDate():String{
    return SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(Calendar.getInstance().time)
}