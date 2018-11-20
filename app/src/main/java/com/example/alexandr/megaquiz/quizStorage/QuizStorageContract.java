package com.example.alexandr.megaquiz.quizStorage;

import com.example.alexandr.megaquiz.bankQuestion.Question;

import java.util.List;
import java.util.Map;

/**
 * Created by Alexandr Mikhalev on 11.09.2018.
 *
 * @author Alexandr Mikhalev
 */
public interface QuizStorageContract {
    interface Interactor {
        Map<String, List<Question>> getBankQuestion();

        List<String> getCategoriesNames();
    }

    interface Presenter {
        List<String> initCategoriesNames();

        void onClick(String key);
    }

    interface View {
        void startActivityQuizView(String key);
    }
}
