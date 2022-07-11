package com.felipe.appExample.ui

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.felipe.appExample.AndroidApp
import com.felipe.appExample.HomeActivity
import com.felipe.appExample.android.R
import com.felipe.appExample.android.databinding.FragmentFirstBinding
import com.felipe.appExample.client.AppClient
import com.felipe.appExample.client.UserClient
import com.felipe.appExample.client.exceptions.HttpClientException
import com.felipe.appExample.client.exceptions.HttpServerException
import com.felipe.appExample.client.exceptions.ManageError
import com.felipe.appExample.client.exceptions.RequestException
import com.felipe.appExample.model.Repository
import com.felipe.appExample.sync.SynchronizeDatabase
import com.felipe.appExample.ui.viewsnippet.UserViewSnippet
import com.felipe.appExample.utils.ParserUtils
import com.felipe.db.User
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class UserFragment : Fragment() {


    private val scopeIO = CoroutineScope(Dispatchers.IO)

    private lateinit var activity: HomeActivity
    private lateinit var userViewSnippet: UserViewSnippet
    private lateinit var adapter: UserAdapter
    private lateinit var repository: Repository
    private lateinit var userManager: UserManager
    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        repository = AndroidApp.instance.getRepository()
        userManager = repository.getUserManager()!!
        activity = requireActivity() as HomeActivity

        initializeRecyclerView()
        checkNeedUpload()
        checkButtons()

        return binding.root

    }

    private fun checkNeedUpload() {
        scopeIO.launch {
            SynchronizeDatabase.sync(
                repository.getUserManager()!!.getQueries(),
                repository.getAppClient()!!,
                object : SynchronizeDatabase.DownloadCallback {
                    @SuppressLint("NotifyDataSetChanged")
                    override fun onDownloaded(responseCode: Int) {
                        if (responseCode != 200)
                            return
                        notifyDataSetChanged()
                    }
                })
        }
    }

    private fun initializeRecyclerView() {
        userViewSnippet = UserViewSnippet(30, 3, userManager.getQueries())
        val layoutManager = LinearLayoutManager(activity)
        binding.rvUsers.layoutManager = LinearLayoutManager(activity)
        adapter = UserAdapter(userViewSnippet, userManager)
        binding.rvUsers.adapter = adapter

        val dividerItemDecoration = DividerItemDecoration(
            activity,
            layoutManager.orientation
        )
        binding.rvUsers.addItemDecoration(dividerItemDecoration)

        adapter.setUserListener(object : UserAdapter.OnUserClickListener {
            override fun onEditClick(user: User, position: Int) {
                showEditDialog(user, position)
            }

            override fun onDeleteClick(user: User, position: Int) {
                deleteUser(user, position)
            }
        })

        binding.rvUsers.post {
            activity.setSearchListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }

                @SuppressLint("NotifyDataSetChanged")
                override fun onQueryTextChange(newText: String?): Boolean {
                    userViewSnippet.setNameLike(newText)
                    notifyDataSetChanged()
                    return true
                }
            })
        }

        activity.setAddedListener {
            notifyDataSetChanged()
        }
    }

    private fun deleteUser(user: User, position: Int) {
        if (user.remote_id != null) {
            repository.getAppClient()!!.deleteUser(
                user.remote_id!!.toInt()
            ) {
                if (it.error == null) {
                    activity.runOnUiThread {
                        deleteUserInViewAndDb(user, position)
                    }
                    return@deleteUser
                }

                if (it.error!!.code > 499) {
                    activity.runOnUiThread {
                        Snackbar.make(binding.root, getString(R.string.error_deleting_user), Snackbar.LENGTH_LONG)
                            .show()
                    }
                    return@deleteUser
                }

                activity.runOnUiThread {
                    showErrorSnackbar(it.error!!.code, it.error!!.message!!)
                }
            }
            return
        }

        deleteUserInViewAndDb(user, position)
    }

    private fun deleteUserInViewAndDb(user: User, position: Int) {
        repository.getUserManager()!!.deleteUser(user.local_id.toInt())
        userViewSnippet.reload()
        adapter.notifyItemRemoved(position)
    }

    private fun showEditDialog(user: User, position: Int) {
        val dialog = MaterialAlertDialogBuilder(activity)

        val layout = LinearLayout(context)
        layout.orientation = LinearLayout.VERTICAL

        val titleBox = EditText(context)
        titleBox.hint = getString(R.string.hint_edit_text)
        titleBox.setText(user.name ?: "")

        layout.addView(titleBox)
        dialog.setView(layout)

        dialog.setPositiveButton(
            getString(android.R.string.ok)
        ) { p0, p1 ->
            run {
                val name = titleBox.text.toString()
                val userManager = repository.getUserManager()!!
                val userClient = repository.getAppClient()!!

                if (user.remote_id != null) {
                    userClient.updateUser(
                        user.remote_id!!.toInt(),
                        name,
                        ParserUtils.parseTimeToTimestamp(user.birth_date)
                    ) {
                        manageUpdateUser(it, userManager, user, name, position)
                    }
                    return@run
                }

                uploadUser(user, name, position, userClient)
            }
        }

        val d = dialog.create()

        titleBox.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val text = s.toString()
                val button = d.getButton(AlertDialog.BUTTON_POSITIVE)
                if (text.isBlank() || user.name == text) {
                    button.isEnabled = false
                    return
                }

                button.isEnabled = true
            }
        })

        d.show()

        d.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false
    }

    private fun manageUpdateUser(
        it: AppClient.Response<String?>,
        userManager: UserManager,
        user: User,
        name: String,
        position: Int
    ) {
        if (it.error != null) {
            showErrorSnackbar(it.error!!.code, it.error!!.message!!)
            return
        }

        userManager.saveUser(
            User(
                user.local_id,
                name,
                user.birth_date,
                user.remote_id
            )
        )

        notifyDataSetChanged()
    }

    private fun uploadUser(user: User, name: String, position: Int, userClient: UserClient) {
        val usrUpdated = User(
            user.local_id,
            name,
            user.birth_date,
            null
        )

        userManager.saveUser(usrUpdated)
        activity.runOnUiThread {
            adapter.notifyItemChanged(position)
        }
        userClient.uploadUser(
            usrUpdated.name,
            ParserUtils.parseTimeToTimestamp(usrUpdated.birth_date)
        ) {
            if (it.error != null) {
                showErrorSnackbar(it.error!!.code, it.error!!.message!!)
                return@uploadUser
            }

            SynchronizeDatabase.sync(
                repository.getUserManager()!!.getQueries(),
                repository.getAppClient()!!,
                object : SynchronizeDatabase.DownloadCallback {
                    override fun onDownloaded(responseCode: Int) {
                        if (responseCode != 200)
                            return
                        notifyDataSetChanged()
                    }
                })
        }
    }


    private fun showErrorSnackbar(code: Int, message: String) {
        val ex =
            ManageError.manageErrorCode(code, message)

        val msgErr: String = when (ex.javaClass) {
            HttpClientException::class.java -> {
                getString(R.string.error_client_user_fragment)
            }
            HttpServerException::class.java -> {
                getString(R.string.error_server_user_fragment)
            }
            RequestException::class.java -> {
                ex.message!!
            }
            else -> {
                getString(R.string.error_app_general)
            }
        }
        activity.runOnUiThread {
            Snackbar.make(
                binding.root,
                msgErr,
                Snackbar.LENGTH_LONG
            ).show()
        }
    }

    private fun checkButtons() {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @SuppressLint("NotifyDataSetChanged")
    fun notifyDataSetChanged() {
        userViewSnippet.reload()
        activity.runOnUiThread {
            adapter.notifyDataSetChanged()
        }
    }
}