import * as functions from "firebase-functions";
import * as admin from "firebase-admin";
import {MulticastMessage} from "firebase-admin/lib/messaging/messaging-api";
admin.initializeApp();
const db = admin.firestore();
const FCM = admin.messaging();

const taskCollection = "tasks";
const setDateField = "setDate";
const progressField = "progress";

const collapseKeyTasks = "TASK";
/*
const dataTypeProp = "TYPE";
const dataIDProp = "ID";
const dataDescProp = "DESC";
const dataNameProp = "NAME";
*/
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
  const plusHour : Date = new Date(currentDate);
  plusHour.setHours(currentDate.getHours()+1);
  const tomorrow : Date = new Date(currentDate);
  tomorrow.setDate(currentDate.getDate()+1);

  const currentTimestamp = admin.firestore.Timestamp.fromDate(currentDate);
  const plusHourTimestamp = admin.firestore.Timestamp.fromDate(plusHour);
  const tomorrowTimestamp = admin.firestore.Timestamp.fromDate(tomorrow);

  functions.logger.info("waos!", currentTimestamp);

  const tasksSnapshot = await db.collection(taskCollection)
    .where(setDateField, "==", currentTimestamp)
    .where(progressField, "in", [0, 1])
    .withConverter(taskCreator).get();

  functions.logger.info(tasksSnapshot.size);

  tasksSnapshot.forEach((doc) => {
    functions.logger.info(doc.id);
    const task : Task = doc.data();
    functions.logger.info("Hello logs!", task.title, task.setDate);
    if (task.token !== undefined && task.token.length > 0) {
      notification(task.token, task.title, 2, collapseKeyTasks, doc.id);
    }
  });

  (await db.collection(taskCollection).withConverter(taskCreator)
    .where(setDateField, "==", plusHourTimestamp)
    .where(progressField, "in", [0, 1])
    .get()).forEach((doc) => {
    const task : Task = doc.data();
    notification(task.token, task.title, 1, collapseKeyTasks, doc.id);
  });

  (await db.collection(taskCollection).withConverter(taskCreator)
    .where(setDateField, "==", tomorrowTimestamp)
    .where(progressField, "in", [0, 1])
    .get()).forEach((doc) => {
    const task : Task = doc.data();
    notification(task.token, task.title, 0, collapseKeyTasks, doc.id);
  });

  (await db.collection(taskCollection).withConverter(taskCreator)
    .where("repeatDates", "array-contains", currentTimestamp)
    .where(progressField, "in", [0, 1])
    .get()).forEach((doc) => {
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
 * @param {string} id ID of document to Intent to when opening notification
 */
async function notification(
  Tokens : Array<string>,
  Name : string,
  Type : number,
  CollapseKey : string,
  id : string,
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
  };

  message.data = {
    TYPE: CollapseKey,
    ID: id,
    DESC: desc,
    NAME: Name,
  };

  const fails = (await FCM.sendEachForMulticast(message)).failureCount;
  if (fails) {
    functions.logger.error("Notification(s) failed!", fails);
  }
}
