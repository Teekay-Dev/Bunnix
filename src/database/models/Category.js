/**
 * Category Data Model
 */
export class Category {
  constructor(data = {}) {
    this.categoryId = data.categoryId || '';
    this.name = data.name || '';
    this.icon = data.icon || '';
    this.imageUrl = data.imageUrl || '';
    this.type = data.type || '';
    this.subCategories = data.subCategories || [];
    this.displayOrder = data.displayOrder || 0;
    this.isActive = data.isActive !== undefined ? data.isActive : true;
    this.createdAt = data.createdAt || null;
  }

  toJSON() {
    return {
      name: this.name,
      icon: this.icon,
      imageUrl: this.imageUrl,
      type: this.type,
      subCategories: this.subCategories,
      displayOrder: this.displayOrder,
      isActive: this.isActive,
      createdAt: this.createdAt
    };
  }
}