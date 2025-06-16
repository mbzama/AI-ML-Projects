import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '@/contexts/AuthContext';
import { 
  Container, 
  Paper, 
  Title, 
  Text, 
  TextInput, 
  PasswordInput, 
  Button, 
  Stack, 
  Alert
} from '@mantine/core';
import { IconAlertCircle, IconUser, IconLock } from '@tabler/icons-react';

export const LoginPage: React.FC = () => {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  
  const { login, isAuthenticated, hasRole } = useAuth();
  const navigate = useNavigate();

  // Effect to handle redirection after successful login
  useEffect(() => {
    if (isAuthenticated) {
      if (hasRole('CREATOR')) {
        navigate('/admin/dashboard', { replace: true });
      } else if (hasRole('VENDOR')) {
        navigate('/vendor/dashboard', { replace: true });
      } else {
        navigate('/', { replace: true });
      }
    }
  }, [isAuthenticated, hasRole, navigate]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    try {
      await login({ username, password });
      // Don't navigate here - let the useEffect handle it after state updates
    } catch (err) {
      setError('Invalid username or password');
    } finally {
      setLoading(false);
    }
  };
  return (
    <Container size="xs" style={{ height: '100vh', display: 'flex', alignItems: 'center' }}>
      <Paper withBorder shadow="md" p={30} radius="md" style={{ width: '100%' }}>
        <Title ta="center" mb="md">Sign in</Title>
        <Text c="dimmed" size="sm" ta="center" mb="xl">
          Enter your credentials to access the procurement system
        </Text>

        <form onSubmit={handleSubmit}>
          <Stack>
            <TextInput
              label="Username"
              placeholder="Enter your username"
              value={username}
              onChange={(e) => setUsername(e.currentTarget.value)}
              leftSection={<IconUser size={16} />}
              required
            />

            <PasswordInput
              label="Password"
              placeholder="Enter your password"
              value={password}
              onChange={(e) => setPassword(e.currentTarget.value)}
              leftSection={<IconLock size={16} />}
              required
            />

            {error && (
              <Alert 
                icon={<IconAlertCircle size={16} />} 
                color="red" 
                variant="light"
              >
                {error}
              </Alert>
            )}

            <Button
              type="submit"
              fullWidth
              loading={loading}
              size="md"
            >
              {loading ? 'Signing in...' : 'Sign in'}
            </Button>
          </Stack>
        </form>
      </Paper>
    </Container>
  );
};
