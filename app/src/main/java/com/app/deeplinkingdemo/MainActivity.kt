package com.app.deeplinkingdemo

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.app.deeplinkingdemo.databinding.ActivityMainBinding
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.dynamiclinks.PendingDynamicLinkData

/**
 * Created by Priyanka
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var deepLinkUrl: String
    private lateinit var builder: DynamicLink.Builder
    private val urlPrefix = "https://deeplinkingDemo18.page.link"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //display data after the app opens by clicking on the link
        getDynamicLinks()

        //dynamic link builder for making link
        builder = FirebaseDynamicLinks.getInstance()
            .createDynamicLink()
            .setDomainUriPrefix(urlPrefix)
            .setAndroidParameters(
                DynamicLink.AndroidParameters.Builder()
                    .setMinimumVersion(0)
                    .build()
            )

        binding.let {

            //long link
            it.btnClick.setOnClickListener {
                startIntent(getDeepLinkUri())
            }

            //short link
            it.btnClick2.setOnClickListener {
                FirebaseDynamicLinks.getInstance().createDynamicLink()
                    .setLongLink(getDeepLinkUri())
                    .buildShortDynamicLink()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // Short link created
                            val shortLink: Uri = task.result.shortLink!!
                            val flowchartLink: Uri = task.result.previewLink!!

                            Log.e("shortLink", "$shortLink")
                            Log.e("flowchartLink", "$flowchartLink")
                            startIntent(shortLink)
                        } else {
                            // Error
                            // ...
                        }
                    }
                    .addOnFailureListener {
                        Log.e("shortLink", "error")
                    }
            }

            //long link with social tags
            it.btnClick3.setOnClickListener {
                builder.setSocialMetaTagParameters(
                    DynamicLink.SocialMetaTagParameters.Builder()
                        .setTitle("Example of a Dynamic Link")
                        .setDescription("This link works whether the app is installed or not!")
                        .build()
                )
                startIntent(getDeepLinkUri())
            }

            //short link with social tags
            it.btnClick4.setOnClickListener {
                FirebaseDynamicLinks.getInstance().createDynamicLink()
                    .setLongLink(getDeepLinkUri())
                    .setSocialMetaTagParameters(
                        DynamicLink.SocialMetaTagParameters.Builder()
                            .setTitle("Example of a Dynamic Link")
                            .setDescription("This link works whether the app is installed or not!")
                            .build()
                    )
                    .buildShortDynamicLink()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // Short link created
                            val shortLink: Uri = task.result.shortLink!!
                            val flowchartLink: Uri = task.result.previewLink!!

                            Log.e("shortLink", "$shortLink")
                            Log.e("flowchartLink", "$flowchartLink")
                            startIntent(shortLink)
                        } else {
                            // Error
                            // ...
                        }
                    }
                    .addOnFailureListener {
                        Log.e("shortLink", "error")
                    }
            }
        }
    }

    private fun getDeepLinkUri(): Uri {
        deepLinkUrl = "https://google.com/?text=${binding.etEnter.text}"

        /* .setIosParameters(
            DynamicLink.IosParameters.Builder("ios_bundle_id").setAppStoreId(
                "appstore_id"
            ).build()
        )*/
        builder.link = Uri.parse(deepLinkUrl)
        return builder.buildDynamicLink().uri
    }

    private fun startIntent(uriLink: Uri) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(
            Intent.EXTRA_TEXT, "Link shared $uriLink"
        )
        startActivity(intent)
    }

    private fun getDynamicLinks() {
        FirebaseDynamicLinks.getInstance()
            .getDynamicLink(intent)
            .addOnSuccessListener(this) { pendingDynamicLinkData: PendingDynamicLinkData? ->
                var deepLink: Uri? = null
                if (pendingDynamicLinkData != null) {
                    deepLink = pendingDynamicLinkData.link
                }
                if (deepLink != null) {
                    if (deepLink.getQueryParameter("text")?.isNotEmpty()!!) {
                        binding.tvDisplay.text = deepLink.getQueryParameter("text")
                    }
                }
            }
            .addOnFailureListener(this) { obj: Exception -> obj.printStackTrace() }
    }
}