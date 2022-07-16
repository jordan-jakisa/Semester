package com.bawano.semester.ui.courses

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bawano.semester.databinding.FragmentCoursesBinding

class CoursesFragment : Fragment() {

    private lateinit var b: FragmentCoursesBinding
    private var scroll = 0


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val vm = ViewModelProvider(this)[CoursesViewModel::class.java]
        b = FragmentCoursesBinding.inflate(inflater, container, false)

        b.shimmerLayout.startShimmer()



        return b.root
    }

}