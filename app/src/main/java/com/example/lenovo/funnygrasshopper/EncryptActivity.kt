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
import android.support.v7.app.AppCompatActivity
import android.text.ClipboardManager
import android.util.Base64
import android.util.TypedValue
import android.view.View
import android.widget.*
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

/**
 * Окно приложения, в котором проиходит шифрование текста в
 * изображение (в случае, если пользователь добавляет его из галереи)
 * или в текст формата Base64 (если изображение не было добавлено).
 * Текст в формате Base64 можно скопировать нажатием на него.
 * Изображение сохраняется нажатием на него. Сохраняет в папку GrassHopper на SD
 * в формате PNG
 */
class EncryptActivity : AppCompatActivity(), View.OnClickListener {

    private var ButEnc: Button? = null
    private var EncText: EditText? = null
    private val rec = GrassHopper()
    private var image: ImageView? = null
    private var AddImg: Button? = null
    private var bitmap: Bitmap? = null
    private var textView: TextView? = null
    private var uri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_encrypt)
        val intent: Intent = intent
        rec.key = intent.getStringArrayExtra("Hoop")
        ButEnc = findViewById(R.id.ButEnc) as Button
        ButEnc?.setOnClickListener(this)
        EncText = findViewById(R.id.encText) as EditText
        image = findViewById(R.id.Image) as ImageView
        image?.setOnClickListener(this)
        AddImg = findViewById(R.id.ButAddImage) as Button
        AddImg?.setOnClickListener(this)
        textView = findViewById(R.id.Textrez) as TextView
        textView?.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25.toFloat())
        textView?.setOnClickListener(this)
    }

    override fun onClick(v: View?): Unit = when (v?.id) {
        R.id.Textrez -> {
            val a = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            a.text = textView?.text.toString()
            textView?.setText("")
            MyToast("Cipher coped")
        }
        R.id.ButEnc -> {
            val text = EncText?.text.toString()
            if (text != "") {
                EncText?.setText("")
                rec.Parsing(text)
                rec.encrypt()
                if (bitmap == null) {
                    textView?.setText(Base64.encodeToString(rec.real().toByteArray(), Base64.NO_WRAP))
                    MyToast("Encrypt without stegano")
                } else {
                    image?.setImageBitmap(Stego(bitmap!!).Stegano_encrypt(rec.real()))
                    MyToast("Encrypt and Stegano successful")
                }
            } else MyToast("Enter text for encrypt")
        }
        R.id.ButAddImage -> startActivityForResult(Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI), 1)

        R.id.Image -> if (bitmap != null) {
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                val filepach = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).path
                val dir = File(filepach + "/FunnyGrassHopper")
                if (!dir.exists()) dir.mkdirs()
                val file = File(dir, "hehe_${getDate()}.png")
                try {
                    val output = FileOutputStream(file)
                    bitmap?.compress(Bitmap.CompressFormat.PNG, 100, output)
                    output.flush()
                    output.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                this.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)))
                bitmap = null
                image?.visibility = View.INVISIBLE
                MyToast("Saved")
            } else MyToast("SD-card don't found")
        } else MyToast("Image not found")

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
                uri = data?.data
                bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri).copy(Bitmap.Config.ARGB_8888, true)
                image?.visibility = View.VISIBLE
                MyToast("Image is added")
            }
        }
    }

}

/**
 * Функция получания даты и времени
 * @return Значение даты и времени типа [String]
 */
@SuppressLint("SimpleDateFormat")
private fun getDate():String{
    return SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(Calendar.getInstance().time)
}


