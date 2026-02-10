/**
 * Product Data Model
 */
export class Product {
  constructor(data = {}) {
    this.productId = data.productId || '';
    this.vendorId = data.vendorId || '';
    this.vendorName = data.vendorName || '';
    this.name = data.name || '';
    this.description = data.description || '';
    this.price = data.price || 0;
    this.discountPrice = data.discountPrice || null;
    this.category = data.category || '';
    this.subCategory = data.subCategory || '';
    this.imageUrls = data.imageUrls || [];
    this.variants = data.variants || [];
    this.totalStock = data.totalStock || 0;
    this.inStock = data.inStock !== undefined ? data.inStock : true;
    this.tags = data.tags || [];
    this.views = data.views || 0;
    this.sold = data.sold || 0;
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
      discountPrice: this.discountPrice,
      category: this.category,
      subCategory: this.subCategory,
      imageUrls: this.imageUrls,
      variants: this.variants,
      totalStock: this.totalStock,
      inStock: this.inStock,
      tags: this.tags,
      views: this.views,
      sold: this.sold,
      isActive: this.isActive,
      createdAt: this.createdAt,
      updatedAt: this.updatedAt
    };
  }
}

/**
 * Product Variant
 */
export class ProductVariant {
  constructor(data = {}) {
    this.size = data.size || '';
    this.color = data.color || '';
    this.stock = data.stock || 0;
  }

  toJSON() {
    return {
      size: this.size,
      color: this.color,
      stock: this.stock
    };
  }
}