import { db, COLLECTIONS } from '../config';
import { 
  collection, 
  doc,
  addDoc, 
  updateDoc,
  deleteDoc,
  getDocs,
  query, 
  where, 
  orderBy,
  limit,
  onSnapshot,
  serverTimestamp 
} from 'firebase/firestore';

const notificationsCollection = collection(db, COLLECTIONS.NOTIFICATIONS);

/**
 * CREATE NOTIFICATION
 */
export const createNotification = async (notificationData) => {
  try {
    const docRef = await addDoc(notificationsCollection, {
      ...notificationData,
      createdAt: serverTimestamp()
    });
    return { success: true, notificationId: docRef.id };
  } catch (error) {
    return { success: false, error: error.message };
  }
};

/**
 * GET USER NOTIFICATIONS (Real-time)
 */
export const subscribeToUserNotifications = (userId, callback, limitCount = 50) => {
  const q = query(
    notificationsCollection,
    where('userId', '==', userId),
    orderBy('createdAt', 'desc'),
    limit(limitCount)
  );
  
  return onSnapshot(q, (snapshot) => {
    const notifications = snapshot.docs.map(doc => ({
      id: doc.id,
      ...doc.data()
    }));
    callback(notifications);
  });
};

/**
 * GET UNREAD NOTIFICATIONS (Real-time)
 */
export const subscribeToUnreadNotifications = (userId, callback) => {
  const q = query(
    notificationsCollection,
    where('userId', '==', userId),
    where('isRead', '==', false),
    orderBy('createdAt', 'desc')
  );
  
  return onSnapshot(q, (snapshot) => {
    const notifications = snapshot.docs.map(doc => ({
      id: doc.id,
      ...doc.data()
    }));
    callback(notifications);
  });
};

/**
 * MARK NOTIFICATION AS READ
 */
export const markAsRead = async (notificationId) => {
  try {
    await updateDoc(doc(notificationsCollection, notificationId), {
      isRead: true
    });
    return { success: true };
  } catch (error) {
    return { success: false, error: error.message };
  }
};

/**
 * MARK ALL AS READ
 */
export const markAllAsRead = async (userId) => {
  try {
    const q = query(
      notificationsCollection,
      where('userId', '==', userId),
      where('isRead', '==', false)
    );
    
    const snapshot = await getDocs(q);
    const updatePromises = snapshot.docs.map(doc => 
      updateDoc(doc.ref, { isRead: true })
    );
    
    await Promise.all(updatePromises);
    return { success: true };
  } catch (error) {
    return { success: false, error: error.message };
  }
};

/**
 * DELETE NOTIFICATION
 */
export const deleteNotification = async (notificationId) => {
  try {
    await deleteDoc(doc(notificationsCollection, notificationId));
    return { success: true };
  } catch (error) {
    return { success: false, error: error.message };
  }
};

/**
 * DELETE ALL NOTIFICATIONS
 */
export const deleteAllNotifications = async (userId) => {
  try {
    const q = query(notificationsCollection, where('userId', '==', userId));
    const snapshot = await getDocs(q);
    
    const deletePromises = snapshot.docs.map(doc => deleteDoc(doc.ref));
    await Promise.all(deletePromises);
    
    return { success: true };
  } catch (error) {
    return { success: false, error: error.message };
  }
};