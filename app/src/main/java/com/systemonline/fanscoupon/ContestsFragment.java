package com.systemonline.fanscoupon;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.systemonline.fanscoupon.Base.BaseFragment;
import com.systemonline.fanscoupon.Helpers.Const;
import com.systemonline.fanscoupon.Helpers.Utility;
import com.systemonline.fanscoupon.Model.Contest;
import com.systemonline.fanscoupon.Model.CustomLoading;
import com.systemonline.fanscoupon.Model.Question;
import com.systemonline.fanscoupon.Model.QuestionAnswer;
import com.systemonline.fanscoupon.WebServices.JSONAsync;
import com.systemonline.fanscoupon.WebServices.JSONWebServices;
import com.systemonline.fanscoupon.WebServices.ParseData;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.List;

public class ContestsFragment extends BaseFragment {


    TextView questNameRadioButton, questDescRadioButton, questNameCheckBox, questDescCheckBox,
            radioChoice1, radioChoice2, radioChoice3, radioChoice4, radioChoice5,
            checkBoxChoice1, checkBoxChoice2, checkBoxChoice3, checkBoxChoice4, checkBoxChoice5, tv_degree, tv_degree1;
    LinearLayout radioLinear, checkboxLinear, submitAnswer, radioLin3, radioLin4, radioLin5, checkBoxLin3, checkBoxLin4, checkBoxLin5;
    RadioButton radio1, radio2, radio3, radio4;
    CheckBox checkBox1, checkBox2, checkBox3, checkBox4, checkBox5;
    Utility _utility;
    Contest contest;
    ArrayList<CheckBox> checkBoxes;
    QuestionAnswer questionAnswer;
    TextView brandName, win_all, ch_all, doneButton;
    JSONAsync call;
    private int contestIndex = 0, totalDegree = 0, radioCheckedIndex = -1;
    private ArrayList<QuestionAnswer> questionAnswers = new ArrayList<>();
    private String requestType;
    private boolean confirmationFlag = false;
    private ImageView brandImage;
    private CustomLoading customLoading;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.contests, null);

        _utility = new Utility(getContext());
        customLoading = new CustomLoading(_utility.getCurrentActivity());

        brandImage = (ImageView) rootView.findViewById(R.id.imgv_brand);
        brandName = (TextView) rootView.findViewById(R.id.tv_brand);
        doneButton = (TextView) rootView.findViewById(R.id.tv_done);

        ch_all = (TextView) rootView.findViewById(R.id.ch_all);
        win_all = (TextView) rootView.findViewById(R.id.win_all);

        questNameCheckBox = (TextView) rootView.findViewById(R.id.tv_quest_name_checkbox);
        questDescCheckBox = (TextView) rootView.findViewById(R.id.tv_quest_desc_checkbox);
        questNameRadioButton = (TextView) rootView.findViewById(R.id.tv_quest_name_radioButton);
        questDescRadioButton = (TextView) rootView.findViewById(R.id.tv_quest_desc_radioButton);

        radioChoice1 = (TextView) rootView.findViewById(R.id.radio_answer_1);
        radioChoice2 = (TextView) rootView.findViewById(R.id.radio_answer_2);
        radioChoice3 = (TextView) rootView.findViewById(R.id.radio_answer_3);
        radioChoice4 = (TextView) rootView.findViewById(R.id.radio_answer_4);
        radioChoice5 = (TextView) rootView.findViewById(R.id.radio_answer_5);

        checkBoxChoice1 = (TextView) rootView.findViewById(R.id.checkBox_answer_1);
        checkBoxChoice2 = (TextView) rootView.findViewById(R.id.checkBox_answer_2);
        checkBoxChoice3 = (TextView) rootView.findViewById(R.id.checkBox_answer_3);
        checkBoxChoice4 = (TextView) rootView.findViewById(R.id.checkBox_answer_4);
        checkBoxChoice5 = (TextView) rootView.findViewById(R.id.checkBox_answer_5);

        tv_degree = (TextView) rootView.findViewById(R.id.tv_degree);
        tv_degree1 = (TextView) rootView.findViewById(R.id.tv_degree1);

        submitAnswer = (LinearLayout) rootView.findViewById(R.id.lin_submit_answer);
        radioLin3 = (LinearLayout) rootView.findViewById(R.id.lin_radio_3);
        radioLin4 = (LinearLayout) rootView.findViewById(R.id.lin_radio_4);
        radioLin5 = (LinearLayout) rootView.findViewById(R.id.lin_radio_5);
        checkBoxLin3 = (LinearLayout) rootView.findViewById(R.id.lin_checkBox_3);
        checkBoxLin4 = (LinearLayout) rootView.findViewById(R.id.lin_checkBox_4);
        checkBoxLin5 = (LinearLayout) rootView.findViewById(R.id.lin_checkBox_5);
        radioLinear = (LinearLayout) rootView.findViewById(R.id.lin_radio_question);
        checkboxLinear = (LinearLayout) rootView.findViewById(R.id.lin_checkBox_question);
        radioLinear.setVisibility(View.GONE);
        checkboxLinear.setVisibility(View.GONE);
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

        checkBox1 = (CheckBox) rootView.findViewById(R.id.checkbox1);
        checkBox2 = (CheckBox) rootView.findViewById(R.id.checkbox2);
        checkBox3 = (CheckBox) rootView.findViewById(R.id.checkbox3);
        checkBox4 = (CheckBox) rootView.findViewById(R.id.checkbox4);
        checkBox5 = (CheckBox) rootView.findViewById(R.id.checkbox5);


        submitAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    InputMethodManager imm = (InputMethodManager) _utility.getCurrentActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    if (calculateFanMarks()) {
                        confirmationFlag = false;
                        contestIndex++;
                        if (contest.getContestQuestions().size() > contestIndex)
                            startContest();
                        else
                            finishContest();
                    }
                    radioCheckedIndex = -1;
                } catch (Exception e) {
                    e.printStackTrace();
                    _utility.showMessage(getResources().getString(R.string.error));
                }

