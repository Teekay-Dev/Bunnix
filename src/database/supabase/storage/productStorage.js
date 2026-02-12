import { supabase, BUCKETS } from '../config';

/**
 * UPLOAD PRODUCT IMAGE
 */
export const uploadProductImage = async (productId, file, imageIndex) => {
  try {
    const fileName = `${productId}/${imageIndex}.jpg`;
    
    const { data, error } = await supabase.storage
      .from(BUCKETS.PRODUCT_IMAGES)
      .upload(fileName, file, {
        cacheControl: '3600',
        upsert: true
      });

    if (error) throw error;

    const { data: publicUrlData } = supabase.storage
      .from(BUCKETS.PRODUCT_IMAGES)
      .getPublicUrl(fileName);

    return { 
      success: true, 
      url: publicUrlData.publicUrl 
    };
  } catch (error) {
    return { 
      success: false, 
      error: error.message 
    };
  }
};

/**
 * UPLOAD MULTIPLE PRODUCT IMAGES
 */
export const uploadMultipleProductImages = async (productId, files) => {
  try {
    const uploadPromises = files.map((file, index) => 
      uploadProductImage(productId, file, index)
    );
    
    const results = await Promise.all(uploadPromises);
    
    const failedUploads = results.filter(r => !r.success);
    if (failedUploads.length > 0) {
      throw new Error('Some images failed to upload');
    }
    
    const urls = results.map(r => r.url);
    
    return { 
      success: true, 
      urls 
    };
  } catch (error) {
    return { 
      success: false, 
      error: error.message 
    };
  }
};

/**
 * DELETE PRODUCT IMAGE
 */
export const deleteProductImage = async (productId, imageIndex) => {
  try {
    const fileName = `${productId}/${imageIndex}.jpg`;
    
    const { error } = await supabase.storage
      .from(BUCKETS.PRODUCT_IMAGES)
      .remove([fileName]);

    if (error) throw error;

    return { success: true };
  } catch (error) {
    return { 
      success: false, 
      error: error.message 
    };
  }
};

/**
 * DELETE ALL PRODUCT IMAGES
 */
export const deleteAllProductImages = async (productId, imageCount) => {
  try {
    const fileNames = Array.from({ length: imageCount }, (_, i) => 
      `${productId}/${i}.jpg`
    );
    
    const { error } = await supabase.storage
      .from(BUCKETS.PRODUCT_IMAGES)
      .remove(fileNames);

    if (error) throw error;

    return { success: true };
  } catch (error) {
    return { 
      success: false, 
      error: error.message 
    };
  }
};