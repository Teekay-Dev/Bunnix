import { supabase, BUCKETS } from '../config';
import { v4 as uuidv4 } from 'uuid';

/**
 * UPLOAD CHAT IMAGE
 */
export const uploadChatImage = async (chatId, file) => {
  try {
    const messageId = uuidv4();
    const fileName = `${chatId}/${messageId}.jpg`;
    
    const { data, error } = await supabase.storage
      .from(BUCKETS.CHAT_IMAGES)
      .upload(fileName, file, {
        cacheControl: '3600',
        upsert: false
      });

    if (error) throw error;

    const { data: publicUrlData } = supabase.storage
      .from(BUCKETS.CHAT_IMAGES)
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
 * DELETE CHAT IMAGE
 */
export const deleteChatImage = async (imageUrl) => {
  try {
    const urlParts = imageUrl.split('/');
    const fileName = urlParts.slice(-2).join('/');
    
    const { error } = await supabase.storage
      .from(BUCKETS.CHAT_IMAGES)
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