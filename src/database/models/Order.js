/**
 * Order Data Model
 */
export class Order {
  constructor(data = {}) {
    this.orderId = data.orderId || '';
    this.orderNumber = data.orderNumber || '';
    this.customerId = data.customerId || '';
    this.customerName = data.customerName || '';
    this.customerPhone = data.customerPhone || '';
    this.vendorId = data.vendorId || '';
    this.vendorName = data.vendorName || '';
    this.orderType = data.orderType || 'product';
    this.items = data.items || [];
    this.totalAmount = data.totalAmount || 0;
    this.deliveryAddress = data.deliveryAddress || '';
    this.deliveryFee = data.deliveryFee || 0;
    this.status = data.status || 'Awaiting Payment';
    this.paymentMethod = data.paymentMethod || '';
    this.paymentReceiptUrl = data.paymentReceiptUrl || '';
    this.paymentVerified = data.paymentVerified || false;
    this.paymentVerifiedAt = data.paymentVerifiedAt || null;
    this.paymentVerifiedBy = data.paymentVerifiedBy || '';
    this.rejectionReason = data.rejectionReason || '';
    this.statusHistory = data.statusHistory || [];
    this.customerNotes = data.customerNotes || '';
    this.vendorNotes = data.vendorNotes || '';
    this.trackingNumber = data.trackingNumber || '';
    this.createdAt = data.createdAt || null;
    this.updatedAt = data.updatedAt || null;
    this.completedAt = data.completedAt || null;
  }

  toJSON() {
    return {
      orderNumber: this.orderNumber,
      customerId: this.customerId,
      customerName: this.customerName,
      customerPhone: this.customerPhone,
      vendorId: this.vendorId,
      vendorName: this.vendorName,
      orderType: this.orderType,
      items: this.items,
      totalAmount: this.totalAmount,
      deliveryAddress: this.deliveryAddress,
      deliveryFee: this.deliveryFee,
      status: this.status,
      paymentMethod: this.paymentMethod,
      paymentReceiptUrl: this.paymentReceiptUrl,
      paymentVerified: this.paymentVerified,
      paymentVerifiedAt: this.paymentVerifiedAt,
      paymentVerifiedBy: this.paymentVerifiedBy,
      rejectionReason: this.rejectionReason,
      statusHistory: this.statusHistory,
      customerNotes: this.customerNotes,
      vendorNotes: this.vendorNotes,
      trackingNumber: this.trackingNumber,
      createdAt: this.createdAt,
      updatedAt: this.updatedAt,
      completedAt: this.completedAt
    };
  }
}

/**
 * Order Item
 */
export class OrderItem {
  constructor(data = {}) {
    this.productId = data.productId || '';
    this.name = data.name || '';
    this.quantity = data.quantity || 0;
    this.price = data.price || 0;
    this.variant = data.variant || '';
    this.imageUrl = data.imageUrl || '';
  }

  toJSON() {
    return {
      productId: this.productId,
      name: this.name,
      quantity: this.quantity,
      price: this.price,
      variant: this.variant,
      imageUrl: this.imageUrl
    };
  }
}

/**
 * Status History
 */
export class StatusHistory {
  constructor(data = {}) {
    this.status = data.status || '';
    this.timestamp = data.timestamp || new Date();
    this.updatedBy = data.updatedBy || '';
    this.notes = data.notes || '';
  }

  toJSON() {
    return {
      status: this.status,
      timestamp: this.timestamp,
      updatedBy: this.updatedBy,
      notes: this.notes
    };
  }
}