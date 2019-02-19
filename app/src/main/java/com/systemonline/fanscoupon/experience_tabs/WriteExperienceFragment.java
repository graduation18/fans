package com.systemonline.fanscoupon.experience_tabs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.hootsuite.nachos.NachoTextView;
import com.hootsuite.nachos.chip.Chip;
import com.hootsuite.nachos.terminator.ChipTerminatorHandler;
import com.hootsuite.nachos.validator.ChipifyingNachoValidator;
import com.systemonline.fanscoupon.Base.BaseFragment;
import com.systemonline.fanscoupon.Helpers.Utility;
import com.systemonline.fanscoupon.Model.CustomLoading;
import com.systemonline.fanscoupon.Model.TagsChip;
import com.systemonline.fanscoupon.R;
import com.systemonline.fanscoupon.WebServices.JSONAsync;
import com.systemonline.fanscoupon.WebServices.JSONWebServices;
import com.systemonline.fanscoupon.WebServices.ParseData;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.List;


public class WriteExperienceFragment extends BaseFragment {

    public static ArrayList<String> validationErrors;
    Utility utility;
    String categoryID;
    NachoTextView tagsTextView;
    int lastChipsCount;
    private String[] SUGGESTIONS = new String[]{""}, splitter;
    private JSONAsync call;
    private CustomLoading customLoading;
    private String requestType = "", tagsText;
    private List<TagsChip> tagsChips, selectedTagsChips = new ArrayList<>();
    private List<String> chipAndTokenValues;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        return inflater.inflate(R.layout.experience_tab_good, null);
        View root = inflater.inflate(R.layout.write_experience, null);
        utility = new Utility(getContext());
        customLoading = new CustomLoading(utility.getCurrentActivity());
        tagsChips = new ArrayList<>();
        final TextView expTitle, expSlug;
        final Spinner expCategories;
        final EditText expContent;
        expTitle = (TextView) root.findViewById(R.id.exp_title);
        expSlug = (TextView) root.findViewById(R.id.exp_slug);
        expTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                expSlug.setText(expTitle.getText().toString().trim().replace(" ", "-"));
            }
        });

        tagsTextView = (NachoTextView) root.findViewById(R.id.exp_tags);

        tagsTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {

//                    if (text.length() > 1) {
//                        tagsChips.add(new TagsChip("2", Uri.parse(""), "oss" + text, ""));
//                        chipsInput.setFilterableList(tagsChips);
//                        Log.e("chip add", "oss" + text);
//                    }


//                    if (text.charAt(text.length() - 1) == ' ') {
//                        chipsInput.addChip(text.toString(), "info");
//                    } else
//                    Log.e("last chips count", lastChipsCount + " change");
//                    Log.e("chips count", tagsTextView.getAllChips().size() + " change");
//                    Log.e("chips update", updateTagsFlag + " change");
                    if (tagsTextView.getAllChips().size() > lastChipsCount) {
                        if (tagsChips != null)
                            selectedTagsChips.addAll(tagsChips);
                    }
                    lastChipsCount = tagsTextView.getAllChips().size();

                    tagsText = s.toString();
                    splitter = tagsText.split(" ");
                    Log.e("after textChanged", splitter[splitter.length - 1]);
                    if (splitter[splitter.length - 1].length() > 1 && !splitter[splitter.length - 1].contains("\u001F")) {
                        getRelatedTags(splitter[splitter.length - 1]);
                    }
                } catch (Exception e) {
//                    e.printStackTrace();
                }

            }
        });
        setupChipTextView(tagsTextView);
//        tagsChips = new ArrayList<>();
//        tagsChips.add(new TagsChip());
////        tagsChips.add(new TagsChip("1", Uri.parse(""), "oss", "012"));
//
//        chipsInput.setFilterableList(tagsChips);
//
//        chipsInput.addChipsListener(new ChipsInput.ChipsListener() {
//            @Override
//            public void onChipAdded(ChipInterface chip, int newSize) {
//                Log.e("add chip", chip.getLabel());
//                tags.add(chip.getLabel());
//            }
//
//            @Override
//            public void onChipRemoved(ChipInterface chip, int newSize) {
//                Log.e("remove chip", chip.getLabel());
//                tags.remove(chip.getLabel());
//                // chip removed
//                // newSize is the size of the updated selected chip list
//            }
//
//            @Override
//            public void onTextChanged(CharSequence text) {
//                // text changed
//                try {
////                    if (text.length() > 1) {
////                        tagsChips.add(new TagsChip("2", Uri.parse(""), "oss" + text, ""));
////                        chipsInput.setFilterableList(tagsChips);
////                        Log.e("chip add", "oss" + text);
////                    }
//
//
//                    if (text.charAt(text.length() - 1) == ' ') {
//                        chipsInput.addChip(text.toString(), "info");
//                    } else if (text.length() > 1) {
//                        getRelatedTags(text.toString());
//                    }
//                } catch (Exception e) {
////                    e.printStackTrace();
//                }
//            }
//        });


        expCategories = (Spinner) root.findViewById(R.id.spin_expCategories);
        String[] expCategoriesData = {getString(R.string.good), getString(R.string.bad), getString(R.string.fun)};
        utility.spinnerProperties(expCategoriesData, expCategories);
        expContent = (EditText) root.findViewById(R.id.et_expContent);
        final LinearLayout createExp = (LinearLayout) root.findViewById(R.id.lin_createExp);
        createExp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) utility.getCurrentActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
