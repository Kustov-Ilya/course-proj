package com.example.lenovo.funnygrasshopper

import java.security.MessageDigest

/**
 * Постоянная переменная размерности блоков
 */
private const val SIZE_OF_BLOCK: Int =16

/**
 * Массив нелинейного преобразования
 */
private val nonlinear_modify_vals = intArrayOf(
        252, 238, 221, 17, 207, 110, 49, 22, 251, 196,      //9
        250, 218, 35, 197, 4, 77, 233, 119, 240, 219,       //19
        147, 46, 153, 186, 23, 54, 241, 187, 20, 205,       //29
        95, 193, 249, 24, 101, 90, 226, 92, 239, 33,        //39
        129, 28, 60, 66, 139, 1, 142, 79, 5, 132,           //49
        2, 174, 227, 106, 143, 160, 6, 11, 237, 152,        //59
        127, 212, 211, 31, 235, 52, 44, 81, 234, 200,       //69
        72, 171, 242, 42, 104, 162, 253, 58, 206, 204,      //79
        181, 112, 14, 86, 8, 12, 118, 18, 191, 114,         //89
        19, 71, 156, 183, 93, 135, 21, 161, 150, 41,        //99
        16, 123, 154, 199, 243, 145, 120, 111, 157, 158,    //109
        178, 177, 50, 117, 25, 61, 255, 53, 138, 126,       //119
        109, 84, 198, 128, 195, 189, 13, 87, 223, 245,      //129
        36, 169, 62, 168, 67, 201, 215, 121, 214, 246,      //139
        124, 34, 185, 3, 224, 15, 236, 222, 122, 148,       //149
        176, 188, 220, 232, 40, 80, 78, 51, 10, 74,         //159
        167, 151, 96, 115, 30, 0, 98, 68, 26, 184,          //169
        56, 130, 100, 159, 38, 65, 173, 69, 70, 146,        //179
        39, 94, 85, 47, 140, 163, 165, 125, 105, 213,       //189
        149, 59, 7, 88, 179, 64, 134, 172, 29, 247,         //199
        48, 55, 107, 228, 136, 217, 231, 137, 225, 27,      //209
        131, 73, 76, 63, 248, 254, 141, 83, 170, 144,       //219
        202, 216, 133, 97, 32, 113, 103, 164, 45, 43,       //229
        9, 91, 203, 155, 37, 208, 190, 229, 108, 82,        //239
        89, 166, 116, 210, 230, 244, 180, 192, 209, 102,    //249
        175, 194, 57, 75, 99, 182)                          //255
/**
 * Инвертируемый массив нелинейного преобразования
 */
private var nonlinear_modify_vals_inverse = Array(256){0}

/**
 * Линейный модификатор поля Галуа
 */
private val linear_modyfi_451 = 0x1C3

/**
 * Массив переменных линейного отображения
 */
private val linear_modify_vals = intArrayOf(
        148, 32, 133, 16, 194, 192, 1, 251,
        1, 192, 194, 16, 133, 32, 148, 1)

/**
 * Лист констант для создания раундовых ключей
 */
private  var iter_const: MutableList<String>? = null

/**
 * Класс для шифрования и расшифрования текста блочным
 * симметричным шифром "Кузнечик".
 * @param key Раундовые ключи типа <b>MutableList<String></b>
 */

class GrassHopper {
    lateinit var key: Array<String>
    private lateinit var text: MutableList<String> //Текст, разбитый на блоки размерности SIZE_OF_BLOCK


    /**
     * Создание листа постоянных для создания раундовых ключей
     */
    private fun init_const() {
        iter_const = mutableListOf()
        val str = "000000000000000"
        for (i in 1..32) {
            var len = str + i.toChar()
            for (j in 0..SIZE_OF_BLOCK) {
                len = linear_modify_block(len)
            }
            iter_const!!.add(len)
        }
    }

    /**
     *Функция шифрования
     */
    fun encrypt() {
        for (num_b in 0 until text.size) {
            XOR(num_b, 0)
            for (i in 1..9) {
                nonlinear_modify(num_b)
                linear_modify(num_b)
                XOR(num_b, i)
            }
        }
    }

    /**
     * Функция дешифрования
     */
    fun decrypt() {
        for (num_b in 0 until text.size) {
            for (i in 9 downTo 1) {
                XOR(num_b, i)
                linear_modify_inverse(num_b)
                nonlinear_modify_inverse(num_b)
            }
            XOR(num_b, 0)
        }
    }

    /**
     * Обратное линейное преобразование
     * @param IndOfList Номер блока текста типа [Int]
     */
    private fun linear_modify_inverse(IndOfList: Int) {
        var buf = text[IndOfList]
        for (i in 0..SIZE_OF_BLOCK) {
            buf = linear_modify_block_inverse(buf)
        }
        text.removeAt(IndOfList)
        text.add(IndOfList, buf)
    }

    private fun linear_modify_block_inverse(tmp: String): String {
        var buf = tmp.substring(1) + tmp[0]
        val value = linear_modify_the_last(buf)
        buf = buf.substring(0, 15) + value
        return buf
    }

    private fun nonlinear_modify_val_make_inverse() {
        for (i in 0 until nonlinear_modify_vals.size)
            nonlinear_modify_vals_inverse[nonlinear_modify_vals[i]] = i
    }

    /**
     * Побитовое сложение по модулю 2
     * @param IndOfList Номер блока текста типа[Int]
     * @param IndOfKey Номер раундового ключа[Int]
     */
    private fun XOR(IndOfList: Int, IndOfKey: Int) {
        val rez = XOR_rez(text[IndOfList], key[IndOfKey])
        text.removeAt(IndOfList)
        text.add(IndOfList, rez)
    }

