import { db, COLLECTIONS } from '../config';
import { 
  collection, 
  getDocs,
  query, 
  where, 
  orderBy,
  onSnapshot
} from 'firebase/firestore';

const categoriesCollection = collection(db, COLLECTIONS.CATEGORIES);

/**
 * GET ALL CATEGORIES (Real-time)
 */
export const subscribeToAllCategories = (callback) => {
  const q = query(
    categoriesCollection,
    where('isActive', '==', true),
    orderBy('displayOrder', 'asc')
  );
  
  return onSnapshot(q, (snapshot) => {
    const categories = snapshot.docs.map(doc => ({
      id: doc.id,
      ...doc.data()
    }));
    callback(categories);
  });
};

/**
 * GET CATEGORIES BY TYPE (Real-time)
 */
export const subscribeToCategoriesByType = (type, callback) => {
  const q = query(
    categoriesCollection,
    where('type', '==', type),
    where('isActive', '==', true),
    orderBy('displayOrder', 'asc')
  );
  
  return onSnapshot(q, (snapshot) => {
    const categories = snapshot.docs.map(doc => ({
      id: doc.id,
      ...doc.data()
    }));
    callback(categories);
  });
};

/**
 * GET PRODUCT CATEGORIES
 */
export const getProductCategories = async () => {
  try {
    const q = query(
      categoriesCollection,
      where('type', '==', 'product'),
      where('isActive', '==', true),
      orderBy('displayOrder', 'asc')
    );
    
    const snapshot = await getDocs(q);
    const categories = snapshot.docs.map(doc => ({
      id: doc.id,
      ...doc.data()
    }));
    
    return { success: true, data: categories };
  } catch (error) {
    return { success: false, error: error.message };
  }
};

/**
 * GET SERVICE CATEGORIES
 */
export const getServiceCategories = async () => {
  try {
    const q = query(
      categoriesCollection,
      where('type', '==', 'service'),
      where('isActive', '==', true),
      orderBy('displayOrder', 'asc')
    );
    
    const snapshot = await getDocs(q);
    const categories = snapshot.docs.map(doc => ({
      id: doc.id,
      ...doc.data()
    }));
    
    return { success: true, data: categories };
  } catch (error) {
    return { success: false, error: error.message };
  }
};