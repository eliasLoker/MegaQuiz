package com.example.alexandr.megaquiz.quizresultfragment.view;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.Group;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import com.example.alexandr.megaquiz.Constants;
import com.example.alexandr.megaquiz.R;
import com.example.alexandr.megaquiz.app.App;
import com.example.alexandr.megaquiz.quizresultfragment.QuizResultFragmentContract;
import com.example.alexandr.megaquiz.quizresultfragment.QuizResultItem;
import com.example.alexandr.megaquiz.quizresultfragment.inject.QuizResultFragmentComponent;
import com.example.alexandr.megaquiz.quizresultfragment.inject.QuizResultFragmentPresenterModule;

import net.bohush.geometricprogressview.GeometricProgressView;

import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.Unbinder;

/**
 * Created by Alexandr Mikhalev on 11.12.2018.
 *
 * @author Alexandr Mikhalev
 */
public class QuizResultFragment extends Fragment implements QuizResultFragmentContract.View {

    @Inject
    QuizResultFragmentContract.Presenter mPresenter;

    @BindView(R.id.result_text)
    TextView mResultTextView;

    @BindView(R.id.for_switch_tv)
    TextView mForSwitchTextView;

    @BindView(R.id.switch_result)
    Switch mSwitchForShowRecyclerView;

    @BindView(R.id.result_recycler)
    RecyclerView mRecyclerView;

    @BindView(R.id.progress_bar_quiz_result)
    GeometricProgressView mProgressBar;

    @BindView(R.id.static_group_quiz_result)
    Group mStaticGroup;

    private QuizResultAdapter mAdapter;
    private Unbinder mUnbinder;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        int correctAnswers = getArguments().getInt(Constants.EXTRAS_FOR_INTENT_QUIZ_RESULT_CORRECT_ANSWERS, 5);
        String nameCategory = getArguments().getString(Constants.EXTRAS_FOR_INTENT_QUIZ_RESULT_NAME_CATEGORY, "Error");
        HashMap<Integer, Boolean> userAnswersMap = (HashMap<Integer, Boolean>) getArguments().getSerializable(Constants.EXTRAS_FOR_INTENT_QUIZ_RESULT_MAP_USER_ANSWERS);
        //App.getApp(mContext).getComponentsHolder().getQuizResultFragmentComponent(nameCategory, userAnswersMap, correctAnswers).inject(this);
        QuizResultFragmentComponent component = (QuizResultFragmentComponent) App.getApp(getContext()).getComponentsHolder()
                .getFragmentComponent(getClass(), new QuizResultFragmentPresenterModule(nameCategory, userAnswersMap,correctAnswers));
        component.inject(this);
        mPresenter.attachView(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quiz_result, null);
        mUnbinder = ButterKnife.bind(this, view);

        FragmentActivity fragmentActivity = getActivity();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(fragmentActivity);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new QuizResultAdapter();
        mPresenter.onStartView();
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
    public void setTextOfResultTextView(int size, int correctAnswers, String categoryName) {
        String level;
        int percent = ((correctAnswers * 100) / size);
        if (percent < 50) {
            level = "ужасно";
        } else if (percent < 75) {
            level = "удовлетворительно";
        } else if (percent <= 89) {
            level = "хорошо";
        } else {
            level = "отлично";
        }
        String textResult = "Вы прошли опрос категории \"" + categoryName + "\". Вы " + level + " владеете знаниями данной в области и дали " + percent + "% верных ответов.";
        mResultTextView.setText(textResult);
    }

    @Override
    public void showLoading() {
        mStaticGroup.setVisibility(View.INVISIBLE);
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        mProgressBar.setVisibility(View.INVISIBLE);
        mStaticGroup.setVisibility(View.VISIBLE);
    }

    @Override
    public void addListQuizResultItemForRecyclerView(List<QuizResultItem> list) {
        mAdapter.setData(list);
    }

    @OnCheckedChanged({R.id.switch_result})
    void onSelected(Switch button, boolean checked) {
        String textForSwitchTextView = checked ? "Скрыть подробности" : "Показать подробности";
        int stateRecycler = checked ? View.VISIBLE : View.INVISIBLE;
        setVisibilityOfRecycler(stateRecycler, textForSwitchTextView);
    }

    @Override
    public void setVisibilityOfRecycler(int state, String text) {
        mRecyclerView.setVisibility(state);
        mForSwitchTextView.setText(text);
    }

    public static QuizResultFragment newInstance(int correctAnswers, String nameCategory, HashMap<Integer, Boolean> map) {
        Bundle args = new Bundle();
        args.putInt(Constants.EXTRAS_FOR_INTENT_QUIZ_RESULT_CORRECT_ANSWERS, correctAnswers);
        args.putString(Constants.EXTRAS_FOR_INTENT_QUIZ_RESULT_NAME_CATEGORY, nameCategory);
        args.putSerializable(Constants.EXTRAS_FOR_INTENT_QUIZ_RESULT_MAP_USER_ANSWERS, map);
        QuizResultFragment fragment = new QuizResultFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
