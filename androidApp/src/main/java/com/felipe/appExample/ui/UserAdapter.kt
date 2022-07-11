package com.felipe.appExample.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.felipe.appExample.android.R
import com.felipe.appExample.android.databinding.ContainerUserLayoutBinding
import com.felipe.appExample.android.databinding.FragmentUserListBinding
import com.felipe.appExample.ui.placeholder.PlaceholderContent.PlaceholderItem
import com.felipe.appExample.ui.viewsnippet.UserViewSnippet
import com.felipe.appExample.utils.ParserUtils
import com.felipe.db.User

/**
 * [RecyclerView.Adapter] that can display a [PlaceholderItem].
 * TODO: Replace the implementation with code for your data type.
 */
class UserAdapter(
    private val userViewSnippet: UserViewSnippet,
    private val userManager: UserManager
) : RecyclerView.Adapter<UserAdapter.ViewHolder>() {

    interface OnUserClickListener {
        fun onEditClick(user: User, position: Int)
        fun onDeleteClick(user: User, position: Int)
    }

    inner class ViewHolder(val binding: FragmentUserListBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }

    private var onUserClickListener: OnUserClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            FragmentUserListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.swlContainer.reset()
        val item = userViewSnippet.getIdAtViewIndex(position)

        val context = holder.binding.ivUserUploaded.context

        val user = userManager.getUserByLocalId(item!!)!!

        if (user.name != null && user.name!!.isNotEmpty())
            holder.binding.tvUserName.text = context.getString(R.string.name_title, user.name)
        else
            holder.binding.tvUserName.text = "No name setted"
        if (user.birth_date != null)
            holder.binding.tvUserBirthdate.text = context.getString(
                R.string.birthdate_title,
                ParserUtils.parseTimeToTimestamp(user.birth_date)
            )
        else
            holder.binding.tvUserBirthdate.text = "No birthdate setted"

        holder.binding.tvUserLocalId.text =
            context.getString(R.string.local_id_title, user.local_id.toString())

        val imageResource =
            if (user.remote_id != null) android.R.drawable.presence_online else android.R.drawable.presence_away
        holder.binding.ivUserUploaded.setImageResource(imageResource)

        setButtonClick(holder, user)
    }

    private fun setButtonClick(holder: UserAdapter.ViewHolder, user: User) {
        holder.binding.clUserDelete.setOnClickListener {
            onUserClickListener?.onDeleteClick(user, holder.absoluteAdapterPosition)
        }

        holder.binding.clUserEdit.setOnClickListener {
            onUserClickListener?.onEditClick(user, holder.absoluteAdapterPosition)
        }
    }

    override fun getItemCount(): Int = userViewSnippet.size

    fun setUserListener(listener: OnUserClickListener) {
        onUserClickListener = listener
    }

}