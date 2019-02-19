package com.systemonline.fanscoupon;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.systemonline.fanscoupon.Base.BaseFragment;
import com.systemonline.fanscoupon.Helpers.Const;
import com.systemonline.fanscoupon.Helpers.Utility;
import com.systemonline.fanscoupon.Model.CustomLoading;
import com.systemonline.fanscoupon.Model.Question;
import com.systemonline.fanscoupon.Model.QuestionAnswer;
import com.systemonline.fanscoupon.Model.Survey;
import com.systemonline.fanscoupon.WebServices.JSONAsync;
import com.systemonline.fanscoupon.WebServices.JSONWebServices;
import com.systemonline.fanscoupon.WebServices.ParseData;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.List;

public class SurveyFragment extends BaseFragment {


    TextView questNameRadioButton, questDescRadioButton, radioChoice1, radioChoice2, radioChoice3, radioChoice4,
            questNameRating, questNameTextAnswer;
    LinearLayout radioLinear, checkboxLinear, submitAnswer, radioLin3, radioLin4, starsLinear, answerLinear;
    RadioButton radio1, radio2, radio3, radio4, radioStars1, radioStars2, radioStars3, radioStars4, radioStars5;
    EditText userTextAnswer;
    Utility _utility;
    Survey survey;
    QuestionAnswer questionAnswer;
    TextView brandName, win_all, ch_all;
    JSONAsync call;
    private int contestIndex = 0, radioCheckedIndex = -1, radioStarsCheckedIndex = -1;
    private Question question;
    private String requestType;
    private ArrayList<QuestionAnswer> questionAnswers = new ArrayList<>();
    private ImageView brandImage;
    private CustomLoading customLoading;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.survey, null);

        _utility = new Utility(getContext());
        customLoading = new CustomLoading(_utility.getCurrentActivity());
        brandImage = (ImageView) rootView.findViewById(R.id.imgv_brand);

        brandName = (TextView) rootView.findViewById(R.id.tv_brand);
        win_all = (TextView) rootView.findViewById(R.id.win_all);
        ch_all = (TextView) rootView.findViewById(R.id.ch_all);

        questNameRadioButton = (TextView) rootView.findViewById(R.id.tv_quest_name_radioButton);
        questDescRadioButton = (TextView) rootView.findViewById(R.id.tv_quest_desc_radioButton);
        questNameRating = (TextView) rootView.findViewById(R.id.tv_quest_name_starsRate);
        questNameTextAnswer = (TextView) rootView.findViewById(R.id.tv_quest_name_textAnswer);
        userTextAnswer = (EditText) rootView.findViewById(R.id.et_textAnswer);

        radioChoice1 = (TextView) rootView.findViewById(R.id.radio_answer_1);
        radioChoice2 = (TextView) rootView.findViewById(R.id.radio_answer_2);
        radioChoice3 = (TextView) rootView.findViewById(R.id.radio_answer_3);
        radioChoice4 = (TextView) rootView.findViewById(R.id.radio_answer_4);


        submitAnswer = (LinearLayout) rootView.findViewById(R.id.lin_submit_answer);
        radioLin3 = (LinearLayout) rootView.findViewById(R.id.lin_radio_3);
        radioLin4 = (LinearLayout) rootView.findViewById(R.id.lin_radio_4);
        radioLinear = (LinearLayout) rootView.findViewById(R.id.lin_radio_question);
        checkboxLinear = (LinearLayout) rootView.findViewById(R.id.lin_checkBox_question);
        starsLinear = (LinearLayout) rootView.findViewById(R.id.lin_starsRate_question);
        answerLinear = (LinearLayout) rootView.findViewById(R.id.lin_textAnswer_question);
        radioLinear.setVisibility(View.GONE);
        starsLinear.setVisibility(View.GONE);
        answerLinear.setVisibility(View.GONE);

        radio1 = (RadioButton) rootView.findViewById(R.id.radio_1);
        radio1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radioCheckedIndex = 1;
                radio2.setChecked(false);
                radio3.setChecked(false);
                radio4.setChecked(false);
            }
        });
        radio2 = (RadioButton) rootView.findViewById(R.id.radio_2);
        radio2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radioCheckedIndex = 2;
                radio1.setChecked(false);
                radio3.setChecked(false);
                radio4.setChecked(false);
            }
        });
        radio3 = (RadioButton) rootView.findViewById(R.id.radio_3);
        radio3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radioCheckedIndex = 3;
                radio1.setChecked(false);
                radio2.setChecked(false);
                radio4.setChecked(false);
            }
        });
        radio4 = (RadioButton) rootView.findViewById(R.id.radio_4);
        radio4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radioCheckedIndex = 4;
                radio1.setChecked(false);
                radio2.setChecked(false);
                radio3.setChecked(false);
            }
        });


        radioStars1 = (RadioButton) rootView.findViewById(R.id.radio_stars_1);
        radioStars1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radioStarsCheckedIndex = 1;
                radioStars2.setChecked(false);
                radioStars3.setChecked(false);
                radioStars4.setChecked(false);
                radioStars5.setChecked(false);
            }
        });
        radioStars2 = (RadioButton) rootView.findViewById(R.id.radio_stars_2);
        radioStars2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radioStarsCheckedIndex = 2;
                radioStars1.setChecked(false);
                radioStars3.setChecked(false);
                radioStars4.setChecked(false);
                radioStars5.setChecked(false);

            }
        });
        radioStars3 = (RadioButton) rootView.findViewById(R.id.radio_stars_3);
        radioStars3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radioStarsCheckedIndex = 3;
                radioStars2.setChecked(false);
                radioStars1.setChecked(false);
                radioStars4.setChecked(false);
                radioStars5.setChecked(false);

            }
        });
        radioStars4 = (RadioButton) rootView.findViewById(R.id.radio_stars_4);
        radioStars4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radioStarsCheckedIndex = 4;
                radioStars2.setChecked(false);
                radioStars3.setChecked(false);
                radioStars1.setChecked(false);
                radioStars5.setChecked(false);

            }
        });
        radioStars5 = (RadioButton) rootView.findViewById(R.id.radio_stars_5);
        radioStars5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radioStarsCheckedIndex = 5;
                radioStars2.setChecked(false);
                radioStars3.setChecked(false);
                radioStars1.setChecked(false);
                radioStars4.setChecked(false);

            }
        });


        submitAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    InputMethodManager imm = (InputMethodManager) _utility.getCurrentActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    if (checkEmptyChoice()) {
                        contestIndex++;
                        if (survey.getContestQuestions().size() > contestIndex)
                            startSurvey();
                        else
                            finishContest();
                    } else
                        _utility.showMessage(getResources().getString(R.string.chk_ans));
                } catch (Exception e) {
                    e.printStackTrace();
                    _utility.showMessage(getResources().getString(R.string.ws_err));

                }
            }
        });
        getSurvey();
        return rootView;
    }


    private boolean checkEmptyChoice() {
        question = survey.getContestQuestions().get(contestIndex);
        switch (question.getQuestionType()) {
            case 87:
                if (radioCheckedIndex == -1) {
                    return false;
                }
                int optionID = -1;
                for (int i = 0; i < radioCheckedIndex; i++) {
                    optionID = question.getQuestionChoices().get(i).getOptionID();
                }
                questionAnswer = new QuestionAnswer();
                questionAnswer.setQuestionID(question.getQuestionID());
                questionAnswer.setAnswer(optionID + "");
                questionAnswers.add(questionAnswer);
                //save user choice/s

                break;
            case 86:
                if (radioStarsCheckedIndex == -1) {
                    return false;
                }
                questionAnswer = new QuestionAnswer();
                questionAnswer.setQuestionID(question.getQuestionID());
                questionAnswer.setAnswer(radioStarsCheckedIndex + "");
                questionAnswers.add(questionAnswer);

                break;

            case 85:
                if (userTextAnswer.getText().toString().isEmpty()) {
                    return false;
                }
                questionAnswer = new QuestionAnswer();
                questionAnswer.setQuestionID(question.getQuestionID());
                questionAnswer.setAnswer(userTextAnswer.getText().toString());
                questionAnswers.add(questionAnswer);

                break;
        }
        return true;
    }

    private void getSurvey() {
        requestType = "getRequest";
        customLoading.showProgress(_utility.getCurrentActivity());
        JSONWebServices service = new JSONWebServices(SurveyFragment.this);
        call = service.getSurveyRequest(null);
    }

    @Override
    public void PostBackExecutionJSON(JSONTokener Result) {
        try {
            call = null;
            Log.e(" survey resp", Result.toString());

            if (requestType.equals("sendRequest")) {
                customLoading.hideProgress();
                if (ParseData.parseActionsResult(Result)) {
                    _utility.updateCoupons("mission");
                    getActivity().onBackPressed();
                    _utility.showHover("pass");
                } else {
                    getActivity().onBackPressed();
                    _utility.showHover("fail");
                }
            } else {
                survey = ParseData.parseSurvey(Result);
                if (survey != null) {
                    Picasso.with(getContext()).load(Const.imagesURL + "brands/50x50/" + survey.getBrandLogo()).placeholder(R.drawable.ph_brand).into(brandImage);
                    brandName.setText(survey.getBrandName());

                    win_all.setText(_utility.colorString(R.string.win, R.color.red, survey.getCouponName(), R.color.black), TextView.BufferType.SPANNABLE);

                    ch_all.setText(_utility.colorString(R.string.challenge, R.color.red, survey.getContestTitle(), R.color.black), TextView.BufferType.SPANNABLE);

                    startSurvey();
                } else {
                    submitAnswer.setVisibility(View.GONE);
                    _utility.showMessage(getResources().getString(R.string.ws_err));
                    customLoading.hideProgress();
                    getActivity().onBackPressed();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(_utility.GetAppContext(), getString(R.string.ws_err), Toast.LENGTH_LONG).show();

        } finally {
            customLoading.hideProgress();
        }
    }

    private void finishContest() {
//        submitAnswer.
        sendData();
    }

    private void sendData() {
        if (!_utility.isConnectingToInternet_ping()) {
            customLoading.hideProgress();
            MainActivity._utility.showMessage(getResources().getString(R.string.no_net));
            return;
        }
        customLoading.showProgress(_utility.getCurrentActivity());
        requestType = "sendRequest";
        JSONWebServices service = new JSONWebServices(SurveyFragment.this);
        List<NameValuePair> nameValuePairs = new ArrayList<>();
        nameValuePairs.add(new BasicNameValuePair("survey_id", survey.getSurveyID() + ""));
        for (int i = 0; i < questionAnswers.size(); i++) {
            nameValuePairs.add(new BasicNameValuePair("questions[" + questionAnswers.get(i).getQuestionID() + "][answer]", questionAnswers.get(i).getAnswer()));
        }
        call = service.sendSurveyData(nameValuePairs);

    }

    private void startSurvey() {
        if (!_utility.isConnectingToInternet_ping()) {
            customLoading.hideProgress();
            MainActivity._utility.showMessage(getResources().getString(R.string.no_net));
            return;
        }
        radioCheckedIndex = -1;
        radioStarsCheckedIndex = -1;
        userTextAnswer.setText("");
        radio1.setChecked(false);
        radio2.setChecked(false);
        radio3.setChecked(false);
        radio4.setChecked(false);
        radioStars1.setChecked(false);
        radioStars2.setChecked(false);
        radioStars3.setChecked(false);
        radioStars4.setChecked(false);
        radioStars5.setChecked(false);
        radioLinear.setVisibility(View.GONE);
        starsLinear.setVisibility(View.GONE);
        answerLinear.setVisibility(View.GONE);

        Question question = survey.getContestQuestions().get(contestIndex);
        Log.e("survey", survey.getContestQuestions().size() + " type " + question.getQuestionType());

        switch (question.getQuestionType()) {
            case 87:
                radioLinear.setVisibility(View.VISIBLE);
                questNameRadioButton.setText(question.getQuestionTitle());
                radioChoice1.setText(question.getQuestionChoices().get(0).getChoiceText());
                radioChoice2.setText(question.getQuestionChoices().get(1).getChoiceText());
                if (question.getQuestionChoices().size() > 2)
                    radioChoice3.setText(question.getQuestionChoices().get(2).getChoiceText());
                else
                    radioLin3.setVisibility(View.GONE);
                if (question.getQuestionChoices().size() > 3)
                    radioChoice4.setText(question.getQuestionChoices().get(3).getChoiceText());
                else
                    radioLin4.setVisibility(View.GONE);
                break;
            case 86:
                radioCheckedIndex = -1;
                radioStarsCheckedIndex = -1;
                starsLinear.setVisibility(View.VISIBLE);
                questNameRating.setText(question.getQuestionTitle());

                break;
            case 85:
                radioCheckedIndex = -1;
                radioStarsCheckedIndex = -1;
                answerLinear.setVisibility(View.VISIBLE);
                questNameTextAnswer.setText(question.getQuestionTitle());
                break;
        }
    }
}