package com.justimagine.imagine.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.justimagine.imagine.*
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel

    val db = FirebaseFirestore.getInstance()
    val posts = ArrayList<Post>()
    val postHelper = PostHelper()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }


    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        homeViewModel.text.observe(this, Observer {

        })
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // RecyclerView node initialized here
        main_feed.apply {
            layoutManager = LinearLayoutManager(activity)

            val adapter = MainAdapter(listOf(Post()))       // Not the ideal solution, but it works
            main_feed.adapter = adapter
        }

        getPosts(more = false)
    }


    fun RecyclerView.addOnScrolledToEnd(onScrolledToEnd: () -> Unit){

        this.addOnScrollListener(object: RecyclerView.OnScrollListener(){

            private val visibleThreshold = 2

            private var loading = true
            private var previousTotal = 0


            override fun onScrollStateChanged(recyclerView: RecyclerView,
                                              newState: Int) {

                with(layoutManager as LinearLayoutManager){

                    val visibleItemCount = childCount
                    val totalItemCount = itemCount
                    val firstVisibleItem = findFirstVisibleItemPosition()


                    if (loading && totalItemCount > previousTotal){

                        loading = false
                        previousTotal = totalItemCount
                    }

                    if(!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)){

                        onScrolledToEnd()
                        loading = true
                    }
                }
            }
        })
    }

    fun getPosts(more: Boolean) {


        postHelper.getPostsForMainFeed(more, object: FirebaseCallback {
             override fun onCallback(value: List<Post>) {

                val adapter = main_feed.adapter as MainAdapter

                 if (adapter != null) {

                         println("New Items in List")
                         adapter.setItems(value)
                     adapter.notifyDataSetChanged()
                 } else {
                     println("No adapter")
                 }

//                 if (more) {
//                     println("New Items in List")
//                     adapter.setItems(value)
//                     adapter.notifyDataSetChanged()
//                     adapter.notifyItemInserted(value.size - 1)
//                 } else {
//                     println("Load initial List")
//                     main_feed.adapter = adapter
//                 }



                 main_feed.addOnScrolledToEnd {
                     println("Bin am Ende angelangt")
                     getPosts(true)
                 }
            }
        })
    }

//    fun setUpAdapter(list: List<Post>) {
//
//        println("SetUpAdapter***")
//
//
//        main_feed.addOnItemTouchListener(RecyclerItemClickListenr(requireContext(), main_feed, object : RecyclerItemClickListenr.OnItemClickListener {
//
//            override fun onItemClick(view: View, position: Int) {
//                val post = list[position]
//
//                println("${post.title} Wurde angeklickt")
//            }
//            override fun onItemLongClick(view: View?, position: Int) {
//                TODO("do nothing")
//            }
//        }))
//    }
}