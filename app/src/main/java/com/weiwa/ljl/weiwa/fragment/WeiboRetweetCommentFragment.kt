package com.weiwa.ljl.weiwa.fragment

import android.app.Fragment
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.weiwa.ljl.weiwa.R
import com.weiwa.ljl.weiwa.activity.MainActivity
import com.weiwa.ljl.weiwa.adapter.CommentAdapter
import com.weiwa.ljl.weiwa.listener.onAdapterEvent
import com.weiwa.ljl.weiwa.network.WeiboCommentPojo
import com.weiwa.ljl.weiwa.view.VerticalSpaceItemDecoration
import com.weiwa.ljl.weiwa.viewholder.Retweeted_ViewHolder


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [WeiboRetweetCommentFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [WeiboRetweetCommentFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class WeiboRetweetCommentFragment : Fragment() {

    // TODO: Rename and change types of parameters
    private var mParam1: String? = null
    private var mParam2: String? = null

    private var commentPojo: WeiboCommentPojo? = null
    private var mView: View? = null
    private var mRecyclerView: RecyclerView? = null
    private val ItemDivider = 25
    private var adapter: CommentAdapter? = null

    private var mListener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mParam1 = arguments.getString(ARG_PARAM1)
            mParam2 = arguments.getString(ARG_PARAM2)
            commentPojo = arguments.getParcelable<WeiboCommentPojo>("CommentPojo")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_weibo_retweet_comment, container, false)
        val holder = Retweeted_ViewHolder(mView!!, this@WeiboRetweetCommentFragment, object : onAdapterEvent {
            override fun onNeedInsert() {
                (activity as MainActivity).getWeiboData()
            }

            override fun onComment(content: String, id: String) {
                (activity as MainActivity).createComment(id, content)
            }

            override fun onNeedComment(id: String) {
                (activity as MainActivity).getWeiboComment(id)
            }

            override fun onRepost(content: String, id: String) {
                (activity as MainActivity).createRepost(id, content)
            }
        }, commentPojo!!.status!!.retweeted_status!!.id!!)
        holder.setRetweetId(commentPojo!!.status!!.id!!)
        holder.refresh(commentPojo!!.status!!)
        mRecyclerView = mView!!.findViewById(R.id.recycler_view) as RecyclerView
        mRecyclerView!!.setHasFixedSize(true)
        mRecyclerView!!.layoutManager = LinearLayoutManager(activity)
        mRecyclerView!!.itemAnimator = DefaultItemAnimator()
        mRecyclerView!!.isNestedScrollingEnabled = false
        mRecyclerView!!.addItemDecoration(VerticalSpaceItemDecoration(ItemDivider))
        adapter = CommentAdapter(commentPojo!!, this@WeiboRetweetCommentFragment)
        mRecyclerView!!.adapter = adapter
        return mView
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        if (mListener != null) {
            mListener!!.onFragmentInteraction(uri)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            mListener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html) for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        // TODO: Rename parameter arguments, choose names that match
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        private val ARG_PARAM1 = "param1"
        private val ARG_PARAM2 = "param2"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.

         * @param param1 Parameter 1.
         * *
         * @param param2 Parameter 2.
         * *
         * @return A new instance of fragment WeiboRetweetCommentFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String, param2: String): WeiboRetweetCommentFragment {
            val fragment = WeiboRetweetCommentFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }
}// Required empty public constructor
