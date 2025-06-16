import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
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
import type { Auction, Bid } from '@/types/api';

export const VendorAuctionBidPage: React.FC = () => {
  const { auctionId } = useParams<{ auctionId: string }>();
  const navigate = useNavigate();
  const [auction, setAuction] = useState<Auction | null>(null);
  const [bids, setBids] = useState<Bid[]>([]);
  const [bidAmount, setBidAmount] = useState('');
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    if (auctionId) {
      fetchAuctionDetails();
      fetchBids();
    }
  }, [auctionId]);

  const fetchAuctionDetails = async () => {
    try {
      const data = await apiClient.getAuction(Number(auctionId));
      setAuction(data);
    } catch (error) {
      console.error('Failed to fetch auction details:', error);
    }
  };

  const fetchBids = async () => {
    try {
      const data = await apiClient.getAuctionBids(Number(auctionId));
      setBids(data.sort((a, b) => a.amount - b.amount)); // Sort by lowest bid first
    } catch (error) {
      console.error('Failed to fetch bids:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleSubmitBid = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!bidAmount || isNaN(Number(bidAmount))) {
      alert('Please enter a valid bid amount');
      return;
    }

    setSubmitting(true);
    try {
      await apiClient.createBid(Number(auctionId), { amount: Number(bidAmount) });
      setBidAmount('');
      await fetchBids(); // Refresh bids
      alert('Bid submitted successfully!');
    } catch (error) {
      console.error('Failed to submit bid:', error);
      alert('Failed to submit bid. Please try again.');
    } finally {
      setSubmitting(false);
    }
  };
  const isAuctionActive = auction?.status === 'ACTIVE';
  const currentTime = new Date();
  const auctionEndTime = auction ? new Date(auction.endDate) : null;
  const isAuctionEnded = auctionEndTime ? currentTime > auctionEndTime : false;

  if (loading) {
    return <div className="flex items-center justify-center min-h-64">Loading auction details...</div>;
  }

  if (!auction) {
    return <div className="flex items-center justify-center min-h-64">Auction not found</div>;
  }

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold tracking-tight">{auction.title}</h1>
          <p className="text-muted-foreground">Auction Details and Bidding</p>
        </div>
        <Button variant="outline" onClick={() => navigate('/vendor/dashboard')}>
          Back to Dashboard
        </Button>
      </div>

      <div className="grid gap-6 md:grid-cols-2">
        <Card>
          <CardHeader>
            <CardTitle>Auction Information</CardTitle>
          </CardHeader>
          <CardContent className="space-y-4">
            <div>
              <Label className="font-semibold">Description</Label>
              <p className="text-sm text-muted-foreground mt-1">{auction.description}</p>
            </div>            <div>
              <Label className="font-semibold">Start Time</Label>
              <p className="text-sm text-muted-foreground mt-1">
                {new Date(auction.startDate).toLocaleString()}
              </p>
            </div>
            <div>
              <Label className="font-semibold">End Time</Label>
              <p className="text-sm text-muted-foreground mt-1">
                {new Date(auction.endDate).toLocaleString()}
              </p>
            </div>
            <div>
              <Label className="font-semibold">Status</Label>
              <div className="mt-1">                <span
                  className={`px-2 py-1 rounded-full text-xs ${
                    auction.status === 'ACTIVE'
                      ? 'bg-green-100 text-green-800'
                      : auction.status === 'CLOSED'
                      ? 'bg-muted text-muted-foreground'
                      : 'bg-destructive/10 text-destructive'
                  }`}
                >
                  {auction.status}
                </span>
              </div>
            </div>
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <CardTitle>Submit Your Bid</CardTitle>
          </CardHeader>
          <CardContent>
            {isAuctionActive && !isAuctionEnded ? (
              <form onSubmit={handleSubmitBid} className="space-y-4">
                <div className="space-y-2">
                  <Label htmlFor="bidAmount">Bid Amount ($)</Label>
                  <Input
                    id="bidAmount"
                    type="number"
                    min="0"
                    step="0.01"
                    value={bidAmount}
                    onChange={(e) => setBidAmount(e.target.value)}
                    placeholder="Enter your bid amount"
                    required
                  />
                </div>
                {bids.length > 0 && (
                  <div className="text-sm text-muted-foreground">
                    Current lowest bid: ${Math.min(...bids.map(b => b.amount)).toFixed(2)}
                  </div>
                )}
                <Button type="submit" fullWidth disabled={submitting}>
                  {submitting ? 'Submitting...' : 'Submit Bid'}
                </Button>
              </form>
            ) : (
              <div className="text-center py-4">
                <p className="text-muted-foreground">
                  {isAuctionEnded
                    ? 'This auction has ended'
                    : 'This auction is not currently accepting bids'}
                </p>
              </div>
            )}
          </CardContent>
        </Card>
      </div>

      <Card>
        <CardHeader>
          <CardTitle>Current Bids</CardTitle>
        </CardHeader>
        <CardContent className="p-0">
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead>Rank</TableHead>
                <TableHead>Bid Amount</TableHead>
                <TableHead>Vendor</TableHead>
                <TableHead>Submitted At</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {bids.length === 0 ? (
                <TableRow>
                  <TableCell colSpan={4} className="text-center py-8 text-muted-foreground">
                    No bids submitted yet
                  </TableCell>
                </TableRow>
              ) : (
                bids.map((bid, index) => (
                  <TableRow key={bid.id}>
                    <TableCell className="font-medium">#{index + 1}</TableCell>
                    <TableCell className="font-bold text-green-600">
                      ${bid.amount.toFixed(2)}
                    </TableCell>
                    <TableCell>{bid.vendor?.name || 'Unknown Vendor'}</TableCell>
                    <TableCell>{new Date(bid.submittedAt).toLocaleString()}</TableCell>
                  </TableRow>
                ))
              )}
            </TableBody>
          </Table>
        </CardContent>
      </Card>
    </div>
  );
};
