package com.whidy.whidyandroid.presentation.map

import android.content.Intent
import android.os.Bundle
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.whidy.whidyandroid.databinding.ActivityAddressBinding

class AddressActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddressBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val webView = binding.webView
        webView.settings.javaScriptEnabled = true
        webView.addJavascriptInterface(BridgeInterface(), "Android")

        webView.webViewClient = object: WebViewClient(){
            //2. 페이지가 로드 되었을 때
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                // android->javascript
                webView.loadUrl("javascript:sample2_execDaumPostcode();")
            }
        }
        //1.
        // 최초 웹뷰 로드
        webView.loadUrl("https://whidy-542a7.web.app/")

    }

    inner class BridgeInterface{
        @JavascriptInterface
        fun processDATA(data: String){
            // 카카오 주소 검색 API 결과를 브릿지 경로를 통해 전달 받는다(from Javascript)
            val intent = Intent()
            intent.putExtra("data", data)
            setResult(1001, intent)
            finish()
        }
    }
}