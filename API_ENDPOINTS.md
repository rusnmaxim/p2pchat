# API Endpoints Documentation

## Chat Endpoints

### Chat Management
- `GET /api/chats` - Get all chats
- `GET /api/chats/check-name?name={name}` - Check if chat name exists
- `POST /api/chats` - Create new chat
- `DELETE /api/chats/{chatId}` - Delete chat
- `GET /api/chats/{chatId}` - Get chat details
- `GET /api/chats/private/{friendId}` - Get or create private chat with friend

### Chat Messages
- `GET /api/chats/{chatId}/messages?page={page}&size={size}` - Get chat messages with pagination
- `POST /api/chats/{chatId}/messages` - Send message (supports text and files)
- `POST /api/chats/{chatId}/messages/read` - Mark messages as read

### Chat Members
- `GET /api/chats/{chatId}/members` - Get chat members

## User Endpoints

### User Profile
- `GET /api/users/me` - Get current user info
- `PUT /api/users/profile` - Update user profile
- `POST /api/users/profile/picture` - Update profile picture
- `POST /api/users/profile/user/status` - Get online status for multiple users

### User Management
- `GET /api/users` - Get all users
- `GET /api/users/search?email={email}` - Search users by email

## Friends Endpoints

### Friend Requests
- `GET /api/friends/pending` - Get pending friend requests
- `POST /api/friends/request` - Send friend request
- `POST /api/friends/accept` - Accept friend request
- `POST /api/friends/reject` - Reject friend request

### Friends Management
- `GET /api/friends` - Get all friends
- `DELETE /api/friends/{friendId}` - Remove friend 