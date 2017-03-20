package com.comp.iitb.vialogue.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.comp.iitb.vialogue.R;
import com.comp.iitb.vialogue.activity.CameraActivity;
import com.comp.iitb.vialogue.activity.CropMainActivity;
import com.comp.iitb.vialogue.activity.GalleryActivity;
import com.comp.iitb.vialogue.adapters.SlidesRecyclerViewAdapterMark2;
import com.comp.iitb.vialogue.coordinators.OnFileCopyCompleted;
import com.comp.iitb.vialogue.coordinators.OnListFragmentInteractionListener;
import com.comp.iitb.vialogue.coordinators.SharedRuntimeContent;
import com.comp.iitb.vialogue.library.Storage;
import com.comp.iitb.vialogue.listeners.ChangeVisibilityOnFocus;
import com.comp.iitb.vialogue.listeners.FileCopyUpdateListener;
import com.comp.iitb.vialogue.listeners.GridLayoutItemTouchHelperCallback;
import com.comp.iitb.vialogue.listeners.MultipleImagePicker;
import com.comp.iitb.vialogue.listeners.ProjectTextWatcher;
import com.comp.iitb.vialogue.listeners.QuestionPickerClick;
import com.comp.iitb.vialogue.listeners.VideoPickerClick;
import com.comp.iitb.vialogue.models.ParseObjects.models.Resources.Image;
import com.comp.iitb.vialogue.models.ParseObjects.models.Resources.Question;
import com.comp.iitb.vialogue.models.ParseObjects.models.Resources.Video;
import com.comp.iitb.vialogue.models.ParseObjects.models.Slide;
import com.comp.iitb.vialogue.utils.ProjectNameUtils;
import com.sangcomz.fishbun.define.Define;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.File;
import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;
import static com.comp.iitb.vialogue.coordinators.SharedRuntimeContent.GET_CAMERA_IMAGE;
import static com.comp.iitb.vialogue.coordinators.SharedRuntimeContent.GET_IMAGE;
import static com.comp.iitb.vialogue.coordinators.SharedRuntimeContent.GET_VIDEO;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CreateVideosMark2.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CreateVideosMark2#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreateVideosMark2 extends Fragment {

    // UI Components
    private Button mImagePicker;
    private Button mVideoPicker;
    private Button mCameraPicker;
    private Button mQuestionPicker;
    private EditText mProjectName;
    private TextView mProjectNameDisplay;
    private AVLoadingIndicatorView mLoadingAnimationView;
    private RecyclerView mSlidesRecyclerView;
    private LinearLayout mCreateVideosRootView;

    // variables
    private OnListFragmentInteractionListener mListener;
    private SlidesRecyclerViewAdapterMark2 mSlideRecyclerViewAdapter;
    private ItemTouchHelper.Callback mItemTouchHelperCallback;
    private ItemTouchHelper mItemTouchHelper;
    private MultipleImagePicker mMultipleImagePicker;
    private Storage mStorage;

    // Required empty public constructor
    public CreateVideosMark2() {}

    public static CreateVideosMark2 newInstance() {
        CreateVideosMark2 fragment = new CreateVideosMark2();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {}
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_create_videos_mark2, container, false);

        // Initialize UI Components
        mImagePicker = (Button) view.findViewById(R.id.image_picker);
        mCameraPicker = (Button) view.findViewById(R.id.camera_image_picker);
        mVideoPicker = (Button) view.findViewById(R.id.video_picker);
        mQuestionPicker = (Button) view.findViewById(R.id.question_picker);
        mProjectName = (EditText) view.findViewById(R.id.project_name);
        mProjectNameDisplay = (TextView) view.findViewById(R.id.project_name_display);
        mLoadingAnimationView = (AVLoadingIndicatorView) view.findViewById(R.id.loading_animation);
        mSlidesRecyclerView = (RecyclerView) view.findViewById(R.id.slides_recycler_view);
        mCreateVideosRootView = (LinearLayout) view.findViewById(R.id.create_videos_root);

        // instantiate Variables
        mSlidesRecyclerView.setLayoutManager(new GridLayoutManager(view.getContext(), 3));
        mSlideRecyclerViewAdapter = new SlidesRecyclerViewAdapterMark2(view.getContext(), mListener, mSlidesRecyclerView);
        mItemTouchHelperCallback = new GridLayoutItemTouchHelperCallback(mSlideRecyclerViewAdapter);
        mItemTouchHelper = new ItemTouchHelper(mItemTouchHelperCallback);
        mStorage = new Storage(getContext());

        // Initialize dependent variables in ShredRuntimeContent
        SharedRuntimeContent.loadingAnimation = mLoadingAnimationView;
        SharedRuntimeContent.projectName = mProjectName;
        SharedRuntimeContent.projectNameDisplay = mProjectNameDisplay;
        SharedRuntimeContent.projectAdapter = mSlideRecyclerViewAdapter;

        // Initialize State
        if((SharedRuntimeContent.getProjectName() != null) && (!SharedRuntimeContent.getProjectName().matches(ProjectNameUtils.untitledProjectNameRegex))) {
            mProjectNameDisplay.setText(SharedRuntimeContent.getProjectName());
            mProjectName.setText(SharedRuntimeContent.getProjectName());
            mProjectNameDisplay.setHint(SharedRuntimeContent.getProjectName());
            mProjectName.setHint(SharedRuntimeContent.getProjectName());
        }

        // Add Listeners
        mProjectName.setOnFocusChangeListener(new ChangeVisibilityOnFocus(mProjectName, mProjectNameDisplay));
        mProjectName.addTextChangedListener(new ProjectTextWatcher(mProjectNameDisplay));
        mProjectName.setFilters(new InputFilter[] { SharedRuntimeContent.filter });
        // Camera Image Picker
        mCameraPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), CameraActivity.class);
                startActivityForResult(intent, SharedRuntimeContent.GET_MULTIPLE_CAMERA_IMAGES);
            }
        });
        //Image Picker
        mMultipleImagePicker = new MultipleImagePicker(view.getContext(), getActivity());
        mImagePicker.setOnClickListener(mMultipleImagePicker);
