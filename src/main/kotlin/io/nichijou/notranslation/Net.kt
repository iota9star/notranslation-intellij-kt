package io.nichijou.notranslation

import com.google.gson.Gson
import okhttp3.*
import java.io.IOException
import java.net.CookieManager
import java.util.concurrent.TimeUnit


object Net {

    private const val url = "https://fanyi.baidu.com/sug"

    private val client by lazy {
        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .cookieJar(CookieJarImpl(CookieManager()))
            .retryOnConnectionFailure(true)
            .build()
    }

    private val headers by lazy {
        Headers.of(
            hashMapOf(
                "Host" to "fanyi.baidu.com",
                "Origin" to "https://fanyi.baidu.com",
                "Referer" to "https://fanyi.baidu.com/",
                "User-Agent" to "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.110 Safari/537.36"
            )
        )
    }

    private val gson by lazy { Gson() }

    fun translate(word: String, success: (Trans) -> Unit, failure: (String?) -> Unit) {
        val body = FormBody.Builder()
            .add("kw", word)
            .build()
        val request = Request.Builder()
            .url(url)
            .headers(headers)
            .post(body)
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                failure.invoke(e.message)
            }

            override fun onResponse(call: Call, response: Response) {
                val b = response.body()
                if (b != null) {
                    val trans = gson.fromJson(b.charStream(), Trans::class.java)
                    success.invoke(trans)
                } else {
                    failure.invoke("没有获得响应的数据")
                }
            }
        })
    }
}