//
//                if (tagsChips != null)
//                    selectedTagsChips.addAll(tagsChips);


                if (!expTitle.getText().toString().isEmpty() && !expSlug.getText().toString().isEmpty() &&
                        !expContent.getText().toString().isEmpty()) {

                    chipAndTokenValues = tagsTextView.getChipAndTokenValues();
                    if (tagsChips != null)
                        tagsChips.clear();
                    for (int i = 0; i < chipAndTokenValues.size(); i++) {
//                        Log.e("tag", chipAndTokenValues.get(i));
                        for (int j = 0; j < selectedTagsChips.size(); j++) {
                            if (chipAndTokenValues.get(i).trim().equals(selectedTagsChips.get(j).getName().trim())) {
//                                Log.e("tag id", selectedTagsChips.get(j).getId());
                                tagsChips.add(selectedTagsChips.get(j));
                                chipAndTokenValues.remove(i);
                                i--;
                                break;
                            }
                        }

                    }

//                    for (int j = 0; j < tagsChips.size(); j++) {
//                        Log.e("tag", tagsChips.get(j).getName());
//                        Log.e("tag id", tagsChips.get(j).getId());
//                    }
//                    for (int j = 0; j < chipAndTokenValues.size(); j++)
//                        Log.e("tag from textView", chipAndTokenValues.get(j));

                    switch (expCategories.getSelectedItemPosition()) {
                        case 0:
                            categoryID = "2";
                            break;
                        case 1:
                            categoryID = "1";
                            break;
                        case 2:
                            categoryID = "3";
                            break;
                    }
                    createExp(expTitle.getText().toString(), expSlug.getText().toString(),
                            categoryID, expContent.getText().toString());
                } else
                    utility.showMessage(getResources().getString(R.string.comp_form));
            }
        });

        if (ExperienceTab.experienceToEdit != null) { //edit case
            expTitle.setText(ExperienceTab.experienceToEdit.getExperienceTitle());
            expSlug.setText(ExperienceTab.experienceToEdit.getExperienceSlug());
            expContent.setText(ExperienceTab.experienceToEdit.getExperienceBody());
            switch (ExperienceTab.experienceToEdit.getExperienceCategoryID()) {
                case 1:
                    expCategories.setSelection(1);
                    break;
                case 2:
                    expCategories.setSelection(0);
                    break;
                case 3:
                    expCategories.setSelection(2);
                    break;
            }
            List<String> expTags = new ArrayList<>();
            for (int i = 0; i < ExperienceTab.experienceToEdit.getTagsChips().size(); i++) {
                expTags.add(ExperienceTab.experienceToEdit.getTagsChips().get(i).getName());
            }
            tagsTextView.setText(expTags);
            selectedTagsChips = ExperienceTab.experienceToEdit.getTagsChips();
        }
        return root;
    }

    private void setupChipTextView(NachoTextView nachoTextView) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(utility.getCurrentActivity(), android.R.layout.simple_dropdown_item_1line, SUGGESTIONS);
        nachoTextView.setAdapter(adapter);
        nachoTextView.setIllegalCharacters('\"');
        nachoTextView.addChipTerminator('\n', ChipTerminatorHandler.BEHAVIOR_CHIPIFY_ALL);
        nachoTextView.addChipTerminator(' ', ChipTerminatorHandler.BEHAVIOR_CHIPIFY_TO_TERMINATOR);
        nachoTextView.addChipTerminator(';', ChipTerminatorHandler.BEHAVIOR_CHIPIFY_CURRENT_TOKEN);
        nachoTextView.setNachoValidator(new ChipifyingNachoValidator());
        nachoTextView.enableEditChipOnTouch(true, true);
        nachoTextView.setOnChipClickListener(new NachoTextView.OnChipClickListener() {
            @Override
            public void onChipClick(Chip chip, MotionEvent motionEvent) {
                Log.e("onChipClick: ", chip.getText().toString());
            }
        });
    }

    /**
     * create exp request
     *
     * @param expTitle
     * @param expSlug
     * @param expCategoryID
     * @param expContent
     */
    public void createExp(String expTitle, String expSlug, String expCategoryID, String expContent) {
        if (utility.isConnectingToInternet_ping()) {
            customLoading.showProgress(utility.getCurrentActivity());

            expSlug = expSlug.replaceAll("[^A-Za-z0-9()\\[\\]]", "");
            List<NameValuePair> nameValuePairs = new ArrayList<>();
            nameValuePairs.add(new BasicNameValuePair("title", expTitle));
            nameValuePairs.add(new BasicNameValuePair("slug", expSlug));
            nameValuePairs.add(new BasicNameValuePair("category_id", expCategoryID));
            nameValuePairs.add(new BasicNameValuePair("description", expContent));
            JSONArray tagsJsonArray = new JSONArray();
            try {
                JSONObject tagJsonObject;
                for (int i = 0; i < chipAndTokenValues.size(); i++) {
                    tagJsonObject = new JSONObject();
                    tagJsonObject.put("keyword", chipAndTokenValues.get(i).trim());
                    tagJsonObject.put("isTag", true);
                    tagsJsonArray.put(tagJsonObject);
                }
                if (tagsChips != null)
                    for (int i = 0; i < tagsChips.size(); i++) {
                        tagJsonObject = new JSONObject();
                        tagJsonObject.put("keyword", tagsChips.get(i).getName().trim());
                        tagJsonObject.put("tag_id", tagsChips.get(i).getId());
                        tagsJsonArray.put(tagJsonObject);
                    }

            } catch (Exception e) {
                e.printStackTrace();
            }
            nameValuePairs.add(new BasicNameValuePair("tag", tagsJsonArray.toString()));
            Log.e("create tag", tagsJsonArray.toString());

            JSONWebServices service = new JSONWebServices(WriteExperienceFragment.this);
            requestType = "createExp";
            if (ExperienceTab.experienceToEdit == null)
                call = service.createExperienceRequest(nameValuePairs);
            else {
                nameValuePairs.add(new BasicNameValuePair("news_id", String.valueOf(ExperienceTab.experienceToEdit.getExperienceID())));
                call = service.editExperienceRequest(nameValuePairs, ExperienceTab.experienceToEdit.getExperienceSlug());
            }
        } else {
            customLoading.hideProgress();
            utility.showMessage(getResources().getString(R.string.no_net));
        }
    }

    /**
     * get related tags
     *
     * @param tagKey
     */
    public void getRelatedTags(String tagKey) {
        if (utility.isConnectingToInternet_ping()) {
//            customLoading.showProgress(utility.getCurrentActivity());
            List<NameValuePair> nameValuePairs = new ArrayList<>();

            nameValuePairs.add(new BasicNameValuePair("keyword", tagKey));
//            Log.e("create tag", tagsJsonArray.toString());

            JSONWebServices service = new JSONWebServices(WriteExperienceFragment.this);
            requestType = "getRelatedTags";
            call = service.getRelatedTags(nameValuePairs);

        } else {
//
            customLoading.hideProgress();
            utility.showMessage(getResources().getString(R.string.no_net));
        }
    }

    @Override
    public void PostBackExecutionJSON(JSONTokener Result) {
        try {
            Log.e("write exp response", "------------- " + Result.toString());

            if (requestType.equals("createExp")) {
                if (!ParseData.parseCreateEditExp(Result, ExperienceTab.experienceToEdit == null)) {
                    for (int i = 0; i < validationErrors.size(); i++) {
                        Log.e("validation errors", validationErrors.get(i));
                    }
                    utility.showMessage(getResources().getString(R.string.error));
//
                    customLoading.hideProgress();
                    showDialog("");
                } else {
                    customLoading.hideProgress();
                    showDialog("success");
                }
            } else {
//                if (ParseData.parseGetRelatedTags(Result) == null)
//                    return;
//                Log.e("last chips count", lastChipsCount + " postBack");
//                Log.e("chips count", tagsTextView.getAllChips().size() + " postBack");
//                Log.e("chips update", updateTagsFlag + " postBack");
//
//                if (updateTagsFlag) {
//                    tagsChips.addAll(ParseData.parseGetRelatedTags(Result));
//                    updateTagsFlag = false;
//                } else
                tagsChips = ParseData.parseGetRelatedTags(Result);
                if (tagsChips != null && !tagsChips.isEmpty()) {

                    SUGGESTIONS = new String[tagsChips.size()];
                    for (int i = 0; i < tagsChips.size(); i++) {
                        SUGGESTIONS[i] = tagsChips.get(i).getName();
                    }
                    setupChipTextView(tagsTextView);
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
            utility.showMessage(getResources().getString(R.string.ws_err));
        } finally {
            customLoading.hideProgress();
            call = null;
        }

    }

    void showDialog(final String type) {
        AlertDialog.Builder build = new AlertDialog.Builder(getContext());
        String message;
        if (type.equals("success")) {
            if (ExperienceTab.experienceToEdit == null)
                message = getResources().getString(R.string.experience_added);
            else
                message = getResources().getString(R.string.experience_edited);
            build.setMessage(message);
        } else {
            message = "";
            build.setItems(validationErrors.toArray(new CharSequence[validationErrors.size()]), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            build.setTitle(message);
        }

        build.setCancelable(false)

                .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        if (type.equals("success"))
                            utility.getCurrentActivity().onBackPressed();
                    }
                });

        AlertDialog alert = build.create();
        alert.show();
    }
}
