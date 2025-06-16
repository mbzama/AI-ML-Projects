import React, { useEffect, useState } from 'react';
import {
  Container,
  Title,
  Text,
  Button,
  TextInput,
  Paper,
  Stack,
  Group,
  Table,
  Modal,
  LoadingOverlay,
  ActionIcon,
  Badge
} from '@mantine/core';
import { IconEdit, IconTrash, IconPlus } from '@tabler/icons-react';
import { apiClient } from '@/lib/api';
import type { Vendor, CreateVendorRequest } from '@/types/api';

export const VendorsPage: React.FC = () => {
  const [vendors, setVendors] = useState<Vendor[]>([]);
  const [loading, setLoading] = useState(true);
  const [showForm, setShowForm] = useState(false);
  const [editingVendor, setEditingVendor] = useState<Vendor | null>(null);
  const [formData, setFormData] = useState<CreateVendorRequest>({
    name: '',
    email: '',
    phone: '',
    address: '',
  });

  useEffect(() => {
    fetchVendors();
  }, []);

  const fetchVendors = async () => {
    try {
      const data = await apiClient.getVendors();
      setVendors(data);
    } catch (error) {
      console.error('Failed to fetch vendors:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      if (editingVendor) {
        await apiClient.updateVendor(editingVendor.id, formData);
      } else {
        await apiClient.createVendor(formData);
      }
      await fetchVendors();
      resetForm();
    } catch (error) {
      console.error('Failed to save vendor:', error);
    }
  };

  const handleEdit = (vendor: Vendor) => {
    setEditingVendor(vendor);
    setFormData({
      name: vendor.name,
      email: vendor.email,
      phone: vendor.phone,
      address: vendor.address,
    });
    setShowForm(true);
  };

  const handleDelete = async (id: number) => {
    if (window.confirm('Are you sure you want to delete this vendor?')) {
      try {
        await apiClient.deleteVendor(id);
        await fetchVendors();
      } catch (error) {
        console.error('Failed to delete vendor:', error);
      }
    }
  };

  const resetForm = () => {
    setFormData({ name: '', email: '', phone: '', address: '' });
    setEditingVendor(null);
    setShowForm(false);
  };

  if (loading) {
    return (
      <Container>
        <LoadingOverlay visible={loading} />
        <Text>Loading vendors...</Text>
      </Container>
    );
  }

  return (
    <Container size="xl">
      <Stack gap="xl">
        <Group justify="space-between">
          <div>
            <Title order={1}>Vendor Management</Title>
            <Text c="dimmed">Manage vendor information and registrations</Text>
          </div>
          <Button 
            leftSection={<IconPlus size={16} />}
            onClick={() => setShowForm(true)}
          >
            Add Vendor
          </Button>
        </Group>

        <Modal
          opened={showForm}
          onClose={resetForm}
          title={editingVendor ? 'Edit Vendor' : 'Add New Vendor'}
          size="md"
        >
          <form onSubmit={handleSubmit}>
            <Stack gap="md">
              <TextInput
                label="Name"
                placeholder="Enter vendor name"
                value={formData.name}
                onChange={(e) => setFormData({ ...formData, name: e.currentTarget.value })}
                required
              />

              <TextInput
                label="Email"
                type="email"
                placeholder="Enter email address"
                value={formData.email}
                onChange={(e) => setFormData({ ...formData, email: e.currentTarget.value })}
                required
              />

              <TextInput
                label="Phone"
                placeholder="Enter phone number"
                value={formData.phone}
                onChange={(e) => setFormData({ ...formData, phone: e.currentTarget.value })}
                required
              />

              <TextInput
                label="Address"
                placeholder="Enter address"
                value={formData.address}
                onChange={(e) => setFormData({ ...formData, address: e.currentTarget.value })}
                required
              />

              <Group justify="flex-end" gap="sm">
                <Button type="submit">
                  {editingVendor ? 'Update' : 'Create'}
                </Button>
                <Button variant="outline" onClick={resetForm}>
                  Cancel
                </Button>
              </Group>
            </Stack>
          </form>
        </Modal>

        <Paper withBorder>
          <Table>
            <Table.Thead>
              <Table.Tr>
                <Table.Th>Name</Table.Th>
                <Table.Th>Email</Table.Th>
                <Table.Th>Phone</Table.Th>
                <Table.Th>Address</Table.Th>
                <Table.Th>Status</Table.Th>
                <Table.Th>Actions</Table.Th>
              </Table.Tr>
            </Table.Thead>
            <Table.Tbody>
              {vendors.length === 0 ? (
                <Table.Tr>
                  <Table.Td colSpan={6}>
                    <Text ta="center" c="dimmed">No vendors found</Text>
                  </Table.Td>
                </Table.Tr>
              ) : (
                vendors.map((vendor) => (
                  <Table.Tr key={vendor.id}>
                    <Table.Td>{vendor.name}</Table.Td>
                    <Table.Td>{vendor.email}</Table.Td>
                    <Table.Td>{vendor.phone}</Table.Td>
                    <Table.Td>{vendor.address}</Table.Td>
                    <Table.Td>
                      <Badge color="green" variant="light">
                        Active
                      </Badge>
                    </Table.Td>
                    <Table.Td>
                      <Group gap="xs">
                        <ActionIcon
                          variant="light"
                          size="sm"
                          onClick={() => handleEdit(vendor)}
                        >
                          <IconEdit size={16} />
                        </ActionIcon>
                        <ActionIcon
                          variant="light"
                          color="red"
                          size="sm"
                          onClick={() => handleDelete(vendor.id)}
                        >
                          <IconTrash size={16} />
                        </ActionIcon>
                      </Group>
                    </Table.Td>
                  </Table.Tr>
                ))
              )}
            </Table.Tbody>
          </Table>
        </Paper>
      </Stack>
    </Container>
  );
};
