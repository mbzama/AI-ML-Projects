<!-- Use this file to provide workspace-specific custom instructions to Copilot. For more details, visit https://code.visualstudio.com/docs/copilot/copilot-customization#_use-a-githubcopilotinstructionsmd-file -->

# Procurement Frontend Application

This is a React TypeScript application built with Vite for procurement management.

## Tech Stack
- **React 18** with TypeScript
- **Vite** for build tooling
- **Tailwind CSS** for styling
- **shadcn/ui** for UI components
- **React Router DOM** for routing
- **Tanstack React Query** for data fetching
- **Axios** for HTTP requests

## Project Structure
- `/src/components/ui/` - Reusable UI components (Button, Card, Input, etc.)
- `/src/components/` - Application-specific components (Layout, ProtectedRoute)
- `/src/pages/` - Page components organized by user role (admin/, vendor/)
- `/src/contexts/` - React contexts (AuthContext)
- `/src/lib/` - Utility functions and API client
- `/src/types/` - TypeScript type definitions

## User Roles
- **Admin**: Can manage vendors, auctions, and view dashboard statistics
- **Vendor**: Can view auctions and submit bids

## Key Features
- Role-based authentication and authorization
- Admin dashboard with vendor/auction/RFQ statistics
- Vendor management (CRUD operations)
- Auction management (CRUD operations)
- Vendor bidding system
- Protected routes based on user roles

## API Integration
The application integrates with a Spring Boot backend API. All API calls are made through the `apiClient` in `/src/lib/api.ts`.

## Styling Guidelines
- Use Tailwind CSS utility classes
- Follow shadcn/ui component patterns
- Maintain consistent spacing and typography
- Use the defined color scheme in CSS variables
