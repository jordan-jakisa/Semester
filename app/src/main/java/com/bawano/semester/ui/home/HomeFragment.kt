package com.bawano.semester.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bawano.semester.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private lateinit var b: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        b = FragmentHomeBinding.inflate(inflater)
        return b.root
    }

}