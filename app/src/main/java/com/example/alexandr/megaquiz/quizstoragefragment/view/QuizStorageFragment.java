package com.example.alexandr.megaquiz.quizstoragefragment.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.Group;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import com.example.alexandr.megaquiz.R;
import com.example.alexandr.megaquiz.app.App;
import com.example.alexandr.megaquiz.quizfragment.view.QuizActivity;
import com.example.alexandr.megaquiz.quizstoragefragment.QuizStorageContract;
import com.example.alexandr.megaquiz.quizstoragefragment.QuizStorageItem;
import com.example.alexandr.megaquiz.quizstoragefragment.inject.QuizStorageFragmentComponent;
import com.example.alexandr.megaquiz.quizstoragefragment.inject.QuizStorageFragmentPresenterModule;

import net.bohush.geometricprogressview.GeometricProgressView;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.Unbinder;

/**
 * Created by Alexandr Mikhalev on 11.09.2018.
 *
 * @author Alexandr Mikhalev
 */
public class QuizStorageFragment extends Fragment implements QuizStorageContract.View {

    @Inject
    QuizStorageContract.Presenter mPresenter;

    @BindView(R.id.list_switch)
    Switch mSwitch;

    @BindView(R.id.text_for_switch)
    TextView mSwitchStateTextView;

    @BindView(R.id.progress_bar_quiz_storage)
    GeometricProgressView mProgressBar;

    @BindView(R.id.storage_recycler)
    RecyclerView mRecyclerView;

    @BindView(R.id.static_group)
    Group mStaticGroup;

    private RecyclerAdapter mAdapter;
    private Unbinder mUnbinder;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        QuizStorageFragmentComponent component = (QuizStorageFragmentComponent) App.getApp(getContext()).getComponentsHolder()
                .getFragmentComponent(getClass(), new QuizStorageFragmentPresenterModule());
        component.inject(this);
        mPresenter.attachView(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quiz_storage, null);
        mUnbinder = ButterKnife.bind(this, view);

        FragmentActivity fragmentActivity = getActivity();
        GridLayoutManager layoutManager = new GridLayoutManager(fragmentActivity, 2);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new RecyclerAdapter(key -> mPresenter.onClick(key));
        mPresenter.onStart();
        mRecyclerView.setAdapter(mAdapter);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
        if (!getActivity().isChangingConfigurations()) {
            mPresenter.onDestroy();
            App.getApp(getContext()).getComponentsHolder().releaseFragmentComponent(getClass());
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mPresenter.detachView();
    }

    @Override
    public void showLoading() {
        mStaticGroup.setVisibility(View.INVISIBLE);
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        mStaticGroup.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void addQuizStorageItemListForRecyclerAdapter(List<QuizStorageItem> list) {
        mAdapter.setData(list);
    }

    @Override
    public void startActivityQuizView(String key) {
        Intent intent = QuizActivity.getIntent(getContext(), key);
        startActivity(intent);
    }

    @Override
    public void updateRecyclerView(List<QuizStorageItem> newList) {
        RecyclerAdapterDiffUtilCallback recyclerAdapterDiffUtilCallback =
                new RecyclerAdapterDiffUtilCallback(mAdapter.getData(), newList);
        DiffUtil.DiffResult recyclerDiffResult = DiffUtil.calculateDiff(recyclerAdapterDiffUtilCallback);
        mAdapter.setData(newList);
        recyclerDiffResult.dispatchUpdatesTo(mAdapter);
    }

    @OnCheckedChanged({R.id.list_switch})
    void onSelected(Switch button, boolean checked) {
        String text = "Показать пустые категории";
        if (checked) text = "Скрыть пустые категории";
        mSwitchStateTextView.setText(text);
        mPresenter.onCheckBoxClick(checked);
    }

    public static QuizStorageFragment newInstance() {
        Bundle args = new Bundle();
        QuizStorageFragment fragment = new QuizStorageFragment();
        fragment.setArguments(args);
        return fragment;
    }
}



