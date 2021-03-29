package com.kalu.appimageupload.datastore;

import com.kalu.appimageupload.profile.UserProfile;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class FakeUserProfileDataStore {
    private static final List<UserProfile> USER_PROFILES = new ArrayList<>();

    static {
        USER_PROFILES.add(new UserProfile(UUID.fromString("8a35ff43-4f98-4e94-8583-015070f409b3"),"janetjames",null));
        USER_PROFILES.add(new UserProfile(UUID.fromString("a5a72ade-c36f-47af-a8a5-fa377823da9f"),"antoniojuniour",null));
    }

    public List<UserProfile> getUserProfiles(){
        return USER_PROFILES;
    }
}
