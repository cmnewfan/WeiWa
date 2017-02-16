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
import android.widget.TextView;

import com.weiwa.ljl.weiwa.WeiboModel.WeiboPojo;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UserViewFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class UserViewFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private View mView;
    private Bundle bundle;
    private RecyclerView mRecyclerView;
    private WeiboAdapter adapter;
    private TextView user_name;
    private TextView user_descrip;
    private TextView user_location;
    private TextView user_gender;
    private TextView user_follow_me;
    private OnUserUpdatedListener listener;
    private onAdapterEvent adapterEvent;
    private int ItemDivider=30;

    public UserViewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            bundle = getArguments();
            ((MainActivity)getActivity()).getWeiboData(3,bundle.getString("name"));
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        adapterEvent = new onAdapterEvent() {
            @Override
            public void onNeedInsert() {
                ((MainActivity)getActivity()).getWeiboData(4,bundle.getString("uid"));
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
        this.listener = new OnUserUpdatedListener() {
            @Override
            public void onUpdate(WeiboPojo pojo, int updateType) {
                if(adapter==null || updateType==3){
                    adapter = new WeiboAdapter(pojo,UserViewFragment.this,adapterEvent);
                    mRecyclerView.setAdapter(adapter);
                }else{
                    adapter.insertNewData(pojo);
                }
            }
        };
        ((MainActivity)getActivity()).setOnUserUpdatedListener(listener);
        this.mView = inflater.inflate(R.layout.fragment_user_view, container, false);
        this.user_name = (TextView) mView.findViewById(R.id.single_user);
        this.user_name.setText(bundle.getString("name"));
        this.user_descrip = (TextView) mView.findViewById(R.id.description);
        this.user_descrip.setText(bundle.getString("description"));
        this.user_location = (TextView) mView.findViewById(R.id.location);
        this.user_location.setText(bundle.getString("location"));
        this.user_gender = (TextView) mView.findViewById(R.id.gender);
        this.user_gender.setText(bundle.getString("gender"));
        this.user_follow_me = (TextView) mView.findViewById(R.id.follow_me);
        this.user_follow_me.setText(bundle.getString("follow_me"));
        this.mRecyclerView = (RecyclerView) mView.findViewById(R.id.main_recycler_view);
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
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
