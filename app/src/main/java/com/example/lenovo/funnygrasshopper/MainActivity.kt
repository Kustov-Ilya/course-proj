package com.example.lenovo.funnygrasshopper

import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.Window
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
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
class MainActivity : AppCompatActivity() {

    private var KEY: String? = ""
    val grassHopper = GrassHopper()
    private var sPref: SharedPreferences? = null
    private var dialog:Dialog? = null
    private var editText:EditText? = null
    private var checkBox:CheckBox? = null

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener{
        item ->
        val transantion = supportFragmentManager.beginTransaction()

        when(item.itemId){
            R.id.action_encrypt->{
                transantion.replace(R.id.frame, EncryptFragment()).commit()
                return@OnNavigationItemSelectedListener true
            }
            R.id.action_decrypt_image->{
                transantion.replace(R.id.frame, DecryptImageFragment()).commit()
                return@OnNavigationItemSelectedListener true
            }
            R.id.action_decrypt_text->{
                transantion.replace(R.id.frame, DecryptTextFragment()).commit()
                return@OnNavigationItemSelectedListener true
            }
        }
        false

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuinflater = menuInflater
        menuinflater.inflate(R.menu.action_bar, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            R.id.action_bar_enter_key->CallDialog()
            R.id.action_bar_delete_key->{
                KEY=""
                if(grassHopper.key[0].length!=0)
                    grassHopper.key.fill("")
                Toast.makeText(this, getString(R.string.deleted_key), Toast.LENGTH_SHORT).show()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    fun CallDialog(){
        dialog = Dialog(this)
        dialog?.setTitle(getString(R.string.enter_key))
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.setContentView(R.layout.enter_key_dialog)
        dialog?.show()
        editText = dialog?.findViewById(R.id.edit_text_key) as EditText
        checkBox = dialog?.findViewById(R.id.check_box_remember) as CheckBox

        val buttonBack = dialog?.findViewById(R.id.button_back) as Button
        buttonBack.setOnClickListener{
            v->if(v.id==R.id.button_back) dialog?.cancel()
        }

        val buttonSave = dialog?.findViewById(R.id.button_save) as Button
        buttonSave.setOnClickListener{
            v->if(v.id==R.id.button_save) SaveKey()
        }
    }

    private fun SaveKey(){
        val tmp = editText?.text.toString()
        if(tmp.isNotEmpty()){
            KEY = tmp
            grassHopper.key_create(KEY!!)
            val ed = sPref?.edit()
            if(checkBox?.isChecked == true)
                ed?.putString(getString(R.string.saves_keys), KEY)
            else
                ed?.putString(getString(R.string.saves_keys), "")
            ed?.commit()
        }else Toast.makeText(this, getString(R.string.not_enter_key), Toast.LENGTH_SHORT).show()

        dialog?.cancel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)
        val mBottomNavigationView = findViewById(R.id.bottomNavigationView) as BottomNavigationView
        mBottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        sPref = getPreferences(Context.MODE_PRIVATE)
        KEY = sPref?.getString(getString(R.string.saves_keys), "")
        if(KEY?.length !=0) grassHopper.key_create(KEY!!)
    }

}

