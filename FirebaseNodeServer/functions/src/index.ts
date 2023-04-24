import * as functions from "firebase-functions";
import * as admin from "firebase-admin";
import {MulticastMessage} from "firebase-admin/lib/messaging/messaging-api";
admin.initializeApp();
const db = admin.firestore();
const FCM = admin.messaging();

const taskCollection = "tasks";
const setDateField = "setDate";

const collapseKeyTasks = "TASK";

const dataTypeProp = "TYPE";
const dataIDProp = "ID";
const dataDescProp = "DESC";
const dataNameProp = "NAME";

const notifDescUpTaskD = "UPCOMING_TASK_DAY";
const notifDescUpTaskH = "UPCOMING_TASK_HOUR";
const notifDescDueTask = "DUE_TASK";

const defaultVal = "DEFAULT";


interface Task {
  userID : string;
  token : Array<string>;
  title : string;
  progress : number;
  categoryID : string;
  categoryName : string;
  dateFinished : string;
  priority : number;
  dueType : number;
  days : Array<number>;
  setDate? : Date;
  repeatDates? : Array<Date>;
}

// // Start writing functions
// // https://firebase.google.com/docs/functions/typescript
//
// export const helloWorld = functions.https.onRequest((request, response) => {
//   functions.logger.info("Hello logs!", {structuredData: true});
//   response.send("Hello from Firebase!");
// });

const taskCreator = {
  toFirestore: (data: Task) => data,
  fromFirestore: (snap: FirebaseFirestore.QueryDocumentSnapshot) =>
    snap.data() as Task,
};

exports.everyFive = functions.pubsub.schedule("*/5 * * * *").onRun(async () =>{
  const currentDate : Date = new Date();
  currentDate.setSeconds(0, 0);
  const plusHour : Date = currentDate;
  plusHour.setHours(currentDate.getHours()+1);
  const tomorrow : Date = currentDate;
  tomorrow.setDate(currentDate.getDate()+1);
  const currentTimestamp = admin.firestore.Timestamp.fromDate(currentDate);
  const plusHourTimestamp = admin.firestore.Timestamp.fromDate(plusHour);
  const tomorrowTimestamp = admin.firestore.Timestamp.fromDate(tomorrow);

  functions.logger.info("waos!", currentTimestamp);

  const tasksSnapshot = await db.collection(taskCollection)
    .where(setDateField, "!=", currentTimestamp)
    .withConverter(taskCreator).get();

  functions.logger.info(tasksSnapshot.size);

  tasksSnapshot.forEach((doc) => {
    functions.logger.info(doc.id);
    const task : Task = doc.data();
    functions.logger.info("Hello logs!", task.title, task.setDate);
    notification(task.token, task.title, 2, collapseKeyTasks, doc.id);
  });

  (await db.collection(taskCollection).withConverter(taskCreator)
    .where(setDateField, "==", plusHourTimestamp).get()).forEach((doc) => {
    const task : Task = doc.data();
    notification(task.token, task.title, 1, collapseKeyTasks, doc.id);
  });

  (await db.collection(taskCollection).withConverter(taskCreator)
    .where(setDateField, "==", tomorrowTimestamp).get()).forEach((doc) => {
    const task : Task = doc.data();
    notification(task.token, task.title, 0, collapseKeyTasks, doc.id);
  });
});

/**
 * Send notification to multiple devices
 * @param {Array<string>} Tokens FCM tokens to send the notification to
 * @param {string} Name name of task, event or ocurrence to alert about
 * @param {number} Type Type of notification to send
 * @param {string} CollapseKey collapseKey to use in notification
 * @param {string} ID ID of document to Intent to when opening notification
 */
async function notification(
  Tokens : Array<string>,
  Name : string,
  Type : number,
  CollapseKey : string,
  ID? : string,
) {
  let desc : string;
  switch (Type) {
  case 0: {
    desc = notifDescUpTaskD;
    break;
  }
  case 1: {
    desc = notifDescUpTaskH;
    break;
  }
  case 2: {
    desc = notifDescDueTask;
    break;
  }
  default: {
    desc = defaultVal;
    break;
  }
  }

  const message : MulticastMessage = {
    tokens: Tokens,
    android: {
      collapseKey: CollapseKey,
    },
  };

  if (message.android !== undefined && ID !== undefined) {
    message.android.data = {
      [dataTypeProp]: CollapseKey,
      [dataIDProp]: ID,
      [dataDescProp]: desc,
      [dataNameProp]: Name,
    };
  }

  const fails = (await FCM.sendEachForMulticast(message)).failureCount;
  functions.logger.info(fails);
}
