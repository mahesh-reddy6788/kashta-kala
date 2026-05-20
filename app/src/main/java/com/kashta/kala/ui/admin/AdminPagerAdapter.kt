package com.kashta.kala.ui.admin

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class AdminPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount() = 3
    override fun createFragment(position: Int): Fragment = when (position) {
        0    -> CatalogCrudFragment()
        1    -> UserListFragment()
        else -> AdminOrdersFragment()
    }
}
