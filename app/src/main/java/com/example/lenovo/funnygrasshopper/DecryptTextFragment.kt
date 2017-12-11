package com.example.lenovo.funnygrasshopper

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.ClipboardManager
import android.util.Base64
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [DecryptTextFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [DecryptTextFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DecryptTextFragment : Fragment(), View.OnClickListener {

    private var mParam1: String? = null
    private var mParam2: String? = null
    private var ButDec: Button? = null
    private var DecText: EditText? = null
    private var textr: TextView? = null
    val grassHopper = GrassHopper()

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
        val view = inflater!!.inflate(R.layout.fragment_decrypt_text, container, false)
        ButDec = view.findViewById(R.id.button_decrypt_text) as Button
        ButDec?.setOnClickListener(this)
        DecText = view.findViewById(R.id.edit_text_for_decrypt) as EditText
        textr = view.findViewById(R.id.text_decrypted_text_from_text) as TextView
        textr?.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25.toFloat())
        textr?.setOnClickListener(this)

        return view
    }

    override fun onClick(v: View?): Unit = when (v?.id) {
        R.id.button_decrypt_text -> Decrypt()

        R.id.text_decrypted_text_from_text -> SaveText()

        else -> MyToast(getString(R.string.element_of_layout_not_found))
    }

    private fun MyToast(str:String) = Toast.makeText(activity, str, Toast.LENGTH_SHORT).show()

    private fun SaveText(){
        val a = activity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        a.text = textr?.text.toString()
        textr?.setText("")!!
    }

    private fun Decrypt(){
        val mainActivity = activity as MainActivity
        val grassHopper = mainActivity.grassHopper
        if(grassHopper.key[0].length==0){
            MyToast(getString(R.string.enter_key))
            mainActivity.CallDialog()
            return
        }
        val txt = DecText?.text.toString()
        DecText?.setText("")
        if (txt != "") {
            grassHopper.Parsing(String(Base64.decode(txt.toByteArray(), Base64.NO_WRAP)))
            grassHopper.decrypt()
            textr?.setText(grassHopper.real())!!
        } else MyToast(getString(R.string.add_text))
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
         * @return A new instance of fragment DecryptTextFragment.
         */
        fun newInstance(param1: String, param2: String): DecryptTextFragment {
            val fragment = DecryptTextFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }
}// Required empty public constructor
