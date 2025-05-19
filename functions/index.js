/**
 * Import function triggers from their respective submodules:
 *
 * const {onCall} = require("firebase-functions/v2/https");
 * const {onDocumentWritten} = require("firebase-functions/v2/firestore");
 *
 * See a full list of supported triggers at https://firebase.google.com/docs/functions
 */

// Create and deploy your first functions
// https://firebase.google.com/docs/functions/get-started

// exports.helloWorld = onRequest((request, response) => {
//   logger.info("Hello logs!", {structuredData: true});
//   response.send("Hello from Firebase!");
// });

const functions = require("firebase-functions");
const admin = require("firebase-admin");

admin.initializeApp();

/* exports.notifyExpiringItems = functions.pubsub
    .schedule("every 24 hours")
    .onRun(async (context) => {
      const db = admin.firestore();
      const now = new Date();
      const warningDate = new Date();
      warningDate.setDate(now.getDate() + 2); // items expiring in 2 days

      const usersSnapshot = await db.collection("users").get();

      for (const userDoc of usersSnapshot.docs) {
        const userId = userDoc.id;
        const fcmToken = userDoc.data().fcmToken;

        const itemsSnapshot = await db.collection("users")
            .doc(userId)
            .collection("foodItems")
            .where("expiresOn", "<=", warningDate.toISOString())
            .get();

        if (!itemsSnapshot.empty && fcmToken) {
          const expiringItems = itemsSnapshot.docs
              .map((doc) => doc.data().name)
              .join(", ");

          await admin.messaging().send({
            token: fcmToken,
            notification: {
              title: "Items Expiring Soon!",
              body: `Don't forget: ${expiringItems} may expire soon.`,
            },
          });
        }
      }

      return null;
    });
*/

exports.testNotifyExpiringItems =
    functions.https.onRequest(async (req, res) => {
      try {
        // await exports.notifyExpiringItems.run();
        res.send("Manual notification check complete!");
      } catch (err) {
        console.error("Manual test error:", err);
        res.status(500).send("Error running notification test.");
      }
    });
