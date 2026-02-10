import { supabase, BUCKETS } from '../config';

/**
 * UPLOAD VENDOR COVER PHOTO
 */
export const uploadVendorCover = async (vendorId, file) => {
  try {
    const fileName = `${vendorId}/cover.jpg`;
    
    const { data, error } = await supabase.storage
      .from(BUCKETS.VENDOR_PHOTOS)
      .upload(fileName, file, {
        cacheControl: '3600',
        upsert: true
      });

    if (error) throw error;

    const { data: publicUrlData } = supabase.storage
      .from(BUCKETS.VENDOR_PHOTOS)
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
 * UPLOAD VENDOR LOGO
 */
export const uploadVendorLogo = async (vendorId, file) => {
  try {
    const fileName = `${vendorId}/logo.jpg`;
    
    const { data, error } = await supabase.storage
      .from(BUCKETS.VENDOR_PHOTOS)
      .upload(fileName, file, {
        cacheControl: '3600',
        upsert: true
      });

    if (error) throw error;

    const { data: publicUrlData } = supabase.storage
      .from(BUCKETS.VENDOR_PHOTOS)
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
 * DELETE VENDOR COVER
 */
export const deleteVendorCover = async (vendorId) => {
  try {
    const fileName = `${vendorId}/cover.jpg`;
    
    const { error } = await supabase.storage
      .from(BUCKETS.VENDOR_PHOTOS)
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