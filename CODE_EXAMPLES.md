# 💡 Code Examples & Best Practices - Network Quiz Application

## Table of Contents
1. [Java Networking Patterns](#java-networking-patterns)
2. [React Communication Patterns](#react-communication-patterns)
3. [Thread-Safe Patterns](#thread-safe-patterns)
4. [Error Handling](#error-handling)
5. [Performance Tips](#performance-tips)

---

## Java Networking Patterns

### Pattern 1: Server Socket Acceptance Loop

```java
// QuizServer.java - Member 1
public void start() {
    try {
        serverSocket = new ServerSocket(5000);
        running = true;
        
        // Accept connections in loop
        while (running) {
            Socket clientSocket = serverSocket.accept();
            
            // Create handler for this client
            ClientHandler handler = new ClientHandler(
                clientSocket, 
                clientsManager, 
                this
            );
            
            // Execute in thread pool
            threadPool.execute(handler);
        }
    } catch (IOException e) {
        System.err.println("Server error: " + e.getMessage());
    }
}

// ✅ Best Practices:
// - Use thread pool for scalability
// - Handle exceptions gracefully
// - Provide graceful shutdown
// - Log important events
```

### Pattern 2: Per-Client Thread Handler

```java
// ClientHandler.java - Member 1
public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private BufferedReader input;
    private PrintWriter output;
    
    @Override
    public void run() {
        try {
            setupStreams();
            
            if (registerClient()) {
                connected = true;
                clientsManager.addClient(this);
                listenForMessages();
            }
        } catch (IOException e) {
            System.err.println("Client error: " + e.getMessage());
        } finally {
            disconnect();
        }
    }
    
    private void listenForMessages() throws IOException {
        String message;
        while (connected && (message = input.readLine()) != null) {
            processMessage(message);
        }
    }
    
    // ✅ Best Practices:
    // - Clean resource management in finally
    // - Use try-with-resources when possible
    // - Proper exception handling
    // - Always disconnect on exit
}
```

### Pattern 3: Thread-Safe Broadcasting

```java
// ConnectedClientsManager.java - Member 1
public class ConnectedClientsManager {
    private final CopyOnWriteArrayList<ClientHandler> clients;
    private final ConcurrentHashMap<String, ClientHandler> clientMap;
    
    // Thread-safe add operation
    public synchronized void addClient(ClientHandler client) {
        clients.add(client);
        clientMap.put(client.getClientId(), client);
    }
    
    // Broadcast to all clients
    public void broadcastToAll(Object message) {
        // CopyOnWriteArrayList allows safe iteration during modification
        for (ClientHandler client : clients) {
            if (client.isConnected()) {
                try {
                    client.sendMessage(message.toString());
                } catch (Exception e) {
                    System.err.println("Broadcast error: " + e.getMessage());
                }
            }
        }
    }
    
    // ✅ Best Practices:
    // - Use CopyOnWriteArrayList for thread-safe iteration
    // - Use ConcurrentHashMap for thread-safe lookup
    // - Synchronized methods for critical sections
    // - Exception handling per-client to avoid broadcast failure
}
```

### Pattern 4: Object Serialization

```java
// QuestionManager.java - Member 2
public void loadQuestions() {
    File file = new File("questions.dat");
    
    if (file.exists()) {
        try (ObjectInputStream ois = new ObjectInputStream(
            new FileInputStream(file))) {
            
            @SuppressWarnings("unchecked")
            List<QuizQuestion> loadedQuestions = 
                (List<QuizQuestion>) ois.readObject();
            questions = loadedQuestions;
            
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading questions: " + 
                e.getMessage());
            createDefaultQuestions();
        }
    } else {
        createDefaultQuestions();
    }
}

public void saveQuestions() {
    try (ObjectOutputStream oos = new ObjectOutputStream(
        new FileOutputStream("questions.dat"))) {
        
        oos.writeObject(questions);
        System.out.println("Questions saved successfully");
        
    } catch (IOException e) {
        System.err.println("Error saving questions: " + e.getMessage());
    }
}

// ✅ Best Practices:
// - Use try-with-resources for auto-closing
// - Handle serialization exceptions properly
// - Suppress unchecked warnings appropriately
// - Always have fallback (default questions)
```

### Pattern 5: Synchronized Thread-Safe Operations

```java
// AnswerEvaluator.java - Member 4
private final Object evaluationLock = new Object();

public void submitAnswer(Answer answer) {
    List<Answer> answers = studentAnswers.get(answer.getStudentId());
    
    if (answers != null) {
        synchronized (evaluationLock) {
            // Evaluate the answer
            QuizQuestion question = questionManager.getQuestionById(
                answer.getQuestionId());
            
            if (question != null) {
                boolean isCorrect = answer.getSelectedAnswerIndex() == 
                    question.getCorrectAnswerIndex();
                answer.setCorrect(isCorrect);
            }
        }
        answers.add(answer);
    }
}

public Map<String, Score> evaluateAllAnswers(
    Map<String, String> studentNames) {
    
    Map<String, Score> scores = new ConcurrentHashMap<>();
    
    synchronized (evaluationLock) {
        int totalMarks = questionManager.getTotalMarks();
        
        for (String studentId : studentAnswers.keySet()) {
            List<Answer> answers = getStudentAnswers(studentId);
            int obtainedMarks = calculateMarks(answers);
            
            String studentName = studentNames.getOrDefault(
                studentId, "Unknown");
            Score score = new Score(studentId, studentName, 
                totalMarks, obtainedMarks);
            scores.put(studentId, score);
        }
    }
    
    return scores;
}

// ✅ Best Practices:
// - Use private lock objects
// - Keep synchronized blocks short
// - Don't call external methods in synchronized blocks
// - Use ConcurrentHashMap for return value
```

### Pattern 6: Non-Blocking Operations with NIO

```java
// ResultBroadcaster.java - Member 5
private ExecutorService executorService;

public void broadcastAllResults() {
    executorService.execute(() -> {
        try {
            // Send individual scores non-blocking
            broadcastIndividualScores();
            
            // Then broadcast leaderboard
            broadcastLeaderboard();
            
            broadcastComplete = true;
            
        } catch (Exception e) {
            System.err.println("Broadcast error: " + e.getMessage());
        }
    });
}

private void broadcastIndividualScores() {
    Map<String, Score> allScores = scoreManager.getAllScores();
    CountDownLatch latch = new CountDownLatch(allScores.size());
    
    for (Score score : allScores.values()) {
        executorService.submit(() -> {
            try {
                sendScoreToStudent(score);
            } finally {
                latch.countDown();
            }
        });
    }
    
    try {
        // Wait for all scores to be sent
        latch.await();
        System.out.println("All individual scores sent!");
    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
    }
}

// ✅ Best Practices:
// - Use ExecutorService for thread pools
// - Use CountDownLatch for coordination
// - Handle InterruptedException properly
// - Non-blocking operations for better throughput
```

---

## React Communication Patterns

### Pattern 1: Socket.IO Connection Setup

```javascript
// App.js - Member 1 (Frontend)
useEffect(() => {
    // Initialize Socket.IO connection
    const newSocket = io('http://localhost:3001', {
        reconnection: true,
        reconnectionDelay: 1000,
        reconnectionDelayMax: 5000,
        reconnectionAttempts: 5,
    });
    
    // Connection established
    newSocket.on('connect', () => {
        console.log('Connected to server');
        setConnected(true);
    });
    
    // Connection lost
    newSocket.on('disconnect', () => {
        console.log('Disconnected from server');
        setConnected(false);
    });
    
    // Error handling
    newSocket.on('error', (error) => {
        console.error('Socket error:', error);
    });
    
    setSocket(newSocket);
    
    return () => {
        newSocket.close();
    };
}, []);

// ✅ Best Practices:
// - Configure reconnection settings
// - Clean up in useEffect return
// - Log connection events
// - Handle all error scenarios
```

### Pattern 2: Event Listening & State Updates

```javascript
// QuestionPanel.jsx - Member 2
useEffect(() => {
    if (socket) {
        // Listen for question events
        socket.on('QUESTION', (data) => {
            setCurrentQuestion(data);
            setQuestionNumber(data.currentQuestion);
            setTimeRemaining(30);
            setSelectedOption(null);
        });
        
        // Cleanup listeners
        return () => {
            socket.off('QUESTION');
        };
    }
}, [socket]);

// ✅ Best Practices:
// - Dependency array includes socket
// - Cleanup listeners in return
// - Separate concerns (effects)
// - Initialize state properly
```

### Pattern 3: Event Emission with Callback

```javascript
// AdminDashboard.jsx - Member 1
const handleStartQuiz = () => {
    setLoading(true);
    
    if (socket) {
        // Emit event with callback
        socket.emit('START_QUIZ', {}, (response) => {
            if (response.success) {
                setQuizStarted(true);
            }
            setLoading(false);
        });
    }
};

// ✅ Best Practices:
// - Use loading states
// - Provide user feedback
// - Handle async responses
// - Error handling on failure
```

### Pattern 4: Conditional Rendering Based on State

```javascript
// StudentQuiz.jsx - Member 3
if (!loggedIn) {
    return (
        <div className="student-quiz">
            <div className="login-container">
                {/* Login form */}
            </div>
        </div>
    );
}

return (
    <div className="student-quiz">
        <div className="quiz-header">
            {/* Header content */}
        </div>
        
        {!quizStarted ? (
            <div className="waiting-screen">
                {/* Waiting screen */}
            </div>
        ) : (
            <QuestionPanel socket={socket} studentId={studentId} />
        )}
    </div>
);

// ✅ Best Practices:
// - Clear state management
// - Component composition
// - Prop drilling minimized
// - Reusable components
```

### Pattern 5: Real-Time Data Updates

```javascript
// Leaderboard.jsx - Member 5
useEffect(() => {
    if (socket) {
        socket.on('LEADERBOARD', (data) => {
            // Update rankings immediately
            setRankings(data.rankings || []);
            setStatistics(data.statistics || null);
            setLoading(false);
        });
        
        return () => {
            socket.off('LEADERBOARD');
        };
    }
}, [socket]);

const getMedalEmoji = (rank) => {
    if (rank === 1) return '🥇';
    if (rank === 2) return '🥈';
    if (rank === 3) return '🥉';
    return null;
};

// Render with dynamic content
{rankings.map((entry, index) => (
    <div key={index} className={`table-row rank-${entry.rank}`}>
        <div className="col-rank">
            <span className="rank-badge">
                {getMedalEmoji(entry.rank)}
                {entry.rank}
            </span>
        </div>
        {/* More content */}
    </div>
))}

// ✅ Best Practices:
// - Real-time updates with socket events
// - Dynamic rendering
// - Performance optimization with key props
// - Responsive UI updates
```

---

## Thread-Safe Patterns

### Pattern 1: CopyOnWriteArrayList

```java
// Safe for concurrent iteration and modification
private final CopyOnWriteArrayList<ClientHandler> connectedClients;

public synchronized void addClient(ClientHandler client) {
    connectedClients.add(client);
    // Notification happens automatically
}

public void broadcastToAll(String message) {
    // Safe iteration even if list is modified
    for (ClientHandler client : connectedClients) {
        client.sendMessage(message);
    }
}

// ✅ Best Practices:
// - Use when iteration > modification
// - Provides snapshot consistency
// - No ConcurrentModificationException
```

### Pattern 2: ConcurrentHashMap

```java
// Thread-safe map for lookups and updates
private final ConcurrentHashMap<String, ClientHandler> clientMap;

public ClientHandler getClientById(String clientId) {
    return clientMap.get(clientId);
}

public void addClientToMap(String id, ClientHandler handler) {
    clientMap.put(id, handler);
}

// ✅ Best Practices:
// - No synchronized blocks needed
// - Segments for better concurrency
// - Atomic operations
```

### Pattern 3: Synchronized Collections Wrapper

```java
// For moderately concurrent scenarios
Map<String, StudentInfo> studentMap = 
    Collections.synchronizedMap(new HashMap<>());

public void registerStudent(String id, StudentInfo info) {
    studentMap.put(id, info);
}

// ✅ Best Practices:
// - Use for moderate concurrency
// - Simpler than manual synchronization
// - Avoid for high-throughput scenarios
```

---

## Error Handling

### Pattern 1: Try-with-Resources

```java
// Automatic resource cleanup
try (ObjectInputStream ois = new ObjectInputStream(
    new FileInputStream("questions.dat"))) {
    
    questions = (List<QuizQuestion>) ois.readObject();
    
} catch (IOException | ClassNotFoundException e) {
    System.err.println("Error: " + e.getMessage());
    createDefaultQuestions();
}

// ✅ Best Practices:
// - Automatic close() call
// - Multiple exceptions in catch
// - Cleaner code
```

### Pattern 2: Graceful Degradation

```java
public void broadcastToAll(String message) {
    for (ClientHandler client : connectedClients) {
        if (client.isConnected()) {
            try {
                client.sendMessage(message);
            } catch (Exception e) {
                // Don't fail entire broadcast
                System.err.println("Failed to send to: " + 
                    client.getStudentName());
                // Continue with next client
            }
        }
    }
}

// ✅ Best Practices:
// - Per-client exception handling
// - Partial success acceptable
// - Log failures for debugging
```

### Pattern 3: Proper Shutdown

```java
public void stop() {
    running = false;
    
    try {
        clientsManager.disconnectAll();
        
        threadPool.shutdown();
        if (!threadPool.awaitTermination(5, TimeUnit.SECONDS)) {
            threadPool.shutdownNow();
        }
        
        if (serverSocket != null && !serverSocket.isClosed()) {
            serverSocket.close();
        }
        
    } catch (IOException | InterruptedException e) {
        System.err.println("Error stopping: " + e.getMessage());
    }
}

// ✅ Best Practices:
// - Graceful shutdown
// - Resource cleanup
// - Handle interruptions
// - Force shutdown if needed
```

---

## Performance Tips

### Tip 1: Use Thread Pools

```java
// ❌ Poor: Creates new thread for each task
new Thread(task).start();

// ✅ Good: Reuses threads
ExecutorService executor = Executors.newFixedThreadPool(10);
executor.submit(task);
```

### Tip 2: Minimize Synchronized Blocks

```java
// ❌ Poor: Large synchronized block
synchronized (lock) {
    // Many operations here
    doExpensiveWork1();
    doExpensiveWork2();
}

// ✅ Good: Minimal synchronization
Object result;
synchronized (lock) {
    result = getData();  // Only critical section
}
doExpensiveWork(result);
```

### Tip 3: Use Appropriate Collections

```java
// ❌ Poor choice for concurrent reads
Map<String, Data> map = new HashMap<>();

// ✅ Better for concurrent reads
Map<String, Data> map = new ConcurrentHashMap<>();

// ✅ Best for mostly iteration
List<Item> list = new CopyOnWriteArrayList<>();
```

### Tip 4: Buffer Network Operations

```java
// ❌ Inefficient: Sends immediately
for (String message : messages) {
    socket.send(message);
}

// ✅ Better: Batch send
StringBuilder batch = new StringBuilder();
for (String message : messages) {
    batch.append(message).append("\n");
}
socket.send(batch.toString());
```

### Tip 5: Async Operations

```javascript
// ❌ Blocking
const result = await expensiveOperation();
setData(result);

// ✅ Non-blocking with feedback
setLoading(true);
expensiveOperation().then(result => {
    setData(result);
    setLoading(false);
}).catch(error => {
    setError(error);
    setLoading(false);
});
```

---

## Testing Examples

### Unit Test Pattern

```java
@Test
public void testAnswerEvaluation() {
    AnswerEvaluator evaluator = new AnswerEvaluator(questionManager);
    
    Answer answer = new Answer("STU001", 1, 0);
    evaluator.submitAnswer(answer);
    
    Score score = evaluator.calculateStudentScore("STU001", "John");
    
    assertTrue(score.getObtainedMarks() >= 0);
    assertEquals(score.getStudentId(), "STU001");
}
```

### Integration Test Pattern

```javascript
// Mock socket for testing
const mockSocket = {
    emit: jest.fn(),
    on: jest.fn(),
    off: jest.fn(),
};

describe('AdminDashboard', () => {
    it('should display connected students', () => {
        const { getByText } = render(
            <AdminDashboard socket={mockSocket} />
        );
        
        expect(mockSocket.on).toHaveBeenCalledWith(
            'CONNECTED_STUDENTS',
            expect.any(Function)
        );
    });
});
```

---

## 🎯 Summary

These patterns and best practices ensure:
- ✅ **Robustness** - Proper error handling
- ✅ **Performance** - Efficient resource usage
- ✅ **Scalability** - Thread pools and async operations
- ✅ **Maintainability** - Clear, documented code
- ✅ **Safety** - Thread-safe operations
- ✅ **Responsiveness** - Non-blocking operations

Use these as templates for extending the application!
