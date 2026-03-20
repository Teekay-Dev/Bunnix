// /controllers/dashboardController.js
const { db } = require("../config/db");

exports.getDashboardStats = async (req, res) => {
  try {
    // 1. Count Users
    const usersSnapshot = await db.collection("users").get();
    const totalUsers = usersSnapshot.size;

    // 2. Count Vendors
    const vendorsSnapshot = await db.collection("vendorProfiles").get();
    const totalVendors = vendorsSnapshot.size;

    // 3. Calculate Revenue (Sum of 'amount' in orders where status is completed)
    // Note: Firestore aggregation is best done via Cloud Functions, 
    // but for an MVP we can fetch and sum here (optimize later).
    const ordersSnapshot = await db.collection("orders").where("status", "==", "Delivered").get();
    
    let totalRevenue = 0;
    ordersSnapshot.forEach(doc => {
      totalRevenue += doc.data().amount || 0;
    });

    // 4. Pending Approvals (Vendors needing verification)
    const pendingVendors = await db.collection("vendorProfiles").where("isVerified", "==", false).get();

    res.json({
      totalUsers,
      totalVendors,
      totalRevenue,
      pendingVendors: pendingVendors.size
    });

  } catch (error) {
    res.status(500).json({ error: error.message });
  }
};