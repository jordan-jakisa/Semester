package com.bawano.semester.ui.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bawano.semester.databinding.FragmentHomeBinding
import com.bawano.semester.utils.Constants.COURSES
import com.bawano.semester.utils.Constants.DETAILS
import com.bawano.semester.utils.Constants.PDFVIEW
import com.bawano.semester.utils.PreferenceManager
import kotlinx.coroutines.launch

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

    override fun onAttach(context: Context) {
        super.onAttach(context)
        lifecycleScope.launch{
            PreferenceManager(context).lastPage.collect {
                val action = when(it.name){
                    COURSES -> HomeFragmentDirections.actionNavHomeToCourses(it)
                    PDFVIEW -> HomeFragmentDirections.actionNavHomeToPdfViewFragment(it)
                    DETAILS -> HomeFragmentDirections.actionNavHomeToDetailsFragment(it)
                    else -> HomeFragmentDirections.actionNavHomeToCourseUnits(it)
                }
                findNavController().navigate(action)
            }

        }
    }

}