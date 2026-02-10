/**
 * Service Data Model
 */
export class Service {
  constructor(data = {}) {
    this.serviceId = data.serviceId || '';
    this.vendorId = data.vendorId || '';
    this.vendorName = data.vendorName || '';
    this.name = data.name || '';
    this.description = data.description || '';
    this.price = data.price || 0;
    this.duration = data.duration || 0;
    this.category = data.category || '';
    this.imageUrl = data.imageUrl || '';
    this.availability = data.availability || [];
    this.totalBookings = data.totalBookings || 0;
    this.rating = data.rating || 0;
    this.isActive = data.isActive !== undefined ? data.isActive : true;
    this.createdAt = data.createdAt || null;
    this.updatedAt = data.updatedAt || null;
  }

  toJSON() {
    return {
      vendorId: this.vendorId,
      vendorName: this.vendorName,
      name: this.name,
      description: this.description,
      price: this.price,
      duration: this.duration,
      category: this.category,
      imageUrl: this.imageUrl,
      availability: this.availability,
      totalBookings: this.totalBookings,
      rating: this.rating,
      isActive: this.isActive,
      createdAt: this.createdAt,
      updatedAt: this.updatedAt
    };
  }
}