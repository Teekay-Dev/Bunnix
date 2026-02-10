import { initializeApp } from 'firebase/app';
import { getFirestore } from 'firebase/firestore';

// Firebase configuration
const firebaseConfig = {
  apiKey: process.env.REACT_APP_FIREBASE_API_KEY,
  authDomain: process.env.REACT_APP_FIREBASE_AUTH_DOMAIN,
  projectId: process.env.REACT_APP_FIREBASE_PROJECT_ID,
  storageBucket: process.env.REACT_APP_FIREBASE_STORAGE_BUCKET,
  messagingSenderId: process.env.REACT_APP_FIREBASE_MESSAGING_SENDER_ID,
  appId: process.env.REACT_APP_FIREBASE_APP_ID
};

// Initialize Firebase
const app = initializeApp(firebaseConfig);

// Initialize Firestore only
export const db = getFirestore(app);

// Collection names
export const COLLECTIONS = {
  USERS: 'users',
  VENDOR_PROFILES: 'vendorProfiles',
  PRODUCTS: 'products',
  SERVICES: 'services',
  ORDERS: 'orders',
  BOOKINGS: 'bookings',
  CHATS: 'chats',
  MESSAGES: 'messages',
  REVIEWS: 'reviews',
  NOTIFICATIONS: 'notifications',
  CATEGORIES: 'categories'
};

export default app;