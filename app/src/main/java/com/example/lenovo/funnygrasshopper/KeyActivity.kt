package com.example.lenovo.funnygrasshopper

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast

/**
 * Окно приложения, в котором производится ввод ключа пользователя
 * Имеется возможность запоминания ключа
 * После добавления ключа возвращает на главное окно приложения
 * и в верхней части окна информирует о используемом пользователем ключом
 */
class KeyActivity : AppCompatActivity(), View.OnClickListener {
    private var ButSave: Button? = null
    private var Key: EditText? = null
    private var Remember: CheckBox? = null
    private val int: Intent? = Intent()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_key)
        ButSave = findViewById(R.id.Save) as Button
        ButSave?.setOnClickListener(this)
        Key = findViewById(R.id.Key) as EditText
        Remember = findViewById(R.id.Remember) as CheckBox

    }

    override fun onClick(v: View?) = when (v?.id) {
        R.id.Save -> {
            int?.putExtra("key", Key?.text.toString())
            int?.putExtra("isSave", Remember?.isChecked())
            Toast.makeText(this, "Key is added", Toast.LENGTH_SHORT).show()
            setResult(
                    RESULT_OK,
                    int
            )
            finish()
        }
        else -> Toast.makeText(this, "Element of lyaout not found", Toast.LENGTH_SHORT).show()
    }
}
