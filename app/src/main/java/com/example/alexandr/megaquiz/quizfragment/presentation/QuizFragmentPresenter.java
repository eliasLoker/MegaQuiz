package com.example.alexandr.megaquiz.quizfragment.presentation;

import android.support.v4.util.ArrayMap;

import com.example.alexandr.megaquiz.Constants;
import com.example.alexandr.megaquiz.quizfragment.Answer;
import com.example.alexandr.megaquiz.quizfragment.QuizFragmentContract;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Alexandr Mikhalev on 10.12.2018.
 *
 * @author Alexandr Mikhalev
 */
public class QuizFragmentPresenter implements QuizFragmentContract.Presenter {
    private QuizFragmentContract.View mView;
    private QuizFragmentContract.Interactor mInteractor;
    private List<String> mQuestions = new ArrayList<>();
    private int mCurrentIndex;
    private Map<Integer, Answer> mAnswers;
    private String mCategoryName;
    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    public QuizFragmentPresenter(QuizFragmentContract.View view, QuizFragmentContract.Interactor interactor) {
        this.mView = view;
        this.mInteractor = interactor;
        this.mAnswers = new ArrayMap<>(); // почитать подробнее потом
        this.mCurrentIndex = 0;
    }

    @Override
    public void initQuestionList(String keyCategory) {
        mCategoryName = keyCategory;
        mView.setProgressBar(true);
        Disposable disposable = mInteractor.getQuestions(keyCategory)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<String>>() {
                    @Override
                    public void accept(List<String> strings) throws Exception {
                        for (String string : strings) {
                            mQuestions.add(string);
                        }
                        mView.setProgressBar(false);
                        prepareViewForFirstQuestion();
                    }
                });
        mCompositeDisposable.add(disposable);
    }

    @Override
    public void prepareViewForFirstQuestion() {
        mView.setQuestionTextView(mQuestions.get(mCurrentIndex));
        countNumberOfQuestion();
    }

    @Override
    public void onNextButton() {
        int newIndex = (mCurrentIndex + 1) % mQuestions.size();
        onButtonByIndex(newIndex);
    }

    @Override
    public void onPrevButton() {
        int newIndex = (mCurrentIndex - 1) % mQuestions.size();
        if (newIndex < 0) newIndex = mQuestions.size() - 1;
        onButtonByIndex(newIndex);
    }

    private void onButtonByIndex(int index) {
        mCurrentIndex = index;
        mView.setQuestionTextView(mQuestions.get(mCurrentIndex));
        countNumberOfQuestion();
        checkAnswerQuestion();
    }

    @Override
    public void onAnswer(Answer answer) {
        mAnswers.put(mCurrentIndex, answer);
        checkAnswerQuestion();
        checkFinalOfQuiz();
    }

    private void countNumberOfQuestion() {
        String text = mCurrentIndex + 1 + "/" + mQuestions.size();
        mView.setQuestionCounter(text);
    }

    private void checkAnswerQuestion() {
        boolean isAnswered = mAnswers.containsKey(mCurrentIndex);
        mView.setButtonsEnabled(!isAnswered);
        int flag = Constants.NOT_PUSH_TRUE_AND_FALSE_BUTTONS;
        if (isAnswered) {
            boolean answer = mAnswers.get(mCurrentIndex).isResult();
            flag = answer ? Constants.PUSH_TRUE_BUTTON : Constants.PUSH_FALSE_BUTTON;
        }
        mView.setCorrectButtonStyle(flag);
    }

    private void checkFinalOfQuiz() {
        final int size = mQuestions.size();
        if (size == mAnswers.size()) {
            mView.setProgressBar(true);
            Disposable disposable = mInteractor.checkQuestions(mCategoryName, mAnswers)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Integer>() {
                        @Override
                        public void accept(Integer integer) throws Exception {
                            mView.setProgressBar(false);
                            mView.startQuizResultFragment(size, integer);
                        }
                    });
            mCompositeDisposable.add(disposable);
        }
    }

    @Override
    public void onDestroy() {
        mCompositeDisposable.clear();
    }
}
