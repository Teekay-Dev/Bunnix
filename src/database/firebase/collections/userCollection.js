import { db, COLLECTIONS } from '../config';
import { 
  collection, 
  doc, 
  setDoc, 
  getDoc, 
  getDocs,
  updateDoc,
  deleteDoc,
  query,
  where,
  onSnapshot,
  serverTimestamp 
} from 'firebase/firestore';

const usersCollection = collection(db, COLLECTIONS.USERS);

/**
 * CREATE USER
 */
export const createUser = async (userId, userData) => {
  try {
    await setDoc(doc(usersCollection, userId), {
      ...userData,
      createdAt: serverTimestamp(),
      lastActive: serverTimestamp()
    });
    return { success: true };
  } catch (error) {
    return { success: false, error: error.message };
  }
};

/**
 * GET USER BY ID
 */
export const getUser = async (userId) => {
  try {
    const userDoc = await getDoc(doc(usersCollection, userId));
    if (userDoc.exists()) {
      return { success: true, data: { id: userDoc.id, ...userDoc.data() } };
    } else {
      return { success: false, error: 'User not found' };
    }
  } catch (error) {
    return { success: false, error: error.message };
  }
};

/**
 * GET ALL USERS
 */
export const getAllUsers = async () => {
  try {
    const snapshot = await getDocs(usersCollection);
    const users = snapshot.docs.map(doc => ({
      id: doc.id,
      ...doc.data()
    }));
    return { success: true, data: users };
  } catch (error) {
    return { success: false, error: error.message };
  }
};

/**
 * UPDATE USER
 */
export const updateUser = async (userId, updates) => {
  try {
    await updateDoc(doc(usersCollection, userId), {
      ...updates,
      lastActive: serverTimestamp()
    });
    return { success: true };
  } catch (error) {
    return { success: false, error: error.message };
  }
};

/**
 * DELETE USER
 */
export const deleteUser = async (userId) => {
  try {
    await deleteDoc(doc(usersCollection, userId));
    return { success: true };
  } catch (error) {
    return { success: false, error: error.message };
  }
};

/**
 * UPDATE PROFILE PICTURE
 */
export const updateProfilePicture = async (userId, imageUrl) => {
  try {
    await updateDoc(doc(usersCollection, userId), {
      profilePicUrl: imageUrl
    });
    return { success: true };
  } catch (error) {
    return { success: false, error: error.message };
  }
};

/**
 * SWITCH TO VENDOR MODE
 */
export const switchToVendorMode = async (userId) => {
  try {
    await updateDoc(doc(usersCollection, userId), {
      isVendor: true
    });
    return { success: true };
  } catch (error) {
    return { success: false, error: error.message };
  }
};

/**
 * LISTEN TO USER CHANGES (Real-time)
 */
export const subscribeToUser = (userId, callback) => {
  return onSnapshot(doc(usersCollection, userId), (doc) => {
    if (doc.exists()) {
      callback({ id: doc.id, ...doc.data() });
    } else {
      callback(null);
    }
  });
};