import { db, COLLECTIONS } from '../config';
import { 
  collection, 
  doc,
  addDoc, 
  getDoc,
  getDocs, 
  updateDoc,
  deleteDoc,
  query, 
  where, 
  orderBy,
  onSnapshot,
  increment,
  serverTimestamp 
} from 'firebase/firestore';

const servicesCollection = collection(db, COLLECTIONS.SERVICES);

/**
 * ADD SERVICE
 */
export const addService = async (serviceData) => {
  try {
    const docRef = await addDoc(servicesCollection, {
      ...serviceData,
      createdAt: serverTimestamp(),
      updatedAt: serverTimestamp()
    });
    return { success: true, serviceId: docRef.id };
  } catch (error) {
    return { success: false, error: error.message };
  }
};

/**
 * GET SERVICE BY ID
 */
export const getService = async (serviceId) => {
  try {
    const serviceDoc = await getDoc(doc(servicesCollection, serviceId));
    if (serviceDoc.exists()) {
      return { success: true, data: { id: serviceDoc.id, ...serviceDoc.data() } };
    } else {
      return { success: false, error: 'Service not found' };
    }
  } catch (error) {
    return { success: false, error: error.message };
  }
};

/**
 * GET ALL SERVICES (Real-time)
 */
export const subscribeToAllServices = (callback) => {
  const q = query(
    servicesCollection,
    where('isActive', '==', true),
    orderBy('createdAt', 'desc')
  );
  
  return onSnapshot(q, (snapshot) => {
    const services = snapshot.docs.map(doc => ({
      id: doc.id,
      ...doc.data()
    }));
    callback(services);
  });
};

/**
 * GET SERVICES BY VENDOR (Real-time)
 */
export const subscribeToVendorServices = (vendorId, callback) => {
  const q = query(
    servicesCollection,
    where('vendorId', '==', vendorId),
    orderBy('createdAt', 'desc')
  );
  
  return onSnapshot(q, (snapshot) => {
    const services = snapshot.docs.map(doc => ({
      id: doc.id,
      ...doc.data()
    }));
    callback(services);
  });
};

/**
 * GET SERVICES BY CATEGORY (Real-time)
 */
export const subscribeToServicesByCategory = (category, callback) => {
  const q = query(
    servicesCollection,
    where('category', '==', category),
    where('isActive', '==', true),
    orderBy('createdAt', 'desc')
  );
  
  return onSnapshot(q, (snapshot) => {
    const services = snapshot.docs.map(doc => ({
      id: doc.id,
      ...doc.data()
    }));
    callback(services);
  });
};

/**
 * UPDATE SERVICE
 */
export const updateService = async (serviceId, updates) => {
  try {
    await updateDoc(doc(servicesCollection, serviceId), {
      ...updates,
      updatedAt: serverTimestamp()
    });
    return { success: true };
  } catch (error) {
    return { success: false, error: error.message };
  }
};

/**
 * DELETE SERVICE
 */
export const deleteService = async (serviceId) => {
  try {
    await deleteDoc(doc(servicesCollection, serviceId));
    return { success: true };
  } catch (error) {
    return { success: false, error: error.message };
  }
};

/**
 * INCREMENT TOTAL BOOKINGS
 */
export const incrementTotalBookings = async (serviceId) => {
  try {
    await updateDoc(doc(servicesCollection, serviceId), {
      totalBookings: increment(1)
    });
    return { success: true };
  } catch (error) {
    return { success: false, error: error.message };
  }
};