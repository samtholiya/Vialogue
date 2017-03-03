package com.comp.iitb.vialogue.models.ParseObjects.models.Resources;

import android.content.Context;
import android.net.Uri;

import com.comp.iitb.vialogue.models.ParseObjects.models.Slide;
import com.comp.iitb.vialogue.models.ParseObjects.models.interfaces.BaseResourceClass;
import com.comp.iitb.vialogue.models.ParseObjects.models.interfaces.CanSaveAudioResource;
import com.comp.iitb.vialogue.models.ParseObjects.models.interfaces.ParseObjectsCollection;
import com.parse.ParseClassName;
import com.parse.ParseFile;

import java.io.File;

/**
 * Created by ironstein on 20/02/17.
 */

@ParseClassName("Image")
public class Image extends CanSaveAudioResource {

    private static final String IMAGE_RESOURCE_NAME = "image";
    private Context mContext;

    public Image() {}

    public Image(Context context) {
        this(Uri.fromFile(BaseResourceClass.makeTempResourceFile(Slide.ResourceType.IMAGE, context)));
        mContext = context;
    }

    public Image(Uri uri) {
        super(uri);
    }

    public void addAudio() {
        addAudio(new Audio(mContext));
    }

}
