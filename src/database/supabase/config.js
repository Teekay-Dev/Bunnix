import { createClient } from '@supabase/supabase-js';

// Supabase configuration
const supabaseUrl = process.env.REACT_APP_SUPABASE_URL;
const supabaseAnonKey = process.env.REACT_APP_SUPABASE_ANON_KEY;

// Initialize Supabase client
export const supabase = createClient(supabaseUrl, supabaseAnonKey);

// Bucket names
export const BUCKETS = {
  USER_PROFILES: 'user-profiles',
  VENDOR_PHOTOS: 'vendor-photos',
  PRODUCT_IMAGES: 'product-images',
  SERVICE_IMAGES: 'service-images',
  PAYMENT_RECEIPTS: 'payment-receipts',
  CHAT_IMAGES: 'chat-images',
  REVIEW_IMAGES: 'review-images'
};