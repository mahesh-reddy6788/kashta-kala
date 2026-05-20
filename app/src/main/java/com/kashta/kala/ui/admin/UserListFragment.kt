package com.kashta.kala.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kashta.kala.databinding.FragmentUserListBinding
import com.kashta.kala.databinding.ItemUserBinding

class UserListFragment : Fragment() {

    private var _binding: FragmentUserListBinding? = null
    private val binding get() = _binding!!

    // Hardcoded user list
    data class UserItem(
        val id: Int, val name: String, val email: String,
        val phone: String, val isAdmin: Boolean
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val dynamicUsers = com.kashta.kala.data.DataRepository.usersList.map { u ->
            UserItem(u.id, u.name, u.email, u.phone, u.isAdmin)
        }
        val adapter = UserAdapter(dynamicUsers)
        binding.rvUsers.layoutManager = LinearLayoutManager(requireContext())
        binding.rvUsers.adapter = adapter
        binding.rvUsers.setHasFixedSize(true)
        binding.tvEmpty.visibility = if (dynamicUsers.isEmpty()) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

class UserAdapter(private val users: List<UserListFragment.UserItem>) :
    RecyclerView.Adapter<UserAdapter.VH>() {

    inner class VH(val b: ItemUserBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        val u = users[position]
        holder.b.tvUserName.text  = u.name
        holder.b.tvUserEmail.text = u.email
        holder.b.tvUserPhone.text = u.phone
        holder.b.tvAdminBadge.visibility = if (u.isAdmin) View.VISIBLE else View.GONE
    }

    override fun getItemCount() = users.size
}
