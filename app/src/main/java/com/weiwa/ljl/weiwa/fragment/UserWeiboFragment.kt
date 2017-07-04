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
import android.view.animation.AnimationUtils
import android.widget.Toast

import com.weiwa.ljl.weiwa.R
import com.weiwa.ljl.weiwa.activity.MainActivity
import com.weiwa.ljl.weiwa.adapter.WeiboAdapter
import com.weiwa.ljl.weiwa.listener.onAdapterEvent
import com.weiwa.ljl.weiwa.listener.onUserWeiboListener
import com.weiwa.ljl.weiwa.network.WeiboCommentPojo
import com.weiwa.ljl.weiwa.network.WeiboPojo
import com.weiwa.ljl.weiwa.view.VerticalSpaceItemDecoration

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [UserWeiboFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [UserWeiboFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class UserWeiboFragment : Fragment() {

    // TODO: Rename and change types of parameters
    private var mParam1: String? = null
    private var mParam2: String? = null

    private var mListener: MainFragment.OnFragmentInteractionListener? = null

    private var mView: View? = null
    private var mRecyclerView: RecyclerView? = null
    private var onNeedInsert: onAdapterEvent? = null
    private var mWeiboAdapter: WeiboAdapter? = null
    private val ItemDivider = 25

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mParam1 = arguments.getString(ARG_PARAM1)
            mParam2 = arguments.getString(ARG_PARAM2)
        }
    }

    fun fadeRecyclerView(index: Int) {
        val manager = mRecyclerView!!.layoutManager as LinearLayoutManager
        for (i in manager.findFirstVisibleItemPosition()..manager.findLastVisibleItemPosition()) {
            if (i != index && mRecyclerView!!.getChildAt(i) != null) {
                mRecyclerView!!.getChildAt(i).startAnimation(AnimationUtils.loadAnimation(activity, R.anim.trans_anim))
            }
        }
    }

    private fun initListener() {
        //init AdapterEvent for adater calling comment or repost
        onNeedInsert = object : onAdapterEvent {
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
        }
        //implements event of activity for refresh following data and display weibo
        val activity = activity as MainActivity
        (getActivity() as MainActivity).setOnUserWeiboListener(object : onUserWeiboListener {
            override fun onUpdate(pojo: WeiboPojo, updateType: Int) {
                if (mWeiboAdapter == null || updateType == 0 || updateType == 7) {
                    mWeiboAdapter = WeiboAdapter(pojo, this@UserWeiboFragment, onNeedInsert!!)
                    mRecyclerView!!.adapter = mWeiboAdapter
                } else {
                    if (pojo == null || pojo.statuses!!.size == 0) {
                        Toast.makeText(getActivity(), "已更新至最后一条", Toast.LENGTH_SHORT).show()
                    } else {
                        mWeiboAdapter!!.insertNewData(pojo)
                    }
                }
                (getActivity() as MainActivity).stopAnimation()
            }

            override fun onComment(comment: WeiboCommentPojo) {
                val bundle = Bundle()
                bundle.putParcelable("CommentPojo", comment)
                if (comment.status!!.retweeted_status != null && comment.status!!.retweeted_status!!.id != null) {
                    val weiboRetweetCommentFragment = WeiboRetweetCommentFragment()
                    weiboRetweetCommentFragment.arguments = bundle
                    (getActivity() as MainActivity).setFragment(weiboRetweetCommentFragment)
                } else {
                    val weiboCommentFragment = WeiboCommentFragment()
                    weiboCommentFragment.arguments = bundle
                    (getActivity() as MainActivity).setFragment(weiboCommentFragment)
                }
            }
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_main, container, false)
        mRecyclerView = mView!!.findViewById(R.id.main_recycler_view) as RecyclerView
        mRecyclerView!!.setHasFixedSize(true)
        mRecyclerView!!.layoutManager = LinearLayoutManager(activity)
        mRecyclerView!!.itemAnimator = DefaultItemAnimator()
        mRecyclerView!!.addItemDecoration(VerticalSpaceItemDecoration(ItemDivider))
        initListener()
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
        if (context is MainFragment.OnFragmentInteractionListener) {
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
        private val mTag = "UserWeiboFragment"


        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.

         * @param param1 Parameter 1.
         * *
         * @param param2 Parameter 2.
         * *
         * @return A new instance of fragment MainFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String, param2: String): MainFragment {
            val fragment = MainFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }
}// Required empty public constructor
