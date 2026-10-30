package com.mosescryptic.audiostudio.test

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.webkit.WebViewAssetLoader
import androidx.webkit.WebViewClientCompat
import java.io.IOException

class MainActivity : Activity() {
    private lateinit var webView: WebView
    private var fileChooserCallback: ValueCallback<Array<Uri>>? = null
    private var pendingExport: PendingExport? = null

    private data class PendingExport(
        val content: String,
        val mimeType: String
    )

    companion object {
        private const val TAG = "AudioStudioWebView"
        private const val REQUEST_OPEN_FILE = 4101
        private const val REQUEST_SAVE_FILE = 4102
        private const val APP_ASSET_HOST = "appassets.androidplatform.net"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        webView = WebView(this)
        webView.setBackgroundColor(android.graphics.Color.rgb(8, 11, 17))
        setContentView(webView)

        WebView.setWebContentsDebuggingEnabled(BuildConfig.DEBUG)

        val assetLoader = WebViewAssetLoader.Builder()
            .addPathHandler("/assets/", WebViewAssetLoader.AssetsPathHandler(this))
            .build()

        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            allowFileAccess = false
            allowContentAccess = true
            javaScriptCanOpenWindowsAutomatically = false
            setSupportMultipleWindows(false)
            builtInZoomControls = false
            displayZoomControls = false
            setSupportZoom(false)
            mediaPlaybackRequiresUserGesture = true
            mixedContentMode = android.webkit.WebSettings.MIXED_CONTENT_NEVER_ALLOW
        }

        webView.addJavascriptInterface(AndroidBridge(), "AndroidBridge")

        webView.webViewClient = object : WebViewClientCompat() {
            override fun shouldInterceptRequest(
                view: WebView,
                request: WebResourceRequest
            ) = assetLoader.shouldInterceptRequest(request.url)

            @Suppress("DEPRECATION")
            override fun shouldInterceptRequest(view: WebView, url: String) =
                assetLoader.shouldInterceptRequest(Uri.parse(url))

            override fun shouldOverrideUrlLoading(
                view: WebView,
                request: WebResourceRequest
            ): Boolean {
                val uri = request.url
                if (uri.scheme == "https" && uri.host == APP_ASSET_HOST) return false
                return try {
                    startActivity(Intent(Intent.ACTION_VIEW, uri))
                    true
                } catch (_: Exception) {
                    true
                }
            }
        }

        webView.webChromeClient = object : WebChromeClient() {
            override fun onShowFileChooser(
                view: WebView,
                callback: ValueCallback<Array<Uri>>,
                params: FileChooserParams
            ): Boolean {
                fileChooserCallback?.onReceiveValue(null)
                fileChooserCallback = callback
                return try {
                    val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                        addCategory(Intent.CATEGORY_OPENABLE)
                        type = "*/*"
                        putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("application/json", "text/plain"))
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                    startActivityForResult(intent, REQUEST_OPEN_FILE)
                    true
                } catch (error: Exception) {
                    fileChooserCallback = null
                    callback.onReceiveValue(null)
                    Toast.makeText(this@MainActivity, "Unable to open file picker", Toast.LENGTH_SHORT).show()
                    false
                }
            }

            override fun onConsoleMessage(message: android.webkit.ConsoleMessage): Boolean {
                Log.d(TAG, "${message.message()} -- line ${message.lineNumber()}")
                return true
            }
        }

        if (savedInstanceState == null) {
            webView.loadUrl("https://$APP_ASSET_HOST/assets/index.html")
        } else {
            webView.restoreState(savedInstanceState)
        }
    }

    private inner class AndroidBridge {
        @JavascriptInterface
        fun saveFile(content: String, fileName: String, mimeType: String) {
            runOnUiThread {
                pendingExport = PendingExport(content, mimeType)
                try {
                    val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                        addCategory(Intent.CATEGORY_OPENABLE)
                        type = mimeType.ifBlank { "application/octet-stream" }
                        putExtra(Intent.EXTRA_TITLE, fileName)
                    }
                    startActivityForResult(intent, REQUEST_SAVE_FILE)
                } catch (_: Exception) {
                    pendingExport = null
                    Toast.makeText(this@MainActivity, "Unable to open save dialog", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    @Deprecated("Uses the platform activity-result callback for broad compatibility")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_OPEN_FILE -> {
                val result = if (resultCode == RESULT_OK) data?.data?.let { arrayOf(it) } else null
                fileChooserCallback?.onReceiveValue(result)
                fileChooserCallback = null
            }
            REQUEST_SAVE_FILE -> {
                val export = pendingExport
                pendingExport = null
                val uri = if (resultCode == RESULT_OK) data?.data else null
                if (export != null && uri != null) {
                    try {
                        contentResolver.openOutputStream(uri)?.use { output ->
                            output.write(export.content.toByteArray(Charsets.UTF_8))
                        } ?: throw IOException("Could not open the selected destination")
                        Toast.makeText(this, "File saved", Toast.LENGTH_SHORT).show()
                    } catch (error: Exception) {
                        Log.e(TAG, "Export failed", error)
                        Toast.makeText(this, "Could not save file: ${error.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        webView.saveState(outState)
        super.onSaveInstanceState(outState)
    }

    @Suppress("DEPRECATION")
    override fun onBackPressed() {
        webView.evaluateJavascript(
            """(function(){const tele=document.getElementById('tele');if(tele&&tele.classList.contains('open')){document.dispatchEvent(new KeyboardEvent('keydown',{key:'Escape'}));return 'handled';}return 'exit';})()"""
        ) { result ->
            if (result != "\"handled\"") finish()
        }
    }

    override fun onDestroy() {
        if (::webView.isInitialized) {
            fileChooserCallback?.onReceiveValue(null)
            fileChooserCallback = null
            webView.removeJavascriptInterface("AndroidBridge")
            webView.stopLoading()
            webView.destroy()
        }
        super.onDestroy()
    }
}
