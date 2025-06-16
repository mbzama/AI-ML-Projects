import React, { useEffect, useState } from 'react';
import { Button } from '@mantine/core';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table';
import { apiClient } from '@/lib/api';
import type { Auction, CreateAuctionRequest, AuctionStatus } from '@/types/api';

export const AuctionsPage: React.FC = () => {
  const [auctions, setAuctions] = useState<Auction[]>([]);
  const [loading, setLoading] = useState(true);
  const [showForm, setShowForm] = useState(false);
  const [editingAuction, setEditingAuction] = useState<Auction | null>(null);  const [formData, setFormData] = useState<CreateAuctionRequest>({
    title: '',
    description: '',
    startDate: '',
    endDate: '',
  });

  useEffect(() => {
    fetchAuctions();
  }, []);

  const fetchAuctions = async () => {
    try {
      const data = await apiClient.getAuctions();
      setAuctions(data);
    } catch (error) {
      console.error('Failed to fetch auctions:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      if (editingAuction) {
        await apiClient.updateAuction(editingAuction.id, formData);
      } else {
        await apiClient.createAuction(formData);
      }
      await fetchAuctions();
      resetForm();
    } catch (error) {
      console.error('Failed to save auction:', error);
    }
  };  const handleEdit = (auction: Auction) => {
    setEditingAuction(auction);
    setFormData({
      title: auction.title,
      description: auction.description,
      startDate: new Date(auction.startDate).toISOString().slice(0, 16),
      endDate: new Date(auction.endDate).toISOString().slice(0, 16),
    });
    setShowForm(true);
  };

  const handleDelete = async (id: number) => {
    if (confirm('Are you sure you want to delete this auction?')) {
      try {
        await apiClient.deleteAuction(id);
        await fetchAuctions();
      } catch (error) {
        console.error('Failed to delete auction:', error);
      }
    }
  };
  const resetForm = () => {
    setFormData({ title: '', description: '', startDate: '', endDate: '' });
    setEditingAuction(null);
    setShowForm(false);
  };
  const getStatusColor = (status: AuctionStatus) => {
    switch (status) {
      case 'ACTIVE':
        return 'bg-green-100 text-green-800';
      case 'CLOSED':
        return 'bg-muted text-muted-foreground';
      case 'CANCELLED':
        return 'bg-destructive/10 text-destructive';
      default:
        return 'bg-primary/10 text-primary';
    }
  };

  if (loading) {
    return <div className="flex items-center justify-center min-h-64">Loading auctions...</div>;
  }

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-3xl font-bold tracking-tight">Auction Management</h1>
          <p className="text-muted-foreground">Create and manage procurement auctions</p>
        </div>
        <Button onClick={() => setShowForm(true)}>Create Auction</Button>
      </div>

      {showForm && (
        <Card>
          <CardHeader>
            <CardTitle>{editingAuction ? 'Edit Auction' : 'Create New Auction'}</CardTitle>
          </CardHeader>
          <CardContent>
            <form onSubmit={handleSubmit} className="space-y-4">
              <div className="space-y-2">
                <Label htmlFor="title">Title</Label>
                <Input
                  id="title"
                  value={formData.title}
                  onChange={(e) => setFormData({ ...formData, title: e.target.value })}
                  required
                />
              </div>
              <div className="space-y-2">
                <Label htmlFor="description">Description</Label>
                <textarea
                  id="description"
                  className="flex min-h-[80px] w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50"
                  value={formData.description}
                  onChange={(e) => setFormData({ ...formData, description: e.target.value })}
                  required
                />
              </div>
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">                <div className="space-y-2">
                  <Label htmlFor="startDate">Start Time</Label>
                  <Input
                    id="startDate"
                    type="datetime-local"
                    value={formData.startDate}
                    onChange={(e) => setFormData({ ...formData, startDate: e.target.value })}
                    required
                  />
                </div>
                <div className="space-y-2">
                  <Label htmlFor="endDate">End Time</Label>
                  <Input
                    id="endDate"
                    type="datetime-local"
                    value={formData.endDate}
                    onChange={(e) => setFormData({ ...formData, endDate: e.target.value })}
                    required
                  />
                </div>
              </div>
              <div className="flex gap-2">
                <Button type="submit">{editingAuction ? 'Update' : 'Create'}</Button>
                <Button type="button" variant="outline" onClick={resetForm}>
                  Cancel
                </Button>
              </div>
            </form>
          </CardContent>
        </Card>
      )}

      <Card>
        <CardContent className="p-0">
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead>Title</TableHead>
                <TableHead>Description</TableHead>
                <TableHead>Start Time</TableHead>
                <TableHead>End Time</TableHead>
                <TableHead>Status</TableHead>
                <TableHead>Actions</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {auctions.map((auction) => (
                <TableRow key={auction.id}>
                  <TableCell className="font-medium">{auction.title}</TableCell>
                  <TableCell className="max-w-xs truncate">{auction.description}</TableCell>                  <TableCell>{new Date(auction.startDate).toLocaleString()}</TableCell>
                  <TableCell>{new Date(auction.endDate).toLocaleString()}</TableCell>
                  <TableCell>
                    <span className={`px-2 py-1 rounded-full text-xs ${getStatusColor(auction.status)}`}>
                      {auction.status}
                    </span>
                  </TableCell>
                  <TableCell>
                    <div className="flex gap-2">
                      <Button
                        variant="outline"
                        size="sm"
                        onClick={() => handleEdit(auction)}
                      >
                        Edit
                      </Button>
                      <Button
                        variant="destructive"
                        size="sm"
                        onClick={() => handleDelete(auction.id)}
                      >
                        Delete
                      </Button>
                    </div>
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </CardContent>
      </Card>
    </div>
  );
};
