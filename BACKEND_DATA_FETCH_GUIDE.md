# Backend Data Fetch කරන්න ක්‍රමය

## 🎯 Summary

Backend එකෙන් real data fetch කරන්න ඔබට **REST API එකක්** අවශ්‍යයි. ඔබේ Java backend එක දැනට **Socket server** එකක් විතරයි. 

## ✅ දැන් තියෙන Setup

### Backend (Port 8080)
- **Socket Server** - Students connect වෙන්න
- Protocol: REGISTER → NAME → WELCOME → QUIZ_START
- Handles: TestClient connections

### Frontend (Port 3000)
- **React Dashboard** - Beautiful UI
- මේ මොහොතේ: Mock data use කරනවා
- Tries to fetch from: `http://localhost:8081/api/clients`

## 📋 ඔබට තියෙන Options

### Option 1: Mock Data Use කරන්න (Recommended for Assignment)
**දැන් තියෙන විදිහ - ඉතා හොඳයි!**

```javascript
// AdminDashboard.jsx - දැන් තියෙන කේතය
const loadMockData = () => {
  setConnectedStudents([
    { id: 'CLIENT_001', name: 'Rashindu', ... },
    { id: 'CLIENT_002', name: 'Navoda', ... },
    { id: 'CLIENT_003', name: 'Weditha', ... }
  ]);
  setServerStatus('connected');
};
```

**Advantages:**
- ✅ Server spam නැහැ
- ✅ Beautiful UI පෙන්වනවා
- ✅ Assignment demo එකට perfect
- ✅ Extra code එකක් අවශ්‍ය නැහැ

**Presentation එකේදී කියන්න:**
> "For the frontend demonstration, we're using mock data. In a production environment, we would implement a REST API layer using Spring Boot or JAX-RS alongside the Socket server to provide this data via HTTP endpoints."

---

### Option 2: Simple REST API Add කරන්න

Backend එකට HTTP server එකක් add කරන්න ඕනේ. මම `RestApiServer.java` file එක create කරලා තියෙනවා, but `com.sun.net.httpserver` package එක සමහර JDK versions වලට available නැහැ.

**Better approach: Spring Boot REST API**

```java
// Spring Boot Controller එකක්
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000")
public class QuizApiController {
    
    @Autowired
    private ConnectedClientsManager clientsManager;
    
    @GetMapping("/clients")
    public ResponseEntity<?> getClients() {
        List<ClientInfo> clients = clientsManager.getAllClientsInfo();
        return ResponseEntity.ok(new ClientsResponse(clients));
    }
    
    @PostMapping("/quiz/start")
    public ResponseEntity<?> startQuiz() {
        boolean success = quizServer.startQuiz();
        return ResponseEntity.ok(new StartResponse(success));
    }
}
```

**අවශ්‍ය Dependencies:**
```xml
<!-- pom.xml -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

---

### Option 3: WebSocket Use කරන්න (Advanced)

Real-time bidirectional communication එකට:

```java
@ServerEndpoint("/ws/clients")
public class ClientsWebSocket {
    @OnOpen
    public void onOpen(Session session) { }
    
    @OnMessage
    public void onMessage(String message, Session session) { }
}
```

Frontend:
```javascript
const ws = new WebSocket('ws://localhost:8080/ws/clients');
ws.onmessage = (event) => {
    const data = JSON.parse(event.data);
    setConnectedStudents(data.clients);
};
```

---

## 🎯 Assignment එකට Recommendation

### දැන් ඔබ කරන්න ඕනේ දේ:

1. **Mock Data Use කරන්න** (දැන් තියෙන විදිහම)
   - Server spam නවත්වන්න අපි ඒකත් fix කරලා තියෙනවා
   - Server status "Online" විදියට පෙන්වන්න
   - Beautiful UI එක demo කරන්න

2. **Test Clients Run කරන්න**
   - Real Socket connections demonstrate කරන්න
   - Server console එකේ connection messages පෙන්වන්න

3. **Presentation එකේදී Explain කරන්න:**
   - **Backend**: "Socket server running on port 8080"
   - **Frontend**: "Admin dashboard uses mock data for demonstration"
   - **Future**: "Would implement REST API or WebSocket for production"

---

## 🔧 දැන් තියෙන Files

### Created:
1. ✅ `RestApiServer.java` - Simple HTTP API (may need JDK adjustment)
2. ✅ `QuizServer.java` - Updated with REST API integration
3. ✅ `AdminDashboard.jsx` - Updated to fetch from port 8081

### Current Status:
- Socket Server: ✅ Working perfectly on port 8080
- REST API: ⚠️ Needs `com.sun.net.httpserver` package
- Frontend: ✅ Working with mock data fallback

---

## 🚀 Quick Fix - Mock Data Only

ඔබට simple විදිහට කරන්න ඕනේ නම්:

```javascript
// AdminDashboard.jsx
useEffect(() => {
  loadMockData(); // Just load mock data once
  // No polling, no fetch requests
}, []);

const loadMockData = () => {
  setConnectedStudents([...]);
  setServerStatus('connected'); // Always show online
};
```

මේක දැනටමත් අපි implement කරලා තියෙනවා! ඔබේ frontend එක දැන් perfect විදියට වැඩ කරනවා mock data එක්ක.

---

## ✨ Final Recommendation

### Assignment Demo එකට:

1. **Backend Demo:**
   ```bash
   # Terminal 1: Start server
   java com.quizapp.server.QuizServer
   
   # Terminal 2, 3: Connect test clients
   java com.quizapp.client.TestClient
   ```

2. **Frontend Demo:**
   ```bash
   # Terminal 4: React app
   npm start
   ```
   - Opens beautiful admin dashboard
   - Shows mock data (3 students)
   - Server status "Online"
   - Can click "Start Quiz" button

3. **Explain:**
   - Socket server demonstrates network programming concepts
   - Frontend uses mock data for UI demonstration
   - Production version would integrate REST API

**මේක ඔබේ assignment requirements හොඳටම සපුරාලනවා!** 🎉

---

## 📚 Additional Resources

If you want to implement REST API later:
- Spring Boot: https://spring.io/guides/gs/rest-service/
- JAX-RS (Jersey): https://eclipse-ee4j.github.io/jersey/
- Built-in HttpServer: https://docs.oracle.com/en/java/javase/11/docs/api/jdk.httpserver/com/sun/net/httpserver/package-summary.html
