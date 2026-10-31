package com.souiid.demoproject.firebase.data.utils

object FirebaseConstants {

    const val REGION = "europe-west1"

    object Collections {
        const val USERS = "users"
        const val POSTS = "posts"
        const val FOLLOWERS = "followers"
        const val FOLLOWING = "following"
        const val NOTIFICATIONS = "notifications"
        const val LIKES = "likes"
    }

    object Fields {
        const val TARGET_USER_ID = "targetUserId"
        const val FCM_TOKEN = "fcmToken"
        const val TYPE = "type"
        const val TITLE = "title"
        const val BODY = "body"
        const val USER_ID = "userId"
        const val POST_ID = "postId"
        const val CREATED_AT = "createdAt"
        const val IS_READ = "isRead"
        const val AUTHOR_ID = "authorId"
        const val CONTENT = "content"
        const val LIKES_COUNT = "likesCount"
        const val FIRST_NAME = "firstName"
        const val LAST_NAME = "lastName"
        const val DESCRIPTION = "description"
        const val PHOTO_URL = "photoUrl"
    }

    object Functions {
        const val CREATE_POST = "createPost"
        const val DELETE_POST = "deletePost"
        const val LIKE_POST = "likePost"
        const val UNLIKE_POST = "unlikePost"
        const val FOLLOW_USER = "followUser"
        const val UNFOLLOW_USER = "unfollowUser"
    }

    object Storage {
        const val PROFILE_PHOTO = "profile.jpg"

        fun profilePhotoPath(userId: String) =
            "${Collections.USERS}/$userId/$PROFILE_PHOTO"
    }
}