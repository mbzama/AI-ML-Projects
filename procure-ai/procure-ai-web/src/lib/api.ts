import axios from 'axios';
import type { AxiosInstance, AxiosResponse } from 'axios';
import type {
  ApiResponse,
  LoginRequest,
  LoginResponse,
  User,
  Vendor,
  Auction,
  Bid,
  RFQ,
  DashboardStats,
  CreateVendorRequest,
  UpdateVendorRequest,
  CreateAuctionRequest,
  UpdateAuctionRequest,
  CreateBidRequest,
  CreateRFQRequest,
  UpdateRFQRequest,
} from '../types/api';

class ApiClient {
  private client: AxiosInstance;
  constructor() {
    this.client = axios.create({
      baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
      headers: {
        'Content-Type': 'application/json',
      },
      withCredentials: false, // Set to true if you need cookies
    });

    // Add request interceptor to include auth token
    this.client.interceptors.request.use((config) => {
      const token = localStorage.getItem('authToken');
      if (token) {
        config.headers.Authorization = `Bearer ${token}`;
      }
      return config;
    });

    // Add response interceptor to handle errors
    this.client.interceptors.response.use(
      (response) => response,
      (error) => {
        if (error.response?.status === 401) {
          localStorage.removeItem('authToken');
          window.location.href = '/login';
        }
        return Promise.reject(error);
      }
    );
  }  // Authentication
  async login(credentials: LoginRequest): Promise<LoginResponse> {
    const response: AxiosResponse<LoginResponse> = await this.client.post('/auth/signin', credentials);
    return response.data;
  }
  async logout(): Promise<void> {
    // No logout endpoint in backend, just clear local storage
    localStorage.removeItem('authToken');
  }

  async getCurrentUser(): Promise<User> {
    // Since there's no /auth/me endpoint, we'll need to decode the JWT token
    // or store user info from login response. For now, return mock data.
    throw new Error('getCurrentUser not implemented - no /auth/me endpoint available');
  }
  // Dashboard
  async getDashboardStats(): Promise<DashboardStats> {
    const response: AxiosResponse<ApiResponse<DashboardStats>> = await this.client.get('/admin/stats');
    return response.data.data;
  }

  // Vendors
  async getVendors(): Promise<Vendor[]> {
    const response: AxiosResponse<ApiResponse<Vendor[]>> = await this.client.get('/admin/vendors');
    return response.data.data;
  }
  async getVendor(id: number): Promise<Vendor> {
    const response: AxiosResponse<ApiResponse<Vendor>> = await this.client.get(`/admin/vendors/${id}`);
    return response.data.data;
  }

  async createVendor(vendor: CreateVendorRequest): Promise<Vendor> {
    const response: AxiosResponse<ApiResponse<Vendor>> = await this.client.post('/admin/vendors', vendor);
    return response.data.data;
  }

  async updateVendor(id: number, vendor: UpdateVendorRequest): Promise<Vendor> {
    const response: AxiosResponse<ApiResponse<Vendor>> = await this.client.put(`/admin/vendors/${id}`, vendor);
    return response.data.data;
  }

  async deleteVendor(id: number): Promise<void> {
    await this.client.delete(`/admin/vendors/${id}`);
  }
  // Auctions
  async getAuctions(): Promise<Auction[]> {
    const response: AxiosResponse<Auction[]> = await this.client.get('/admin/auctions');
    return response.data;
  }

  async getAuction(id: number): Promise<Auction> {
    const response: AxiosResponse<Auction> = await this.client.get(`/admin/auctions/${id}`);
    return response.data;
  }

  async createAuction(auction: CreateAuctionRequest): Promise<Auction> {
    const response: AxiosResponse<Auction> = await this.client.post('/admin/auctions', auction);
    return response.data;
  }
  async updateAuction(id: number, auction: UpdateAuctionRequest): Promise<Auction> {
    const response: AxiosResponse<Auction> = await this.client.put(`/admin/auctions/${id}`, auction);
    return response.data;
  }

  async deleteAuction(id: number): Promise<void> {
    await this.client.delete(`/admin/auctions/${id}`);
  }

  // Bids
  async getAuctionBids(auctionId: number): Promise<Bid[]> {
    const response: AxiosResponse<ApiResponse<Bid[]>> = await this.client.get(`/auctions/${auctionId}/bids`);
    return response.data.data;
  }

  async createBid(auctionId: number, bid: CreateBidRequest): Promise<Bid> {
    const response: AxiosResponse<ApiResponse<Bid>> = await this.client.post(`/auctions/${auctionId}/bids`, bid);
    return response.data.data;
  }

  // RFQs
  async getRFQs(): Promise<RFQ[]> {
    const response: AxiosResponse<ApiResponse<RFQ[]>> = await this.client.get('/rfqs');
    return response.data.data;
  }

  async getRFQ(id: number): Promise<RFQ> {
    const response: AxiosResponse<ApiResponse<RFQ>> = await this.client.get(`/rfqs/${id}`);
    return response.data.data;
  }

  async createRFQ(rfq: CreateRFQRequest): Promise<RFQ> {
    const response: AxiosResponse<ApiResponse<RFQ>> = await this.client.post('/rfqs', rfq);
    return response.data.data;
  }

  async updateRFQ(id: number, rfq: UpdateRFQRequest): Promise<RFQ> {
    const response: AxiosResponse<ApiResponse<RFQ>> = await this.client.put(`/rfqs/${id}`, rfq);
    return response.data.data;
  }

  async deleteRFQ(id: number): Promise<void> {
    await this.client.delete(`/rfqs/${id}`);
  }
}

export const apiClient = new ApiClient();
