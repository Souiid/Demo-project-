const {setGlobalOptions} = require("firebase-functions");
const {onCall, HttpsError} = require("firebase-functions/v2/https");
const admin = require("firebase-admin");

setGlobalOptions({maxInstances: 10});

admin.initializeApp();

const db = admin.firestore();

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

  await db.runTransaction(async (transaction) => {
    const target = await transaction.get(targetRef);

    if (!target.exists) {
      throw new HttpsError("not-found", "User not found.");
    }

    transaction.set(followingRef, {
      createdAt: admin.firestore.FieldValue.serverTimestamp(),
    });

    transaction.set(followerRef, {
      createdAt: admin.firestore.FieldValue.serverTimestamp(),
    });
  });

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
