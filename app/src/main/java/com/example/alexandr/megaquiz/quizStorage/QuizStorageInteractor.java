package com.example.alexandr.megaquiz.quizStorage;

import com.example.alexandr.megaquiz.bankQuestion.BankQuestion;
import com.example.alexandr.megaquiz.bankQuestion.Question;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Alexandr Mikhalev on 11.09.2018.
 *
 * @author Alexandr Mikhalev
 */
public class QuizStorageInteractor implements QuizStorageContract.Interactor {
    private Map<String, List<Question>> mBankQuestions;
    private List<String> mCategoriesNames;

    public QuizStorageInteractor(BankQuestion bankQuestion) {
        this.mBankQuestions = bankQuestion.getBankQuestion();
        this.mCategoriesNames = initCategoriesNames();
    }

    @Override
    public Map<String, List<Question>> getBankQuestion() {
        return mBankQuestions;
    }

    private List<String> initCategoriesNames() {
        List<String> list = new ArrayList<>();
        for (Map.Entry<String, List<Question>> entry : mBankQuestions.entrySet()) {
            list.add(entry.getKey());
        }
        return list;
    }

    @Override
    public List<String> getCategoriesNames() {
        return mCategoriesNames;
    }
}
