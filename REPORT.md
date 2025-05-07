# ChatP2P - Peer-to-Peer Chat Application

## Table of Contents
1. [Project Overview](#project-overview)
2. [Architecture](#architecture)
3. [Backend Implementation](#backend-implementation)
4. [Frontend Implementation](#frontend-implementation)
5. [Docker Configuration](#docker-configuration)
6. [Features](#features)
7. [Security](#security)
8. [Real-time Communication](#real-time-communication)
9. [File Handling](#file-handling)
10. [Deployment](#deployment)

## Project Overview

ChatP2P is a modern web-based peer-to-peer chat application that allows users to communicate in real-time. The application features private messaging, group chats, file sharing, and user status management.

### Key Technologies
- **Backend**: Spring Boot, MongoDB
- **Frontend**: HTML5, CSS3, JavaScript
- **Containerization**: Docker
- **Real-time Communication**: Server-Sent Events (SSE)
- **Authentication**: OAuth2

## Architecture

### System Architecture
```
┌─────────────────┐     ┌─────────────────┐     ┌─────────────────┐
│                 │     │                 │     │                 │
│  Frontend       │◄───►│  Spring Boot    │◄───►│  MongoDB        │
│  (HTML/JS/CSS)  │     │  Backend        │     │  Database       │
│                 │     │                 │     │                 │
└─────────────────┘     └─────────────────┘     └─────────────────┘
```

### Component Interaction
1. User Interface (Frontend)
   - Handles user interactions
   - Displays messages and notifications
   - Manages real-time updates

2. Application Server (Spring Boot)
   - Processes HTTP requests
   - Manages user authentication
   - Handles real-time communication
   - Manages file storage

3. Database (MongoDB)
   - Stores user data
   - Stores chat messages
   - Stores file metadata

## Backend Implementation

### Spring Boot Application
The backend is built using Spring Boot with the following key components:

#### Controllers
- `ChatController`: Handles chat operations
- `MessageController`: Manages message operations
- `UserController`: Handles user-related operations
- `FileController`: Manages file uploads and downloads

#### Services
- `MessageService`: Business logic for messages
- `UserService`: User management
- `FileStorageService`: File handling
- `ChatService`: Chat management

#### Models
- `Message`: Message data structure
- `Chat`: Chat room structure
- `User`: User information
- `FileAttachment`: File metadata

### Database Schema
```json
{
  "users": {
    "id": "String",
    "username": "String",
    "email": "String",
    "status": "String"
  },
  "chats": {
    "id": "String",
    "name": "String",
    "userIds": ["String"],
    "createdAt": "Long"
  },
  "messages": {
    "id": "String",
    "content": "String",
    "senderId": "String",
    "chatId": "String",
    "fileAttachments": [{
      "fileName": "String",
      "fileUrl": "String",
      "fileSize": "Long",
      "mimeType": "String"
    }]
  }
}
```

## Frontend Implementation

### User Interface Components
1. **Chat Interface**
   - Message display area
   - Message input field
   - File attachment button
   - User status indicators

2. **User Management**
   - User profile display
   - Status updates
   - Friend list management

3. **Real-time Updates**
   - Server-Sent Events for live updates
   - Message notifications
   - User status changes

### Key JavaScript Functions
```javascript
// Message handling
function displayMessage(message, isSent) {
    // Display message in chat
}

// File handling
function handleFiles(files) {
    // Process file uploads
}

// Real-time updates
function initializeSSE() {
    // Set up Server-Sent Events
}
```

## Docker Configuration

### Dockerfile
```dockerfile
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### Docker Compose
```yaml
version: '3.8'
services:
  app:
    build: .
    ports:
      - "8080:8080"
    environment:
      - SPRING_DATA_MONGODB_URI=mongodb://mongo:27017/chatp2p
    depends_on:
      - mongo
  mongo:
    image: mongo:latest
    ports:
      - "27017:27017"
    volumes:
      - mongodb_data:/data/db

volumes:
  mongodb_data:
```

## Features

### Core Features
1. **Real-time Messaging**
   - Instant message delivery
   - Message read status
   - Typing indicators

2. **File Sharing**
   - File upload and download
   - File preview
   - Multiple file support

3. **User Management**
   - User profiles
   - Online status
   - Friend management

4. **Group Chats**
   - Create and manage groups
   - Add/remove participants
   - Group settings

### Advanced Features
1. **Message Reactions**
   - Emoji reactions
   - Reaction counts

2. **Rich Text Support**
   - Text formatting
   - Links
   - Code blocks

3. **Search Functionality**
   - Message search
   - User search
   - File search

## Security

### Authentication
- OAuth2 integration
- JWT token management
- Session handling

### Data Protection
- Encrypted file storage
- Secure message transmission
- Input validation

### Access Control
- Role-based access
- Permission management
- Resource protection

## Real-time Communication

### Server-Sent Events Implementation
```java
@GetMapping(path = "/open-sse-stream/{nick}")
public Flux<ServerSentEvent> openSseStream(@PathVariable String nick) {
    // SSE implementation
}
```

### Event Types
1. **Message Events**
   - New message notifications
   - Message updates
   - Message deletions

2. **User Events**
   - Status changes
   - Profile updates
   - Online/offline status

3. **Chat Events**
   - New chat creation
   - Participant changes
   - Chat updates

## File Handling

### File Storage
- Local file system storage
- File metadata management
- File size limits

### File Processing
- MIME type detection
- File preview generation
- Thumbnail creation

### Security Measures
- File type validation
- Size restrictions
- Virus scanning

## Deployment

### Requirements
- Java 17+
- MongoDB 4.4+
- Docker (optional)

### Deployment Steps
1. Build the application
2. Configure environment variables
3. Start MongoDB
4. Deploy Spring Boot application
5. Configure reverse proxy (optional)

### Monitoring
- Application metrics
- Error logging
- Performance monitoring

## Conclusion

ChatP2P demonstrates a modern approach to real-time communication applications, combining robust backend services with an intuitive frontend interface. The use of Docker for containerization ensures easy deployment and scalability, while the implementation of Server-Sent Events provides efficient real-time updates.

### Future Improvements
1. WebSocket implementation for bidirectional communication
2. End-to-end encryption for messages
3. Video/audio call functionality
4. Mobile application support
5. Advanced search capabilities 