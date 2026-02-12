import { db, COLLECTIONS } from '../config';
import { 
  collection, 
  doc,
  addDoc, 
  getDoc,
  getDocs,
  updateDoc,
  query, 
  where, 
  orderBy,
  onSnapshot,
  increment,
  serverTimestamp 
} from 'firebase/firestore';

const reviewsCollection = collection(db, COLLECTIONS.REVIEWS);

/**
 * ADD REVIEW
 */
export const addReview = async (reviewData) => {
  try {
    const docRef = await addDoc(reviewsCollection, {
      ...reviewData,
      createdAt: serverTimestamp(),
      updatedAt: serverTimestamp()
    });
    
    // Update vendor rating
    await updateVendorRating(reviewData.vendorId);
    
    return { success: true, reviewId: docRef.id };
  } catch (error) {
    return { success: false, error: error.message };
  }
};

/**
 * GET VENDOR REVIEWS (Real-time)
 */
export const subscribeToVendorReviews = (vendorId, callback) => {
  const q = query(
    reviewsCollection,
    where('vendorId', '==', vendorId),
    orderBy('createdAt', 'desc')
  );
  
  return onSnapshot(q, (snapshot) => {
    const reviews = snapshot.docs.map(doc => ({
      id: doc.id,
      ...doc.data()
    }));
    callback(reviews);
  });
};

/**
 * GET CUSTOMER REVIEWS (Real-time)
 */
export const subscribeToCustomerReviews = (customerId, callback) => {
  const q = query(
    reviewsCollection,
    where('customerId', '==', customerId),
    orderBy('createdAt', 'desc')
  );
  
  return onSnapshot(q, (snapshot) => {
    const reviews = snapshot.docs.map(doc => ({
      id: doc.id,
      ...doc.data()
    }));
    callback(reviews);
  });
};

/**
 * ADD VENDOR RESPONSE
 */
export const addVendorResponse = async (reviewId, response) => {
  try {
    await updateDoc(doc(reviewsCollection, reviewId), {
      vendorResponse: response,
      vendorResponseAt: serverTimestamp(),
      updatedAt: serverTimestamp()
    });
    return { success: true };
  } catch (error) {
    return { success: false, error: error.message };
  }
};

/**
 * INCREMENT HELPFUL COUNT
 */
export const incrementHelpful = async (reviewId) => {
  try {
    await updateDoc(doc(reviewsCollection, reviewId), {
      helpful: increment(1)
    });
    return { success: true };
  } catch (error) {
    return { success: false, error: error.message };
  }
};

/**
 * UPDATE VENDOR RATING
 */
const updateVendorRating = async (vendorId) => {
  try {
    const q = query(reviewsCollection, where('vendorId', '==', vendorId));
    const snapshot = await getDocs(q);
    
    if (snapshot.empty) return;
    
    const reviews = snapshot.docs.map(doc => doc.data());
    const totalRating = reviews.reduce((sum, review) => sum + review.rating, 0);
    const averageRating = totalRating / reviews.length;
    const roundedRating = Math.round(averageRating * 10) / 10; // Round to 1 decimal
    
    // Update vendor profile (import from vendorProfileCollection)
    const vendorProfilesCollection = collection(db, COLLECTIONS.VENDOR_PROFILES);
    await updateDoc(doc(vendorProfilesCollection, vendorId), {
      rating: roundedRating,
      totalReviews: reviews.length
    });
  } catch (error) {
    console.error('Error updating vendor rating:', error);
  }
};