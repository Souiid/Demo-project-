package com.souiid.demoproject.firebase.data.utils

import dev.gitlive.firebase.firestore.FirebaseFirestore

fun FirebaseFirestore.usersCollection() =
    collection(FirebaseConstants.Collections.USERS)

fun FirebaseFirestore.userDocument(userId: String) =
    usersCollection().document(userId)

fun FirebaseFirestore.postsCollection() =
    collection(FirebaseConstants.Collections.POSTS)

fun FirebaseFirestore.postDocument(postId: String) =
    postsCollection().document(postId)

fun FirebaseFirestore.followersCollection(userId: String) =
    userDocument(userId).collection(FirebaseConstants.Collections.FOLLOWERS)

fun FirebaseFirestore.followingCollection(userId: String) =
    userDocument(userId).collection(FirebaseConstants.Collections.FOLLOWING)

fun FirebaseFirestore.notificationsCollection(userId: String) =
    userDocument(userId).collection(FirebaseConstants.Collections.NOTIFICATIONS)

fun FirebaseFirestore.likesCollection(postId: String) =
    postDocument(postId).collection(FirebaseConstants.Collections.LIKES)