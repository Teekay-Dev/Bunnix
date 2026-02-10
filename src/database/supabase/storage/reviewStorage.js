import { supabase, BUCKETS } from '../config';

/**
 * UPLOAD REVIEW IMAGE
 */
export const uploadReviewImage = async (reviewId, file, imageIndex) => {
  try {
    const fileName = `${reviewId}/${imageIndex}.jpg`;
    
    const { data, error } = await supabase.storage
      .from(BUCKETS.REVIEW_IMAGES)
      .upload(fileName, file, {
        cacheControl: '3600',
        upsert: true
      });

    if (error) throw error;

    const { data: publicUrlData } = supabase.storage
      .from(BUCKETS.REVIEW_IMAGES)
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
 * UPLOAD MULTIPLE REVIEW IMAGES
 */
export const uploadMultipleReviewImages = async (reviewId, files) => {
  try {
    const uploadPromises = files.map((file, index) => 
      uploadReviewImage(reviewId, file, index)
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
 * DELETE REVIEW IMAGE
 */
export const deleteReviewImage = async (reviewId, imageIndex) => {
  try {
    const fileName = `${reviewId}/${imageIndex}.jpg`;
    
    const { error } = await supabase.storage
      .from(BUCKETS.REVIEW_IMAGES)
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
 * DELETE ALL REVIEW IMAGES
 */
export const deleteAllReviewImages = async (reviewId, imageCount) => {
  try {
    const fileNames = Array.from({ length: imageCount }, (_, i) => 
      `${reviewId}/${i}.jpg`
    );
    
    const { error } = await supabase.storage
      .from(BUCKETS.REVIEW_IMAGES)
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