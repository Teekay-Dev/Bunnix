import { db, COLLECTIONS } from '../config';
import { 
  collection, 
  doc,
  addDoc, 
  getDoc, 
  updateDoc,
  query, 
  where, 
  orderBy,
  onSnapshot,
  arrayUnion,
  serverTimestamp 
} from 'firebase/firestore';

const bookingsCollection = collection(db, COLLECTIONS.BOOKINGS);

/**
 * GENERATE BOOKING NUMBER
 */
const generateBookingNumber = () => {
  const date = new Date();
  const dateStr = date.toISOString().split('T')[0].replace(/-/g, '');
  const random = Math.floor(100000 + Math.random() * 900000);
  return `BNX-${dateStr}-${random}`;
};

/**
 * CREATE BOOKING
 */
export const createBooking = async (bookingData) => {
  try {
    const bookingNumber = generateBookingNumber();
    const docRef = await addDoc(bookingsCollection, {
      ...bookingData,
      bookingNumber,
      createdAt: serverTimestamp(),
      updatedAt: serverTimestamp()
    });
    return { success: true, bookingId: docRef.id, bookingNumber };
  } catch (error) {
    return { success: false, error: error.message };
  }
};

/**
 * GET BOOKING BY ID
 */
export const getBooking = async (bookingId) => {
  try {
    const bookingDoc = await getDoc(doc(bookingsCollection, bookingId));
    if (bookingDoc.exists()) {
      return { success: true, data: { id: bookingDoc.id, ...bookingDoc.data() } };
    } else {
      return { success: false, error: 'Booking not found' };
    }
  } catch (error) {
    return { success: false, error: error.message };
  }
};

/**
 * GET CUSTOMER BOOKINGS (Real-time)
 */
export const subscribeToCustomerBookings = (customerId, callback) => {
  const q = query(
    bookingsCollection,
    where('customerId', '==', customerId),
    orderBy('scheduledDate', 'desc')
  );
  
  return onSnapshot(q, (snapshot) => {
    const bookings = snapshot.docs.map(doc => ({
      id: doc.id,
      ...doc.data()
    }));
    callback(bookings);
  });
};

/**
 * GET VENDOR BOOKINGS (Real-time)
 */
export const subscribeToVendorBookings = (vendorId, callback) => {
  const q = query(
    bookingsCollection,
    where('vendorId', '==', vendorId),
    orderBy('scheduledDate', 'desc')
  );
  
  return onSnapshot(q, (snapshot) => {
    const bookings = snapshot.docs.map(doc => ({
      id: doc.id,
      ...doc.data()
    }));
    callback(bookings);
  });
};

/**
 * GET PENDING BOOKINGS (for vendor)
 */
export const subscribeToPendingBookings = (vendorId, callback) => {
  const q = query(
    bookingsCollection,
    where('vendorId', '==', vendorId),
    where('status', '==', 'Payment Submitted'),
    where('paymentVerified', '==', false)
  );
  
  return onSnapshot(q, (snapshot) => {
    const bookings = snapshot.docs.map(doc => ({
      id: doc.id,
      ...doc.data()
    }));
    callback(bookings);
  });
};

/**
 * UPLOAD PAYMENT RECEIPT
 */
export const uploadPaymentReceipt = async (bookingId, receiptUrl, paymentMethod) => {
  try {
    await updateDoc(doc(bookingsCollection, bookingId), {
      paymentReceiptUrl: receiptUrl,
      paymentMethod: paymentMethod,
      status: 'Payment Submitted',
      updatedAt: serverTimestamp()
    });
    return { success: true };
  } catch (error) {
    return { success: false, error: error.message };
  }
};

/**
 * VERIFY PAYMENT
 */
export const verifyPayment = async (bookingId, vendorId) => {
  try {
    const statusUpdate = {
      status: 'Payment Confirmed',
      timestamp: new Date(),
      updatedBy: vendorId,
      notes: 'Payment verified by vendor'
    };
    
    await updateDoc(doc(bookingsCollection, bookingId), {
      paymentVerified: true,
      status: 'Payment Confirmed',
      statusHistory: arrayUnion(statusUpdate),
      updatedAt: serverTimestamp()
    });
    return { success: true };
  } catch (error) {
    return { success: false, error: error.message };
  }
};

/**
 * ACCEPT BOOKING (Vendor)
 */
export const acceptBooking = async (bookingId, vendorId, notes = '') => {
  try {
    const statusUpdate = {
      status: 'Vendor Accepted',
      timestamp: new Date(),
      updatedBy: vendorId,
      notes: notes
    };
    
    await updateDoc(doc(bookingsCollection, bookingId), {
      status: 'Vendor Accepted',
      vendorNotes: notes,
      statusHistory: arrayUnion(statusUpdate),
      updatedAt: serverTimestamp()
    });
    return { success: true };
  } catch (error) {
    return { success: false, error: error.message };
  }
};

/**
 * UPDATE BOOKING STATUS
 */
export const updateBookingStatus = async (bookingId, newStatus, userId, notes = '') => {
  try {
    const statusUpdate = {
      status: newStatus,
      timestamp: new Date(),
      updatedBy: userId,
      notes: notes
    };
    
    const updates = {
      status: newStatus,
      statusHistory: arrayUnion(statusUpdate),
      updatedAt: serverTimestamp()
    };
    
    if (newStatus === 'Completed') {
      updates.completedAt = serverTimestamp();
    }
    
    await updateDoc(doc(bookingsCollection, bookingId), updates);
    return { success: true };
  } catch (error) {
    return { success: false, error: error.message };
  }
};

/**
 * CANCEL BOOKING
 */
export const cancelBooking = async (bookingId, userId, reason) => {
  try {
    return await updateBookingStatus(bookingId, 'Cancelled', userId, reason);
  } catch (error) {
    return { success: false, error: error.message };
  }
};