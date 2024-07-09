package com.autumn.leaves.ss;

import androidx.annotation.NonNull;

import com.autumn.leaves.WindHelper;
import com.google.firebase.messaging.FirebaseMessagingService;

/**
 * Date：2024/7/5
 * Describe:
 */
public class FcmServiceImpl extends FirebaseMessagingService {
    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        WindHelper.INSTANCE.log("token=" + token);
    }
}
