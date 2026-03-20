// /controllers/authController.js
const { db } = require("../config/db");

// @desc    Create/Setup Admin (Run this once manually or via Postman)
// @route   POST /api/admin/setup
exports.setupAdmin = async (req, res) => {
  try {
    const { uid, email } = req.body; 

    // Check if already exists
    const doc = await db.collection("admins").doc(uid).get();
    if (doc.exists) {
      return res.status(400).json({ message: "Admin already exists" });
    }

    // Create Admin Record
    await db.collection("admins").doc(uid).set({
      email: email,
      role: "super_admin",
      createdAt: new Date().toISOString(),
      permissions: ["users", "vendors", "orders", "analytics"]
    });

    res.status(201).json({ message: "Admin created successfully" });
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
};