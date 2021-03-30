package com.jasvir.localgalleryapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

/**
 * @author Jasvir
 */
abstract class BindingFragment<T : ViewDataBinding> : BaseFragment() { //SupportNavigationFragment() {

    protected lateinit var binding: T
    protected abstract val layoutId: Int

    final override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, layoutId, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onViewCreated()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onCreate()
    }

    open fun onCreate() {}

    open fun onViewCreated() {}

    fun close() {
        activity?.supportFragmentManager?.popBackStack()
    }
}