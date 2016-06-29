package com.bidhee.model.comm;

public class UploadPhotoResponseData extends BaseResponseData {

    private UploadPhotoData values;

    public UploadPhotoData getUploadPhotoData () {

        return values;
    }

    public void setUploadPhotoData (UploadPhotoData values) {

        this.values = values;
    }
}
