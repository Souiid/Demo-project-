import UIKit
import FirebaseMessaging
import UserNotifications
import Shared

class AppDelegate: NSObject, UIApplicationDelegate, UNUserNotificationCenterDelegate {

    func application(
        _ application: UIApplication,
        didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]? = nil
    ) -> Bool {
        UNUserNotificationCenter.current().delegate = self
        return true
    }

    func application(
        _ application: UIApplication,
        didRegisterForRemoteNotificationsWithDeviceToken deviceToken: Data
    ) {
        Messaging.messaging().apnsToken = deviceToken
        print("APNs token received")
    }

    func application(
        _ application: UIApplication,
        didFailToRegisterForRemoteNotificationsWithError error: Error
    ) {
        print("APNs registration failed: \(error)")
    }

    func userNotificationCenter(
        _ center: UNUserNotificationCenter,
        didReceive response: UNNotificationResponse,
        withCompletionHandler completionHandler: @escaping () -> Void
    ) {
        let data = response.notification.request.content.userInfo

        let type = data["type"] as? String
        let userId = data["userId"] as? String
        let postId = data["postId"] as? String

        if let type = type {
            NotificationNavigation.shared.navigate(
                type: type,
                userId: userId,
                postId: postId
            )
        }

        print("Notification clicked: \(type ?? "unknown")")
        print("User ID: \(userId ?? "none")")
        print("Post ID: \(postId ?? "none")")

        completionHandler()
    }
}