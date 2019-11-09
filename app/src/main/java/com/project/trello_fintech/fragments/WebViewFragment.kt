package com.project.trello_fintech.fragments

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import com.project.trello_fintech.BuildConfig
import com.project.trello_fintech.api.RetrofitClient
import com.project.trello_fintech.utils.StringsRepository

/**
 * Используется при OAuth2 аутентификации
 */
private const val FAKE_USER_AGENT = "Mozilla/5.0 (Linux; Android 4.1.1; Galaxy Nexus Build/JRO03C) AppleWebKit/535.19 " +
        "(KHTML, like Gecko) Chrome/18.0.1025.166 Mobile Safari/535.19"

/**
 * Фрагмент браузера
 */
class WebViewFragment: Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        WebView(container?.context)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val url = RetrofitClient.getAuthUrl(view.context)

        val webView = (view as WebView).apply {
            with(settings) {
                javaScriptEnabled = true
                userAgentString = FAKE_USER_AGENT
            }
            webViewClient = object: WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView?, url: String): Boolean {
                    if (url.startsWith(BuildConfig.TRELLO_URL_CALLBACK)) {
                        StringsRepository.put("token", url.substringAfter("#token="))
                        requireActivity().supportFragmentManager.popBackStack()
                        return false
                    }
                    loadUrl(url)
                    return true
                }
            }
        }

        webView.loadUrl(url)

        view.setOnKeyListener { _, keyCode, _ -> keyCode == KeyEvent.KEYCODE_BACK }
    }
}
