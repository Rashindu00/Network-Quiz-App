# Network Quiz App - Member 1 Implementation


## 👤 Member 1 - Implementation Details

### Backend Components (Java)

#### 1. **QuizServer.java** 
- **Network Concepts**: `ServerSocket`, `Socket`, `Multithreading`, `Thread Pool`
- **Functionality**:
  - Initializes ServerSocket on port 8080
  - Accepts multiple client connections
  - Creates separate threads for each client
  - Manages quiz state and lifecycle
  - Broadcasts messages to all clients

#### 2. **ClientHandler.java**
- **Network Concepts**: `Thread`, `Socket Communication`, `InputStream/OutputStream`
- **Functionality**:
  - Runs in a separate thread per client
  - Handles client registration
  - Manages bidirectional communication
  - Processes client messages
  - Handles disconnections gracefully

#### 3. **ConnectedClientsManager.java**
- **Network Concepts**: `Thread-safe Collections`, `Synchronization`
- **Functionality**:
  - Maintains list of all connected clients
  - Thread-safe add/remove operations
  - Broadcasts messages to all/specific clients
  - Provides client statistics
  - Manages concurrent access

### Frontend Component (React)

#### **AdminDashboard.jsx**
- **Features**:
  - Real-time display of connected students
  - Start Quiz button with validation
  - Connection statistics dashboard
  - Server status monitoring
  - Responsive design

## 🚀 Setup Instructions

### Backend (Java)

#### Prerequisites
- Java JDK 11 or higher
- Any Java IDE (IntelliJ IDEA, Eclipse, VS Code)

#### Running the Server

1. **Compile the Java files**:
```bash
cd backend/src/main/java

javac -encoding UTF-8 com\quizapp\server\QuizServer.java com\quizapp\server\ClientHandler.java com\quizapp\server\ConnectedClientsManager.java com\quizapp\client\TestClient.java


```

2. **Run the server**:
```bash
java com.quizapp.server.QuizServer
```

The server will start on port `8080` and wait for client connections.

### Frontend (React)

#### Prerequisites
- Node.js 14 or higher
- npm or yarn

#### Running the Frontend

1. **Install dependencies**:
```bash
cd frontend
npm install
```

2. **Start the development server**:
```bash
npm start
```

The application will open at `http://localhost:3000`
