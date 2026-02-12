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
  increment,
  serverTimestamp 
} from 'firebase/firestore';

const chatsCollection = collection(db, COLLECTIONS.CHATS);

/**
 * GET OR CREATE CHAT
 */
export const getOrCreateChat = async (customerId, vendorId) => {
  try {
    // Check if chat exists
    const q = query(
      chatsCollection,
      where('participants', 'array-contains', customerId)
    );
    
    const snapshot = await getDocs(q);
    const existingChat = snapshot.docs.find(doc => {
      const participants = doc.data().participants;
      return participants.includes(vendorId);
    });
    
    if (existingChat) {
      return { success: true, chatId: existingChat.id };
    }
    
    // Create new chat
    const chatData = {
      participants: [customerId, vendorId],
      participantDetails: {},
      lastMessage: '',
      lastMessageTime: null,
      lastMessageSender: '',
      unreadCount: {
        [customerId]: 0,
        [vendorId]: 0
      },
      relatedOrderId: '',
      relatedBookingId: '',
      isActive: true,
      createdAt: serverTimestamp(),
      updatedAt: serverTimestamp()
    };
    
    const docRef = await addDoc(chatsCollection, chatData);
    return { success: true, chatId: docRef.id };
  } catch (error) {
    return { success: false, error: error.message };
  }
};

/**
 * GET USER CHATS (Real-time)
 */
export const subscribeToUserChats = (userId, callback) => {
  const q = query(
    chatsCollection,
    where('participants', 'array-contains', userId),
    orderBy('lastMessageTime', 'desc')
  );
  
  return onSnapshot(q, (snapshot) => {
    const chats = snapshot.docs.map(doc => ({
      id: doc.id,
      ...doc.data()
    }));
    callback(chats);
  });
};

/**
 * GET MESSAGES (Real-time)
 */
export const subscribeToMessages = (chatId, callback) => {
  const messagesCollection = collection(db, `${COLLECTIONS.CHATS}/${chatId}/messages`);
  const q = query(messagesCollection, orderBy('timestamp', 'asc'));
  
  return onSnapshot(q, (snapshot) => {
    const messages = snapshot.docs.map(doc => ({
      id: doc.id,
      ...doc.data()
    }));
    callback(messages);
  });
};

/**
 * SEND MESSAGE
 */
export const sendMessage = async (chatId, senderId, senderName, text, imageUrl = '', messageType = 'text') => {
  try {
    const messagesCollection = collection(db, `${COLLECTIONS.CHATS}/${chatId}/messages`);
    
    const messageData = {
      senderId,
      senderName,
      text,
      imageUrl,
      messageType,
      orderPreview: {},
      isRead: false,
      timestamp: serverTimestamp(),
      deletedFor: []
    };
    
    await addDoc(messagesCollection, messageData);
    
    // Update chat's last message
    await updateLastMessage(chatId, senderId, text);
    
    return { success: true };
  } catch (error) {
    return { success: false, error: error.message };
  }
};

/**
 * SEND MESSAGE WITH ORDER PREVIEW
 */
export const sendMessageWithOrderPreview = async (chatId, senderId, senderName, text, orderPreview) => {
  try {
    const messagesCollection = collection(db, `${COLLECTIONS.CHATS}/${chatId}/messages`);
    
    const messageData = {
      senderId,
      senderName,
      text,
      imageUrl: '',
      messageType: 'order_link',
      orderPreview,
      isRead: false,
      timestamp: serverTimestamp(),
      deletedFor: []
    };
    
    await addDoc(messagesCollection, messageData);
    await updateLastMessage(chatId, senderId, text);
    
    return { success: true };
  } catch (error) {
    return { success: false, error: error.message };
  }
};

/**
 * UPDATE LAST MESSAGE
 */
const updateLastMessage = async (chatId, senderId, text) => {
  try {
    const chatDoc = await getDoc(doc(chatsCollection, chatId));
    if (!chatDoc.exists()) return;
    
    const participants = chatDoc.data().participants;
    const otherUserId = participants.find(id => id !== senderId);
    
    const currentUnread = chatDoc.data().unreadCount || {};
    
    await updateDoc(doc(chatsCollection, chatId), {
      lastMessage: text,
      lastMessageTime: serverTimestamp(),
      lastMessageSender: senderId,
      [`unreadCount.${otherUserId}`]: (currentUnread[otherUserId] || 0) + 1,
      updatedAt: serverTimestamp()
    });
  } catch (error) {
    console.error('Error updating last message:', error);
  }
};

/**
 * MARK MESSAGES AS READ
 */
export const markMessagesAsRead = async (chatId, userId) => {
  try {
    await updateDoc(doc(chatsCollection, chatId), {
      [`unreadCount.${userId}`]: 0
    });
    
    // Mark individual messages as read
    const messagesCollection = collection(db, `${COLLECTIONS.CHATS}/${chatId}/messages`);
    const q = query(messagesCollection, where('isRead', '==', false));
    const snapshot = await getDocs(q);
    
    const updatePromises = snapshot.docs
      .filter(doc => doc.data().senderId !== userId)
      .map(doc => updateDoc(doc.ref, { isRead: true }));
    
    await Promise.all(updatePromises);
    
    return { success: true };
  } catch (error) {
    return { success: false, error: error.message };
  }
};

/**
 * DELETE MESSAGE FOR USER
 */
export const deleteMessageForUser = async (chatId, messageId, userId) => {
  try {
    const messageRef = doc(db, `${COLLECTIONS.CHATS}/${chatId}/messages`, messageId);
    await updateDoc(messageRef, {
      deletedFor: arrayUnion(userId)
    });
    return { success: true };
  } catch (error) {
    return { success: false, error: error.message };
  }
};