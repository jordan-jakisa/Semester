package com.bawano.semester.ui.courses

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bawano.semester.databinding.FragmentCoursesBinding

class CoursesFragment : Fragment() {

    private var b: FragmentCoursesBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = b!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val coursesViewModel =
            ViewModelProvider(this).get(CoursesViewModel::class.java)

        b = FragmentCoursesBinding.inflate(inflater, container, false)
        val root: View = binding.root

        coursesViewModel.text.observe(viewLifecycleOwner) {
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        b = null
    }
}