package com.weiwa.ljl.weiwa;

import android.content.Context;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.weiwa.ljl.weiwa.WeiboModel.WeiboCommentPojo;
import com.weiwa.ljl.weiwa.WeiboModel.WeiboPojo;

import java.net.URLEncoder;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MainFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private View mView;
    private RecyclerView mRecyclerView;
    private onAdapterEvent onNeedInsert;
    private WeiboAdapter mWeiboAdapter;
    private int ItemDivider = 25;

    public MainFragment() {
        // Required empty public constructor
    }


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MainFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MainFragment newInstance(String param1, String param2) {
        MainFragment fragment = new MainFragment();
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
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //init AdapterEvent for adater calling comment or repost
        onNeedInsert = new onAdapterEvent() {
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
        };
        //implements event of activity for refresh following data and display weibo
        ((MainActivity)getActivity()).setOnWeiboPOJOUpdated(new onWeiboUpdatedListener() {
            @Override
            public void onUpdate(WeiboPojo pojo, int updateType) {
                if(mWeiboAdapter==null || updateType==0){
                    mWeiboAdapter = new WeiboAdapter(pojo,MainFragment.this,onNeedInsert);
                    mRecyclerView.setAdapter(mWeiboAdapter);
                }else{
                    if(pojo==null || pojo.getStatuses().length==0){
                        Toast.makeText(getActivity(),"已更新至最后一条",Toast.LENGTH_SHORT).show();
                    }else {
                        mWeiboAdapter.insertNewData(pojo);
                    }
                }
            }
            @Override
            public void onComment(WeiboCommentPojo comment){
                Bundle bundle = new Bundle();
                bundle.putParcelable("CommentPojo",comment);
                if(comment.getStatus().getRetweeted_status()!=null && comment.getStatus().getRetweeted_status().getId()!=null){
                    WeiboRetweetCommentFragment weiboRetweetCommentFragment = new WeiboRetweetCommentFragment();
                    weiboRetweetCommentFragment.setArguments(bundle);
                    ((MainActivity)getActivity()).setFragment(weiboRetweetCommentFragment);
                }else{
                    WeiboCommentFragment weiboCommentFragment = new WeiboCommentFragment();
                    weiboCommentFragment.setArguments(bundle);
                    ((MainActivity)getActivity()).setFragment(weiboCommentFragment);
                }
            }
        });
        mView = inflater.inflate(R.layout.fragment_main, container, false);
        mRecyclerView = (RecyclerView) mView.findViewById(R.id.main_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new VerticalSpaceItemDecoration(ItemDivider));
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

class VerticalSpaceItemDecoration extends RecyclerView.ItemDecoration {

    private final int verticalSpaceHeight;

    public VerticalSpaceItemDecoration(int verticalSpaceHeight) {
        this.verticalSpaceHeight = verticalSpaceHeight;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                               RecyclerView.State state) {
        outRect.bottom = verticalSpaceHeight;
    }
}