    /**
     * Побитовое сложение двух строк
     * @param tmp1 Первая строка типа [String]
     * @param tmp2 Вторая строка типа [String]
     * @return Результат сложения в [String]
     */
    private fun XOR_rez(tmp1: String, tmp2: String): String {
        var rez = ""
        for (i in 0 until SIZE_OF_BLOCK) {
            rez += Integer.valueOf(tmp1[i].toInt()).xor(tmp2[i].toInt()).toChar()
        }
        return rez
    }

    /**
     * Нелинейное преобразование
     * @param IndOfList Номер блока текста типа [Int]
     */
    private fun nonlinear_modify(IndOfList: Int) {
        var rez = ""
        for (i in 0 until SIZE_OF_BLOCK) {
            rez += nonlinear_modify_vals[text[IndOfList][i].toInt()].toChar()
        }
        text.removeAt(IndOfList)
        text.add(IndOfList, rez)
    }

    private fun nonlinear_modify_inverse(IndOfList: Int) {
        nonlinear_modify_val_make_inverse()
        var rez = ""
        for (i in 0 until SIZE_OF_BLOCK) {
            rez += nonlinear_modify_vals_inverse[text[IndOfList][i].toInt()].toChar()
        }
        text.removeAt(IndOfList)
        text.add(IndOfList, rez)
    }

    /**
     * Линейное преобразование
     * @param IndOfList Номер блока текста типа [Int]
     */
    private fun linear_modify(IndOfList: Int) {
        var buf = text[IndOfList]
        for (i in 0..SIZE_OF_BLOCK) {
            buf = linear_modify_block(buf)
        }
        text.removeAt(IndOfList)
        text.add(IndOfList, buf)
    }

    private fun linear_modify_block(tmp: String): String {
        var buf = tmp.substring(0, 15)
        val value = linear_modify_the_last(tmp)
        buf = value + buf
        return buf
    }

    private fun linear_modify_the_last(tmp: String): Char {
        var result = 0
        for (i in 0 until SIZE_OF_BLOCK) {
            result = Integer.valueOf(result).xor(linear_modify_display(tmp[i].toInt(), linear_modify_vals[i]))
        }
        return result.toChar()
    }

    private fun linear_modify_display(one: Int, two: Int): Int {
        var One = one
        var result = 0
        var mod_451 = linear_modyfi_451 shl 7
        var degr = 1
        while (degr != 256) {
            if (Integer.valueOf(two).and(degr) != 0) result = Integer.valueOf(result).xor(One)
            degr = degr shl 1
            One = One shl 1
        }
        degr = 32768
        while (degr != 128) {
            if (Integer.valueOf(result).and(degr) != 0)
                result = Integer.valueOf(result).xor(mod_451)
            degr = degr shr 1
            mod_451 = mod_451 shr 1
        }
        return result
    }

    /**
     * Функция, возвращающая текст, хранящийся в классе
     * @return текст
     */
    fun real(): String {
        var str = ""
        for (num_b in 0 until text.size) {
            str += text[num_b]
        }
        return str
    }

    /**
     * Функция, разбивающая текст на блоки по 16 символов
     * @param tmp текст
     */
    fun Parsing(tmp: String) {
        val NulChar = (0x00).toChar()
        var tm = tmp
        text = mutableListOf()
        val size = tm.length
        val i = size / SIZE_OF_BLOCK
        for (x in 1..i) {
            val Str16 = tm.substring(0, SIZE_OF_BLOCK)
            text.add(Str16)
            tm = tm.substring(SIZE_OF_BLOCK)
        }
        if (i * SIZE_OF_BLOCK < size) {
            while (tm.length != 16) tm += NulChar
            text.add(tm)
        }
    }

    /**
     * Функция, создающая раундовые ключи из ключа пользователя
     */
    fun key_create(KEY: String) {
        init_const()
        val Key = sha256(KEY)
        key = Array(10, { "" })
        key[0] = Key.substring(0, 16)
        key[1] = Key.substring(16)
        for (i in 0 until 4) {
            key[i * 2 + 2] = key[i * 2]
            key[i * 2 + 3] = key[i * 2 + 1]
            for (j in 0 until 8) {
                key_modify(i * 2 + 2, i * 2 + 3, i * 8 + j)
            }
        }
    }

    /**
     * Создание двух следующий раундовых ключей
     * @param key1 Номер первого ключа[Int]
     * @param key2 Номер второго ключа[Int]
     * @param iconst [Int]
     */
    private fun key_modify(key1: Int, key2: Int, iconst: Int) {
        val tmp = key[key1]
        key[key1] = XOR_rez(key[key1], iter_const!![iconst])
        var rez = ""
        for (i in 0 until SIZE_OF_BLOCK) {
            rez += nonlinear_modify_vals[key[key1][i].toInt()].toChar()
        }
        for (j in 0..SIZE_OF_BLOCK) {
            rez = linear_modify_block(rez)
        }
        key[key1] = XOR_rez(rez, key[key2])
        key[key2] = tmp
    }

    /**
     * Получание мастер ключа из ключа пользователя
     * @param input Пользовательский ключ типа [String]
     */
    private fun sha256(input: String): String {
        val rez = StringBuilder()
        val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        bytes.forEach {
            rez.append(Math.abs(it.toInt()).toChar())
        }
        return rez.toString()
    }
}
