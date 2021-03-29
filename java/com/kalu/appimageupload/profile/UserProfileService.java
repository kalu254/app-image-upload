package com.kalu.appimageupload.profile;

import com.kalu.appimageupload.bucket.BucketName;
import com.kalu.appimageupload.filestore.FileStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

import static org.apache.http.entity.ContentType.*;

@Service
public class UserProfileService {

    private final UserProfileDataAccessService userProfileDataAccessService;
    private final FileStore fileStore;


    @Autowired
    public UserProfileService(UserProfileDataAccessService userProfileDataAccessService, FileStore fileStore) {
        this.userProfileDataAccessService = userProfileDataAccessService;
        this.fileStore = fileStore;
    }

   List<UserProfile> getUserProfile(){
        return userProfileDataAccessService.getUserProfiles();
   }

     void uploadUserProfileImage(UUID userProfileId, MultipartFile file) {
        //check if image is not empty
         isFileEmpty(file);
         //check if file is an image
         isImage(file);
         //check whether a user exists

         UserProfile user = getUserProfileOrElseThrow(userProfileId);
         //grab some metadata from file if any

         Map<String, String> metadata = extractMetaData(file);

         //store the image in s3 and update the database with link

         String path = String.format("%s/%s", BucketName.PROFILE_IMAGE.getBucketName(), user.getUserProfileId());
         String filename = String.format("%s-%s", file.getOriginalFilename(), UUID.randomUUID());

         try {
             fileStore.save(path,filename,Optional.of(metadata),file.getInputStream());
             user.setUserProfileImageLink(filename);
         } catch (IOException e) {
             e.printStackTrace();
         }
     }


    public byte[] downloadUserProfileImage(UUID userProfileId) {
        UserProfile user = getUserProfileOrElseThrow(userProfileId);
        String path = String.format("%s/%s",
            BucketName.PROFILE_IMAGE.getBucketName(),
            user.getUserProfileId());

       return user.getUserProfileImageLink()
            .map(key -> fileStore.download(path,key))
            .orElse(new byte[0]);


    }

    private Map<String, String> extractMetaData(MultipartFile file) {
        Map<String, String> metadata = new HashMap<>();
        metadata.put("Content-Type", file.getContentType());
        metadata.put("Content-Length", String.valueOf(file.getSize()));
        return metadata;
    }

    private UserProfile getUserProfileOrElseThrow(UUID userProfileId) {
        UserProfile user = userProfileDataAccessService.
             getUserProfiles()
             .stream()
             .filter(userProfile -> userProfile.getUserProfileId().equals(userProfileId))
             .findFirst()
             .orElseThrow(() -> new IllegalStateException(String.format("User profile %s not found", userProfileId)));
        return user;
    }

    private void isImage(MultipartFile file) {
        if (!Arrays.asList(IMAGE_JPEG.getMimeType(), IMAGE_PNG.getMimeType(), IMAGE_GIF.getMimeType()).contains(file.getContentType())){
            throw new IllegalStateException("file must be an image");
        }
    }

    private void isFileEmpty(MultipartFile file) {
        if (file.isEmpty()){
            throw new IllegalStateException("cannot upload empty file [ " + file.getSize() + "]" );
        }
    }

}
