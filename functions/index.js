const {setGlobalOptions} = require("firebase-functions");
const {onCall, HttpsError} = require("firebase-functions/v2/https");
const admin = require("firebase-admin");

setGlobalOptions({maxInstances: 10});

admin.initializeApp();

const db = admin.firestore();

/**
 * Sends a Firebase Cloud Messaging notification.
 * @param {string} token - Recipient FCM token.
 * @param {string} title - Notification title.
 * @param {string} body - Notification body.
 * @param {Object} data - Additional notification data.
 * @return {Promise<void>} Resolves after the send attempt.
*/
async function sendNotification(token, title, body, data = {}) {
  if (!token) return;

  try {
    await admin.messaging().send({
      token,
      notification: {title, body},
      data,
    });
  } catch (error) {
    console.error("Failed to send notification:", error);
  }
}

/**
 * Saves a notification to the recipient's history.
 * @param {string} userId - Recipient user ID.
 * @param {string} title - Notification title.
 * @param {string} body - Notification body.
 * @param {Object} data - Additional notification data.
 * @return {Promise<void>} Resolves after saving.
 */
async function saveNotification(userId, title, body, data = {}) {
  await db.collection("users").doc(userId)
      .collection("notifications").add({
        title,
        body,
        ...data,
        createdAt: admin.firestore.FieldValue.serverTimestamp(),
      });
}

/**
 * Returns the user's full name.
 * @param {FirebaseFirestore.DocumentSnapshot} user - User document.
 * @return {string} User's full name.
*/
function getUserName(user) {
  if (!user?.exists) return "Someone";

  return [user.get("firstName"), user.get("lastName")]
      .filter(Boolean).join(" ") || "Someone";
}


/*
 * Follow user
 */
exports.followUser = onCall(async (request) => {
  const userId = request.auth?.uid;
  const targetUserId = request.data?.targetUserId;

  if (!userId) {
    throw new HttpsError("unauthenticated", "User must be authenticated.");
  }

  if (typeof targetUserId !== "string" || !targetUserId.trim()) {
    throw new HttpsError("invalid-argument", "Invalid target user ID.");
  }

  if (userId === targetUserId) {
    throw new HttpsError("invalid-argument", "You cannot follow yourself.");
  }

  const userRef = db.collection("users").doc(userId);
  const targetRef = db.collection("users").doc(targetUserId);
  const followingRef = userRef.collection("following").doc(targetUserId);
  const followerRef = targetRef.collection("followers").doc(userId);

  const result = await db.runTransaction(async (transaction) => {
    const user = await transaction.get(userRef);
    const target = await transaction.get(targetRef);
    const following = await transaction.get(followingRef);

    if (!target.exists) {
      throw new HttpsError("not-found", "User not found.");
    }

    if (following.exists) {
      return {isNewFollow: false};
    }

    transaction.set(followingRef, {
      createdAt: admin.firestore.FieldValue.serverTimestamp(),
    });

    transaction.set(followerRef, {
      createdAt: admin.firestore.FieldValue.serverTimestamp(),
    });

    return {
      isNewFollow: true,
      token: target.get("fcmToken"),
      name: getUserName(user),
    };
  });

  if (result.isNewFollow) {
    const title = "New follower";
    const body = `${result.name} started following you.`;
    const data = {type: "NEW_FOLLOWER", userId};

    try {
      await saveNotification(targetUserId, title, body, data);
    } catch (error) {
      console.error("Failed to save follower notification:", error);
    }

    await sendNotification(result.token, title, body, data);
  }

  return {isFollowing: true};
});


/*
 * Unfollow user
 */
exports.unfollowUser = onCall(async (request) => {
  const userId = request.auth?.uid;
  const targetUserId = request.data?.targetUserId;

  if (!userId) {
    throw new HttpsError("unauthenticated", "User must be authenticated.");
  }

  if (
    typeof targetUserId !== "string" ||
    !targetUserId.trim() ||
    userId === targetUserId
  ) {
    throw new HttpsError("invalid-argument", "Invalid target user ID.");
  }

  const userRef = db.collection("users").doc(userId);
  const targetRef = db.collection("users").doc(targetUserId);
  const followingRef = userRef.collection("following").doc(targetUserId);
  const followerRef = targetRef.collection("followers").doc(userId);

  const batch = db.batch();
  batch.delete(followingRef);
  batch.delete(followerRef);
  await batch.commit();

  return {isFollowing: false};
});

/*
 * Create post
 */
