package com.bawano.semester.ui.units

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bawano.semester.databinding.FragmentCourseUnitsBinding

class CourseUnitsFragment : Fragment() {

    private var b: FragmentCourseUnitsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = b!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val courseUnitsViewModel =
            ViewModelProvider(this).get(CourseUnitsViewModel::class.java)

        b = FragmentCourseUnitsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        courseUnitsViewModel.text.observe(viewLifecycleOwner) {
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        b = null
    }
}