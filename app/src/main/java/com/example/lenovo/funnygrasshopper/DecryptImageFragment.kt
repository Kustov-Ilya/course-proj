package com.example.lenovo.funnygrasshopper

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.text.ClipboardManager
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [DecryptImageFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [DecryptImageFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DecryptImageFragment : Fragment(), View.OnClickListener {

    private var mParam1: String? = null
    private var mParam2: String? = null
    private var Load: Button? = null
    private var bitmap: Bitmap? = null
    private var ButDec: Button? = null
    private var textr: TextView? = null

    private var mListener: OnFragmentInteractionListener? = null

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
        val view =  inflater!!.inflate(R.layout.fragment_decrypt_image, container, false)

        ButDec = view.findViewById(R.id.button_decrypt_image) as Button
        ButDec?.setOnClickListener(this)
        textr = view.findViewById(R.id.text_decrypted_text_from_image) as TextView
        textr?.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25.toFloat())
        textr?.setOnClickListener(this)
        Load = view.findViewById(R.id.button_load_image_decrypt) as Button
        Load?.setOnClickListener(this)
        return view
    }

    override fun onClick(v: View?): Unit = when (v?.id) {
        R.id.text_decrypted_text_from_image -> CopyText()
        R.id.button_decrypt_image -> Decrypt()

        R.id.button_load_image_decrypt -> startActivityForResult(Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI), 1)

        else -> MyToast(getString(R.string.element_of_layout_not_found))
    }

    /**
     * Функция, создающая всплывающий текст
     * @param str Текст, который необходимо вывести на экран[String]
     */
    private fun MyToast(str: String) = Toast.makeText(activity, str, Toast.LENGTH_SHORT).show()

    private fun CopyText(){
        val a = activity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        a.text = textr?.text.toString()
        textr?.setText("")!!
        MyToast(getString(R.string.text_copied))
    }

    private fun Decrypt(){
        val mainActivity = activity as MainActivity
        val grassHopper = mainActivity.grassHopper
        if(grassHopper.key[0].length==0){
            MyToast(getString(R.string.enter_key))
            mainActivity.CallDialog()
            return
        }
        if (bitmap != null) {
            val text = Stego(bitmap!!).Stegano_decrypt()
            if (text != "") {
                grassHopper.Parsing(text)
                grassHopper.decrypt()
                textr?.setText(grassHopper.real())!!
            } else MyToast(getString(R.string.image_cannot_be_decrypted))
        } else MyToast(getString(R.string.please_add_image))
    }

    fun onButtonPressed(uri: Uri) {
        if (mListener != null) {
            mListener!!.onFragmentInteraction(uri)
        }
    }

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
                if (bitmap != null) bitmap = null
                bitmap = MediaStore.Images.Media.getBitmap(activity.contentResolver, data?.data)
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
         * @return A new instance of fragment DecryptImageFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String, param2: String): DecryptImageFragment {
            val fragment = DecryptImageFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }
}// Required empty public constructor
