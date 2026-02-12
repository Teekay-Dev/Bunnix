/**
 * Chat Data Model
 */
export class Chat {
  constructor(data = {}) {
    this.chatId = data.chatId || '';
    this.participants = data.participants || [];
    this.participantDetails = data.participantDetails || {};
    this.lastMessage = data.lastMessage || '';
    this.lastMessageTime = data.lastMessageTime || null;
    this.lastMessageSender = data.lastMessageSender || '';
    this.unreadCount = data.unreadCount || {};
    this.relatedOrderId = data.relatedOrderId || '';
    this.relatedBookingId = data.relatedBookingId || '';
    this.isActive = data.isActive !== undefined ? data.isActive : true;
    this.createdAt = data.createdAt || null;
    this.updatedAt = data.updatedAt || null;
  }

  toJSON() {
    return {
      participants: this.participants,
      participantDetails: this.participantDetails,
      lastMessage: this.lastMessage,
      lastMessageTime: this.lastMessageTime,
      lastMessageSender: this.lastMessageSender,
      unreadCount: this.unreadCount,
      relatedOrderId: this.relatedOrderId,
      relatedBookingId: this.relatedBookingId,
      isActive: this.isActive,
      createdAt: this.createdAt,
      updatedAt: this.updatedAt
    };
  }
}

/**
 * Participant Info
 */
export class ParticipantInfo {
  constructor(data = {}) {
    this.name = data.name || '';
    this.profilePic = data.profilePic || '';
    this.isVendor = data.isVendor || false;
  }

  toJSON() {
    return {
      name: this.name,
      profilePic: this.profilePic,
      isVendor: this.isVendor
    };
  }
}

/**
 * Message Data Model
 */
export class Message {
  constructor(data = {}) {
    this.messageId = data.messageId || '';
    this.senderId = data.senderId || '';
    this.senderName = data.senderName || '';
    this.text = data.text || '';
    this.imageUrl = data.imageUrl || '';
    this.messageType = data.messageType || 'text';
    this.orderPreview = data.orderPreview || {};
    this.isRead = data.isRead || false;
    this.timestamp = data.timestamp || null;
    this.deletedFor = data.deletedFor || [];
  }

  toJSON() {
    return {
      senderId: this.senderId,
      senderName: this.senderName,
      text: this.text,
      imageUrl: this.imageUrl,
      messageType: this.messageType,
      orderPreview: this.orderPreview,
      isRead: this.isRead,
      timestamp: this.timestamp,
      deletedFor: this.deletedFor
    };
  }
}