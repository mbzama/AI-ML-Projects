import React from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { 
  AppShell, 
  Text, 
  Group, 
  Button as MantineButton, 
  Title, 
  Container,
  Stack
} from '@mantine/core';
import { IconLogout, IconDashboard, IconBuilding, IconGavel } from '@tabler/icons-react';
import { useAuth } from '@/contexts/AuthContext';

interface LayoutProps {
  children: React.ReactNode;
}

export const Layout: React.FC<LayoutProps> = ({ children }) => {
  const { user, logout, hasRole } = useAuth();
  const location = useLocation();
  const navigate = useNavigate();

  const handleLogout = async () => {
    await logout();
    navigate('/login');
  };

  const isAdmin = hasRole('CREATOR');
  const isVendor = hasRole('VENDOR');

  const adminNavItems = [
    { path: '/admin/dashboard', label: 'Dashboard', icon: IconDashboard },
    { path: '/admin/vendors', label: 'Vendors', icon: IconBuilding },
    { path: '/admin/auctions', label: 'Auctions', icon: IconGavel },
  ];

  const vendorNavItems = [
    { path: '/vendor/dashboard', label: 'Dashboard', icon: IconDashboard },
    { path: '/vendor/auctions', label: 'Auctions', icon: IconGavel },
  ];

  const navItems = isAdmin ? adminNavItems : isVendor ? vendorNavItems : [];

  return (
    <AppShell
      navbar={{
        width: 250,
        breakpoint: 'sm',
      }}
      header={{
        height: 70,
      }}
      padding="md"
    >
      <AppShell.Header>
        <Group justify="space-between" h="100%" px="md">
          <Link to="/" style={{ textDecoration: 'none' }}>
            <Title order={2} c="blue">ProcureAI</Title>
          </Link>
          <Group>
            <Text size="sm" c="dimmed">
              Welcome, {user?.username}
            </Text>
            <MantineButton
              variant="outline"
              size="sm"
              leftSection={<IconLogout size={16} />}
              onClick={handleLogout}
            >
              Logout
            </MantineButton>
          </Group>
        </Group>
      </AppShell.Header>

      <AppShell.Navbar p="md">
        <Stack gap="xs">
          {navItems.map((item) => {
            const Icon = item.icon;
            const isActive = location.pathname === item.path;
            
            return (
              <Link
                key={item.path}
                to={item.path}
                style={{ textDecoration: 'none' }}
              >
                <Group
                  p="md"
                  style={{
                    borderRadius: '8px',
                    backgroundColor: isActive ? '#e3f2fd' : 'transparent',
                    color: isActive ? '#1976d2' : '#666',
                    cursor: 'pointer',
                  }}
                >
                  <Icon size={20} />
                  <Text fw={isActive ? 600 : 400}>{item.label}</Text>
                </Group>
              </Link>
            );
          })}
        </Stack>
      </AppShell.Navbar>

      <AppShell.Main>
        <Container size="xl">
          {children}
        </Container>
      </AppShell.Main>
    </AppShell>
  );
};
