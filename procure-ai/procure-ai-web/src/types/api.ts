export interface User {
  id: number;
  username: string;
  email: string;
  roles: UserRole[];
  isActive: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface UserRole {
  id: number;
  name: string;
  description?: string;
}

export interface Vendor {
  id: number;
  name: string;
  email: string;
  phone: string;
  address: string;
  isActive: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface Auction {
  id: number;
  title: string;
  description: string;
  startDate: string;
  endDate: string;
  status: AuctionStatus;
  createdBy: number;
  createdAt: string;
  updatedAt: string;
  bids?: Bid[];
}

export interface Bid {
  id: number;
  auctionId: number;
  vendorId: number;
  amount: number;
  submittedAt: string;
  vendor?: Vendor;
}

export interface RFQ {
  id: number;
  title: string;
  description: string;
  requirements: string;
  deadline: string;
  status: RFQStatus;
  createdBy: number;
  createdAt: string;
  updatedAt: string;
}

export const AuctionStatus = {
  DRAFT: 'DRAFT',
  ACTIVE: 'ACTIVE',
  CLOSED: 'CLOSED',
  CANCELLED: 'CANCELLED'
} as const;

export type AuctionStatus = typeof AuctionStatus[keyof typeof AuctionStatus];

export const RFQStatus = {
  DRAFT: 'DRAFT',
  PUBLISHED: 'PUBLISHED',
  CLOSED: 'CLOSED',
  CANCELLED: 'CANCELLED'
} as const;

export type RFQStatus = typeof RFQStatus[keyof typeof RFQStatus];

// API Response Types
export interface ApiResponse<T> {
  data: T;
  message?: string;
  success: boolean;
}

export interface LoginRequest {
  username: string;
  password: string;
}

export interface LoginResponse {
  accessToken: string;
  tokenType: string;
  id: number;
  username: string;
  email: string;
  roles: string[];
}

// Temporary User interface to match LoginResponse structure
export interface User {
  id: number;
  username: string;
  email: string;
  roles: UserRole[];
  isActive: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface DashboardStats {
  vendorCount: number;
  auctionCount: number;
  rfqCount: number;
  activeBidsCount?: number;
}

export interface CreateVendorRequest {
  name: string;
  email: string;
  phone: string;
  address: string;
}

export interface UpdateVendorRequest extends Partial<CreateVendorRequest> {
  isActive?: boolean;
}

export interface CreateAuctionRequest {
  title: string;
  description: string;
  startDate: string; // ISO string format for LocalDateTime
  endDate: string;   // ISO string format for LocalDateTime
  currency?: string;
  minimumBidIncrement?: number;
}

export interface UpdateAuctionRequest extends Partial<CreateAuctionRequest> {
  status?: AuctionStatus;
}

export interface CreateBidRequest {
  amount: number;
}

export interface CreateRFQRequest {
  title: string;
  description: string;
  requirements: string;
  deadline: string;
}

export interface UpdateRFQRequest extends Partial<CreateRFQRequest> {
  status?: RFQStatus;
}
