package com.kalu.appimageupload.bucket;

public enum  BucketName {

    PROFILE_IMAGE("kalu-image-uploading-123");

    private final String bucketName;


    BucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getBucketName() {
        return bucketName;
    }
}
