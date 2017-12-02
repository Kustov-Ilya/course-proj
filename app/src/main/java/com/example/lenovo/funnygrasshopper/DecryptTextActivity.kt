package com.example.lenovo.funnygrasshopper

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.ClipboardManager
import android.util.Base64
import android.util.TypedValue
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast


/**
 * Окно приложения, расшифровывающее текст формата Base64.
 * Расшифрованный текст выводится в окно, который можно скопировать
 * нажатием на него.
 */
class DecryptTextActivity : AppCompatActivity(), View.OnClickListener {

    private var ButDec: Button? = null
    private var DecText: EditText? = null
    private var textr: TextView? = null
    private val rec = GrassHopper()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_decrypt_text)
        val intent = getIntent()
        rec.key = intent.getStringArrayExtra("Hoop")
        ButDec = findViewById(R.id.ButDecText) as Button
        ButDec?.setOnClickListener(this)
        DecText = findViewById(R.id.decrTextofT) as EditText
        textr = findViewById(R.id.TextrezofT) as TextView
        textr?.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25.toFloat())
        textr?.setOnClickListener(this)
    }

    /**
     * Функция, создающая всплывающий текст
     * @param str Текст, который необходимо вывести на экран[String]
     */
    private fun MyToast(str:String) = Toast.makeText(this, str, Toast.LENGTH_SHORT).show()

    override fun onClick(v: View?): Unit = when (v?.id) {
        R.id.ButDecText -> {
            val txt = DecText?.text.toString()
            DecText?.setText("")
            if (txt != "") {
                rec.Parsing(String(Base64.decode(txt.toByteArray(), Base64.NO_WRAP)))
                rec.decrypt()
                textr?.setText(rec.real())!!
            } else MyToast("Add Text")
        }
        R.id.TextrezofT -> {
            val a = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            a.text = textr?.text.toString()
            textr?.setText("")!!
        }
        else -> MyToast("Element of lyaout not found")
    }
}

