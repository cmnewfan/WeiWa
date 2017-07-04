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
import android.widget.TextView

import com.weiwa.ljl.weiwa.R
import com.weiwa.ljl.weiwa.activity.MainActivity
import com.weiwa.ljl.weiwa.adapter.WeiboAdapter
import com.weiwa.ljl.weiwa.listener.OnUserUpdatedListener
import com.weiwa.ljl.weiwa.listener.onAdapterEvent
import com.weiwa.ljl.weiwa.network.WeiboPojo
import com.weiwa.ljl.weiwa.view.VerticalSpaceItemDecoration


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [UserViewFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 */
class UserViewFragment : Fragment() {

    private var mListener: OnFragmentInteractionListener? = null
    private var mView: View? = null
    private var bundle: Bundle? = null
    private var mRecyclerView: RecyclerView? = null
    private var adapter: WeiboAdapter? = null
    private var user_name: TextView? = null
    private var user_descrip: TextView? = null
    private var user_location: TextView? = null
    private var user_gender: TextView? = null
    private var user_follow_me: TextView? = null
    private var listener: OnUserUpdatedListener? = null
    private var adapterEvent: onAdapterEvent? = null
    private val ItemDivider = 30

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            bundle = arguments
            (activity as MainActivity).getWeiboData(3, bundle!!.getString("name"))
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle): View? {
        // Inflate the layout for this fragment
        adapterEvent = object : onAdapterEvent {
            override fun onNeedInsert() {
                (activity as MainActivity).getWeiboData(4, bundle!!.getString("uid"))
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
        this.listener = object : OnUserUpdatedListener {
            override fun onUpdate(pojo: WeiboPojo, updateType: Int) {
                if (adapter == null || updateType == 3) {
                    adapter = WeiboAdapter(pojo, this@UserViewFragment, adapterEvent!!)
                    mRecyclerView!!.adapter = adapter
                } else {
                    adapter!!.insertNewData(pojo)
                }
            }
        }
        (activity as MainActivity).setOnUserUpdatedListener(listener!!)
        this.mView = inflater.inflate(R.layout.fragment_user_view, container, false)
        this.user_name = mView!!.findViewById(R.id.single_user) as TextView
        this.user_name!!.text = bundle!!.getString("name")
        this.user_descrip = mView!!.findViewById(R.id.description) as TextView
        this.user_descrip!!.text = bundle!!.getString("description")
        this.user_location = mView!!.findViewById(R.id.location) as TextView
        this.user_location!!.text = bundle!!.getString("location")
        this.user_gender = mView!!.findViewById(R.id.gender) as TextView
        this.user_gender!!.text = bundle!!.getString("gender")
        this.user_follow_me = mView!!.findViewById(R.id.follow_me) as TextView
        this.user_follow_me!!.text = bundle!!.getString("follow_me")
        this.mRecyclerView = mView!!.findViewById(R.id.main_recycler_view) as RecyclerView
        mRecyclerView!!.setHasFixedSize(true)
        mRecyclerView!!.layoutManager = LinearLayoutManager(activity)
        mRecyclerView!!.itemAnimator = DefaultItemAnimator()
        mRecyclerView!!.addItemDecoration(VerticalSpaceItemDecoration(ItemDivider))
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
}// Required empty public constructor
