package com.icloud.kakaotranslator

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.text.TextUtils
import android.util.Log
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        val handler = object : Handler(Looper.getMainLooper()) {
            override fun handleMessage(msg: Message) {
                val result = msg.obj as String
                textTarget.setText(result.toString())
            }
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var srcLanguage: String? = null
        var targetLanguage: String? = null


        //번역 대상 문장의 언어 선택.
        btnSrc.setOnClickListener {
            var langArray = arrayOf("한국어", "영어", "일본어", "중국어")

            AlertDialog.Builder(this@MainActivity)
                .setTitle("언어 선택")
                .setItems(langArray) { dialog, which ->
                    when (which) {
                        0 -> srcLanguage = "kr"
                        1 -> srcLanguage = "en"
                        2 -> srcLanguage = "jp"
                        3 -> srcLanguage = "cn"
                    }
                    btnSrc.setText(langArray[which])
                    Log.e("lang", langArray[which])

                }.setNegativeButton("취소") { dialog, which ->
                    Toast.makeText(applicationContext, "취소하셨습니다.", Toast.LENGTH_SHORT).show()
                }
                .show()

        }

        btnTarget.setOnClickListener {
            var langArray = arrayOf("한국어", "영어", "일본어", "중국어")

            AlertDialog.Builder(this@MainActivity)
                .setTitle("언어선택")
                .setItems(langArray) { dialog, which ->
                    when (which) {
                        0 -> targetLanguage = "kr"
                        1 -> targetLanguage = "en"
                        2 -> targetLanguage = "jp"
                        3 -> targetLanguage = "cn"
                    }
                    btnTarget.setText(langArray[which])
                }
                .setNegativeButton("취소") { dialog, which ->
                    Toast.makeText(applicationContext, "취소하셨습니다.", Toast.LENGTH_SHORT).show()
                }
                .show()
        }

        btnTransStart.setOnClickListener {
            if (TextUtils.isEmpty(srcLanguage) || TextUtils.isEmpty(targetLanguage)) {
                Toast.makeText(applicationContext, "설정을 완료해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }else if (TextUtils.isEmpty(textSource.text.toString())) {
                Toast.makeText(applicationContext, "내용을 입력해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }else if (srcLanguage.equals(targetLanguage)) {
                Toast.makeText(applicationContext, "설정을 다시 확인해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            object : Thread() {
                override fun run() {
                    val query = textSource.text.toString()
                    val APIKEY = "KakaoAK 55faa09da8198c321b5db87e0ee1ed95"

                    var addr = "https://dapi.kakao.com/v2/translation/translate?query=${
                        URLEncoder.encode(query,
                            "UTF-8")
                    }&src_lang=${srcLanguage}&target_lang=${targetLanguage}"

                    val url: URL = URL(addr)
                    val con = url.openConnection() as HttpURLConnection

                    //접속 설정
                    con.connectTimeout = 30000
                    con.requestMethod = "GET"
                    con.setRequestProperty("Authorization", APIKEY)
                    con.useCaches = false


                    val sb = StringBuilder()
                    val br = BufferedReader(InputStreamReader(con.inputStream))

                    while (true) {
                        val line = br.readLine()
                        if (line == null) {
                            break
                        }
                        sb.append(line)
                    }
                    Log.e("sb", sb.toString())

                    val data = JSONObject(sb.toString())
                    val result = data.getString("translated_text")

                    Log.e("result", result.toString())

                    val msg = Message()
                    msg.obj = result

                    handler.sendMessage(msg)

                }
            }.start()

        }


    }
}