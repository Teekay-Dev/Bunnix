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
  orderBy,
  serverTimestamp 
} from 'firebase/firestore';

const vendorProfilesCollection = collection(db, COLLECTIONS.VENDOR_PROFILES);

/**
 * CREATE VENDOR PROFILE
 */
export const createVendorProfile = async (vendorId, profileData) => {
  try {
    await setDoc(doc(vendorProfilesCollection, vendorId), {
      ...profileData,
      createdAt: serverTimestamp(),
      updatedAt: serverTimestamp()
    });
    return { success: true };
  } catch (error) {
    return { success: false, error: error.message };
  }
};

/**
 * GET VENDOR PROFILE
 */
export const getVendorProfile = async (vendorId) => {
  try {
    const vendorDoc = await getDoc(doc(vendorProfilesCollection, vendorId));
    if (vendorDoc.exists()) {
      return { success: true, data: { id: vendorDoc.id, ...vendorDoc.data() } };
    } else {
      return { success: false, error: 'Vendor not found' };
    }
  } catch (error) {
    return { success: false, error: error.message };
  }
};

/**
 * GET ALL VENDOR PROFILES
 */
export const getAllVendorProfiles = async () => {
  try {
    const snapshot = await getDocs(vendorProfilesCollection);
    const vendors = snapshot.docs.map(doc => ({
      id: doc.id,
      ...doc.data()
    }));
    return { success: true, data: vendors };
  } catch (error) {
    return { success: false, error: error.message };
  }
};

/**
 * GET VENDORS BY CATEGORY (Real-time)
 */
export const subscribeToVendorsByCategory = (category, callback) => {
  const q = query(
    vendorProfilesCollection,
    where('category', '==', category),
    where('isAvailable', '==', true)
  );
  
  return onSnapshot(q, (snapshot) => {
    const vendors = snapshot.docs.map(doc => ({
      id: doc.id,
      ...doc.data()
    }));
    callback(vendors);
  });
};

/**
 * UPDATE VENDOR PROFILE
 */
export const updateVendorProfile = async (vendorId, updates) => {
  try {
    await updateDoc(doc(vendorProfilesCollection, vendorId), {
      ...updates,
      updatedAt: serverTimestamp()
    });
    return { success: true };
  } catch (error) {
    return { success: false, error: error.message };
  }
};

/**
 * DELETE VENDOR PROFILE
 */
export const deleteVendorProfile = async (vendorId) => {
  try {
    await deleteDoc(doc(vendorProfilesCollection, vendorId));
    return { success: true };
  } catch (error) {
    return { success: false, error: error.message };
  }
};

/**
 * UPDATE VENDOR RATING
 */
export const updateVendorRating = async (vendorId, newRating, totalReviews) => {
  try {
    await updateDoc(doc(vendorProfilesCollection, vendorId), {
      rating: newRating,
      totalReviews: totalReviews
    });
    return { success: true };
  } catch (error) {
    return { success: false, error: error.message };
  }
};

/**
 * INCREMENT TOTAL SALES
 */
export const incrementTotalSales = async (vendorId, amount) => {
  try {
    const vendorDoc = await getDoc(doc(vendorProfilesCollection, vendorId));
    if (vendorDoc.exists()) {
      const currentSales = vendorDoc.data().totalSales || 0;
      const currentRevenue = vendorDoc.data().totalRevenue || 0;
      
      await updateDoc(doc(vendorProfilesCollection, vendorId), {
        totalSales: currentSales + 1,
        totalRevenue: currentRevenue + amount
      });
      return { success: true };
    }
    return { success: false, error: 'Vendor not found' };
  } catch (error) {
    return { success: false, error: error.message };
  }
};