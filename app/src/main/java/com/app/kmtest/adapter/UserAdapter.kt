package com.app.kmtest.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.kmtest.R
import com.app.kmtest.model.Data
import com.app.kmtest.databinding.ItemUserBinding
import com.bumptech.glide.Glide

class UserAdapter : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    private lateinit var onItemClickListener: OnItemClickListener
    private var listUser = ArrayList<Data>()

    fun setData(data: ArrayList<Data>) {
        listUser.addAll(data)
        notifyDataSetChanged()
    }

    fun clear() {
        listUser.clear()
        notifyDataSetChanged()
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): UserAdapter.UserViewHolder {
        val itemUserBinding =
            ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(itemUserBinding)
    }

    override fun onBindViewHolder(holder: UserAdapter.UserViewHolder, position: Int) {
        holder.bind(listUser[position])
    }

    override fun getItemCount(): Int = listUser.size

    inner class UserViewHolder(private val binding: ItemUserBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(user: Data) {
            with(binding) {
                tvUsersName.text =
                    itemView.context.getString(R.string.user_name, user.firstName, user.lastName)
                tvUsersEmail.text = user.email

                Glide.with(itemView.context)
                    .load(user.avatar)
                    .centerCrop()
                    .into(imgUser)

                itemView.setOnClickListener {
                    onItemClickListener.onItemClicked(user)
                }
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClicked(user: Data)
    }
}