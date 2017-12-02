package com.example.lenovo.funnygrasshopper

import android.graphics.Bitmap
import android.graphics.Color

/**
 * Класс, используемый для:
 * стеганографирования текста в изображение и преобразование его в формат PNG;
 * извлечения текста из изображения
 * @param bitm Изображение, представленное в битовой карте пикселей типа [Bitmap]
 * @param len  Расстояние между изменяемыми битами
 */
class Stego(var bitm:Bitmap){
    var len = 4

    /**
     * Функция преобразования числа, представленного в битовой строке, в число
     * @param str Число преставленное в битовой строке типа [String]
     * @return Число типа [Int]
     */
    private fun ToInt(str:String):Int{
        return (str.length-1 downTo 0).sumBy { Math.pow(2.toDouble(), (str.length-1- it).toDouble()).toInt()*(str[it].toInt()-48) }
    }

    /**
     * Функция, меняющая последние 2 бита синей составляющей пикселя
     * @param i Координата по горизонтали типа [Int]
     * @param j Координата по вертикали типа [Int]
     * @param char Строка типа[String], от которой отрезают 2 первых бита для добавление в пиксельную карту
     *@return Остаток строки типа [String]
     */
    private fun setPix(i:Int, j:Int, char:String):String{
        val pixel = bitm.getPixel(i, j)
        var blue = getBit(Color.blue(pixel), 8)
        blue = blue.substring(0, 6)
        blue+=char.substring(0, 2)
        bitm.setPixel(
                i,
                j,
                Color.argb(
                        85,
                        Color.red(pixel),
                        Color.green(pixel),
                        ToInt(str = blue)
                )
        )
        return char.substring(2)
    }

    /**
     * Получение битового представления символа
     * @param char Символ пеерведенный в [Int]
     * @param pred Колличество символом в битовом представлении типа [Int]
     * @return Битовое представление символа в типе [String]
     */
    private fun getBit(char:Int, pred:Int):String{
        var str = Integer.toBinaryString(char)
        while(str.length!=pred) str = '0'+str
        return str
    }

    /**
     * Обнуление остатка столбца, в который записывается колличество символов
     * @param tmp Координата по вертикали типа [Int]
     */
    private fun OnNull(tmp:Int){
        var j = tmp
        while(j<bitm.height){
            setPix(0, j, "00")
            j+=len
        }
    }

    /**
     * Проверка на обнуление остатка столбца
     * @param tmp Координата по вертикали типа [Int]
     * @return Возвращает [true], если все обнулено
     */
    private fun IsNull(tmp:Int):Boolean{
        var j = tmp
        while(j<bitm.height){
            var blue = Integer.toBinaryString(Color.blue(bitm.getPixel(0, j)))
            while(blue.length!=8) blue = '0'+blue
            if(blue.substring(6)!="00") return false
            j+=len
        }
        return true
    }

    /**
     * Функция, которая скрывает текст в изображение
     * @param text Текст, который необходимо скрыть[String]
     * @return Возвращает измененную битовую карту формата [Bitmap]
     */
    fun Stegano_encrypt(text:String): Bitmap {
        var symb=0 //number of symbol in string
        var i = 1 //number of string of pixel matrix
        var j = 0 //number of columns of pixel matrix
        //In first string of pixel matrix we encrypt number of symbol in text
        var NumOfText = getBit(text.length, 20)
        for(n in 0 until 10) {
            NumOfText= setPix(0, j, NumOfText)
            j+=len
        }
        OnNull(j)
        j=0
        //Now we add text in picture
        while(symb<text.length){
            var char = getBit(text[symb].toInt(), 8)
            for(n in 0 until 4) {
                char = setPix(i, j, char)
                j += len
                if (j >= bitm.height) {
                    j = 0
                    i++
                }
            }
            symb++
        }
        return bitm
    }

    /**
     * Функция, которая извлекает из изображения скрытый текст
     * @return Если в изображении скрыт текст, возвращает его в формате [String]
     * Если скрытого текста в изображении нет, фозвращает пустую строку формата [String]
     */
    fun Stegano_decrypt():String{
        var rez = ""
        var tmpByte =""
        var i = 1 //number of string of pixel matrix
        var j = 0 //number of columns of pixel matrix
        //Get number of symbols
        for(n in 0 until 10){
            tmpByte+=getBit(Color.blue(bitm.getPixel(0, j)), 8).substring(6)
            j+=len
        }
        val NumOfText = ToInt(tmpByte)
        if (NumOfText%16!=0) return ""
        if(!IsNull(j)) return ""
        j=0
        //Get text
        var symb=0 //number of symbol in string now
        while(symb < NumOfText){
            tmpByte=""
            for(n in 0 until 4){
                tmpByte+=getBit(Color.blue(bitm.getPixel(i, j)), 8).substring(6)
                j += len
                if (j >= bitm.height) {
                    j = 0
                    i++
                }
            }
            rez+=ToInt(tmpByte).toChar()
            symb++
        }
        return rez
    }
}

