// Simple Chat App
let ws = null;
let username = localStorage.getItem('username') || 'Guest';
let displayName = localStorage.getItem('displayName') || '게스트';
let token = localStorage.getItem('token');

// Check auth
if (!token) {
    window.location.href = '/';
}

// Connect WebSocket
function connect() {
    const wsProtocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:';
    const wsUrl = `${wsProtocol}//${window.location.host}/ws?token=${token}`;

    ws = new WebSocket(wsUrl);

    ws.onopen = () => {
        console.log('WebSocket Connected');
        updateStatus(true);

        // Join general room
        const joinMsg = {
            type: 'joinRoom',
            roomId: 'general',
            sender: username
        };
        ws.send(JSON.stringify(joinMsg));
    };

    ws.onmessage = (event) => {
        try {
            const message = JSON.parse(event.data);
            handleMessage(message);
        } catch (e) {
            console.error('Failed to parse message:', e);
        }
    };

    ws.onerror = (error) => {
        console.error('WebSocket Error:', error);
        updateStatus(false);
    };

    ws.onclose = () => {
        console.log('WebSocket Disconnected');
        updateStatus(false);

        // Auto reconnect after 3 seconds
        setTimeout(() => {
            console.log('Reconnecting...');
            connect();
        }, 3000);
    };
}

function handleMessage(message) {
    console.log('Received:', message);

    switch (message.type) {
        case 'message':
            if (message.sender !== username) {
                addMessage(message.sender, message.content, false);
            }
            break;
        case 'system':
            addSystemMessage(message.content);
            break;
        case 'roomlist':
        case 'userlist':
            // Ignore for now
            break;
        default:
            console.log('Unknown message type:', message.type);
    }
}

function sendMessage() {
    const input = document.getElementById('messageInput');
    const text = input.value.trim();

    if (!text || !ws || ws.readyState !== WebSocket.OPEN) {
        return;
    }

    const message = {
        type: 'message',
        sender: username,
        content: text
    };

    ws.send(JSON.stringify(message));

    // Add own message to chat immediately
    addMessage(username, text, true);

    input.value = '';
    input.style.height = 'auto';
}

function addMessage(sender, text, isOwn = false) {
    const messagesDiv = document.getElementById('messages');

    const messageDiv = document.createElement('div');
    messageDiv.className = 'message' + (isOwn ? ' own' : '');

    const initial = sender.charAt(0).toUpperCase();
    const time = new Date().toLocaleTimeString('ko-KR', { hour: '2-digit', minute: '2-digit' });

    messageDiv.innerHTML = `
        <div class="message-avatar">${initial}</div>
        <div class="message-content">
            <div class="message-header">
                <span class="message-sender">${escapeHtml(sender)}</span>
                <span class="message-time">${time}</span>
            </div>
            <div class="message-text">${escapeHtml(text)}</div>
        </div>
    `;

    messagesDiv.appendChild(messageDiv);
    messagesDiv.scrollTop = messagesDiv.scrollHeight;
}

function addSystemMessage(text) {
    const messagesDiv = document.getElementById('messages');
    const messageDiv = document.createElement('div');
    messageDiv.className = 'system-message';
    messageDiv.textContent = text;
    messagesDiv.appendChild(messageDiv);
    messagesDiv.scrollTop = messagesDiv.scrollHeight;
}

function updateStatus(connected) {
    const statusEl = document.getElementById('status');
    const statusTextEl = document.getElementById('statusText');

    if (connected) {
        statusEl.classList.add('connected');
        statusTextEl.textContent = '온라인';
    } else {
        statusEl.classList.remove('connected');
        statusTextEl.textContent = '연결 끊김 - 재연결 중...';
    }
}

function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

function logout() {
    if (confirm('로그아웃 하시겠습니까?')) {
        if (ws) ws.close();
        localStorage.clear();
        window.location.href = '/';
    }
}

// Event Listeners
document.getElementById('messageInput').addEventListener('keydown', (e) => {
    if (e.key === 'Enter' && !e.shiftKey) {
        e.preventDefault();
        sendMessage();
    }
});

// Auto-resize textarea
document.getElementById('messageInput').addEventListener('input', function() {
    this.style.height = 'auto';
    this.style.height = Math.min(this.scrollHeight, 100) + 'px';
});

// Initialize
connect();