//                else
//                    _utility.showMessage(getResources().getString(R.string.chk_ans));
            }
        });
        getContests();
        return rootView;
    }

    private void finishContest() {
        Log.e("total quest  marks", totalDegree + "");
        submitAnswer.setEnabled(false);
        sendData();
    }

    private boolean calculateFanMarks() {
        Log.e("skip flag", confirmationFlag + "---");
        Question question = contest.getContestQuestions().get(contestIndex);
        questionAnswer = new QuestionAnswer();
        questionAnswer.setAnswerStatus(1);
        if (question.getQuestionType() == 1) {
            if (radioCheckedIndex == -1) {
                questionAnswer.setAnswerStatus(0);
                questionAnswer.setUserDegree(0);
                if (!confirmationFlag) {
                    confirmationDialog();
                    return false;
                }
            } else if (question.getQuestionChoices().get(radioCheckedIndex - 1).getIsTrue() == 1) {
                totalDegree += question.getQuestionDegree();
                questionAnswer.setUserDegree(question.getQuestionDegree());
            }
            questionAnswer.setQuestionID(question.getQuestionID());

        } else {
            if (!checkBox1.isChecked() && !checkBox2.isChecked() && !checkBox3.isChecked() && !checkBox4.isChecked() && !checkBox5.isChecked()) {
                questionAnswer.setAnswerStatus(0);
                questionAnswer.setUserDegree(0);
                if (!confirmationFlag) {
                    confirmationDialog();
                    return false;
                }
            }
            boolean successFlag = true;
            for (int i = 0; i < checkBoxes.size(); i++) {
                if (question.getQuestionChoices().get(i).getIsTrue() == 1 && !checkBoxes.get(i).isChecked())
                    successFlag = false;
            }
            if (successFlag) {
                totalDegree += question.getQuestionDegree();
                questionAnswer.setUserDegree(question.getQuestionDegree());
            }
            questionAnswer.setQuestionID(question.getQuestionID());


        }

        questionAnswers.add(questionAnswer);
        Log.e("quest type " + question.getQuestionType() + " total marks", totalDegree + "");
        return true;
    }

    private void getContests() {
        if (!_utility.isConnectingToInternet_ping()) {
            customLoading.hideProgress();
            MainActivity._utility.showMessage(getResources().getString(R.string.no_net));
            return;
        }
        requestType = "getRequest";
        customLoading.showProgress(_utility.getCurrentActivity());
        JSONWebServices service = new JSONWebServices(ContestsFragment.this);
        call = service.getContestsRequest(null);
    }

    private void sendData() {
        if (!_utility.isConnectingToInternet_ping()) {
            customLoading.hideProgress();
            MainActivity._utility.showMessage(getResources().getString(R.string.no_net));
            return;
        }
        requestType = "sendRequest";
        customLoading.showProgress(_utility.getCurrentActivity());
        JSONWebServices service = new JSONWebServices(ContestsFragment.this);
        List<NameValuePair> nameValuePairs = new ArrayList<>();
        nameValuePairs.add(new BasicNameValuePair("contest_id", contest.getContestId() + ""));
        nameValuePairs.add(new BasicNameValuePair("total_degree", totalDegree + ""));
        nameValuePairs.add(new BasicNameValuePair("min_pass_degree", contest.getMinPassContestDegree() + ""));
        for (int i = 0; i < questionAnswers.size(); i++) {
            nameValuePairs.add(new BasicNameValuePair("questions[" + i + "][answer_status]", questionAnswers.get(i).getAnswerStatus() + ""));
            nameValuePairs.add(new BasicNameValuePair("questions[" + i + "][degree]", questionAnswers.get(i).getUserDegree() + ""));
            nameValuePairs.add(new BasicNameValuePair("questions[" + i + "][question_id]", questionAnswers.get(i).getQuestionID() + ""));
        }
        call = service.sendContestData(nameValuePairs);

    }

    @Override
    public void PostBackExecutionJSON(JSONTokener Result) {
        try {
            call = null;
            if (requestType.equals("sendRequest")) {
                customLoading.hideProgress();
//                Log.e("send Contest resp", Result.toString());
                if (ParseData.parseActionsResult(Result)) {
                    _utility.updateCoupons("mission");

//                    _utility.showMessage(getResources().getString(R.string.truee));
                    getActivity().onBackPressed();
                    _utility.showHover("pass");
                } else {
//                    _utility.showMessage(getResources().getString(R.string.falsee));
                    getActivity().onBackPressed();
                    _utility.showHover("fail");
                }
            } else {
                contest = ParseData.parseContest(Result);
                if (contest != null) {
                    Picasso.with(getContext()).load(Const.imagesURL + "brands/50x50/" + contest.getBrandLogo()).placeholder(R.drawable.ph_brand).into(brandImage);

                    brandName.setText(contest.getBrandName());

                    win_all.setText(_utility.colorString(R.string.win, R.color.red, contest.getCouponName(), R.color.black), TextView.BufferType.SPANNABLE);

                    ch_all.setText(_utility.colorString(R.string.challenge, R.color.red, contest.getContestTitle(), R.color.black), TextView.BufferType.SPANNABLE);

                    startContest();
                } else {
                    submitAnswer.setVisibility(View.GONE);
                    _utility.showMessage(getResources().getString(R.string.contest_ws_err));
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

    private void startContest() {
        if (!_utility.isConnectingToInternet_ping()) {
            customLoading.hideProgress();
            MainActivity._utility.showMessage(getResources().getString(R.string.no_net));
            return;
        }
        doneButton.setText(getContext().getResources().getString(R.string.next));

        Log.e("contest", contest.getContestQuestions().size() + "");
        radioCheckedIndex = -1;
        radio1.setChecked(false);
        radio2.setChecked(false);
        radio3.setChecked(false);
        radio4.setChecked(false);

        checkBox1.setChecked(false);
        checkBox2.setChecked(false);
        checkBox3.setChecked(false);
        checkBox4.setChecked(false);
        checkBox5.setChecked(false);

        radioLinear.setVisibility(View.GONE);
        checkboxLinear.setVisibility(View.GONE);

        Question question = contest.getContestQuestions().get(contestIndex);

        if (question.getQuestionType() == 1) {

            tv_degree.setText(_utility.colorString(String.valueOf(question.getQuestionDegree())
                    , R.color.red, R.string.degree, R.color.black), TextView.BufferType.SPANNABLE);

            if (question.getQuestionChoices().size() > 0) {
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
                if (question.getQuestionChoices().size() > 4)
                    radioChoice5.setText(question.getQuestionChoices().get(4).getChoiceText());
                else
                    radioLin5.setVisibility(View.GONE);
            }

        } else {
            tv_degree1.setText(_utility.colorString(String.valueOf(question.getQuestionDegree())
                    , R.color.red, R.string.degree, R.color.black), TextView.BufferType.SPANNABLE);

            radioCheckedIndex = -1;
            checkBoxes = new ArrayList<>();
            if (question.getQuestionChoices().size() > 0) {
                checkboxLinear.setVisibility(View.VISIBLE);
                questNameCheckBox.setText(question.getQuestionTitle());
                checkBoxChoice1.setText(question.getQuestionChoices().get(0).getChoiceText());
                checkBoxes.add(checkBox1);
                checkBoxChoice2.setText(question.getQuestionChoices().get(1).getChoiceText());
                checkBoxes.add(checkBox2);
                if (question.getQuestionChoices().size() > 2) {
                    checkBoxChoice3.setText(question.getQuestionChoices().get(2).getChoiceText());
                    checkBoxes.add(checkBox3);
                } else
                    checkBoxLin3.setVisibility(View.GONE);
                if (question.getQuestionChoices().size() > 3) {
                    checkBoxChoice4.setText(question.getQuestionChoices().get(3).getChoiceText());
                    checkBoxes.add(checkBox4);
                } else
                    checkBoxLin4.setVisibility(View.GONE);
                if (question.getQuestionChoices().size() > 4) {
                    checkBoxChoice5.setText(question.getQuestionChoices().get(4).getChoiceText());
                    checkBoxes.add(checkBox5);
                } else
                    checkBoxLin5.setVisibility(View.GONE);

            }
        }
    }

    void confirmationDialog() {
        AlertDialog.Builder build = new AlertDialog.Builder(getContext());
        build.setMessage(R.string.skip_question)
                .setCancelable(false)
                .setPositiveButton(getContext().getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        confirmationFlag = true;
                        doneButton.setText(getContext().getResources().getString(R.string.skip));
                    }
                });

        AlertDialog alert = build.create();
        alert.show();
    }
}