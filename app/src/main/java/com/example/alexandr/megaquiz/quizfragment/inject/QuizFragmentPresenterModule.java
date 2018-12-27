package com.example.alexandr.megaquiz.quizfragment.inject;

import com.example.alexandr.megaquiz.bankquestion.BankQuestion;
import com.example.alexandr.megaquiz.quizfragment.QuizFragmentContract;
import com.example.alexandr.megaquiz.quizfragment.domain.QuizFragmentInteractor;
import com.example.alexandr.megaquiz.quizfragment.presentation.QuizFragmentPresenter;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Alexandr Mikhalev on 15.12.2018.
 *
 * @author Alexandr Mikhalev
 */
@Module
public class QuizFragmentPresenterModule {

    private QuizFragmentContract.View mView;
    private QuizFragmentContract.Router mRouter;
    private String mCategoryName;

    public QuizFragmentPresenterModule(QuizFragmentContract.View quizFragment, String categoryName) {
        mView = quizFragment;
        mCategoryName = categoryName;
    }

    @QuizFragmentScope
    @Provides
    QuizFragmentContract.Presenter providePresenter(QuizFragmentContract.Interactor quizFragmentInteractor) {
        return new QuizFragmentPresenter(quizFragmentInteractor, mCategoryName);
    }

    @QuizFragmentScope
    @Provides
    QuizFragmentContract.Interactor provideInteractor(BankQuestion bankQuestion) {
        return new QuizFragmentInteractor(bankQuestion);
    }

    @QuizFragmentScope
    @Provides
    @Named("keyCategory")
    String provideCategoryName() {
        return mCategoryName;
    }
}
