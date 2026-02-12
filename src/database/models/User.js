/**
 * User Data Model
 */
export class User {
  constructor(data = {}) {
    this.userId = data.userId || '';
    this.name = data.name || '';
    this.email = data.email || '';
    this.phone = data.phone || '';
    this.profilePicUrl = data.profilePicUrl || '';
    this.isVendor = data.isVendor || false;
    this.address = data.address || '';
    this.city = data.city || '';
    this.state = data.state || '';
    this.country = data.country || 'Nigeria';
    this.fcmToken = data.fcmToken || '';
    this.createdAt = data.createdAt || null;
    this.lastActive = data.lastActive || null;
  }

  toJSON() {
    return {
      name: this.name,
      email: this.email,
      phone: this.phone,
      profilePicUrl: this.profilePicUrl,
      isVendor: this.isVendor,
      address: this.address,
      city: this.city,
      state: this.state,
      country: this.country,
      fcmToken: this.fcmToken,
      createdAt: this.createdAt,
      lastActive: this.lastActive
    };
  }
}