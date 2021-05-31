package org.techtown.cryptoculus.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import org.techtown.cryptoculus.R
import org.techtown.cryptoculus.databinding.FragmentHelpBinding

// HelpActivity에서 사용되는 프래그먼트
class HelpFragment(private val description: String, private val image: Int?) : Fragment() {
// class HelpFragment(private val description: String) : Fragment() {
    // private lateinit var binding: FragmentHelpBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) = (DataBindingUtil
        .inflate(inflater, R.layout.fragment_help, container, false) as FragmentHelpBinding)
        .apply {
            Glide.with(this@HelpFragment).load(image).into(imageView)
            description = this@HelpFragment.description
        }.root
}