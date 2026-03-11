
const express = require("express");
const router = express.Router();
const { protect } = require("../middleware/auth");
const authController = require("../controllers/authController");
const dashboardController = require("../controllers/dashboardController");
const userController = require("../controllers/userController");
const vendorController = require("../controllers/vendorController");

// === Auth Routes ===
router.post("/setup", authController.setupAdmin); // Use this to create your first admin

// === Protected Routes (Requires Token) ===
router.use(protect); 

// Dashboard
router.get("/stats", dashboardController.getDashboardStats);

// Users
router.get("/users", userController.getUsers);
router.put("/users/:userId/status", userController.updateUserStatus);
router.put("/users/:userId/role", userController.updateUserRole); 

// Vendors
router.get("/vendors", vendorController.getVendors);
router.put("/vendors/:vendorId/verify", vendorController.verifyVendor);
router.post("/view-receipt", vendorController.viewReceipt);

module.exports = router;