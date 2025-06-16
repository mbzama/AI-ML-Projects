import React, { useEffect, useState } from 'react';
import { 
  Grid, 
  Card, 
  Title, 
  Text, 
  Stack, 
  Loader, 
  Alert, 
  Group,
  ThemeIcon
} from '@mantine/core';
import { 
  IconBuilding, 
  IconGavel, 
  IconUsers, 
  IconAlertCircle
} from '@tabler/icons-react';
import { apiClient } from '@/lib/api';
import type { DashboardStats } from '@/types/api';

export const AdminDashboard: React.FC = () => {
  const [stats, setStats] = useState<DashboardStats | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    const fetchStats = async () => {
      try {
        const data = await apiClient.getDashboardStats();
        setStats(data);
      } catch (err) {
        setError('Failed to load dashboard statistics');
      } finally {
        setLoading(false);
      }
    };

    fetchStats();
  }, []);

  if (loading) {
    return (
      <Stack align="center" justify="center" h={400}>
        <Loader size="lg" />
        <Text>Loading dashboard...</Text>
      </Stack>
    );
  }

  if (error) {
    return (
      <Stack align="center" justify="center" h={400}>
        <Alert 
          icon={<IconAlertCircle size={16} />} 
          color="red" 
          variant="light"
        >
          {error}
        </Alert>
      </Stack>
    );
  }
  return (
    <Stack gap="xl">
      <div>
        <Title order={1} mb="xs">Admin Dashboard</Title>
        <Text c="dimmed">
          Overview of procurement system statistics
        </Text>
      </div>

      <Grid>
        <Grid.Col span={{ base: 12, md: 6, lg: 4 }}>
          <Card withBorder>
            <Group justify="space-between" mb="md">
              <Text size="sm" fw={500}>Total Vendors</Text>
              <ThemeIcon variant="light" size="sm">
                <IconBuilding size={16} />
              </ThemeIcon>
            </Group>
            <Text size="xl" fw={700} mb="xs">{stats?.vendorCount || 0}</Text>
            <Text size="xs" c="dimmed">
              Registered vendors in the system
            </Text>
          </Card>
        </Grid.Col>

        <Grid.Col span={{ base: 12, md: 6, lg: 4 }}>
          <Card withBorder>
            <Group justify="space-between" mb="md">
              <Text size="sm" fw={500}>Total Auctions</Text>
              <ThemeIcon variant="light" size="sm">
                <IconGavel size={16} />
              </ThemeIcon>
            </Group>
            <Text size="xl" fw={700} mb="xs">{stats?.auctionCount || 0}</Text>
            <Text size="xs" c="dimmed">
              Active and completed auctions
            </Text>
          </Card>
        </Grid.Col>

        <Grid.Col span={{ base: 12, md: 6, lg: 4 }}>
          <Card withBorder>
            <Group justify="space-between" mb="md">
              <Text size="sm" fw={500}>Total RFQs</Text>
              <ThemeIcon variant="light" size="sm">
                <IconUsers size={16} />
              </ThemeIcon>
            </Group>
            <Text size="xl" fw={700} mb="xs">{stats?.rfqCount || 0}</Text>
            <Text size="xs" c="dimmed">
              Requests for quotations issued
            </Text>
          </Card>
        </Grid.Col>
      </Grid>
    </Stack>
  );
};
