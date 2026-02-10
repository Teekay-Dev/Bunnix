import { supabase, BUCKETS } from '../config';

/**
 * UPLOAD SERVICE IMAGE
 */
export const uploadServiceImage = async (serviceId, file) => {
  try {
    const fileName = `${serviceId}/image.jpg`;
    
    const { data, error } = await supabase.storage
      .from(BUCKETS.SERVICE_IMAGES)
      .upload(fileName, file, {
        cacheControl: '3600',
        upsert: true
      });

    if (error) throw error;

    const { data: publicUrlData } = supabase.storage
      .from(BUCKETS.SERVICE_IMAGES)
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
 * DELETE SERVICE IMAGE
 */
export const deleteServiceImage = async (serviceId) => {
  try {
    const fileName = `${serviceId}/image.jpg`;
    
    const { error } = await supabase.storage
      .from(BUCKETS.SERVICE_IMAGES)
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