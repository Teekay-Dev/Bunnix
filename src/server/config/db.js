// /config/db.js
const admin = require("firebase-admin");
const { createClient } = require("@supabase/supabase-js");
const serviceAccount = require("./serviceAccountKey.json"); // You provide this file

// 1. Initialize Firebase Admin
admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
});

const db = admin.firestore();

// 2. Initialize Supabase Client (Service Role Key needed for bypassing RLS if necessary)
const supabaseUrl = process.env.SUPABASE_URL;
const supabaseKey = process.env.SUPABASE_SERVICE_KEY; // Use Service Role Key, not Anon Key
const supabase = createClient(supabaseUrl, supabaseKey);

module.exports = { db, admin, supabase };