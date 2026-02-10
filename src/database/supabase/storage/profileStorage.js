import { supabase, BUCKETS } from '../config';

/**
 * UPLOAD PROFILE PICTURE
 */
export const uploadProfilePicture = async (userId, file) => {
  try {
    const fileName = `${userId}/profile.jpg`;
    
    const { data, error } = await supabase.storage
      .from(BUCKETS.USER_PROFILES)
      .upload(fileName, file, {
        cacheControl: '3600',
        upsert: true
      });

    if (error) throw error;

    const { data: publicUrlData } = supabase.storage
      .from(BUCKETS.USER_PROFILES)
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
 * DELETE PROFILE PICTURE
 */
export const deleteProfilePicture = async (userId) => {
  try {
    const fileName = `${userId}/profile.jpg`;
    
    const { error } = await supabase.storage
      .from(BUCKETS.USER_PROFILES)
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