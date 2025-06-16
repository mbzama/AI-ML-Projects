import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { AuthProvider } from '@/contexts/AuthContext';
import { Layout } from '@/components/Layout';
import { ProtectedRoute } from '@/components/ProtectedRoute';
import { LoginPage } from '@/pages/LoginPage';
import { AdminDashboard } from '@/pages/admin/AdminDashboard';
import { VendorsPage } from '@/pages/admin/VendorsPage';
import { AuctionsPage } from '@/pages/admin/AuctionsPage';
import { VendorDashboard } from '@/pages/vendor/VendorDashboard';
import { VendorAuctionBidPage } from '@/pages/vendor/VendorAuctionBidPage';
import { TestPage } from '@/TestPage';

const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      retry: 1,
      refetchOnWindowFocus: false,
    },
  },
});

const AppRoutes: React.FC = () => {
  return (
    <Routes>
      <Route path="/test" element={<TestPage />} />
      <Route path="/test-vendors" element={<VendorsPage />} />
      <Route path="/login" element={<LoginPage />} />
      <Route path="/" element={<Navigate to="/login" replace />} />
      
      {/* Admin Routes */}
      <Route
        path="/admin/dashboard"
        element={
          <ProtectedRoute requiredRole="CREATOR">
            <Layout>
              <AdminDashboard />
            </Layout>
          </ProtectedRoute>
        }
      />
      <Route
        path="/admin/vendors"
        element={
          <ProtectedRoute requiredRole="CREATOR">
            <Layout>
              <VendorsPage />
            </Layout>
          </ProtectedRoute>
        }
      />
      <Route
        path="/admin/auctions"
        element={
          <ProtectedRoute requiredRole="CREATOR">
            <Layout>
              <AuctionsPage />
            </Layout>
          </ProtectedRoute>
        }
      />

      {/* Vendor Routes */}
      <Route
        path="/vendor/dashboard"
        element={
          <ProtectedRoute requiredRole="VENDOR">
            <Layout>
              <VendorDashboard />
            </Layout>
          </ProtectedRoute>
        }
      />
      <Route
        path="/vendor/auctions/:auctionId/bid"
        element={
          <ProtectedRoute requiredRole="VENDOR">
            <Layout>
              <VendorAuctionBidPage />
            </Layout>
          </ProtectedRoute>
        }
      />

      {/* Catch all route */}
      <Route path="*" element={<Navigate to="/login" replace />} />
    </Routes>
  );
};

const App: React.FC = () => {
  return (
    <QueryClientProvider client={queryClient}>
      <Router>
        <AuthProvider>
          <AppRoutes />
        </AuthProvider>
      </Router>
    </QueryClientProvider>
  );
};

export default App;