exports.createPost = onCall(async (request) => {
  const userId = request.auth?.uid;
  const content = request.data?.content;

  if (!userId) {
    throw new HttpsError("unauthenticated", "User must be authenticated.");
  }

  if (typeof content !== "string" || !content.trim()) {
    throw new HttpsError("invalid-argument", "Post content cannot be empty.");
  }

  if (content.trim().length > 1000) {
    throw new HttpsError("invalid-argument", "Post content is too long.");
  }

  const postRef = db.collection("posts").doc();

  await postRef.set({
    authorId: userId,
    content: content.trim(),
    createdAt: admin.firestore.FieldValue.serverTimestamp(),
    likesCount: 0,
  });

  try {
    const userRef = db.collection("users").doc(userId);
    const [user, followers] = await Promise.all([
      userRef.get(),
      userRef.collection("followers").get(),
    ]);

    const followerIds = followers.docs.map((doc) => doc.id);
    const followerRefs = followerIds.map((id) =>
      db.collection("users").doc(id),
    );

    const followerUsers = followerRefs.length ?
      await db.getAll(...followerRefs) : [];

    const title = "New post";
    const body = `${getUserName(user)} published a new post.`;
    const data = {type: "NEW_POST", userId, postId: postRef.id};

    try {
      for (let i = 0; i < followerIds.length; i += 400) {
        const batch = db.batch();

        followerIds.slice(i, i + 400).forEach((followerId) => {
          const notificationRef = db.collection("users").doc(followerId)
              .collection("notifications").doc();

          batch.set(notificationRef, {
            title,
            body,
            ...data,
            createdAt: admin.firestore.FieldValue.serverTimestamp(),
          });
        });

        await batch.commit();
      }
    } catch (error) {
      console.error("Failed to save post notifications:", error);
    }

    const tokens = [...new Set(followerUsers
        .map((doc) => doc.get("fcmToken"))
        .filter((token) => typeof token === "string" && token.length > 0))];

    for (let i = 0; i < tokens.length; i += 500) {
      const batch = tokens.slice(i, i + 500);

      await admin.messaging().sendEachForMulticast({
        tokens: batch,
        notification: {title, body},
        data,
      });
    }
  } catch (error) {
    console.error("Failed to notify followers:", error);
  }

  return {postId: postRef.id};
});


/*
 * Like post
*/
exports.likePost = onCall(async (request) => {
  const userId = request.auth?.uid;
  const postId = request.data?.postId;

  if (!userId) {
    throw new HttpsError("unauthenticated", "User must be authenticated.");
  }

  if (typeof postId !== "string" || !postId.trim()) {
    throw new HttpsError("invalid-argument", "Invalid post ID.");
  }

  const postRef = db.collection("posts").doc(postId);
  const likeRef = postRef.collection("likes").doc(userId);

  const isNewLike = await db.runTransaction(async (transaction) => {
    const post = await transaction.get(postRef);
    const like = await transaction.get(likeRef);

    if (!post.exists) {
      throw new HttpsError("not-found", "Post not found.");
    }

    if (like.exists) return false;

    transaction.set(likeRef, {
      createdAt: admin.firestore.FieldValue.serverTimestamp(),
    });

    transaction.update(postRef, {
      likesCount: admin.firestore.FieldValue.increment(1),
    });

    return true;
  });

  if (isNewLike) {
    try {
      const post = await postRef.get();
      const authorId = post.get("authorId");

      if (authorId && authorId !== userId) {
        const [author, user] = await Promise.all([
          db.collection("users").doc(authorId).get(),
          db.collection("users").doc(userId).get(),
        ]);

        const title = "New like";
        const body = `${getUserName(user)} liked your post.`;
        const data = {type: "NEW_LIKE", userId, postId};

        try {
          await saveNotification(authorId, title, body, data);
        } catch (error) {
          console.error("Failed to save like notification:", error);
        }

        await sendNotification(author.get("fcmToken"), title, body, data);
      }
    } catch (error) {
      console.error("Failed to notify post author:", error);
    }
  }

  return {isLiked: true};
});


/*
 * Unlike post
 */
exports.unlikePost = onCall(async (request) => {
  const userId = request.auth?.uid;
  const postId = request.data?.postId;

  if (!userId) {
    throw new HttpsError("unauthenticated", "User must be authenticated.");
  }

  if (typeof postId !== "string" || !postId.trim()) {
    throw new HttpsError("invalid-argument", "Invalid post ID.");
  }

  const postRef = db.collection("posts").doc(postId);
  const likeRef = postRef.collection("likes").doc(userId);

  const isLiked = await db.runTransaction(async (transaction) => {
    const post = await transaction.get(postRef);
    const like = await transaction.get(likeRef);

    if (!post.exists) {
      throw new HttpsError("not-found", "Post not found.");
    }

    if (!like.exists) {
      return false;
    }

    transaction.delete(likeRef);

    transaction.update(postRef, {
      likesCount: admin.firestore.FieldValue.increment(-1),
    });

    return false;
  });

  return {isLiked};
});
