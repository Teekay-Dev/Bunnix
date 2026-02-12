/**
 * Review Data Model
 */
export class Review {
  constructor(data = {}) {
    this.reviewId = data.reviewId || '';
    this.vendorId = data.vendorId || '';
    this.customerId = data.customerId || '';
    this.customerName = data.customerName || '';
    this.orderId = data.orderId || '';
    this.bookingId = data.bookingId || '';
    this.rating = data.rating || 0;
    this.comment = data.comment || '';
    this.images = data.images || [];
    this.vendorResponse = data.vendorResponse || '';
    this.vendorResponseAt = data.vendorResponseAt || null;
    this.isVerifiedPurchase = data.isVerifiedPurchase || false;
    this.helpful = data.helpful || 0;
    this.createdAt = data.createdAt || null;
    this.updatedAt = data.updatedAt || null;
  }

  toJSON() {
    return {
      vendorId: this.vendorId,
      customerId: this.customerId,
      customerName: this.customerName,
      orderId: this.orderId,
      bookingId: this.bookingId,
      rating: this.rating,
      comment: this.comment,
      images: this.images,
      vendorResponse: this.vendorResponse,
      vendorResponseAt: this.vendorResponseAt,
      isVerifiedPurchase: this.isVerifiedPurchase,
      helpful: this.helpful,
      createdAt: this.createdAt,
      updatedAt: this.updatedAt
    };
  }
}