import { supabase, BUCKETS } from '../config';

/**
 * UPLOAD PAYMENT RECEIPT
 */
export const uploadPaymentReceipt = async (orderId, file) => {
  try {
    const timestamp = Date.now();
    const fileName = `${orderId}/receipt_${timestamp}.jpg`;
    
    const { data, error } = await supabase.storage
      .from(BUCKETS.PAYMENT_RECEIPTS)
      .upload(fileName, file, {
        cacheControl: '3600',
        upsert: false
      });

    if (error) throw error;

    const { data: publicUrlData } = supabase.storage
      .from(BUCKETS.PAYMENT_RECEIPTS)
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
 * DELETE PAYMENT RECEIPT
 */
export const deletePaymentReceipt = async (receiptUrl) => {
  try {
    // Extract file path from URL
    const urlParts = receiptUrl.split('/');
    const fileName = urlParts.slice(-2).join('/');
    
    const { error } = await supabase.storage
      .from(BUCKETS.PAYMENT_RECEIPTS)
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