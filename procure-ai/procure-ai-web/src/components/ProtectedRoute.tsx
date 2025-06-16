import React from 'react';
import { Navigate, useLocation } from 'react-router-dom';
import { Container, Text, Center, Loader } from '@mantine/core';
import { useAuth } from '@/contexts/AuthContext';

interface ProtectedRouteProps {
  children: React.ReactNode;
  requiredRole?: string;
}

export const ProtectedRoute: React.FC<ProtectedRouteProps> = ({ 
  children, 
  requiredRole 
}) => {
  const { isAuthenticated, loading, hasRole } = useAuth();
  const location = useLocation();

  if (loading) {
    return (
      <Container h="100vh">
        <Center h="100%">
          <Loader size="lg" />
        </Center>
      </Container>
    );
  }

  if (!isAuthenticated) {
    return <Navigate to="/login" state={{ from: location }} replace />;
  }

  if (requiredRole && !hasRole(requiredRole)) {
    return (
      <Container h="100vh">
        <Center h="100%">
          <Text size="lg" c="red">Access denied. Insufficient permissions.</Text>
        </Center>
      </Container>
    );
  }

  return <>{children}</>;
};
