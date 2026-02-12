/**
 * Vendor Profile Data Model
 */
export class VendorProfile {
  constructor(data = {}) {
    this.vendorId = data.vendorId || '';
    this.userId = data.userId || '';
    this.businessName = data.businessName || '';
    this.description = data.description || '';
    this.coverPhotoUrl = data.coverPhotoUrl || '';
    this.category = data.category || '';
    this.subCategories = data.subCategories || [];
    this.bankName = data.bankName || '';
    this.accountNumber = data.accountNumber || '';
    this.accountName = data.accountName || '';
    this.alternativePayment = data.alternativePayment || '';
    this.rating = data.rating || 0;
    this.totalReviews = data.totalReviews || 0;
    this.totalSales = data.totalSales || 0;
    this.totalRevenue = data.totalRevenue || 0;
    this.isAvailable = data.isAvailable !== undefined ? data.isAvailable : true;
    this.workingHours = data.workingHours || {};
    this.location = data.location || null;
    this.address = data.address || '';
    this.phone = data.phone || '';
    this.email = data.email || '';
    this.createdAt = data.createdAt || null;
    this.updatedAt = data.updatedAt || null;
  }

  toJSON() {
    return {
      userId: this.userId,
      businessName: this.businessName,
      description: this.description,
      coverPhotoUrl: this.coverPhotoUrl,
      category: this.category,
      subCategories: this.subCategories,
      bankName: this.bankName,
      accountNumber: this.accountNumber,
      accountName: this.accountName,
      alternativePayment: this.alternativePayment,
      rating: this.rating,
      totalReviews: this.totalReviews,
      totalSales: this.totalSales,
      totalRevenue: this.totalRevenue,
      isAvailable: this.isAvailable,
      workingHours: this.workingHours,
      location: this.location,
      address: this.address,
      phone: this.phone,
      email: this.email,
      createdAt: this.createdAt,
      updatedAt: this.updatedAt
    };
  }
}