package com.felipe.appExample

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.SearchView
import androidx.core.view.WindowCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.felipe.appExample.android.R
import com.felipe.appExample.android.databinding.ActivityHomeBinding
import com.felipe.appExample.client.exceptions.HttpClientException
import com.felipe.appExample.client.exceptions.HttpServerException
import com.felipe.appExample.client.exceptions.RequestException
import com.felipe.appExample.model.Repository
import com.felipe.appExample.sync.SynchronizeDatabase
import com.felipe.appExample.ui.AddUserDialog
import com.felipe.appExample.ui.utils.DialogUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.launch

class HomeActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityHomeBinding
    private lateinit var repository: Repository

    private val scopeIO = CoroutineScope(Dispatchers.IO)
    private var callbackWhenAdded: Runnable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        repository = AndroidApp.instance.getRepository()
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        navController = findNavController(R.id.nav_host_fragment_content_home)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        prepareButtons()
    }

    private fun prepareButtons() {
        binding.fabAddUser.setOnClickListener { view ->
            val addDialog = AddUserDialog.newInstance()


            addDialog.setListener(object : AddUserDialog.OnUserListener {
                override fun onUserPopulate(name: String, time: Long) {
                    repository.getUserManager()!!.getQueries().insertUser(
                        name,
                        time
                    )

                    addDialog.dismiss()
                    callbackWhenAdded?.run()
                    scopeIO.launch {
                        SynchronizeDatabase.sync(
                            repository.getUserManager()!!.getQueries(),
                            repository.getAppClient()!!,
                            object : SynchronizeDatabase.DownloadCallback {
                                override fun onDownloaded(responseCode: Int) {
                                    callbackWhenAdded?.run()
                                }
                            }
                        )
                    }
                }
            })
            addDialog.show(supportFragmentManager, null)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_home)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    private fun handleException(manageErrorCode: Exception) {
        val clazz = manageErrorCode.javaClass
        var title = ""
        var message = ""

        when (clazz) {
            RequestException::class.java.javaClass -> {
                title = "Request Error"
                message =
                    "This only can see in development, if u can see this message, write to xxx@gmailcom"
            }
            HttpClientException::class.java.javaClass -> {
                title = "Http Client Error"
                message = manageErrorCode.message!!
            }
            HttpServerException::class.java.javaClass -> {
                title = "Server Error"
                message = "The server is not available, please try again later"
            }
            else -> {
                title = "Error"
                message = manageErrorCode.message!!
            }
        }

        this.runOnUiThread {
            val context: Context = ContextThemeWrapper(this, R.style.AppTheme)

            DialogUtils.buildDialog(context, title, message).show()
        }
    }

    fun setAddedListener(callbackWhenAdded: Runnable) {
        this.callbackWhenAdded = callbackWhenAdded
    }

    fun setSearchListener(listener: SearchView.OnQueryTextListener) {
        binding.svHomeButton.setOnQueryTextListener(listener)
    }
}