/**
 * Booking Data Model
 */
export class Booking {
  constructor(data = {}) {
    this.bookingId = data.bookingId || '';
    this.bookingNumber = data.bookingNumber || '';
    this.customerId = data.customerId || '';
    this.customerName = data.customerName || '';
    this.customerPhone = data.customerPhone || '';
    this.vendorId = data.vendorId || '';
    this.vendorName = data.vendorName || '';
    this.serviceId = data.serviceId || '';
    this.serviceName = data.serviceName || '';
    this.servicePrice = data.servicePrice || 0;
    this.scheduledDate = data.scheduledDate || null;
    this.scheduledTime = data.scheduledTime || '';
    this.status = data.status || 'Booking Requested';
    this.paymentMethod = data.paymentMethod || '';
    this.paymentReceiptUrl = data.paymentReceiptUrl || '';
    this.paymentVerified = data.paymentVerified || false;
    this.rejectionReason = data.rejectionReason || '';
    this.customerNotes = data.customerNotes || '';
    this.vendorNotes = data.vendorNotes || '';
    this.statusHistory = data.statusHistory || [];
    this.createdAt = data.createdAt || null;
    this.updatedAt = data.updatedAt || null;
    this.completedAt = data.completedAt || null;
  }

  toJSON() {
    return {
      bookingNumber: this.bookingNumber,
      customerId: this.customerId,
      customerName: this.customerName,
      customerPhone: this.customerPhone,
      vendorId: this.vendorId,
      vendorName: this.vendorName,
      serviceId: this.serviceId,
      serviceName: this.serviceName,
      servicePrice: this.servicePrice,
      scheduledDate: this.scheduledDate,
      scheduledTime: this.scheduledTime,
      status: this.status,
      paymentMethod: this.paymentMethod,
      paymentReceiptUrl: this.paymentReceiptUrl,
      paymentVerified: this.paymentVerified,
      rejectionReason: this.rejectionReason,
      customerNotes: this.customerNotes,
      vendorNotes: this.vendorNotes,
      statusHistory: this.statusHistory,
      createdAt: this.createdAt,
      updatedAt: this.updatedAt,
      completedAt: this.completedAt
    };
  }
}