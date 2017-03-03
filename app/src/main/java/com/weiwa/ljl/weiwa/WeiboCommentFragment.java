package com.weiwa.ljl.weiwa;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.weiwa.ljl.weiwa.WeiboModel.WeiboCommentPojo;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link WeiboCommentFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link WeiboCommentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WeiboCommentFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private WeiboCommentPojo commentPojo;
    private View mView;
    private RecyclerView mRecyclerView;
    private int ItemDivider = 25;
    private CommentAdapter adapter;

    private OnFragmentInteractionListener mListener;

    public WeiboCommentFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment WeiboCommentFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WeiboCommentFragment newInstance(String param1, String param2) {
        WeiboCommentFragment fragment = new WeiboCommentFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            commentPojo = getArguments().getParcelable("CommentPojo");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_weibo_comment_single, container, false);
        SingleWB_ViewHolder holder = new SingleWB_ViewHolder(mView,WeiboCommentFragment.this,new onAdapterEvent() {
            @Override
            public void onNeedInsert() {
                ((MainActivity)getActivity()).getWeiboData();
            }
            @Override
            public void onComment(String content,String id){
                ((MainActivity)getActivity()).createComment(id,content);
            }
            @Override
            public void onNeedComment(String id){
                ((MainActivity)getActivity()).getWeiboComment(id);
            }
            @Override
            public void onRepost(String content,String id){
                ((MainActivity)getActivity()).createRepost(id,content);
            }
        },commentPojo.getStatus().getId());
        holder.setCommentUnderline();
        holder.refresh(commentPojo.getStatus());
        mRecyclerView = (RecyclerView) mView.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.addItemDecoration(new VerticalSpaceItemDecoration(ItemDivider));
        adapter = new CommentAdapter(commentPojo,WeiboCommentFragment.this);
        mRecyclerView.setAdapter(adapter);
        return mView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
