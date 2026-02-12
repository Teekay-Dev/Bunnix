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
  limit,
  onSnapshot,
  increment,
  serverTimestamp 
} from 'firebase/firestore';

const productsCollection = collection(db, COLLECTIONS.PRODUCTS);

/**
 * ADD PRODUCT
 */
export const addProduct = async (productData) => {
  try {
    const docRef = await addDoc(productsCollection, {
      ...productData,
      createdAt: serverTimestamp(),
      updatedAt: serverTimestamp()
    });
    return { success: true, productId: docRef.id };
  } catch (error) {
    return { success: false, error: error.message };
  }
};

/**
 * GET PRODUCT BY ID
 */
export const getProduct = async (productId) => {
  try {
    const productDoc = await getDoc(doc(productsCollection, productId));
    if (productDoc.exists()) {
      return { success: true, data: { id: productDoc.id, ...productDoc.data() } };
    } else {
      return { success: false, error: 'Product not found' };
    }
  } catch (error) {
    return { success: false, error: error.message };
  }
};

/**
 * GET ALL PRODUCTS (Real-time)
 */
export const subscribeToAllProducts = (callback) => {
  const q = query(
    productsCollection, 
    where('inStock', '==', true),
    where('isActive', '==', true),
    orderBy('createdAt', 'desc')
  );
  
  return onSnapshot(q, (snapshot) => {
    const products = snapshot.docs.map(doc => ({
      id: doc.id,
      ...doc.data()
    }));
    callback(products);
  });
};

/**
 * GET PRODUCTS BY VENDOR (Real-time)
 */
export const subscribeToVendorProducts = (vendorId, callback) => {
  const q = query(
    productsCollection,
    where('vendorId', '==', vendorId),
    orderBy('createdAt', 'desc')
  );
  
  return onSnapshot(q, (snapshot) => {
    const products = snapshot.docs.map(doc => ({
      id: doc.id,
      ...doc.data()
    }));
    callback(products);
  });
};

/**
 * GET PRODUCTS BY CATEGORY (Real-time)
 */
export const subscribeToProductsByCategory = (category, callback) => {
  const q = query(
    productsCollection,
    where('category', '==', category),
    where('inStock', '==', true),
    where('isActive', '==', true),
    orderBy('createdAt', 'desc')
  );
  
  return onSnapshot(q, (snapshot) => {
    const products = snapshot.docs.map(doc => ({
      id: doc.id,
      ...doc.data()
    }));
    callback(products);
  });
};

/**
 * SEARCH PRODUCTS BY NAME
 */
export const searchProducts = async (searchTerm) => {
  try {
    const snapshot = await getDocs(productsCollection);
    const products = snapshot.docs
      .map(doc => ({ id: doc.id, ...doc.data() }))
      .filter(product => 
        product.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
        product.tags.some(tag => tag.toLowerCase().includes(searchTerm.toLowerCase()))
      );
    return { success: true, data: products };
  } catch (error) {
    return { success: false, error: error.message };
  }
};

/**
 * UPDATE PRODUCT
 */
export const updateProduct = async (productId, updates) => {
  try {
    await updateDoc(doc(productsCollection, productId), {
      ...updates,
      updatedAt: serverTimestamp()
    });
    return { success: true };
  } catch (error) {
    return { success: false, error: error.message };
  }
};

/**
 * DELETE PRODUCT
 */
export const deleteProduct = async (productId) => {
  try {
    await deleteDoc(doc(productsCollection, productId));
    return { success: true };
  } catch (error) {
    return { success: false, error: error.message };
  }
};

/**
 * UPDATE STOCK
 */
export const updateStock = async (productId, newStock) => {
  try {
    await updateDoc(doc(productsCollection, productId), {
      totalStock: newStock,
      inStock: newStock > 0,
      updatedAt: serverTimestamp()
    });
    return { success: true };
  } catch (error) {
    return { success: false, error: error.message };
  }
};

/**
 * INCREMENT VIEWS
 */
export const incrementViews = async (productId) => {
  try {
    await updateDoc(doc(productsCollection, productId), {
      views: increment(1)
    });
    return { success: true };
  } catch (error) {
    return { success: false, error: error.message };
  }
};

/**
 * INCREMENT SOLD
 */
export const incrementSold = async (productId, quantity) => {
  try {
    await updateDoc(doc(productsCollection, productId), {
      sold: increment(quantity),
      totalStock: increment(-quantity)
    });
    return { success: true };
  } catch (error) {
    return { success: false, error: error.message };
  }
};