//        mImagePicker.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(getContext(), GalleryActivity.class);
//                startActivityForResult(intent, 1456);
//            }
//        });
        //Video Picker
        VideoPickerClick videoPickerClickListener = new VideoPickerClick(this);
        mVideoPicker.setOnClickListener(videoPickerClickListener);
        //Question Picker
        QuestionPickerClick questionPickerClickListener = new QuestionPickerClick(getActivity(), CreateVideosMark2.this);
        mQuestionPicker.setOnClickListener(questionPickerClickListener);
        // slideRecyclerView
        mSlidesRecyclerView.setAdapter(mSlideRecyclerViewAdapter);
        mItemTouchHelper.attachToRecyclerView(mSlidesRecyclerView);

        // return the inflated view
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
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

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            SharedRuntimeContent.calculatePreviewFabVisibility();
        } else {}
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.out.println("onActivityResult");
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(getClass().getName(), requestCode + " " + resultCode);
        if (resultCode == RESULT_OK) {
            handlePickedData(requestCode, data);
        } else {
            Toast.makeText(getContext(), R.string.wrongBuddy, Toast.LENGTH_SHORT).show();
        }
    }

    private String mFilePath = null;

    public void handlePickedData(int requestCode, Intent data) {
        System.out.println("handlePickedData : called");
        Log.d(getClass().getName(), "data " + String.valueOf(data == null));

        if (requestCode == GET_VIDEO) {
            // GET VIDEO FROM GALLERY
            if (data != null) {

                System.out.println(mStorage.getRealPathFromURI(data.getData()));
                System.out.println(data.getData());

                try {
                    new File(mStorage.getRealPathFromURI(data.getData()));
                } catch (Exception e) {
                    Toast.makeText(getContext(), "The selected video file is either corrupted or not supported", Toast.LENGTH_LONG).show();
                    return;
                }

                final Video video = new Video(getContext());
                final File v = video.getResourceFile();
                mStorage.addFileToDirectory(
                        new File(mStorage.getRealPathFromURI(data.getData())),
                        v,
                        new FileCopyUpdateListener(getContext()),
                        new OnFileCopyCompleted() {
                            @Override
                            public void done(File file, boolean isSuccessful) {

                                Slide slide = new Slide();
                                try {
                                    slide.addResource(video, Slide.ResourceType.VIDEO);
                                    if(!SharedRuntimeContent.addSlide(slide)) {
                                        throw new Exception();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Toast.makeText(getContext(), R.string.wrongBuddy, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            } else {
                // TODO maybe show a toast
            }

        } else if(requestCode == SharedRuntimeContent.GET_QUESTION) {
            // GET QUESTION
            Bundle extras = data.getExtras();
            Question question = new Question(
                    extras.getString(Question.Fields.QUESTION_STRING_FIELD),
                    extras.getString(Question.Fields.QUESTION_TYPE_FIELD),
                    extras.getStringArrayList(Question.Fields.OPTIONS_FIELD),
                    extras.getIntegerArrayList(Question.Fields.CORRECT_OPTIONS_FIELD),
                    extras.getString(Question.Fields.SOLUTION_FIELD),
                    extras.getStringArrayList(Question.Fields.HINTS_FIELD),
                    extras.getBoolean(Question.Fields.IS_COMPULSORY_FIELD, true)
            );

            Slide slide = new Slide();
            try {
                slide.addResource(question, Slide.ResourceType.QUESTION);
                SharedRuntimeContent.addSlide(slide);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getContext(),  R.string.wrongBuddy, Toast.LENGTH_SHORT);
            }
        } else if(requestCode == SharedRuntimeContent.GET_MULTIPLE_IMAGES) {
            ArrayList<Uri> paths = new ArrayList<>();
            for(int i = 0; i<data.getParcelableArrayListExtra(Define.INTENT_PATH).size(); i++) {
                paths.add(Uri.parse(data.getParcelableArrayListExtra(Define.INTENT_PATH).get(i).toString()));
            }

            for(Uri uri : paths) {
                Slide slide = new Slide();
                final Image image = new Image(getContext());

                mStorage.addFileToDirectory(
                        new File(mStorage.getRealPathFromURI(uri)),
                        image.getResourceFile(),
                        new FileCopyUpdateListener(getContext()),
                        new OnFileCopyCompleted() {
                            @Override
                            public void done(File file, boolean isSuccessful) {

                                Slide slide = new Slide();
                                try {
                                    slide.addResource(image, Slide.ResourceType.IMAGE);
                                    if(!SharedRuntimeContent.addSlide(slide)) {
                                        throw new Exception();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Toast.makeText(getContext(), R.string.wrongBuddy, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

            }
        } else if(requestCode == SharedRuntimeContent.GET_MULTIPLE_CAMERA_IMAGES) {
            ArrayList<String> paths = data.getStringArrayListExtra(CameraActivity.RESULT_KEY);

            for(String path : paths) {

                try {
                    Slide slide = new Slide();
                    Image image = new Image(Uri.fromFile(new File(path)));
                    slide.addResource(image, Slide.ResourceType.IMAGE);
                    if(!SharedRuntimeContent.addSlide(slide)) {
                        throw new Exception();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), R.string.wrongBuddy, Toast.LENGTH_SHORT).show();
                }

            }
        }
    }
}
