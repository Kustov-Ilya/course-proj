package com.example.lenovo.funnygrasshopper

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.text.ClipboardManager
import android.util.TypedValue
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

/**
 * Окно приложения, в котором происходит расшифрования из изображения
 * Расшифрованный текст выводится в окно, который можно скопировать
 * нажатием на него.
 */
class DecryptActivity : AppCompatActivity(), View.OnClickListener {

    private val rec = GrassHopper()
    private var Load: Button? = null
    private var bitmap: Bitmap? = null
    private var ButDec: Button? = null
    private var textr: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_decrypt)
        val intent: Intent = intent
        rec.key = intent.getStringArrayExtra("Hoop")
        ButDec = findViewById(R.id.ButDec) as Button
        ButDec?.setOnClickListener(this)
        textr = findViewById(R.id.Textrezz) as TextView
        textr?.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25.toFloat())
        textr?.setOnClickListener(this)
        Load = findViewById(R.id.LoadImage) as Button
        Load?.setOnClickListener(this)
    }

    override fun onClick(v: View?): Unit = when (v?.id) {
        R.id.Textrezz -> {
            val a = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            a.text = textr?.text.toString()
            textr?.setText("")!!
        }
        R.id.ButDec -> {
            if (bitmap != null) {
                val text = Stego(bitmap!!).Stegano_decrypt()
                if (text != "") {
                    rec.Parsing(text)
                    rec.decrypt()
                    textr?.setText(rec.real())!!
                } else MyToast("Image can't be decrypted")
            } else MyToast("Add Image")
        }

        R.id.LoadImage -> startActivityForResult(Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI), 1)
        else -> MyToast("Element of lyaout not found")
    }

    /**
     * Функция, создающая всплывающий текст
     * @param str Текст, который необходимо вывести на экран[String]
     */
    private fun MyToast(str: String) = Toast.makeText(this, str, Toast.LENGTH_SHORT).show()

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            1 -> if (resultCode == Activity.RESULT_OK) {
                if (bitmap != null) bitmap = null
                bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, data?.data)
                MyToast("Add Image")
            }
        }
    }
}
