// /controllers/vendorController.js
const { db, supabase } = require("../config/db");

// Get All Vendors
exports.getVendors = async (req, res) => {
  const snapshot = await db.collection("vendorProfiles").get();
  const vendors = [];
  snapshot.forEach(doc => vendors.push({ id: doc.id, ...doc.data() }));
  res.json(vendors);
};

// Verify Vendor
exports.verifyVendor = async (req, res) => {
  const { vendorId } = req.params;
  await db.collection("vendorProfiles").doc(vendorId).update({ isVerified: true });
  res.json({ message: "Vendor Verified" });
};

// View Payment Receipt (Generates secure URL for Admin to view)
exports.viewReceipt = async (req, res) => {
  try {
    const { path } = req.body; // Path from Supabase storage
    // 'path' looks like: 'payment-receipts/userId/receipt.jpg'
    
    const { data, error } = await supabase.storage
      .from('payment-receipts') // Bucket name
      .createSignedUrl(path, 60); // Valid for 60 seconds

    if (error) throw error;
    res.json({ url: data.signedUrl });
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
};