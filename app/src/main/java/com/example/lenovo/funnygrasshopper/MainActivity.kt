package com.example.lenovo.funnygrasshopper

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

/**
 * Главное окно приложения, в котором находятся кнопки, направляющие в
 * окна:
 * <b>Ключ</b>
 * <b>Зашифрование</b>
 * <b>Расшифрование изображения</b>
 * <b>Расшифрование текста</b>
 * Если ключ не введен, то нажатие на любую кнопку откроет окно для введения пользователького ключа
 * В верхней части окна отображается текущий пользовательский ключ, если он существует
 */
class MainActivity : AppCompatActivity(), View.OnClickListener {

    private var KEY: String? = ""
    private var Encry: Button? = null
    private var Decry: Button? = null
    private var Key: Button? = null
    private var Text: TextView? = null
    private var isSave: Boolean = false
    private var sPref: SharedPreferences? = null
    private var DecryText: Button? = null
    private val rec = GrassHopper()

    private fun toKeyActivity() {
        val intent = Intent(this, KeyActivity::class.java)
        intent.putExtra("key", KEY)
        startActivityForResult(intent, 1)
    }

    /**
     * Функция, создающая всплывающий текст
     * @param str Текст, который необходимо вывести на экран[String]
     */
    private fun MyToast(str:String) = Toast.makeText(this, str, Toast.LENGTH_SHORT).show()

    private fun toAnyActivity(_class: String) {
        var intent: Intent? = null
        when (_class) {
            "EncryptActivity" -> intent = Intent(this, EncryptActivity::class.java)
            "DecryptTextActivity" -> intent = Intent(this, DecryptTextActivity::class.java)
            "DecryptActivity" -> intent = Intent(this, DecryptActivity::class.java)
            else -> MyToast("I don't know this layout")
        }
        intent?.putExtra("Hoop", rec.key)
        startActivity(intent)
    }

    override fun onClick(v: View?) =  when (v?.id) {
            R.id.decrText -> {
                if (KEY == "") {
                    MyToast("The key does not exist")
                    toKeyActivity()
                } else toAnyActivity("DecryptTextActivity")
            }
            R.id.encr -> {
                if (KEY == "") {
                    MyToast("The key does not exist")
                    toKeyActivity()
                } else toAnyActivity("EncryptActivity")
            }
            R.id.decr -> {
                if (KEY == "") {
                    MyToast("The key does not exist")
                    toKeyActivity()
                } else toAnyActivity("DecryptActivity")
            }
            R.id.key -> toKeyActivity()
        else -> MyToast("Element of lyaout not found")
    }

    /**
     * Функция, проверки наличия пользовательского ключа и,
     * если он существует, вывод его в верхней части окна
     */
    @SuppressLint("SetTextI18n")
    private fun AboutKey() = if (KEY != "") {
        Text?.text = "Your key is $KEY"
            rec.key_create(KEY!!)
        }
    else Text?.text = "You haven,t key"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)
        Encry = findViewById(R.id.encr) as Button
        Encry?.setOnClickListener(this)
        Decry = findViewById(R.id.decr) as Button
        Decry?.setOnClickListener(this)
        DecryText = findViewById(R.id.decrText) as Button
        DecryText?.setOnClickListener(this)
        Key = findViewById(R.id.key) as Button
        Key?.setOnClickListener(this)
        Text = findViewById(R.id.textView) as TextView
        sPref = getPreferences(Context.MODE_PRIVATE)
        KEY = sPref?.getString("saves_key", "")
        isSave = sPref?.getBoolean("saved_is", false)!!
        AboutKey()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data == null) return

        KEY = data.getStringExtra("key")
        isSave = data.getBooleanExtra("isSave", true)
        AboutKey()

        sPref = getPreferences(Context.MODE_PRIVATE)
        val ed = sPref?.edit()

        if (isSave) ed?.putString("saves_key", KEY)
        else ed?.putString("saves_key", "")

        ed?.putBoolean("saved_is", isSave)
        ed?.commit()
    }
}
