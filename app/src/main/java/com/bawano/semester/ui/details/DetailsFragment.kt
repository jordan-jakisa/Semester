package com.bawano.semester.ui.details

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bawano.semester.R
import com.bawano.semester.databinding.FragmentDetailsBinding
import com.bawano.semester.models.CourseUnit
import com.bawano.semester.models.LastPage
import com.bawano.semester.ui.pdf.PdfAdapter
import com.bawano.semester.utils.Constants
import com.bawano.semester.utils.PreferenceManager
import com.bawano.semester.utils.Utils
import com.bumptech.glide.Glide
import com.google.gson.Gson
import kotlinx.coroutines.launch


class DetailsFragment : Fragment(), Utils.OnPdf {

    private lateinit var b:FragmentDetailsBinding
    private var courseUnit:CourseUnit? = null
    private val gson = Gson()
    private lateinit var page: Utils.FragmentPage
    private var isExpanded = false
    private val args by navArgs<DetailsFragmentArgs>()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        page = context as Utils.FragmentPage
        lifecycleScope.launch {
            PreferenceManager(context).lastPage.collect {
                if (it.name == Constants.DETAILS) {
                    it.pageVariables[Constants.COURSE_UNITS]?.let { c ->
                        courseUnit = gson.fromJson(c, CourseUnit::class.java)
                    }
                }
            }

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        b =  FragmentDetailsBinding.inflate(inflater)
        if(courseUnit == null) courseUnit = args.courseUnit
        b.courseUnit = courseUnit
        b.detailsLayout.setOnClickListener { expand() }
        b.courseDetails.setOnClickListener { expand() }
        b.courseDetailsMini.setOnClickListener { expand() }
        val adapter = PdfAdapter(this)
        b.recyclerView.apply {
            setAdapter(adapter)
            layoutManager = GridLayoutManager(requireContext(), 3)
        }
        adapter.submitList(courseUnit!!.notes)


        return b.root
    }

    private fun expand() {
        if (isExpanded) {
            b.courseDetails.visibility = View.GONE
            Glide.with(this).load(R.drawable.ic_arrow_down).into(b.expandIv)
            b.courseDetailsMini.visibility = View.VISIBLE
            b.moreTv.setText(R.string.more)
        } else {
            b.moreTv.setText(R.string.less)
            b.courseDetailsMini.visibility = View.GONE
            Glide.with(this).load(R.drawable.ic_arrow_up).into(b.expandIv)
            b.courseDetails.visibility = View.VISIBLE
        }
        isExpanded = !isExpanded
    }


    override fun onDestroyView() {
        super.onDestroyView()
        val map: HashMap<String, String> = HashMap()
        map[Constants.COURSE_UNITS] = gson.toJson(courseUnit)
        page.setLastPage(LastPage(Constants.COURSES, map))
    }

    override fun click(name: String) {
        val action = DetailsFragmentDirections.actionDetailsFragmentToPdfViewFragment(name)
        findNavController().navigate(action)
    }


}