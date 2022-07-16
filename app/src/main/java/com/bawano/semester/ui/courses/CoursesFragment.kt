package com.bawano.semester.ui.courses

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bawano.semester.databinding.FragmentCoursesBinding
import com.bawano.semester.models.Course
import com.bawano.semester.models.CourseAdapter
import com.bawano.semester.models.LastPage
import com.bawano.semester.utils.Constants.COURSES
import com.bawano.semester.utils.PreferenceManager
import com.bawano.semester.utils.Utils
import com.bawano.semester.utils.errorDialog
import kotlinx.coroutines.launch

class CoursesFragment : Fragment(), Utils.OnCourse {
    companion object {
        const val SCROLL_KEY = "scroll key"
    }

    private lateinit var b: FragmentCoursesBinding
    private var scroll = 0
    private lateinit var page: Utils.FragmentPage

    override fun onAttach(context: Context) {
        super.onAttach(context)
        page = context as Utils.FragmentPage
        lifecycleScope.launch {
            PreferenceManager(context).lastPage.collect {
                if (it.name == COURSES) {
                    it.pageVariables[SCROLL_KEY]?.let { s ->
                        scroll = Integer.valueOf(s)
                    }
                }
            }

        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val vm = ViewModelProvider(this)[CoursesViewModel::class.java]
        b = FragmentCoursesBinding.inflate(inflater, container, false)
        b.shimmerLayout.startShimmer()
        val courseAdapter = CourseAdapter(this)
        b.recyclerView.adapter = courseAdapter
        b.recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        b.recyclerView.scrollToPosition(scroll)
        lifecycleScope.launch {
            vm.fetchCourses().collect {
                when {
                    it.isSuccess -> {
                        val list = it.getOrNull()
                        list?.let {
                            courseAdapter.submitList(list)
                        }
                    }
                    it.isFailure -> requireContext().errorDialog("Couldn't Fetch Courses",
                        (it.exceptionOrNull()?:"Unknown error") as String
                    )
                }

                b.shimmerLayout.stopShimmer()
                b.shimmerLayout.visibility = View.GONE
            }
        }


        return b.root
    }

    override fun click(course: Course) {
    }

    override fun onDestroyView() {
        super.onDestroyView()
        val map: HashMap<String, String> = HashMap()
        map[SCROLL_KEY] = scroll.toString()
        page.setLastPage(LastPage(COURSES, map))
    }

}