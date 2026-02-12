/**
 * Notification Data Model
 */
export class Notification {
  constructor(data = {}) {
    this.notificationId = data.notificationId || '';
    this.userId = data.userId || '';
    this.type = data.type || '';
    this.title = data.title || '';
    this.message = data.message || '';
    this.relatedId = data.relatedId || '';
    this.relatedType = data.relatedType || '';
    this.actionUrl = data.actionUrl || '';
    this.imageUrl = data.imageUrl || '';
    this.isRead = data.isRead || false;
    this.createdAt = data.createdAt || null;
    this.expiresAt = data.expiresAt || null;
  }

  toJSON() {
    return {
      userId: this.userId,
      type: this.type,
      title: this.title,
      message: this.message,
      relatedId: this.relatedId,
      relatedType: this.relatedType,
      actionUrl: this.actionUrl,
      imageUrl: this.imageUrl,
      isRead: this.isRead,
      createdAt: this.createdAt,
      expiresAt: this.expiresAt
    };
  }
}