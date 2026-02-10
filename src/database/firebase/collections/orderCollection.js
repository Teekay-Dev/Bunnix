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
  arrayUnion,
  serverTimestamp 
} from 'firebase/firestore';

const ordersCollection = collection(db, COLLECTIONS.ORDERS);

/**
 * GENERATE ORDER NUMBER
 */
const generateOrderNumber = () => {
  const date = new Date();
  const dateStr = date.toISOString().split('T')[0].replace(/-/g, '');
  const random = Math.floor(100000 + Math.random() * 900000);
  return `BNX-${dateStr}-${random}`;
};

/**
 * CREATE ORDER
 */
export const createOrder = async (orderData) => {
  try {
    const orderNumber = generateOrderNumber();
    const docRef = await addDoc(ordersCollection, {
      ...orderData,
      orderNumber,
      createdAt: serverTimestamp(),
      updatedAt: serverTimestamp()
    });
    return { success: true, orderId: docRef.id, orderNumber };
  } catch (error) {
    return { success: false, error: error.message };
  }
};

/**
 * GET ORDER BY ID
 */
export const getOrder = async (orderId) => {
  try {
    const orderDoc = await getDoc(doc(ordersCollection, orderId));
    if (orderDoc.exists()) {
      return { success: true, data: { id: orderDoc.id, ...orderDoc.data() } };
    } else {
      return { success: false, error: 'Order not found' };
    }
  } catch (error) {
    return { success: false, error: error.message };
  }
};

/**
 * GET CUSTOMER ORDERS (Real-time)
 */
export const subscribeToCustomerOrders = (customerId, callback) => {
  const q = query(
    ordersCollection,
    where('customerId', '==', customerId),
    orderBy('createdAt', 'desc')
  );
  
  return onSnapshot(q, (snapshot) => {
    const orders = snapshot.docs.map(doc => ({
      id: doc.id,
      ...doc.data()
    }));
    callback(orders);
  });
};

/**
 * GET VENDOR ORDERS (Real-time)
 */
export const subscribeToVendorOrders = (vendorId, callback) => {
  const q = query(
    ordersCollection,
    where('vendorId', '==', vendorId),
    orderBy('createdAt', 'desc')
  );
  
  return onSnapshot(q, (snapshot) => {
    const orders = snapshot.docs.map(doc => ({
      id: doc.id,
      ...doc.data()
    }));
    callback(orders);
  });
};

/**
 * GET PENDING PAYMENT ORDERS (for vendor)
 */
export const subscribeToPendingPaymentOrders = (vendorId, callback) => {
  const q = query(
    ordersCollection,
    where('vendorId', '==', vendorId),
    where('status', '==', 'Payment Submitted'),
    where('paymentVerified', '==', false)
  );
  
  return onSnapshot(q, (snapshot) => {
    const orders = snapshot.docs.map(doc => ({
      id: doc.id,
      ...doc.data()
    }));
    callback(orders);
  });
};

/**
 * UPLOAD PAYMENT RECEIPT
 */
export const uploadPaymentReceipt = async (orderId, receiptUrl, paymentMethod) => {
  try {
    await updateDoc(doc(ordersCollection, orderId), {
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
 * VERIFY PAYMENT (Vendor confirms)
 */
export const verifyPayment = async (orderId, vendorId) => {
  try {
    const statusUpdate = {
      status: 'Payment Confirmed',
      timestamp: new Date(),
      updatedBy: vendorId,
      notes: 'Payment verified by vendor'
    };
    
    await updateDoc(doc(ordersCollection, orderId), {
      paymentVerified: true,
      paymentVerifiedAt: serverTimestamp(),
      paymentVerifiedBy: vendorId,
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
 * REJECT PAYMENT
 */
export const rejectPayment = async (orderId, vendorId, reason) => {
  try {
    const statusUpdate = {
      status: 'Payment Rejected',
      timestamp: new Date(),
      updatedBy: vendorId,
      notes: reason
    };
    
    await updateDoc(doc(ordersCollection, orderId), {
      status: 'Payment Rejected',
      rejectionReason: reason,
      statusHistory: arrayUnion(statusUpdate),
      updatedAt: serverTimestamp()
    });
    return { success: true };
  } catch (error) {
    return { success: false, error: error.message };
  }
};

/**
 * UPDATE ORDER STATUS
 */
export const updateOrderStatus = async (orderId, newStatus, userId, notes = '') => {
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
    
    if (newStatus === 'Delivered') {
      updates.completedAt = serverTimestamp();
    }
    
    await updateDoc(doc(ordersCollection, orderId), updates);
    return { success: true };
  } catch (error) {
    return { success: false, error: error.message };
  }
};

/**
 * CANCEL ORDER
 */
export const cancelOrder = async (orderId, userId, reason) => {
  try {
    return await updateOrderStatus(orderId, 'Cancelled', userId, reason);
  } catch (error) {
    return { success: false, error: error.message };
  }
};

/**
 * ADD TRACKING NUMBER
 */
export const addTrackingNumber = async (orderId, trackingNumber) => {
  try {
    await updateDoc(doc(ordersCollection, orderId), {
      trackingNumber: trackingNumber,
      updatedAt: serverTimestamp()
    });
    return { success: true };
  } catch (error) {
    return { success: false, error: error.message };
  }
};